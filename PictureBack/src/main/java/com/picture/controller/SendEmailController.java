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
    @RequestMapping("/code")
    public String sendCode(@RequestParam("email")String email){

        String code = se.sendEmail(email);
        redisUtil.set(email,code,validTime);

        return "Success";
    }
}
