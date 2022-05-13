package com.example.p2p.service.loan;

/**
 * @类名 IncomeRecordService
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/9 11:01
 * @版本 1.0
 */
public interface IncomeRecordService {

    /**
     * 生成收益记录
     * @throws Exception
     */
    void generateIncomeRecord() throws Exception;

    /**
     * 根据生成的收益记录生成返还收益数据到用户账户中
     * @throws Exception
     */
    void generateIncomeBackFinanceAccount() throws Exception;
}
