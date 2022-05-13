package com.example.timer;

import com.example.p2p.service.loan.IncomeRecordService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @类名 GenerateIncomeBackFinanceAccountTimer
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/10 09:06
 * @版本 1.0
 */

@Component
public class GenerateIncomeBackFinanceAccountTimer {

    @DubboReference(interfaceClass = IncomeRecordService.class, version = "1.0.0", check = false, timeout = 15000)
    private IncomeRecordService incomeRecordService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomeBackFinanceAccount() {
        try {
            incomeRecordService.generateIncomeBackFinanceAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
