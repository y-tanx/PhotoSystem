package com.picture.dao;

import com.picture.domain.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@SpringBootTest
class ImageMapperTest {
    @Autowired
    private ImageMapper imageMapper;

    @Test
    void testaddImages() {
        Image img1 = new Image(null, "pic1.jpg", 2048L, "地点1", "描述1",
                "http://url1.com", "http://url1-small.com", new Date());

        Image img2 = new Image(null, "pic2.jpg", 4096L, "地点2", "描述2",
                "http://url2.com", "http://url2-small.com", new Date());

        List<Image> imageList = Arrays.asList(img1, img2);

        imageMapper.addImages(imageList);

        // 可选：打印插入后的 ID，确认主键回填成功
        imageList.forEach(img -> System.out.println("生成的ID: " + img.getImageId()));
    }

    @Test
    void testaddImageType() {
        List<Integer> imageIds = Arrays.asList(1, 2);  // 假设插入后的 ID 是 1 和 2
        String imageType = "风景";

        imageMapper.addImageType(imageIds, imageType);

        System.out.println("插入 image_type 成功");
    }
}