package com.example.util;

import java.util.Random;

/**
 * @类名 CodeUtil
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/4 10:31
 * @版本 1.0
 */
public class CodeUtil {
    public static String getCode(int digit) {
        int num;
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < digit; i++) {
            num = random.nextInt(10);
            stringBuilder.append(num);
        }
        return stringBuilder.toString();
    }
}
