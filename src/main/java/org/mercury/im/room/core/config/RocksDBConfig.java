package org.mercury.im.room.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rocks")
public class RocksDBConfig {

    /**
     * message 相关的db
     */
    private String messageDb;

    /**
     * conversation 相关的db
     */
    private String conversationDb;

    /**
     * userBox 相关的db
     */
    private String userBoxDb;

}
