package com.picture.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.imagerecog20190930.Client;
import com.aliyun.imagerecog20190930.models.TaggingImageAdvanceRequest;
import com.aliyun.imagerecog20190930.models.TaggingImageResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.picture.domain.VO.AIResultVO;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class FileServerUtil {
    @Value("${file.upload.imgPath}")
    private String uploadPath;
    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    private String uploadPrefix = "/serve/";
    private String originPrefix = "/origin/";
    private String compressPrefix = "/compress/";
    private float limitSize = 3145728;
    private float minSize =   100000;


    /**
     * 将存储在数据库中的路径转换为磁盘上对应的文件路径
     * @param ServePath 数据库路径，如 "/serve/img/x.jpg"
     * @return 绝对路径
     */
    public String ServPathToAP(String ServePath){
        String AbsolutePath = "";
        String path = ServePath.substring(uploadPrefix.length());   // 去除/server/前缀
        AbsolutePath = uploadPath+path;     // 本地文件路径
        return AbsolutePath;
    }

    /**
     * 获取文件的唯一标识 MD5 + 时间戳，确保每个文件唯一性
     * @param file 文件对象
     * @return 唯一标识字符串
     */
    public String getMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;

            // 读取文件，更新MD5摘要信息
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
//            采用时间加MD5方式保证每个文件的唯一性
            String md5 = new String(Hex.encodeHex(MD5.digest()));
            Date date = new Date();
            long time = date.getTime();
            String str = md5+time;  // 文件标识符
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 上传文件到服务器并返回供 Web 和数据库访问的路径（采用唯一文件名，避免重复）
     *
     * @param uploadType     上传的文件类型（例如 "img" 表示图片）
     * @param folderName     要保存到的子目录（通常是用户 ID 或分类名）
     * @param multipartFile  Spring 提供的上传文件对象
     * @return 返回供前端访问或数据库存储的文件访问路径（形如 /serve/img/origin/user123/xxx.jpg）
     * @throws IOException   文件读写异常
     */
    public String uploadServe(String uploadType, String folderName, MultipartFile multipartFile) throws IOException {
        // 获取上传文件的原始文件名（包含扩展名）
        String originalFilename = multipartFile.getOriginalFilename();

        // 构建中间目录路径，例如：img/origin/user123/ 或 video/user123/
        String MidPath = "";
        if (uploadType.equals("img")) {
            // 如果是图片，路径形如 img/origin/user123/
            MidPath = uploadType + originPrefix + folderName + "/";
        } else {
            // 否则为通用路径形如 video/user123/，无 originPrefix
            MidPath = uploadType + "/" + folderName + "/";
        }

        // 构建原始文件的完整磁盘路径（还未重命名为唯一名）
        File imgFile = new File(uploadPath + MidPath + originalFilename);

        // 如果上级目录不存在，则递归创建（确保路径存在）
        if (!imgFile.getParentFile().exists()) {
            imgFile.getParentFile().mkdirs();
        }

        //  把文件写入imgFile路径下
        //  原文件通过HTTP请求传到后端服务器中，暂时保存在内存或临时文件中，该方法将临时文件写入到特定路径下
        multipartFile.transferTo(imgFile);

        // 提取文件的扩展名，例如 ".jpg"
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 通过文件内容生成唯一的 MD5 值，作为文件名（防止重复、便于查重）
        String FileMd5 = getMD5(imgFile);

        // 构建最终保存路径，使用 MD5 重命名文件，保持后缀名不变
        // 例如：img/origin/user123/d41d8cd98f00b204e9800998ecf8427e.jpg
        String Path = MidPath + FileMd5 + suffix;

        // 重命名文件：将原文件名改为 MD5 命名后的新文件名
        imgFile.renameTo(new File(uploadPath + Path));

        // 构建对外暴露的 Web 访问路径，需要加上前缀 /serve/
        // 该路径会通过 WebMvcConfig 中的 addResourceHandler 映射到本地磁盘路径
        String ServePath = uploadPrefix + Path;

        // 返回 Web 访问路径（用于数据库记录或前端页面引用）
        return ServePath;
    }

    /**
     * 删除服务器上的文件（本质上是删除本地磁盘目录中的文件）
     *
     * @param filePath 数据库中保存的相对路径（如 "/serve/img/xxx/abc.jpg"）
     */
    public void deleteServe(String filePath){
        // 删除本地磁盘上的文件
        String ApPath = ServPathToAP(filePath);
        new File(ApPath).delete();
    }

    /**
     * 压缩图片文件（自动判断是否需要压缩）
     *
     * @param ApPath    原图片的绝对路径
     * @param folderName 存储压缩图像的子目录名
     * @param imgSize    原始图片大小（单位：字节）
     * @return 返回压缩图像的相对服务器路径（供数据库使用）
     * @throws IOException 图片读写异常
     */
    public String CompressImage(String ApPath,String folderName ,Float imgSize) throws IOException {
        // 压缩的图片路径为: /server/img/compress/xxx.jpg
        String MidPath= "img"+compressPrefix+folderName;

        File tempFile = new File(ApPath);
        if(!tempFile.getParentFile().exists()){
            tempFile.getParentFile().mkdirs();
        }
        //压缩比率
        float imgRate = limitSize>imgSize?(float) 0.9:(limitSize)/imgSize;

        // 提取文件名（包含后缀，如abc.jpg）
        String suffix = ApPath.substring(ApPath.lastIndexOf("/"));

        // 构建压缩后的目标文件路径
        File imgFile = new File(uploadPath+MidPath+suffix);
        if(!imgFile.getParentFile().exists()){
            imgFile.getParentFile().mkdirs();
        }
//        最小100kb，否则不能压缩
        if(imgSize<minSize){
            copyFileUsingStream(new File(ApPath), new File(uploadPath + MidPath + suffix)); // 直接复制
        }
        else{
            // 使用Thumbnailator进行压缩
            Thumbnails.of(ApPath).scale(1f).outputQuality(imgRate).toFile(uploadPath+MidPath+suffix);
        }
        System.out.println(uploadPrefix);
        System.out.println(MidPath);
        System.out.println(suffix);

        // 返回服务器中的访问路径
        return  uploadPrefix+MidPath+suffix;
    }

    /**
     * 使用流复制文件（适用于复制图片）
     *
     * @param source 原文件
     * @param dest   目标文件
     * @throws IOException 文件读写异常
     */
    public void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);

            // 逐块读写缓冲区
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    /**
     * 使用阿里云图像识别功能，为图像打标
     * 参考: https://help.aliyun.com/zh/viapi/use-cases/general-image-marking-1?spm=a2c4g.11186623.0.0.621b5304H8OqzS
     *
     * @param imagePath 图片的本地文件路径
     * @return 打标结果，包括标签和可信度
     * @throws Exception
     */
    public List<AIResultVO> imageLabel(String imagePath) throws Exception {
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

        // 使用本地文件
        InputStream inputStream = new FileInputStream(new File(imagePath));
        TaggingImageAdvanceRequest taggingImageAdvanceRequest = new TaggingImageAdvanceRequest()
                .setImageURLObject(inputStream);
        RuntimeOptions runtime = new RuntimeOptions();

        try {
            // 获得json格式的识别结果
            TaggingImageResponse taggingImageResponse = client.taggingImageAdvance(taggingImageAdvanceRequest, runtime);
//            System.out.println(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(taggingImageResponse)));

            // 将json转换为实体对象
            Gson gson = new Gson();
            List<AIResultVO> result = gson.fromJson(JSONObject.toJSONString(taggingImageResponse.getBody().getData().tags), new TypeToken<ArrayList<AIResultVO>>(){}.getType());

            return result;
        }catch (TeaException teaException){
            // 获取整体报错信息。
            System.out.println(com.aliyun.teautil.Common.toJSONString(teaException));
            // 获取单个字段。
            System.out.println(teaException.getCode());

            return null;
        }
    }
}
