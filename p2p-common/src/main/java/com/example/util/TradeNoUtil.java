package com.example.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @类名 GenerateTradeNoUtil
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/12 23:02
 * @版本 1.0
 */
public class TradeNoUtil {

    public static String generateTradeNo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(new Date());

        StringBuilder tradeCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            tradeCode.append(random.nextInt(2));
        }

        return date + tradeCode;
    }

    public static void main(String[] args) {
        System.out.println(generateTradeNo());
    }
}
