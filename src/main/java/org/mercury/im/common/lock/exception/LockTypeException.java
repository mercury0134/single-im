package org.mercury.im.common.lock.exception;

public class LockTypeException extends RuntimeException {

    public LockTypeException(String message) {
        super(message);
    }

    public LockTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
