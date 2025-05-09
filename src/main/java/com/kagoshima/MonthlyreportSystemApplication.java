package com.kagoshima;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonthlyreportSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonthlyreportSystemApplication.class, args);
    }

}
