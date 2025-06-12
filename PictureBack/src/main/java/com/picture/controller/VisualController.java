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
     * 查询当前用户所有图片的分类信息与地理位置信息。
     * 返回的数据适用于图表展示，如分类统计图和地图分布图。
     *
     * @param token 用户身份令牌，用于身份验证
     * @return 包含状态码和 VisualDataVO 数据的 JSON 对象
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

    /**
     * 查询当前用户所有图片的详细信息，用于前端可视化展示。
     *
     * @param token 用户身份令牌，用于身份验证
     * @return 包含状态码 和 图片信息数据的 JSON 对象
     */
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
