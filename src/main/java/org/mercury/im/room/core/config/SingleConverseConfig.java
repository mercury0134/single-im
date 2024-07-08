package org.mercury.im.room.core.config;

import lombok.Data;

@Data
public class SingleConverseConfig {

    /**
     * 单聊消息段最大长度 单位 字节
     */
    public static Integer rangeMaxSize;

    /**
     * 单聊消息的hmac key
     */
    public static String singleMessageSecretKey;


}
