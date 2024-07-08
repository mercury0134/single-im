package org.mercury.im.common.lock.config;

import org.mercury.im.common.lock.annonation.EnableLock;
import org.mercury.im.common.lock.annonation.LockType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.type.AnnotationMetadata;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * EnvironmentPostProcessor:一个接口，允许在应用上下文被刷新之前，但在@Configuration类被处理之前，对环境进行自定义
 * ApplicationContextInitializer:一个接口，允许在应用上下文被刷新之前对其进行自定义配置
 * 但是都需要在META-INF/spring.factories中配置
 */
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class EnableLockImportSelector implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(EnableLockImportSelector.class);

    private Environment environment;

    @SuppressWarnings("unchecked")
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        Map<String, Object> annotationAttributes =
                importingClassMetadata.getAnnotationAttributes(EnableLock.class.getName());

        if (annotationAttributes == null) {
            return;
        }

        LockType[] types = (LockType[]) annotationAttributes.get("types");
        for (LockType type : types) {
            if (type == LockType.MEMORY) {
                RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(LockAutoMemoryConfiguration.class);
                rootBeanDefinition.setLazyInit(false);
                rootBeanDefinition.setAbstract(false);
                rootBeanDefinition.setAutowireCandidate(true);
                registry.registerBeanDefinition("lockAutoMemoryConfiguration", rootBeanDefinition);
                logger.info("lock memory configuration open!");
            }
            if (type == LockType.MYSQL) {
                RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(LockAutoSqlConfiguration.class);
                rootBeanDefinition.setLazyInit(false);
                rootBeanDefinition.setAbstract(false);
                rootBeanDefinition.setAutowireCandidate(true);
                registry.registerBeanDefinition("lockAutoSqlConfiguration", rootBeanDefinition);
                logger.info("lock sql configuration open!");
            }
            if (this.environment instanceof ConfigurableEnvironment configEnv) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put(type.getEnv(), true);
                MapPropertySource propertySource = new MapPropertySource("mercury-lock", map);
                configEnv.getPropertySources().addLast(propertySource);
            }
        }

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
