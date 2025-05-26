package com.picture.dao;

import com.picture.domain.Image;
import com.picture.domain.VisualDataSite;
import com.picture.domain.VisualDataType;
import com.picture.service.ImageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VisualMapperTest {
    @Resource
    private VisualMapper visualMapper;
    @Resource
    private ImageService imageService;

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
    void selectVisualDataType() {
        // 查询类别
        List<VisualDataType> list = visualMapper.selectVisualDataType(userId);
        assertNotNull(list);
        System.out.println(list);
    }

    @Test
    void selectVisualDataSite() {
        // 查询地点
        List<VisualDataSite> list = visualMapper.selectVisualDataSite(userId);
        assertNotNull(list);
        System.out.println(list);
    }

    @Test
    void selectImageTotalSize() {
        // 查询大小
        Integer size = visualMapper.selectImageTotalSize(userId);
        assertNotNull(size);
        System.out.println(size);
    }
}