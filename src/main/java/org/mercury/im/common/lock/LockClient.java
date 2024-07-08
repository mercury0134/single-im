package org.mercury.im.common.lock;

import java.util.concurrent.TimeUnit;

public interface LockClient {

    Boolean lock(String key, Object value, long expire, TimeUnit timeout);


    Boolean unlock(String key, Object value);

}
