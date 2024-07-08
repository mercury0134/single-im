package org.mercury.im.domain.room.model.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SingleConversationExtra extends ConversationExtra {

    /**
     * 用户集合
     */
    private List<Long> userIds;

    /**
     * 发起聊天后双方有回应才可以真是聊天 0 未开始 1 第一位发过消息 2 第二位发过消息 3 双方开始
     */
    private Byte begin = 0;

    /**
     * 拉黑关系 0 未拉黑 1 第一位拉黑第二位 2 第二位拉黑第一位 3 双方拉黑
     * 默认为0
     */
    private Byte block = 0;

    public static final byte BEGIN_NOT = 0;
    public static final byte BEGIN_BOTH = 3;
}
