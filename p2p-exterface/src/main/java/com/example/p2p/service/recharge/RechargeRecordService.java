package com.example.p2p.service.recharge;

import com.example.p2p.model.user.User;

/**
 * @类名 RechargeRecordService
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/11 15:06
 * @版本 1.0
 */
public interface RechargeRecordService {
    Double saveRechargeRecord(User user, String rechargeMoney, String out_trade_no, String descBody) throws Exception;
}
