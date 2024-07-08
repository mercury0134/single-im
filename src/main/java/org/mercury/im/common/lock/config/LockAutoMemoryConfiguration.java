package org.mercury.im.common.lock.config;

import org.mercury.im.common.lock.memory.MemoryLockClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

public class LockAutoMemoryConfiguration {


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 100)
    public MemoryLockClient memoryLockClient() {
        return new MemoryLockClient();
    }

}
