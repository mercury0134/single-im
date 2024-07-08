package org.mercury.im.room.service;

import org.mercury.im.domain.room.model.ConversationBo;

import java.util.List;

/**
 * 会话db处理
 */
public interface ConverseDbService {

    /**
     * 新增单聊会话
     */
    boolean addSingle(ConversationBo bo);

    /**
     * 根据会话id获取会话
     */
    ConversationBo getById(String converseId);

    /**
     * 为用户添加会话列表
     */
    boolean toUser(Long userId, ConversationBo bo);

    /**
     * 获取用户会话列表
     */
    List<String> userConverse(Long userId);

    /**
     * 清理用户个人会话(清理对象为 最近聊天会话为1天)
     */
    boolean clearUser(Long userId, String value);

    /**
     * 用户会话列表任务
     *
     * @param flag 标识
     */
    void userConverseTask(String flag);
}
