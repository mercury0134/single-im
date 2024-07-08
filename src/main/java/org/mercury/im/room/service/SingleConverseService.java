package org.mercury.im.room.service;


import org.mercury.im.domain.room.core.rpc.conversation.SendMessageVo;
import org.mercury.im.domain.room.model.MessageItem;

import java.util.List;

public interface SingleConverseService {

    /**
     * 创建单聊会话
     */
    void createConverse(String converseId);

    /**
     * 单聊发送消息
     */
    SendMessageVo sendMessage(Long userId,
                              String converseId,
                              String token,
                              MessageItem messageItem);

    /**
     * 拉黑 userId 拉黑 toUserId
     * block 1 userId 拉黑 toUserId
     */
    boolean block(String converseId, Long userId, Byte block);

    /**
     * 获取用户消息列表
     */
    List<MessageItem> userMessage(Long userId);
}
