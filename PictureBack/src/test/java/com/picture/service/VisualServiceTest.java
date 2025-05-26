package com.picture.service;

import com.alibaba.fastjson.JSONObject;
import com.picture.domain.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@SpringBootTest
@Transactional
class VisualServiceTest {
    @Resource
    private ImageService imageService;
    @Resource
    private VisualService visualService;

    private Integer userId = 1;
    private Integer albumId = 2;
    private String albumName = "test";
    private String imageType = "风景";

    @BeforeEach
    void setUpBeforeEach() throws Exception {
        Image img1 = new Image(null, "pic1.jpg", 2048L, "地点1", "描述1",
                "http://url1.com", "http://url1-small.com", new Date());

        Image img2 = new Image(null, "pic2.jpg", 4096L, "地点2", "描述2",
                "http://url2.com", "http://url2-small.com", new Date());

        List<Image> images = Arrays.asList(img1, img2);
        imageService.uploadImage(images, userId, albumId, albumName, imageType);
    }

    @Test
    void selectAllImageInfo() {
        JSONObject json = visualService.selectAllImageInfo(userId);
        assert json != null;
        System.out.println(json);
    }
}