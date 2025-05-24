package com.picture.service.Impl;

import com.picture.domain.Image;
import com.picture.domain.VO.ImageVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ImageServiceImplTest {
    @Resource
    private ImageServiceImpl imageService;

    private Integer userId = 1;
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

    @Test
    void selectAllImage() {
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
        imageService.uploadImage(imageList, userId, albumId, albumName, imageType);

        ImageVO images = imageService.selectAllImage(userId, 0, 2);
        assertNotNull(images);
        System.out.println(images);
    }

    @Test
    void selectImageByTime() {
        LocalDate localDate1 = LocalDate.of(2020, 1, 1);
        LocalDate localDate2 = LocalDate.of(2020, 1, 2);
        List<Image> imageList = new ArrayList<>();

        Image image1 = new Image();
        image1.setImageName("image1");
        image1.setImageUrL("/server/img/origin/image1.jpg");
        image1.setCompressUrL("/server/img/compress/image1.jpg");
        image1.setImageSite("中国，哈尔滨");
        image1.setImageDesc("这是一段注释!");
        image1.setImageDate(java.sql.Date.valueOf(localDate1));

        Image image2 = new Image();
        image2.setImageName("image2");
        image2.setImageUrL("/server/img/origin/image2.jpg");
        image2.setCompressUrL("/server/img/compress/image2.jpg");
        image2.setImageSite("中国，北京");
        image2.setImageDesc("这是image2的注释!");
        image2.setImageDate(java.sql.Date.valueOf(localDate2));

        imageList.add(image1);
        imageList.add(image2);
        imageService.uploadImage(imageList, userId, albumId, albumName, imageType);

        // 查询用户1，日期为2020-1-1的照片
       ImageVO imageVO1 = imageService.selectImageByTime(userId, java.sql.Date.valueOf(localDate1), 0, 2);
       assertNotNull(imageVO1);
       System.out.println(imageVO1);

       // 查询用户1，日期为2020-1-2的图片
        ImageVO imageVO2 = imageService.selectImageByTime(userId, java.sql.Date.valueOf(localDate2), 0, 2);
        assertNotNull(imageVO2);
        System.out.println(imageVO2);
    }
}