package com.picture.controller;

import com.picture.utils.RedisUtil;
import com.picture.utils.SendEmailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
public class SendEmailController {
    @Resource
    RedisUtil redisUtil;
    @Value("${spring.mail.code.time}")
    private int validTime;
    @Resource
    private SendEmailUtil se;

    /**
     * 向指定邮箱发送验证码，并将验证码缓存到 Redis。
     *
     * @param email 接收验证码的邮箱地址
     * @return 返回操作结果字符串 "Success"
     */
    @RequestMapping("/code")
    public String sendCode(@RequestParam("email")String email){

        String code = se.sendEmail(email);
        redisUtil.set(email,code,validTime);

        return "Success";
    }
}
