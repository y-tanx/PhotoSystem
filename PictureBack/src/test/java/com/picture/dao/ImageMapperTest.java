package com.picture.dao;

import com.picture.domain.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@SpringBootTest
@Transactional
class ImageMapperTest {
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private UserMapper userMapper;

    @Test
    void addImages() {
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
    void addImageType() {
        List<Integer> imageIds = Arrays.asList(1, 2);  // 假设插入后的 ID 是 1 和 2
        String imageType = "风景";

        imageMapper.addImageType(imageIds, imageType);

        System.out.println("插入 image_type 成功");
    }

    @Test
    void selectAllImageType() {
        // 插入user-image 和 image-type表
        List<Integer> imageIds1 = Arrays.asList(1, 2);
        String type1 = "风景";
        List<Integer> imageIds2 = Arrays.asList(3, 4);
        String type2 = "人物";
        imageMapper.addImageType(imageIds1, type1);
        imageMapper.addImageType(imageIds2, type2);
        userMapper.addUserImage(1, imageIds1);
        userMapper.addUserImage(1, imageIds2);

        List<String> imageTypes = imageMapper.selectAllImageType(1);
        System.out.println(imageTypes);
    }

    @Test
    void selectAllImageDate() {
        Image img1 = new Image(null, "pic1.jpg", 2048L, "地点1", "描述1",
                "http://url1.com", "http://url1-small.com", new Date());

        Image img2 = new Image(null, "pic2.jpg", 4096L, "地点2", "描述2",
                "http://url2.com", "http://url2-small.com", new Date());

        List<Image> imageList = Arrays.asList(img1, img2);

        imageMapper.addImages(imageList);
        List<Integer> imageIds = Arrays.asList(img1.getImageId(), img2.getImageId());
        userMapper.addUserImage(2, imageIds);

        // 查找用户2的所有图片的时间
        List<Date> imageDates = imageMapper.selectAllImageTime(2);
        System.out.println(imageDates);
    }

    @Test
    void selectAllImage() {
        // 插入user-image 和 image-type表
        List<Integer> imageIds1 = Arrays.asList(1, 2);
        String type1 = "风景";
        List<Integer> imageIds2 = Arrays.asList(3, 4);
        String type2 = "人物";
        imageMapper.addImageType(imageIds1, type1);
        imageMapper.addImageType(imageIds2, type2);
        userMapper.addUserImage(1, imageIds1);
        userMapper.addUserImage(1, imageIds2);
        List<Image> imageList = imageMapper.selectAllImage(1);
        System.out.println(imageList);
    }

    @Test
    void selectImageCount() {
        List<Integer> imageIds1 = Arrays.asList(1, 2);
        String type1 = "风景";
        List<Integer> imageIds2 = Arrays.asList(3, 4);
        String type2 = "人物";
        imageMapper.addImageType(imageIds1, type1);
        imageMapper.addImageType(imageIds2, type2);
        userMapper.addUserImage(1, imageIds1);
        userMapper.addUserImage(1, imageIds2);
        Integer count = imageMapper.selectImageCount(1);
        assert count == 2;
    }

    @Test
    void selectImageByType() {
        Image img1 = new Image(null, "pic1.jpg", 2048L, "地点1", "描述1",
                "http://url1.com", "http://url1-small.com", new Date());

        Image img2 = new Image(null, "pic2.jpg", 4096L, "地点2", "描述2",
                "http://url2.com", "http://url2-small.com", new Date());

        List<Image> imageList = Arrays.asList(img1, img2);

        imageMapper.addImages(imageList);
        List<Integer> imageIds = Arrays.asList(img1.getImageId(), img2.getImageId());
        userMapper.addUserImage(1, imageIds);
        imageMapper.addImageType(imageIds, "风景");

        // 查询用户1，风景的图片
        List<Image> images = imageMapper.selectImageByType(1, "风景");
        assert images.size() == 2;
        System.out.println(images);
    }


    @Test
    void selectImageByTime() {
        LocalDate localDate1 = LocalDate.of(2020, 1, 1);
        LocalDate localDate2 = LocalDate.of(2020, 1, 2);

        Image img1 = new Image(null, "pic1.jpg", 2048L, "地点1", "描述1",
                "http://url1.com", "http://url1-small.com", java.sql.Date.valueOf(localDate1));

        Image img2 = new Image(null, "pic2.jpg", 4096L, "地点2", "描述2",
                "http://url2.com", "http://url2-small.com", java.sql.Date.valueOf(localDate2));

        List<Image> imageList = Arrays.asList(img1, img2);

        imageMapper.addImages(imageList);
        List<Integer> imageIds = Arrays.asList(img1.getImageId(), img2.getImageId());
        userMapper.addUserImage(1, imageIds);
        imageMapper.addImageType(imageIds, "风景");

        // 查询用户1，日期为2020-1-1的图片
        List<Image> images1 = imageMapper.selectImageByTime(1, java.sql.Date.valueOf(localDate1));
        assert images1.size() == 1;
        System.out.println(images1);

         // 查询用户1，日期为2020-1-2的图片
        List<Image> images2 = imageMapper.selectImageByTime(1, java.sql.Date.valueOf(localDate2));
        assert images2.size() == 1;
        System.out.println(images2);
    }

    @Test
    void selectImageCountByTime() {
    }
}