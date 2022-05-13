package com.example.p2p.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @类名 SystemController
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/13 16:32
 * @版本 1.0
 */

@Controller
public class SystemController {

    @RequestMapping("/system/toSaveRechargeRecord.do")
    public String toSaveRechargeRecord() {
        return "system/toSaveRechargeRecord";
    }
}
