package com.example.p2p;

import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubboConfig
public class P2pWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(P2pWebApplication.class, args);
    }
}
