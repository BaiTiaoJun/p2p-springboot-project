package com.example.p2p;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = "com.example.p2p.mapper")
@EnableDubbo
@EnableTransactionManagement
public class P2pDataserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(P2pDataserviceApplication.class, args);
    }
}