package org.mercury.im.domain.room.core.rpc.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageVo {

    /**
     * 发送消息状态 200 成功 300 失败 可重新发送 400 失败 不可重复发送
     */
    private Integer code;

    private String msg;

    private String token;

    private String firstMessageId;

    private String converseId;
}