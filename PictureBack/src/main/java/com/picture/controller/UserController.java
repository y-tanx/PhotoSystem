package com.picture.controller;

import com.picture.common.ResultMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import com.picture.domain.User;
import com.picture.service.UserService;
import com.picture.utils.RedisUtil;
import com.picture.utils.TokenUtil;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private TokenUtil tokenUtil;
    @Resource
    RedisUtil redisUtil;
    @Resource
    private UserService userService;
    /**
     * 用户登录
     * @param userName
     * @param passWord
     * @return
     */
    @RequestMapping("/login")
    public ResultMessage loginUser(String userName, String passWord){
        User user = new User(userName,passWord);

        System.out.println("password:"+passWord);
        Integer userId = userService.selectUserId(user);
        if(userId==null){
            return ResultMessage.fail(401, "账户密码错误！");
        }
        else {
            String token = tokenUtil.createToken(user.getUserName(), userId);
            return ResultMessage.success(200,"登录成功!",token);
        }
    }
}
