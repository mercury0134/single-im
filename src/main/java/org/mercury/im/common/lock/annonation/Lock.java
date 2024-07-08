package org.mercury.im.common.lock.annonation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Lock {

    /**
     * 锁的类型
     */
    LockType type() default LockType.MEMORY;

    /**
     * 锁的key
     */
    String[] keys() default {};

    /**
     * 前缀
     */
    String prefix() default "";

    /**
     * 锁的过期时间
     */
    long expire() default 0;

    /**
     * 锁的过期时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MICROSECONDS;

    /**
     * 尝试获取锁,如果获取失败则直接抛出异常,分布式场景做不了原子性操作
     */
    boolean tryLock() default false;
}
