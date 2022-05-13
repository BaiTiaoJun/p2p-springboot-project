package com.example.p2p.service.impl.user;

import com.example.p2p.mapper.user.FinanceAccountMapper;
import com.example.p2p.mapper.user.UserMapper;
import com.example.p2p.model.user.FinanceAccount;
import com.example.p2p.model.user.User;
import com.example.p2p.service.user.UserService;
import com.example.util.ConstantsUtil;
import com.example.util.DateTimeUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@DubboService(interfaceClass = UserService.class, version = "1.0.0", timeout = 15000)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public Integer queryAllUsersCount() {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();
        Integer allUsersCount = (Integer) operations.get(ConstantsUtil.ALL_USERS_COUNT);
        if (ObjectUtils.allNull(allUsersCount)) {
//            防止缓存击穿现象，提高执行效率
            synchronized (this) {
//                高并发下的判断条件
                allUsersCount = (Integer) operations.get(ConstantsUtil.ALL_USERS_COUNT);
                if (ObjectUtils.allNull(allUsersCount)) {
                    allUsersCount = userMapper.selectAllUsersCount();
                    operations.set(ConstantsUtil.ALL_USERS_COUNT, allUsersCount, 7, TimeUnit.DAYS);
                }
            }
        }
        return allUsersCount;
    }

    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }

    @Override
    @Transactional (
            isolation = Isolation.REPEATABLE_READ,
            propagation = Propagation.REQUIRED,
            readOnly = false,
            timeout = -1,
            rollbackFor = {
                    Exception.class
            }
    )
    public User register(Map<String, Object> map) throws Exception {
        String phone = (String) map.get("phone");
        String pass = (String) map.get("pass");

//        用户信息添加
        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(pass);
        user.setAddTime(DateTimeUtil.getFullDateTime(new Date()));
        user.setLastLoginTime(DateTimeUtil.getFullDateTime(new Date()));
        int userRes = userMapper.insert(user);

//        用户资金账户添加
        if (userRes > 0 ) {
            FinanceAccount financeAccount = new FinanceAccount();
            financeAccount.setUid(user.getId());
            financeAccount.setAvailableMoney(0.00);
            int financeAccountRes = financeAccountMapper.insert(financeAccount);
            if (financeAccountRes == 0) {
                throw new RuntimeException("注册失败，请稍后再试");
            }
        } else {
            throw new RuntimeException("注册失败，请稍后再试");
        }

        return user;
    }

    @Override
    public int editUserByPhone(User user) {
        return userMapper.updateUserByPhone(user);
    }

    @Override
    @Transactional
    public User login(Map<String, Object> map) throws Exception {
        User user = userMapper.selectUserByPhoneAndPassword(map);
        if (ObjectUtils.allNull(user)) {
            throw new Exception("用户登录失败");
        }

        Map<String, Object> map1 = new HashMap<>();
        map1.put("lastLoginTime", DateTimeUtil.getFullDateTime(new Date()));
        map1.put("id", user.getId());

        int userLastLoginTimeRes = userMapper.updateUserLastLoginTime(map1);
        if (userLastLoginTimeRes == 0) {
            throw new Exception("更新用户最近登录时间失败");
        }
        return user;
    }
}