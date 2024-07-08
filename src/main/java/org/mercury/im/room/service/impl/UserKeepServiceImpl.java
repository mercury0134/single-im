package org.mercury.im.room.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import io.netty.channel.ChannelId;
import jakarta.annotation.Resource;
import org.mercury.im.gateway.core.secuirty.JwtTokenResolver;
import org.mercury.im.link.core.infra.protocol.SocketCommand;
import org.mercury.im.link.core.infra.protocol.SocketRequestHeader;
import org.mercury.im.link.core.infra.ws.pojo.Session;
import org.mercury.im.link.core.infra.ws.util.SessionUtil;
import org.mercury.im.room.service.UserKeepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserKeepServiceImpl implements UserKeepService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 已注册的session userId -> session
     */
    private final ConcurrentHashMap<Long, Session> registrySession = new ConcurrentHashMap<>();

    /**
     * 未注册的session session id -> session
     * websocket基于tcp长连接,由四元组保证,Netty channel tcp相对应 当session建立连接后鉴权未通过则关闭
     */
    private final Cache<ChannelId, Session> cache;

    @Resource
    private JwtTokenResolver jwtTokenResolver;

    public UserKeepServiceImpl() {
        RemovalListener<ChannelId, Session> removalListener = (key, session, cause) -> {
            assert key != null;
            assert session != null;

            if (Objects.equals(cause, RemovalCause.EXPLICIT)) {
                return;
            }

            if (!session.isActive()) {
                logger.info("user keep cache remove session: {}", key.asLongText());
                return;
            }

            session.channel().close().addListener(future -> {
                if (future.isSuccess()) {
                    logger.info("user keep cache success remove session: {}", key.asLongText());
                } else {
                    logger.info("user keep cache fail remove session: {}", key.asLongText());
                }
            });
        };
        cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.of(60, ChronoUnit.SECONDS))
                .removalListener(removalListener) // 延迟移除
                .build();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 60 * 10);
                    removeSession();
                } catch (InterruptedException e) {
                    logger.error("remove session error ", e);
                }
            }
        }).start();
    }

    private synchronized void removeSession() {
        registrySession.entrySet().removeIf(entry -> {
            Session session = entry.getValue();
            return session == null || !session.isActive() || (new Date().getTime() - session.getChannelActiveTime()) > 30 * 60 * 1000L; // 加一个 连接超过30分钟也移除
        });
    }

    public boolean cache(Session session) {
        if (session == null) {
            return false;
        }

        cache.put(session.id(), session);
        return true;
    }

    public synchronized boolean authenticate(SocketCommand cmd) {
        Session session = cmd.getSession();
        SocketRequestHeader header = cmd.getHeader();
        ChannelId id = session.id();

        Session cacheSession = cache.getIfPresent(id);

        if (header == null || header.getKey() == null) {
            logger.debug("session authenticate fail, header key empty");
            return false;
        }

        // 是否当前已注册
        boolean isRegister = registrySession.containsKey(header.getKey());

        // 缓存存在 则鉴权
        if (cacheSession != null) {
            if (StrUtil.isBlank(header.getToken())) {
                logger.debug("session authenticate fail, cache exist but header token empty");
                return false;
            }

            // 验签
            try {
                jwtTokenResolver.resolve(header.getToken());
            } catch (Exception e) {
                logger.error("session authenticate fail, jwtTokenResolver resolve error ", e);
                return false;
            }

            // 鉴权通过则移除缓存
            cache.invalidate(id);

            // 注册
            registrySession.compute(header.getKey(), (s, old) -> {
                if (old != null && old.isActive()) {
                    old.close().addListener(future -> {
                        if (future.isSuccess()) {
                            logger.info("register old session close success, {}", s);
                        } else {
                            logger.error("register old session close fail, {}", s);
                        }
                    });
                }
                return session;
            });
            logger.info("success registry session: {} key:{}", SessionUtil.getKey(session), header.getKey());

            return true;
        } else {
            // 已注册
            if (isRegister) {
                return true;
            }
            // 这是一个错误会话 注册失败且不在缓存,需要关闭
            if (session.isActive()) {
                session.channel().close().addListener(future -> {
                    if (future.isSuccess()) {
                        logger.info("arena not in cache success remove session: {}", id.asLongText());
                    } else {
                        logger.info("arena not in cache fail remove session: {}", id.asLongText());
                    }
                });
                logger.info("authenticate cache empty and unRegister, need close");
            } else {
                logger.info("authenticate cache empty and unRegister and already close");
            }
            return false;
        }
    }

    @Override
    public Session get(Long key) {
        return registrySession.get(key);
    }
}
