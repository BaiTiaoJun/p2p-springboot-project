package com.example.p2p.mapper.user;

import com.example.p2p.model.user.FinanceAccount;

import java.util.Map;

public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    /**
     * 根据电话号码查询可用金额
     * @param phone
     * @return
     */
    FinanceAccount selectAvailableMoneyByPhone(String phone);

    /**
     * 根据用户id更新账户金额
     * @param map
     * @return
     */
    int updateAvailableMoneyByUId(Map<String, Object> map);

    /**
     * 根据遍历出的uid、bidmoney、incomemoney返回给相关用户的账户
     * @param financeAccount
     * @return
     */
    int updateAvailableMoneyByIncomeAndUId(FinanceAccount financeAccount);
}