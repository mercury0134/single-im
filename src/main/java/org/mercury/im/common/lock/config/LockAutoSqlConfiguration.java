package org.mercury.im.common.lock.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "mercury.lock.mysql", havingValue = "true")
public class LockAutoSqlConfiguration {

}
