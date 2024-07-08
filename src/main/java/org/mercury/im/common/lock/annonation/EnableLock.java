package org.mercury.im.common.lock.annonation;

import org.mercury.im.common.lock.config.EnableLockImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({EnableLockImportSelector.class})
public @interface EnableLock {

    /**
     * 锁的类型
     */
    LockType[] types() default {LockType.MEMORY};
}
