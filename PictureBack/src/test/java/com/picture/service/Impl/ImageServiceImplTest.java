package com.picture.service.Impl;

import com.picture.domain.Image;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ImageServiceImplTest {
    @Resource
    private ImageServiceImpl imageService;

    private Integer userId = 1001;
    private Integer albumId = 2;
    private String albumName = "test";
    private String imageType = "风景";

    @Test
    void uploadImage_Success() {
        List<Image> imageList = new ArrayList<>();

        Image image1 = new Image();
        image1.setImageName("image1");
        image1.setImageUrL("/server/img/origin/image1.jpg");
        image1.setCompressUrL("/server/img/compress/image1.jpg");
        image1.setImageSite("中国，哈尔滨");
        image1.setImageDesc("这是一段注释!");
        image1.setImageDate(new Date());

        Image image2 = new Image();
        image2.setImageName("image2");
        image2.setImageUrL("/server/img/origin/image2.jpg");
        image2.setCompressUrL("/server/img/compress/image2.jpg");
        image2.setImageSite("中国，北京");
        image2.setImageDesc("这是image2的注释!");
        image2.setImageDate(new Date());

        imageList.add(image1);
        imageList.add(image2);

        assertTrue(imageService.uploadImage(imageList, userId, albumId, albumName, imageType));
        assertNotNull(image1.getImageId());
        assertNotNull(image2.getImageId());
    }
}