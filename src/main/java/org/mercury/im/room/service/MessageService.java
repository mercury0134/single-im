package org.mercury.im.room.service;

import org.mercury.im.domain.room.model.MessageItem;

import java.util.List;

/**
 * 将消息同步到对应用户
 */
public interface MessageService {

    /**
     * 保存消息到数据库
     */
    boolean sendMessageToDB(String converseId, MessageItem message);

    /**
     * 获取消息列表
     */
    List<MessageItem> list(String firstMessageId);
}
