package com.example.p2p.service.user;

import com.example.p2p.model.user.User;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * 查询平台总人数
     * @return
     */
    Integer queryAllUsersCount();

    /**
     * 根据电话号码查询此用户是否已经存在
     * @param phone
     * @return
     */
    User queryUserByPhone(String phone);

    /**
     * 向用户个人信息表中保存注册的信息,向用户财务资金表中创建信息
     * @param map
     * @return
     */
    User register(Map<String, Object> map) throws Exception;

    /**
     * 验证通过后，通过手机号码进行用户更新
     * @param user
     * @return
     */
    int editUserByPhone(User user);

    /**
     * 用户登录，根据账户的帐号和密码进行查询用户是否存在，更新最后一次上线时间，返回user
     * @param map
     * @return
     */
    User login(Map<String, Object> map) throws Exception;
}
