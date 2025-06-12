package com.picture.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AliyunOssUtilTest {
  @Resource
  AliyunOssUtil aliyunOssUtil;

  @Test
  void deleteImages() {
    List<String> imageUrls = new ArrayList<>();
    imageUrls.add("https://myc-picture.oss-cn-beijing.aliyuncs.com/image/origin/myc123456/9054ccaf21cb4b1e93ecf4d2b97e6bfc.jpg");
    imageUrls.add("https://myc-picture.oss-cn-beijing.aliyuncs.com/image/origin/myc123456/78f4bf3b26814af5ad60092e5154640d.jpg");
    aliyunOssUtil.deleteImages(imageUrls);
  }
}