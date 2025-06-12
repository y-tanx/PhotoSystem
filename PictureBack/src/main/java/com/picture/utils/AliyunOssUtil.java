package com.picture.utils;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.Callback;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AliyunOssUtil {

  private static final Logger log = LoggerFactory.getLogger(AliyunOssUtil.class);
  @Value("${aliyun.accessKeyId}")
  private String accessKeyId;
  @Value("${aliyun.accessKeySecret}")
  private String accessKeySecret;
  @Value("${aliyun.bucket}")
  private String bucket;
  @Value("${aliyun.endPoint}")
  private String endPoint;
  @Value("${aliyun.imgPath}")
  private String imgPath;
  private float limitSize = 3145728;
  private float minSize =   100000;

  private static OSSClient ossClient;

  private synchronized void createOssClient() {
    if(ossClient == null) {
      DefaultCredentialProvider defaultCredentialProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
      ClientConfiguration config = null;
      ossClient = new OSSClient(endPoint, defaultCredentialProvider, config);
    }
  }

  private OSSClient getOssClient() {
    if(ossClient == null) {
      createOssClient();
    }
    return ossClient;
  }

  public String uploadOriginImage(String userName, String imageName , InputStream inputStream, CannedAccessControlList acl, Callback callback) throws Exception {
    String suffix = imageName.substring(imageName.lastIndexOf("."));
    String key = generateOssKey("origin", userName, suffix);

    return uploadInputStreamFile(key, inputStream, acl, callback);

  }

  public String uploadCompressImage(String userName, String imageName, Float imgSize, InputStream inputStream, CannedAccessControlList acl, Callback callback) throws Exception {
    // 判断压缩比率
    float imgRate = (imgSize < minSize) ? 1.0f : ((imgSize > limitSize) ? (limitSize / imgSize) : 0.9f);

    // 创建输出流存储压缩后的图片
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      // 执行压缩
      Thumbnails.of(inputStream)
          .scale(1f)
          .outputQuality(imgRate)
          .toOutputStream(baos);

      // 转换为InputStream，用于上传
      ByteArrayInputStream compressInputStream = new ByteArrayInputStream(baos.toByteArray());

      // 构造 OSS 路径
      String suffix = imageName.substring(imageName.lastIndexOf("."));
      String key = generateOssKey("compress", userName, suffix);

      // 上传到OSS
      String ossPath = uploadInputStreamFile(key, compressInputStream, acl, callback);

      // 关闭流
      compressInputStream.close();
      return ossPath;
    } finally {
      baos.close();
    }
  }

  public void deleteImages(List<String> imageUrls) {
    if(imageUrls == null || imageUrls.size() == 0 || imageUrls.size() > 1000) {
      log.error("删除出错，检查传入的删除列表!");
    }
    List<String> keys = getImageKeys(imageUrls);
    DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket).withKeys(keys);
    getOssClient().deleteObjects(deleteObjectsRequest);
  }

  private List<String> getImageKeys(List<String> imageUrLs) {
    List<String> imageKeys = new ArrayList<String>();
    for(String imageUrL : imageUrLs) {
      int index = imageUrL.indexOf(".com/");
      if(index != -1 && index + 5 < imageUrL.length()) {
        imageKeys.add(imageUrL.substring(index + 5)); // 跳过.com/
      }
    }
    return imageKeys;
  }

  private String uploadInputStreamFile(String key, InputStream inputStream, CannedAccessControlList acl, Callback callback) throws Exception {
    PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, inputStream);
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setObjectAcl(acl); // 设置访问权限
    putObjectRequest.setMetadata(metadata);

    if (callback != null) {
      putObjectRequest.setCallback(callback);
    }

    getOssClient().putObject(putObjectRequest);

    // 返回完整的阿里云 URL
    return "https://" + bucket + "." + endPoint + "/" + key;
  }

  private String generateOssKey(String imageType, String userName, String fileSuffix) {
    // imageType: original 或 compress
    // userId: 当前用户 ID
    // fileSuffix: 如 ".jpg"、".png" 等

    String uuid = UUID.randomUUID().toString().replaceAll("-", ""); // 保证唯一性
    return String.format("%s%s/%s/%s%s", imgPath, imageType, userName, uuid, fileSuffix);
  }
}
