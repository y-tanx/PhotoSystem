package com.picture.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.imagerecog20190930.Client;
import com.aliyun.imagerecog20190930.models.TaggingImageAdvanceRequest;
import com.aliyun.imagerecog20190930.models.TaggingImageResponse;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.Callback;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.picture.domain.VO.AIResultVO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
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

  /**
   * 创建oss客户端，用于连接OSS
   */
  private synchronized void createOssClient() {
    if(ossClient == null) {
      DefaultCredentialProvider defaultCredentialProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
      ClientConfiguration config = null;
      ossClient = new OSSClient(endPoint, defaultCredentialProvider, config);
    }
  }

  /**
   * 获取OSS客户端对象
   *
   * @return OSS客户端对象
   */
  private OSSClient getOssClient() {
    if(ossClient == null) {
      createOssClient();
    }
    return ossClient;
  }

  /**
   * 上传原图文件到 OSS
   *
   * @param userName 用户名
   * @param imageName 原始文件名
   * @param inputStream 文件输入流
   * @param acl 访问权限（如公共读）
   * @param callback 上传完成回调（可为空）
   * @return 上传后的 OSS URL
   */
  public String uploadOriginImage(String userName, String imageName , InputStream inputStream, CannedAccessControlList acl, Callback callback) throws Exception {
    String suffix = imageName.substring(imageName.lastIndexOf("."));
    String key = generateOssKey("origin", userName, suffix);

    return uploadInputStreamFile(key, inputStream, acl, callback);

  }

  /**
   * 压缩图片，并将压缩图上传到 OSS
   *
   * @param userName 用户名
   * @param imageName 文件名
   * @param imgSize 原图大小
   * @param inputStream 原图输入流
   * @param acl OSS访问权限
   * @param callback 上传回调（可选）
   * @return 上传后 OSS 的 URL
   */
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

  /**
   * 删除 OSS 上的多张图片
   *
   * @param imageUrls 图片的完整 URL 列表
   */
  public void deleteImages(List<String> imageUrls) {
    if(imageUrls == null || imageUrls.size() == 0 || imageUrls.size() > 1000) {
      log.error("删除出错，检查传入的删除列表!");
    }
    List<String> keys = getImageKeys(imageUrls);
    DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket).withKeys(keys);
    getOssClient().deleteObjects(deleteObjectsRequest);
  }

  /**
   * 使用阿里云图像识别功能，为图像打标
   * 参考: https://help.aliyun.com/zh/viapi/use-cases/general-image-marking-1?spm=a2c4g.11186623.0.0.621b5304H8OqzS
   *
   * @param imageCompressUrL 图片的本地文件路径
   * @return 打标结果，包括标签和可信度
   * @throws Exception
   */
  public List<AIResultVO> imageLabel(String imageCompressUrL) throws Exception {
        /*
          初始化配置对象com.aliyun.teaopenapi.models.Config
          Config对象存放 AccessKeyId、AccessKeySecret、endpoint等配置
          */
    Config config = new Config() .setAccessKeyId(accessKeyId)
        .setAccessKeySecret(accessKeySecret);

    // 访问的域名
    config.setEndpoint("imagerecog.cn-shanghai.aliyuncs.com");

    // 创建Client
    Client client = new Client(config);

    // 使用网络URL
    URL url = new URL(imageCompressUrL);
    InputStream inputStream = url.openConnection().getInputStream();
    TaggingImageAdvanceRequest taggingImageAdvanceRequest = new TaggingImageAdvanceRequest()
        .setImageURLObject(inputStream);
    RuntimeOptions runtime = new RuntimeOptions();

    try {
      // 获得json格式的识别结果
      TaggingImageResponse taggingImageResponse = client.taggingImageAdvance(taggingImageAdvanceRequest, runtime);

      // 将json转换为实体对象
      Gson gson = new Gson();
      List<AIResultVO> result = gson.fromJson(JSONObject.toJSONString(taggingImageResponse.getBody().getData().tags), new TypeToken<ArrayList<AIResultVO>>(){}.getType());

      return result;
    }catch (TeaException teaException){
      // 获取整体报错信息。
      log.error(com.aliyun.teautil.Common.toJSONString(teaException));
      // 获取单个字段。
      log.error(teaException.getCode());

      return null;
    }
  }

  /**
   * 从 URL 中提取 OSS key，即图片在OSS上的路径
   *
   * @param imageUrLs 完整 URL 列表
   * @return OSS key 列表
   */
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

  /**
   * 上传文件流到 OSS，并返回图片的网络地址
   *
   * @param key OSS 文件路径 key
   * @param inputStream 文件流
   * @param acl 访问控制权限
   * @param callback 上传回调
   * @return OSS 文件访问地址
   */
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

  /**
   * 生成 OSS 文件存储路径
   *
   * @param imageType 图片类型（origin/compress）
   * @param userName 用户名
   * @param fileSuffix 文件后缀（.jpg/.png等）
   * @return 构造后的 OSS key
   */
  private String generateOssKey(String imageType, String userName, String fileSuffix) {
    String uuid = UUID.randomUUID().toString().replaceAll("-", ""); // 保证唯一性
    return String.format("%s%s/%s/%s%s", imgPath, imageType, userName, uuid, fileSuffix);
  }
}
