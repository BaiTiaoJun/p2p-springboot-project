package com.example.p2p.service.user;

import com.example.p2p.model.user.FinanceAccount;

/**
 * @类名 FinanceAccountService
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/3 14:30
 * @版本 1.0
 */
public interface FinanceAccountService {

    /**
     * 根据电话号码查询可用金额
     * @param phone
     * @return
     */
    FinanceAccount queryAvailableMoneyByPhone(String phone);
}
