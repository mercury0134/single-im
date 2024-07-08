package org.mercury.im.domain.room.model.po;

import lombok.Data;

@Data
public class ConversationPo {

    private String converseId;

    /**
     * 类型 0 单聊 1 群聊
     */
    private Byte type;

    /**
     * 额外数据
     */
    private String extraStr;
}
