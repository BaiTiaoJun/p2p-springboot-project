package com.example.p2p.service.impl;

import com.example.p2p.service.RedisService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * @类名 RedisServiceImpl
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/4 14:15
 * @版本 1.0
 */
@DubboService(interfaceClass = RedisService.class, version = "1.0.0", timeout = 15000)
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void put(String key, Object value, int timeout) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }
}
