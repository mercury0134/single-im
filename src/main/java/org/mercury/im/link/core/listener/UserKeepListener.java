package org.mercury.im.link.core.listener;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.mercury.im.common.core.objects.Tuple2;
import org.mercury.im.common.json.JsonUtils;
import org.mercury.im.link.core.infra.protocol.CommandDecoder;
import org.mercury.im.link.core.infra.protocol.SocketCommand;
import org.mercury.im.link.core.infra.ws.pojo.Session;
import org.mercury.im.link.core.infra.ws.util.SessionUtil;
import org.mercury.im.link.core.listener.dto.MessageEvent;
import org.mercury.im.link.core.listener.dto.RegisterEvent;
import org.mercury.im.link.core.util.RouterManager;
import org.mercury.im.room.service.UserKeepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserKeepListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private UserKeepService userKeepService;

    @Resource
    private RouterManager routerManager;

    private final CommandDecoder decoder = (session, message) -> {
        SocketCommand socketCommand = JsonUtils.parse(message, SocketCommand.class);
        assert socketCommand != null;
        socketCommand.setSession(session);
        return socketCommand;
    };

    @EventListener(RegisterEvent.class)
    public void register(RegisterEvent event) {
        Session session = event.getSession();
        boolean cached = userKeepService.cache(session);
    }

    @EventListener(MessageEvent.class)
    public void handler(MessageEvent event) {
        String message = event.getMessage();
        if (StrUtil.isBlank(message)) {
            return;
        }
        Session session = event.getSession();

        Thread.startVirtualThread(() -> {
            boolean b = handlerMessage(session, message);
        });
    }

    private boolean handlerMessage(Session session, String message) {
        SocketCommand cmd;
        try {
            cmd = decoder.decode(session, message);
        } catch (Exception e) {
            logger.error("decode message:{} exception ", message, e);
            return false;
        }

        if (Objects.equals("heart", cmd.getEvent())) {
            session.sendText("{\"event\":\"/heart\"}");
            return true;
        }

        try {
            boolean authenticate = userKeepService.authenticate(cmd);
            if (!authenticate) {
                Tuple2<String, Integer> info = SessionUtil.getInfo(session);
                logger.error("authenticate fail session info:{}:{}", info.getFirst(), info.getSecond());
                return false;
            }
        } catch (Exception e) {
            logger.error("authenticate cmd:{} exception ", cmd, e);
            return false;
        }

        return routerManager.execute(cmd.getEvent(), cmd);
    }


}
