package com.picture.service.Impl;

import com.picture.domain.User;
import com.picture.dao.UserMapper;
import com.picture.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public Integer addUser(User user) {
        Integer userId = userMapper.add(user);  // 将用户信息插入到user表中
        return userId;
    }

    /**
     * 查询用户id
     * @param user
     * @return
     */
    @Override
    public Integer selectUserId(User user) {
        Integer userId = userMapper.selectUserId(user);
        return userId;
    }

    @Override
    public boolean resetPassword(User user) {
        if(user == null) {
            return false;
        }

        String userName = user.getUserName();
        if(userName == null || "".equals(userName)) {
            return false;
        }

        String password = user.getPassWord();

        // 修改用户名
        userMapper.resetPasswordByUserName(userName, password);
        return true;
    }

    @Override
    public String selectUserName(String UserName) {

        return userMapper.selectUserName(UserName); // 查询用户名是否存在，避免出现重复注册
    }
}
