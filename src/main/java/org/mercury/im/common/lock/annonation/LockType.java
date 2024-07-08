package org.mercury.im.common.lock.annonation;

import lombok.Getter;

@Getter
public enum LockType {

    MEMORY,
    MYSQL,
    REDIS;

    private final String env;

    LockType() {
        this.env = "mercury.lock." + name().toLowerCase();
    }

}
