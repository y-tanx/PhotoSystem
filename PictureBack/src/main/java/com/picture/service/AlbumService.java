package com.picture.service;
import com.alibaba.fastjson.JSONObject;
import com.picture.domain.VO.PartAlbumVO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

@Service
public interface AlbumService {
    /**
     * 添加一个新相册，并记录日志
     *
     * @param req        Http 请求对象（用于记录操作信息）
     * @param albumName  相册名称
     * @param userId     用户 ID
     */
    void addAlbum(HttpServletRequest req,String albumName, Integer userId);
}
