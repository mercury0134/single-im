package org.mercury.im.common.lock.exception;

public class OnLockException extends RuntimeException {

    public OnLockException(String message) {
        super(message);
    }

    public OnLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
