package com.example.p2p.service.impl.recharge;

import com.example.p2p.mapper.loan.RechargeRecordMapper;
import com.example.p2p.mapper.user.FinanceAccountMapper;
import com.example.p2p.model.loan.RechargeRecord;
import com.example.p2p.model.user.FinanceAccount;
import com.example.p2p.model.user.User;
import com.example.p2p.service.recharge.RechargeRecordService;
import com.example.util.ConstantsUtil;
import com.example.util.DateTimeUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @类名 RechargeRecordServiceImpl
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/12 23:49
 * @版本 1.0
 */

@DubboService(interfaceClass = RechargeRecordService.class, version = "1.0.0", timeout = 15000)
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    @Transactional
    public Double saveRechargeRecord(User user, String rechargeMoney, String out_trade_no, String descBody) throws Exception {

        int uId = user.getId();

        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(uId);
        rechargeRecord.setRechargeNo(out_trade_no);
        rechargeRecord.setRechargeMoney(Double.valueOf(rechargeMoney));
        rechargeRecord.setRechargeStatus(ConstantsUtil.RECHARGE_STATUS_ING);
        rechargeRecord.setRechargeTime(DateTimeUtil.getFullDateTime(new Date()));
        rechargeRecord.setRechargeDesc(descBody);

        Map<String, Object> map = new HashMap<>();

        int rechargeRecordRow = rechargeRecordMapper.insertSelective(rechargeRecord);
        if (rechargeRecordRow == 0) {
            map.put("rechargeStatus", ConstantsUtil.RECHARGE_STATUS_FAIL);
            map.put("uid", uId);
            rechargeRecordMapper.updateRechargeStatusByUid(map);
            throw new Exception("添加充值记录失败");
        }

        map.put("rechargeStatus", ConstantsUtil.RECHARGE_STATUS_SUCCESS);
        map.put("uid", uId);
        rechargeRecordRow = rechargeRecordMapper.updateRechargeStatusByUid(map);
        if (rechargeRecordRow == 0) {
            throw new Exception("添加充值记录失败");
        }

        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.setAvailableMoney(Double.valueOf(rechargeMoney));
        financeAccount.setUid(uId);
        int financeAccountRow = financeAccountMapper.updateAvailableMoneyByIncomeAndUId(financeAccount);
        if (financeAccountRow == 0) {
            throw new Exception("添加充值记录失败");
        }

        return financeAccountMapper.selectAvailableMoneyByPhone(user.getPhone()).getAvailableMoney();
    }
}
