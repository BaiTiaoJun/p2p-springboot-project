package com.example.p2p.service.impl.user;

import com.example.p2p.mapper.user.FinanceAccountMapper;
import com.example.p2p.model.user.FinanceAccount;
import com.example.p2p.service.user.FinanceAccountService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @类名 FinanceAccountServiceImpl
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/5 17:34
 * @版本 1.0
 */

@DubboService(interfaceClass = FinanceAccountService.class, version = "1.0.0", timeout = 15000)
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public FinanceAccount queryAvailableMoneyByPhone(String phone) {
        return financeAccountMapper.selectAvailableMoneyByPhone(phone);
    }
}
