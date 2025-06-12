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
    TokenUtil tokenUtil;

    /**
     * 查询当前用户回收站中的所有图片信息。
     *
     * @param token 用户身份验证令牌
     * @return 包含图片信息的 JSON 对象，若 token 无效返回 fail 状态
     */
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

    /**
     * 将回收站中的图片恢复至正常状态。
     *
     * @param token   用户身份验证令牌
     * @param imageId 要恢复的图片 ID 列表
     * @return 操作结果的 JSON 对象
     */
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

    /**
     * 永久删除回收站中的图片。
     *
     * @param token   用户身份验证令牌
     * @param imageId 要删除的图片 ID 列表
     * @return 操作结果的 JSON 对象
     */
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
