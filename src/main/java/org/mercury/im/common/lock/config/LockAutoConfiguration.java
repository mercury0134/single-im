package org.mercury.im.common.lock.config;

import org.mercury.im.common.lock.LockFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class LockAutoConfiguration {

    @Bean
    public LockFactory lockFactory() {
        return new LockFactory();
    }


}
