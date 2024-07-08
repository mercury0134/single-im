package org.mercury.im.common.lock.aspect;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mercury.im.common.json.JsonUtils;
import org.mercury.im.common.lock.LockClient;
import org.mercury.im.common.lock.LockFactory;
import org.mercury.im.common.lock.annonation.Lock;
import org.mercury.im.common.lock.annonation.LockType;
import org.mercury.im.common.lock.exception.LockTypeException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
public class LockAspect {

    @Resource
    private LockFactory lockFactory;

    @Pointcut("@annotation(org.mercury.im.common.lock.annonation.Lock)")
    private void lock1() {
    }

    @Around("lock1()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Lock lock = methodSignature.getMethod().getAnnotation(Lock.class);

        LockType type = lock.type();
        LockClient lockClient = lockFactory.getLockClient(type);
        if (lockClient == null) {
            throw new LockTypeException("lock client " + type + " cannot support");
        }

        long expire = lock.expire();
        TimeUnit timeUnit = lock.timeUnit();
        String prefix = lock.prefix();

        StringBuilder sb = new StringBuilder(prefix).append(":");
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        List<String> names = Arrays.stream(parameters).map(Parameter::getName).collect(Collectors.toList());
        Object[] args = joinPoint.getArgs();

        String[] keys = lock.keys();

        // 全部字段
        if (keys.length == 0) {
            Stream.of(args).forEach(arg -> sb.append(JsonUtils.toJsonString(arg)));
        } else {
            Arrays.stream(keys).forEach(field -> {
                        if (StrUtil.isBlank(field)) {
                            return;
                        }
                        sb.append(field.contains(".") ?
                                handlerNest(field, args, names) : handler(field, args, names));
                        sb.append(":");
                    }
            );
        }

        String key = sb.toString();
        String value = UUID.randomUUID().toString();
        try {
            lockClient.lock(key, value, expire, timeUnit);
            return joinPoint.proceed();
        } finally {
            lockClient.unlock(key, value);
        }
    }

    private Object handlerNest(String field, Object[] args, List<String> names) {
        String[] split = field.split("\\.");
        String obj = split[0];
        String prop = split[1];
        for (int i = 0; i < names.size(); i++) {
            if (!obj.equals(names.get(i))) {
                continue;
            }
            Object object = args[i];
            Map<String, Object> map = JsonUtils.toMapObject(object);
            Object s = map.get(prop);
            if (s == null) {
                break;
            }
            return s;
        }
        return null;
    }

    private String handler(String field, Object[] args, List<String> names) {
        for (int i = 0; i < names.size(); i++) {
            if (!field.equals(names.get(i))) {
                continue;
            }
            Object arg = args[i];
            return JsonUtils.toJsonString(arg);
        }
        return null;
    }
}
