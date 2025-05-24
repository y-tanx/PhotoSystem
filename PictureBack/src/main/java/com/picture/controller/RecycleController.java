package com.picture.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.picture.dao.ImageMapper;
import com.picture.domain.Image;
import com.picture.domain.User;
import com.picture.service.ImageService;
import com.picture.service.RecycleService;
import com.picture.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/recycle")
public class RecycleController {
    @Resource
    private RecycleService recycleService;
    @Resource
    private ImageService imageService;
    @Resource
    TokenUtil tokenUtil;
    @Autowired
    private ImageMapper imageMapper;

    @RequestMapping("/selectAll")
    public JSONObject selectAll(String token) {
        JSONObject json = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if (user == null) {
            json.put("status", "fail");
            return json;
        }

        Integer userId = user.getUserId();
        JSONObject res = recycleService.selectAll(userId);

        json.put("status", "success");
        json.put("data", res);
        return json;
    }

    @RequestMapping("/recoverImage")
    public JSONObject recoverImage(String token, @RequestParam("imageId")List<Integer> imageId) {
        JSONObject json = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if (user == null) {
            json.put("status", "fail");
            return json;
        }

        Integer userId = user.getUserId();
        recycleService.recoverImage(userId, imageId);
        json.put("status", "success");
        return json;
    }

    @RequestMapping("/deleteImage")
    public JSONObject deleteImage(String token, @RequestParam("imageId")List<Integer> imageId) {
        JSONObject json = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if (user == null) {
            json.put("status", "fail");
            return json;
        }

        Integer userId = user.getUserId();
        recycleService.deleteImage(userId, imageId);
        json.put("status", "success");
        return json;
    }
}
