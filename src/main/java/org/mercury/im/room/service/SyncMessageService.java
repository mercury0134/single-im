package org.mercury.im.room.service;

/**
 * 发送消息队列准备
 */
public interface SyncMessageService {


    /**
     * 异步通知
     */
    boolean sendMessage(Long userId);


}
