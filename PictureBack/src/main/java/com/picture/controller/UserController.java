package com.picture.controller;

import com.alibaba.fastjson.JSONObject;
import com.picture.common.ResultMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import com.picture.domain.User;
import com.picture.service.UserService;
import com.picture.utils.RedisUtil;
import com.picture.utils.TokenUtil;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
    public ResultMessage loginUser(String userName, String passWord) {
        User user = new User(userName, passWord);

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

    @RequestMapping("/resetPassword")
    public JSONObject resetPassword(@RequestParam("username") String userName, String password, String email, String codeNumber){
        JSONObject jsonObject = new JSONObject();
        // 检验验证码
        String redisCode = redisUtil.get(email);
        if(!codeNumber.equals(redisCode)){
            jsonObject.put("status","codeError");
            return jsonObject;
        }

        // 用户不存在
        if(userService.selectUserName(userName) == null){
            jsonObject.put("status","notExist");
            return jsonObject;
        }

        // 调用Service层，修改密码
        User user = new User();
        user.setUserName(userName);
        user.setPassWord(password);
        boolean status = userService.resetPassword(user);
        if(status){
            jsonObject.put("status","success");
        }else {
            jsonObject.put("status","fail");
        }
        return jsonObject;
    }


}
