package com.example.p2p.service;

/**
 * @类名 RedisService
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/4 14:08
 * @版本 1.0
 */
public interface  RedisService {

    void put(String key, Object value, int timeout);

    String get(String key);

    void remove(String key);
}
