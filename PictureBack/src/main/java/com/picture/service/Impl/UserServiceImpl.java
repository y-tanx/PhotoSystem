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
    /**
     * 查询用户
     * @param id
     * @return
     */
    @Override
    public User selectUserById(int id) {
        User user = userMapper.selectUserById(id);  // 根据id在user表中查询用户信息
        return user;
    }
    /**
     * 用户资料更新
     * @param user
     */
    @Override
    public void updateUser(User user) {
        // 更新用户信息为user
        userMapper.updateUser(user);
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
