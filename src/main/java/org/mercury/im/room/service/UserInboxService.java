package org.mercury.im.room.service;

import org.mercury.im.domain.room.model.MessageItem;
import org.mercury.im.room.service.impl.UserInboxServiceImpl;

import java.util.List;

/**
 * 用户消息盒子
 */
public interface UserInboxService {

    /**
     * 用户收信箱
     */
    UserInboxServiceImpl.MessageQueue user(Long userId);

    /**
     * 收信箱添加消息
     */
    boolean addMessage(Long toUserId, MessageItem message);

    /**
     * 获取用户维度的消息列表
     */
    List<MessageItem> userNoAlreadyMessage(Long userId);

    /**
     * 已读消息
     *
     * @param messageId 从firstMessageId获取
     */
    boolean alreadyRead(Long toUserId, String messageId);

    /**
     * 获取已读的消息id
     */
    String alreadyRead(Long userId);
}
