package org.mercury.im.room.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.mercury.im.common.core.objects.Tuple2;
import org.mercury.im.common.lock.annonation.Lock;
import org.mercury.im.domain.room.core.rpc.conversation.SendMessageVo;
import org.mercury.im.domain.room.model.ConversationBo;
import org.mercury.im.domain.room.model.MessageItem;
import org.mercury.im.domain.room.repository.ConverseRangeRepository;
import org.mercury.im.room.core.util.BusinessIdUtil;
import org.mercury.im.room.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SingleConverseServiceImpl implements SingleConverseService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 单聊免打扰消息缓存
     * key: token
     * value: ConserveMessageContext
     */
    private final Cache<String, ConserveMessageContext> signleConversationCache;

    /**
     * 会话产生的token cache时不时的清理一下
     */
    private final Cache<String, Map<String, Object>> tokenCache;

    public SingleConverseServiceImpl() {
        this.signleConversationCache = Caffeine.newBuilder()
                .expireAfterAccess(Duration.of(1, ChronoUnit.MINUTES)) // 1分钟
                .maximumSize(10000)
                .build();
        tokenCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.of(1, ChronoUnit.HOURS)) // 1分钟
                .maximumSize(10000)
                .build();
    }

    @Resource
    private ConverseDbService converseDbService;

    @Resource
    private MessageService messageService;

    @Resource
    private ConverseRangeRepository converseRangeRepository;

    @Resource
    private MessageTokenService messageTokenService;

    @Resource
    private UserInboxService userInboxService;

    @Resource
    private SyncMessageService syncMessageService;

    @Resource
    @Lazy
    private SingleConverseServiceImpl singleConverseService;

    @Lock(keys = {"converseId"}, prefix = "NEW_CONVERSE_", timeUnit = TimeUnit.SECONDS, expire = 10)
    @Override
    public void createConverse(String converseId) {
        if (StrUtil.isBlank(converseId)) {
            return;
        }
        Tuple2<Long, Long> tuple2 = BusinessIdUtil.parseConverseId(converseId);

        // TODO 判断user id是否存在 rpc

        // 数据库获取conversation
        ConversationBo conversation = converseDbService.getById(converseId);
        if (conversation != null) {
            return;
        }

        ConversationBo newCon = ConversationBo.newSingleConverse(List.of(tuple2.getFirst(), tuple2.getSecond()));
        boolean b = converseDbService.addSingle(newCon);
        b = converseDbService.toUser(tuple2.getFirst(), newCon);
        b = converseDbService.toUser(tuple2.getSecond(), newCon);
    }

    @Override
    public SendMessageVo sendMessage(Long userId,
                                     String converseId,
                                     String token,
                                     MessageItem messageItem) {
        if (StrUtil.isBlank(converseId)) {
            return new SendMessageVo(SingleErrorEnums.CONVERSE_NOT_EXIST.code(),
                    SingleErrorEnums.CONVERSE_NOT_EXIST.message(),
                    null,
                    null,
                    null);
        }

        // message item init
        messageItem.setUserId(userId);
        messageItem.setTime(new Date().getTime());
        messageItem.getNewId();
        messageItem.setConverseId(converseId);
        messageItem.secret(SingleConverseConfig.singleMessageSecretKey);

        // 处理token
        if (StrUtil.isNotBlank(token)) {
            try {
                Object o = Optional.of(tokenCache.get(converseId, k -> new HashMap<>()).get(token))
                        .orElseThrow(() -> new RuntimeException("token invalid"));
                ConserveMessageContext messageContext = signleConversationCache.getIfPresent(token);
                assert messageContext != null;
                SendMessageVo sendMessageVo = auditContext(token, messageItem, messageContext);
                if (sendMessageVo != null) {
                    return sendMessageVo;
                }
            } catch (Exception ignore) {
            }
        }

        return singleConverseService.noTokenSend(userId, converseId, messageItem);
    }

    /**
     * 发送无token数据
     */
    @Lock(keys = {"converseId", "messageItem.userId"}, prefix = "SEND_SINGLE_MSG_", timeUnit = TimeUnit.SECONDS, expire = 10)
    public SendMessageVo noTokenSend(Long userId, String converseId, MessageItem messageItem) {

        ConversationBo conversationBo = converseDbService.getById(converseId);
        SingleConversationExtra extra = (SingleConversationExtra) conversationBo.getExtra();

        // 只有对方有回应才能建立真正的聊天
        SendMessageVo sendMessageVo = handlerBothBegin(userId, extra);
        if (sendMessageVo != null) {
            return sendMessageVo;
        }

        // 生成token
        ConverseMessageRangePo po = converseRangeRepository.nextOne(converseId, messageItem.getId(), 5 * 60 * 1000L);
        String token = messageTokenService.token(messageItem.getId(), conversationBo);
        ConserveMessageContext context = new ConserveMessageContext();
        context.setFirstMessageId(po.getFirstMessageId());
        context.setUserIds(extra.getUserIds());

        tokenCache.get(converseId, k -> new HashMap<>()).put(token, 1);
        signleConversationCache.put(token, context);

        return sendMessageToDb(po.getFirstMessageId(), token, extra.getUserIds(), messageItem);
    }

    public SendMessageVo handlerBothBegin(Long userId, SingleConversationExtra extra) {
        List<Long> userIds = extra.getUserIds();
        Byte begin = extra.getBegin();
        if (!Objects.equals(extra.getBegin(), SingleConversationExtra.BEGIN_NOT)
                && !Objects.equals(extra.getBegin(), SingleConversationExtra.BEGIN_BOTH)) {
            if (BitsUtil.isBitSet(begin, userIds.getFirst().equals(userId) ? 1 : 0)) {
                return new SendMessageVo(400, "对方未回应", null, null, null);
            }
        }
        if (!Objects.equals(extra.getBegin(), SingleConversationExtra.BEGIN_BOTH)) {
            // 设置当前位为1
            int i = BitsUtil.setBit(begin, userIds.indexOf(userId));
            extra.setBegin(Integer.valueOf(i).byteValue());
        }
        return null;
    }

    public SendMessageVo auditContext(String token,
                                      MessageItem messageItem,
                                      ConserveMessageContext context) {
        // 是否超过最大数量
        synchronized (context) {
            if (context.getSize() >= SingleConverseConfig.rangeMaxSize) {
                return new SendMessageVo(400, "消息数量超过最大限制", token, context.getFirstMessageId(), messageItem.getConverseId());
            }
            context.setSize(context.getSize() + messageItem.getContent().getBytes().length);
        }

        // 是否是会话中的用户
        if (!context.getUserIds().contains(messageItem.getUserId())) {
            return new SendMessageVo(400, "无权限发送消息", token, context.getFirstMessageId(), messageItem.getConverseId());
        }

        // 发送数据
        return sendMessageToDb(context.firstMessageId, token, context.userIds, messageItem);
    }

    public SendMessageVo sendMessageToDb(String firstMessageId, String token, List<Long> userIds, MessageItem message) {
        Long toUserId = userIds.stream().filter(id -> !id.equals(message.getUserId())).findFirst().orElse(null);
        boolean b = sendMessageToDb(
                toUserId,
                firstMessageId,
                message);

        return b ? new SendMessageVo(201, "", token, firstMessageId, message.getConverseId())
                : new SendMessageVo(301, "发送失败", token, firstMessageId, message.getConverseId());
    }

    public boolean sendMessageToDb(Long toUserId, String firstMessageId, MessageItem message) {
        // 是否支持历史消息存储
        boolean b = messageService.sendMessageToDB(firstMessageId, message);
        if (!b) {
            return false;
        }
        // 用户收信箱
        b = userInboxService.addMessage(toUserId, message);
        if (!b) {
            return false;
        }
        // 通知用户长连接
        boolean b1 = syncMessageService.sendMessage(toUserId);
        return true;
    }

    @Data
    public static class ConserveMessageContext {
        /**
         * 可添加数量
         */
        private volatile int size = 0;

        /**
         * messageId
         */
        private String firstMessageId;

        /**
         * 用户ids
         */
        @NotNull
        private List<Long> userIds;
    }

    @Lock(keys = {"converseId"}, prefix = "NEW_CONVERSE_", timeUnit = TimeUnit.SECONDS, expire = 10)
    @Override
    public boolean block(String converseId, Long userId, Byte block) {
        Tuple2<Long, Long> tuple2 = BusinessIdUtil.parseConverseId(converseId);
        List<Long> userIds = List.of(tuple2.getFirst(), tuple2.getSecond());
        ConversationBo conversationBo = Optional.ofNullable(converseDbService.getById(converseId))
                .orElse(ConversationBo.newSingleConverse(userIds));
        SingleConversationExtra extra = (SingleConversationExtra) conversationBo.getExtra();

        // 设置位
        int i;
        int index = userIds.indexOf(userId);
        if (block == 1) {
            i = BitsUtil.setBit(extra.getBlock(), index);
        } else {
            i = BitsUtil.clearBit(extra.getBlock(), index);
        }
        extra.setBlock(Integer.valueOf(i).byteValue());

        // 清空会话
        tokenCache.invalidate(converseId);

        return converseDbService.addSingle(conversationBo);
    }

    @Lock(keys = {"userId"}, prefix = "USER_MSG_", timeUnit = TimeUnit.SECONDS, expire = 20)
    public List<MessageItem> userMessage(Long userId) {
        // 从用户收信箱获取消息
        List<MessageItem> messageItems = userInboxService.userNoAlreadyMessage(userId);

        // 消息进行去重 根据客户端id
        Set<String> set = new HashSet<>();
        return messageItems.stream().filter(messageItem -> {
            if (set.contains(messageItem.getDeviceId())) {
                return false;
            }
            set.add(messageItem.getDeviceId());
            return true;
        }).sorted(Comparator.comparing(MessageItem::getTime).reversed()).toList();
    }
}
