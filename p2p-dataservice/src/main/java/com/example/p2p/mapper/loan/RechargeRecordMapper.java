package com.example.p2p.mapper.loan;

import com.example.p2p.model.loan.RechargeRecord;

import java.util.Map;

public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);

//    充值成功后更新根据用户id充值状态
    int updateRechargeStatusByUid(Map<String, Object> map);

}