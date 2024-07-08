package org.mercury.im.room.core.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component("roomInitRunner")
public class InitRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        SingleConverseConfig.rangeMaxSize = 1024 * 1024;
        SingleConverseConfig.singleMessageSecretKey = "mercury_im";

    }
}
