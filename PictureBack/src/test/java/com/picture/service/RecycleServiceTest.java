package com.picture.service;

import com.alibaba.fastjson.JSONObject;
import com.picture.dao.ImageMapper;
import com.picture.dao.RecycleMapper;
import com.picture.dao.UserMapper;
import com.picture.domain.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RecycleServiceTest {
    @Resource
    private RecycleService recycleService;
    @Resource
    private RecycleMapper recycleMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private UserMapper userMapper;

    private Integer userId = 1;
    private Integer albumId = 2;
    private String albumName = "p1";
    @Autowired
    private ImageService imageService;

    @Test
    void selectAll() {
        // 将图片加入到回收站中
        // 插入图片
        Image img1 = new Image(null, "pic1.jpg", 2048L, "地点1", "描述1",
                "http://url1.com", "http://url1-small.com", new Date());

        Image img2 = new Image(null, "pic2.jpg", 4096L, "地点2", "描述2",
                "http://url2.com", "http://url2-small.com", new Date());

        List<Image> imageList = Arrays.asList(img1, img2);
        imageMapper.addImages(imageList);
        List<Integer> imageIds = Arrays.asList(img1.getImageId(), img2.getImageId());
        userMapper.addUserImage(1, imageIds);
        imageMapper.addImageType(imageIds, "风景");
        recycleMapper.addImageToRecycle(userId, imageIds);

        // 查询用户1 回收站的所有图片
        JSONObject res = recycleService.selectAll(userId);
        assert res != null;
        System.out.println(res);
    }

    @Test
    void recoverImage() {
        Image img1 = new Image(null, "pic1.jpg", 2048L, "地点1", "描述1",
                "http://url1.com", "http://url1-small.com", new Date());

        Image img2 = new Image(null, "pic2.jpg", 4096L, "地点2", "描述2",
                "http://url2.com", "http://url2-small.com", new Date());
        List<Image> imageList = Arrays.asList(img1, img2);

        // 插入图片
        assertTrue(imageService.uploadImage(imageList, userId, albumId, albumName, "风景"));
        List<Integer> imageIds = Arrays.asList(img1.getImageId(), img2.getImageId());

        // 删除图片
        imageService.deleteImage(userId, imageIds);

        // 从回收站中恢复图片2
        List<Integer> imageIds2 = Arrays.asList(img2.getImageId());
        recycleService.recoverImage(userId, imageIds2);
    }
}