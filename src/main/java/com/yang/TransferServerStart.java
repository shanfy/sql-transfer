package com.yang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 * @author shanfy
 * @date 2023-09-19 11:09
 */
@EnableScheduling
@SpringBootApplication
public class TransferServerStart {
    public static void main(String[] args) {
        SpringApplication.run(TransferServerStart.class);
    }
}
