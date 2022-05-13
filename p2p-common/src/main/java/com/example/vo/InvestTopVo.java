package com.example.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @类名 InvestTopVo
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/8 15:31
 * @版本 1.0
 */

@Data
public class InvestTopVo implements Serializable {
    private static final long serialVersionUID = 8923581787416449349L;
    private String phone;
    private Double bidMoney;
}
