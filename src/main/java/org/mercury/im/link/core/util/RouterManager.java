package org.mercury.im.link.core.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mercury.im.link.core.annonation.EventListener;
import org.mercury.im.link.core.infra.protocol.SocketCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class RouterManager implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(RouterManager.class);

    private final ApplicationContext applicationContext;

    public RouterManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private final Map<String, RouterIns> map = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);

            Class<?> beanType = AopProxyUtils.ultimateTargetClass(bean);
            Method[] methods = beanType.getDeclaredMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(EventListener.class)) {
                    EventListener eventListener = method.getAnnotation(EventListener.class);
                    // 确保方法是可访问的
                    method.setAccessible(true);
                    map.put(eventListener.event(), new RouterIns(bean, method));
                }
            }
        }
    }

    public boolean execute(String event, Object... args) {
        RouterIns routerIns = map.get(event);
        if (routerIns == null) {
            logger.debug("未找到事件处理器:{}", event);
            return false;
        }
        try {
            routerIns.getMethod().invoke(routerIns.bean, args);
        } catch (Exception e) {
            logger.error("route manager execute err: {} ", event, e);
            return false;
        }
        return true;
    }

    @EventListener(event = "register")
    public void register(SocketCommand socketCommand) {
    }

    @Data
    @AllArgsConstructor
    public static class RouterIns {
        private Object bean;

        private Method method;
    }
}
