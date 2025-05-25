package com.picture.utils;

import com.picture.domain.VO.AIResultVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileServerUtilTest {
    @Resource
    private FileServerUtil fileServerUtil;

    @Test
    void imageLabel() {
        // 本地图片位置
        String imagePath = "D:/1.jpg";
        try {
            List<AIResultVO> tagList = fileServerUtil.imageLabel(imagePath);
            assertNotEquals(0, tagList.size());
            System.out.println(tagList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}