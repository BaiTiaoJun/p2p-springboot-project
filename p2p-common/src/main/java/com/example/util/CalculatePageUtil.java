package com.example.util;

/**
 * @类名 CalculatePageUtil
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/2 13:12
 * @版本 1.0
 */
public class CalculatePageUtil {
    public static int getTotalPage(Integer totalSize) {
        int totalPage = totalSize / ConstantsUtil.PAGE_SIZE;
        if (totalSize % ConstantsUtil.PAGE_SIZE > 0) {
            totalPage = totalPage + 1;
        }
        return totalPage;
    }
}
