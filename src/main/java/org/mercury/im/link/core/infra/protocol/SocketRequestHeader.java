package org.mercury.im.link.core.infra.protocol;

import lombok.Data;

@Data
public class SocketRequestHeader {

    /**
     * 用户id
     */
    private Long key;

    /**
     * 签名
     */
    private String sign;

    /**
     * token
     */
    private String token;

    /**
     * 时间戳
     */
    private String timeout;

}
