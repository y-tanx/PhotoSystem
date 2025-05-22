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
     * 查询用户id
     * @param user
     * @return
     */
    @Override
    public Integer selectUserId(User user) {
        Integer userId = userMapper.selectUserId(user);
        return userId;
    }

}
