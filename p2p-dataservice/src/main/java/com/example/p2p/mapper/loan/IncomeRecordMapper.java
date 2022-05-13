package com.example.p2p.mapper.loan;

import com.example.p2p.model.loan.IncomeRecord;

import java.util.List;

public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);

    /**
     * 向交易表添加一条记录
     * @param incomeRecord
     * @return
     */
    int insertIncomeRecord(IncomeRecord incomeRecord);

    /**
     * 根据状态为0，查询出待返还收益的数据
     * @param incomeStatusNotBack
     * @return
     */
    List<IncomeRecord> selectIncomeRecordsByIncomeStatusAndIncomeDate(int incomeStatusNotBack);
}