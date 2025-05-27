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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private TokenUtil tokenUtil;
    @Resource
    RedisUtil redisUtil;
    @Resource
    private UserService userService;
    //默认头像
    private String defaultAvatar = "/static/avatar/default.jpg";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 用户注册
     * @param userName 用户名
     * @param passWord 密码
     * @param email 邮箱地址
     * @param codeNumber 验证码（来自邮箱）
     * @return 注册结果（状态码：exist/codeError/success）
     */
    @RequestMapping("/add")
    public JSONObject addUser(@RequestParam("username")String userName,@RequestParam("password")String passWord,String email,String codeNumber){
        JSONObject jsonObject = new JSONObject();
        if(userService.selectUserName(userName)!=null){
            jsonObject.put("status","exist");
            return jsonObject;
        }

        // Redis用于存储验证码
        String redisCode = redisUtil.get(email);
        if(!codeNumber.equals(redisCode)){
            jsonObject.put("status","codeError");
            return jsonObject;
        }
        User user = new User(null,userName,passWord,null,email,null,null,null,5000,defaultAvatar);
        userService.addUser(user);
        jsonObject.put("status","success");
        return jsonObject;
    }
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
    /**
     * 查询当前登录用户的信息
     * @param token 用户 token
     * @return 用户信息（包含 token）
     */
    @RequestMapping("/selectUser")
    public JSONObject selectUser(String token){
        JSONObject jsonObject= new JSONObject();
        User userToken = tokenUtil.jwtParser(token);
        User user = null;

        // 根据userID获得user对象
        // 把查询到的user对象添加到返回的JSON中
        if(userToken.getUserId()!=null) user = userService.selectUserById(userToken.getUserId());
        if(user!=null) {
            jsonObject.put("status", "success");
            jsonObject.put("user", user);
            JSONObject res =  jsonObject.getJSONObject("user");
            res.put("token",token); // 附带token传回前端
            jsonObject.put("user",res);
            return jsonObject;
        }
        else{
            jsonObject.put("status", "fail");

        }
        return jsonObject;
    }
    /**
     * 更新用户信息（性别、邮箱、电话、城市、生日）
     * @param token 用户 token
     * @param sex 性别
     * @param email 邮箱
     * @param phone 电话
     * @param city 城市
     * @param birthday 生日（格式为 yyyy-MM-dd）
     * @return 更新结果状态
     */
    @RequestMapping("/updateUser")
    public JSONObject updateUser(String token,String sex,String email,String phone,String city,String birthday) throws ParseException {
        JSONObject jsonObject= new JSONObject();
        User userToken = tokenUtil.jwtParser(token);
        if(userToken!=null){
            userToken.setSex(sex);
            userToken.setEmail(email);
            userToken.setPhone(phone);
            userToken.setCity(city);
        }
        else{
            jsonObject.put("status", "fail");
            return jsonObject;
        }
        Date b;
        if(birthday.equals("null")||birthday.equals("")){
            System.out.println(1);
            b=null;
        }
        else{
            b  =dateFormat.parse(birthday);
        }
        userToken.setBirthday(b);
        userService.updateUser(userToken);
        jsonObject.put("status", "success");
        return jsonObject;
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
