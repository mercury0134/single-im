package org.mercury.im.common.lock;

import org.mercury.im.common.lock.annonation.LockType;

import java.util.concurrent.ConcurrentHashMap;

public class LockFactory {

    private final ConcurrentHashMap<LockType, LockClient> map = new ConcurrentHashMap<>();

    public LockClient getLockClient(LockType type) {
        return map.get(type);
    }

    public void register(LockType type, LockClient client) {
        map.put(type, client);
    }


}
