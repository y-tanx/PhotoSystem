package com.picture.dao;

import com.picture.domain.Image;
import com.picture.domain.Recycle;
import com.picture.domain.VO.RecycleVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RecycleMapperTest {
    @Resource
    private RecycleMapper recycleMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private UserMapper userMapper;

    private Integer userId = 1;

    @Test
    void addImageToRecycle() {
        List<Integer> imageIds = Arrays.asList(1001, 1002, 1003);

        // 添加到recycle表中
        recycleMapper.addImageToRecycle(userId, imageIds);
    }

    @Test
    void selectAll() {
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

        // 查询recycle中所有图片
        List<RecycleVO> recycleVOS = recycleMapper.selectAll(userId);
        assertNotNull(recycleVOS);
        System.out.println(recycleVOS);
    }

    @Test
    void deleteImageFromRecycle() {
        // 向recycle中插入图片
        List<Integer> imageIds = Arrays.asList(1001, 1002, 1003);
        recycleMapper.addImageToRecycle(userId, imageIds);

        // 删除图片
        List<Integer> imageIds2 = Arrays.asList(1001, 1002);
        recycleMapper.deleteImageFromRecycle(userId, imageIds2);
    }
}