package com.picture.utils;

import com.picture.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class TokenUtilTest {
    @Resource
    private TokenUtil tokenUtil;

    @Test
    void createToken() {
        String token = tokenUtil.createToken("myc".toString(), 1);
        System.out.println(token);
    }

    @Test
    void jwtParser() {
        User user = tokenUtil.jwtParser("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiIxIiwidXNlck5hbWUiOiJteWMiLCJzdWIiOiJNZW1vcnkiLCJleHAiOjE3NDgwMjMyMzAsImp0aSI6IjU3MmY2NjY0LTFlMDAtNDI4Mi05Yjg1LWE1ZTg2YTAxODM3ZCJ9.S1m6OKxn2Vx8X574UMfMlJl80ifpb_lyjAfYpKcDs-Y");
        System.out.println(user.getUserId());
        System.out.println(user.getUserName());
    }
}