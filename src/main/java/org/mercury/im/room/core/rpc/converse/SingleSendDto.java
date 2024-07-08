package org.mercury.im.room.core.rpc.converse;

import lombok.Data;
import org.mercury.im.domain.room.model.MessageItem;

import java.util.List;

@Data
public class SingleSendDto {

    /**
     * 用户ids
     */
    private List<Long> userIds;

    /**
     * 会话id
     */
    private String converseId;

    /**
     * 验签token
     */
    private String token;

    /**
     * 幂等作用的消息id
     */
    private String deviceId;

    /**
     * 文本内容
     */
    private String content;

    public MessageItem messageItem() {
        MessageItem messageItem = new MessageItem();
        messageItem.setConverseId(converseId);
        messageItem.setContent(content);
        messageItem.setDeviceId(deviceId);
        return messageItem;
    }
}
