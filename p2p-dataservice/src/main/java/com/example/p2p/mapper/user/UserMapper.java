package com.example.p2p.mapper.user;

import com.example.p2p.model.user.User;

import java.util.Date;
import java.util.Map;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 查询平台总人数
     * @return
     */
    Integer selectAllUsersCount();

    /**
     * 根据电话号码查询此用户是否已经存在
     * @param phone
     * @return
     */
    User selectUserByPhone(String phone);

    /**
     * 验证通过后，通过手机号码进行用户更新
     * @param user
     * @return
     */
    int updateUserByPhone(User user);

    /**
     * 根据账户的帐号和密码进行查询，是否有该用户
     * @param map
     * @return
     */
    User selectUserByPhoneAndPassword(Map<String, Object> map);

    /**
     * 登录后更新用户最新登录时间
     * @param map
     * @return
     */
    int updateUserLastLoginTime(Map<String, Object> map);
}