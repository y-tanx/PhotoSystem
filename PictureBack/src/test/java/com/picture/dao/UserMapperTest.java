package com.picture.dao;

import com.picture.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@Transactional
class UserMapperTest {
    @Resource
    private UserMapper userMapper;

    @Test
    void testAddUserImage() {
        Integer userId = 1;
        List<Integer> imageIds = Arrays.asList(1001, 1002, 1003);
        userMapper.addUserImage(userId, imageIds);
    }

    @Test
    void deleteUserImage() {
        Integer userId = 1;
        List<Integer> imageIds = Arrays.asList(1001, 1002, 1003);
        userMapper.addUserImage(userId, imageIds);
        userMapper.deleteUserImage(userId, Arrays.asList(1001, 1002));
    }

  @Test
  void updateUser() {
        User user = new User();
        user.setSex("沃尔玛购物袋");
        user.setUserId(2);
        userMapper.updateUser(user);
  }
}