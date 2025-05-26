package com.picture.service;

import com.alibaba.fastjson.JSONObject;
import com.picture.domain.VO.VisualDataVO;
import org.springframework.stereotype.Service;

@Service
public interface VisualService {
    /**
     * 查询用户所有图片类别和地点信息
     * @param userId
     * @return
     */
    VisualDataVO selectTypeSite(Integer userId);

    JSONObject selectAllImageInfo(Integer userId);
}
