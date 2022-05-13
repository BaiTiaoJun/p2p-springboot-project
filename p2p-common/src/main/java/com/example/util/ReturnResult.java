package com.example.util;

import java.util.HashMap;

/**
 * @类名 ReturnResult
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/3 11:27
 * @版本 1.0
 */
public class ReturnResult extends HashMap<String, Object> {

    private static final long serialVersionUID = -4138650209100043674L;

    private static ReturnResult returnResult;

    private ReturnResult() {}

    public static ReturnResult getResult(int code, String msg) {
        if (returnResult == null) {
            returnResult = new ReturnResult();
        }
        returnResult.put("code", code);
        returnResult.put("message", msg);
        return returnResult;
    }
}
