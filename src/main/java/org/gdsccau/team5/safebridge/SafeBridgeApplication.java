package org.gdsccau.team5.safebridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SafeBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafeBridgeApplication.class, args);
    }
}
