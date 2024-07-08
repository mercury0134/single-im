package org.mercury.im;

import org.mercury.im.common.lock.annonation.EnableLock;
import org.mercury.im.common.lock.annonation.LockType;
import org.rocksdb.RocksDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"org.mercury.im.domain.room.model.po"})
@EnableJpaRepositories(basePackages = {"org.mercury.im.domain.room.repository"})
@SpringBootApplication
@EnableLock(types = {LockType.MEMORY})
public class Application {

    static {
        RocksDB.loadLibrary();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
