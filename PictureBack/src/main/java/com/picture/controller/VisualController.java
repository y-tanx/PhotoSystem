package com.picture.controller;

import com.alibaba.fastjson.JSONObject;
import com.picture.domain.User;
import com.picture.domain.VO.VisualDataVO;
import com.picture.service.VisualService;
import com.picture.utils.TokenUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/visual")
@RestController
public class VisualController {
    @Resource
    private VisualService visualService;
    @Resource
    private TokenUtil tokenUtil;

    /**
     * 查询用户所有图片的类别和地址信息
     *
     * @param token
     * @return
     */
    @RequestMapping("/selectTypeSite")
    public JSONObject selectTypeSite(String token) {
        JSONObject json = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user == null) {
            json.put("status", "fail");
            return json;
        }

        Integer userId = user.getUserId();

        // 调用Service层，查询用户所有图片的类别和地址信息
        VisualDataVO visualDataVO = visualService.selectTypeSite(userId);
        json.put("status", "success");
        json.put("data", visualDataVO);
        return json;
    }

    @RequestMapping("/selectImageInfo")
    public JSONObject selectImageInfo(String token) {
        JSONObject json = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user == null) {
            json.put("status", "fail");
            return json;
        }

        Integer userId = user.getUserId();
        JSONObject res = visualService.selectAllImageInfo(userId);
        if(res == null) {
            json.put("status", "fail");
            return json;
        }
        json.put("status", "success");
        json.put("data", res);
        return json;
    }
}
