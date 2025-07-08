package com.shurona.holiday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HolidayKeeperApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolidayKeeperApplication.class, args);
    }

}
