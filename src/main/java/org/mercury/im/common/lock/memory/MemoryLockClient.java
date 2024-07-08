package org.mercury.im.common.lock.memory;

import jakarta.annotation.Resource;
import org.mercury.im.common.lock.LockClient;
import org.mercury.im.common.lock.LockFactory;
import org.mercury.im.common.lock.annonation.LockType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MemoryLockClient implements LockClient, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private LockFactory lockFactory;

    private final ConcurrentMap<String, LockAndCondition> lockMap = new ConcurrentHashMap<>();

    private static class LockAndCondition {
        final ReentrantLock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        volatile long expirationTime = 0; // 锁的过期时间
    }

    @Override
    public Boolean lock(String key, Object value, long expire, TimeUnit timeout) {
        // 获取或创建锁对象
        LockAndCondition lockAndCondition = lockMap.computeIfAbsent(key, k -> new LockAndCondition());

        boolean isLocked = false;
        long lockExpireNanos = timeout.toNanos(expire); // 转换持有锁的时间为纳秒
        long waitTimeNanos = TimeUnit.MINUTES.toNanos(1); // 阻塞等待时间固定为1分钟

        final ReentrantLock localLock = lockAndCondition.lock;
        try {
            // 尝试获取锁，带固定阻塞等待时间
            if (localLock.tryLock(waitTimeNanos, TimeUnit.NANOSECONDS)) {
                try {
                    // 获取成功，设置过期时间
                    lockAndCondition.expirationTime = System.nanoTime() + lockExpireNanos;
                    lockMap.put(key, lockAndCondition); // 设置锁对象
                    isLocked = true;
                } finally {
                    // 如果没有成功设置值，释放锁
                    if (!isLocked) {
                        localLock.unlock();
                    }
                }
            } else {
                // 获取锁超时，使用 Condition 进行等待
                if (lockAndCondition.condition.await(waitTimeNanos, TimeUnit.NANOSECONDS)) {
                    // 等待超时后重新尝试获取锁
                    isLocked = localLock.tryLock(lockExpireNanos, TimeUnit.NANOSECONDS);
                    if (isLocked) {
                        lockAndCondition.expirationTime = System.nanoTime() + lockExpireNanos;
                        lockMap.put(key, lockAndCondition); // 设置锁对象
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 设置中断状态
        }

        return isLocked;
    }

    @Override
    public Boolean unlock(String key, Object value) {
        LockAndCondition lockAndCondition = lockMap.get(key);

        // 检查锁是否存在并且是否由当前线程持有
        if (lockAndCondition != null && lockAndCondition.lock.isHeldByCurrentThread()) {
            try {
                lockAndCondition.lock.unlock(); // 释放锁
                lockMap.remove(key); // 移除锁对象
                return true;
            } catch (IllegalMonitorStateException e) {
                // 如果当前线程不持有锁，则会抛出异常
                return false;
            }
        }

        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        lockFactory.register(LockType.MEMORY, this);
    }
}
