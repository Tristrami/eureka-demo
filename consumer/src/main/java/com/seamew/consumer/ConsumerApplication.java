package com.seamew.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ConsumerApplication {

    public static void main(String[] args) {
        // 服务发现: https://juejin.cn/post/7020312146516934669
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
