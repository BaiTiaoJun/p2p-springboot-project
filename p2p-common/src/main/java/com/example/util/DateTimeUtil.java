package com.example.util;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @类名 DateTimeUtil
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/3 14:55
 * @版本 1.0
 */
public class DateTimeUtil {

    public static Date getFullDateTime(Date date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(date);
        return simpleDateFormat.parse(time);
    }
}
