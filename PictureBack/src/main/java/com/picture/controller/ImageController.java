package com.picture.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.model.CannedAccessControlList;
import com.picture.domain.Image;
import com.picture.domain.User;
import com.picture.domain.VO.AllTimeTypeVO;
import com.picture.domain.VO.ImageVO;
import com.picture.service.ImageService;
import com.picture.utils.AliyunOssUtil;
import com.picture.utils.EXIFUtil;
import com.picture.utils.TokenUtil;
import java.io.InputStream;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 响应前端有关 图片管理 的请求
 */
@RestController     // 标识该类是一个控制器，用于处理前端发来的请求，并返回响应数据
@RequestMapping("/image")   // 前端统一的请求前缀为/image
public class ImageController {

    @Resource
    private TokenUtil tokenUtil;
    @Resource
    private EXIFUtil exifUtil;
    @Resource
    private ImageService imageService;
    @Resource
    private AliyunOssUtil aliyunOssUtil;

    /**
     * 上传图片，同一批上传的图片的拍摄地点,类型和注释相同
     *
     * @param albumName 相册名
     * @param albumId 相册Id
     * @param imgSite 图片拍摄地点
     * @param imgType 图片类型
     * @param imgDesc 图片注释
     * @param token   用户身份令牌
     * @param multipartFiles 要上传的图片文件
     * @return JSONObject，字段为status，标识图片上传是否成功
     * @throws IOException
     */
    @RequestMapping("/upload")
    public JSONObject upload(String albumName, Integer albumId, String imgSite, String imgType, String imgDesc, String token, @RequestParam("file")MultipartFile[] multipartFiles)
        throws Exception {
        JSONObject json = new JSONObject();

        // 通过token解析出用户信息
        User user = tokenUtil.jwtParser(token);
        if(user == null) {
            json.put("status", "fail");
            return json;
        }

        // 提取用户名和用户Id
        Integer userId = user.getUserId();
        String userName = user.getUserName();

        // 设置图片的拍摄地点 和 注释
        imgSite = imgSite == null ? "其它" : imgSite;
        imgDesc = imgDesc == null ? "请输入文本" : imgDesc;

        // 将所有要上传的图片信息存入集合
        List<Image> imageList = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            // 获取图片的名称和字节数
            String imageName = file.getOriginalFilename();
            long imageSize = file.getSize();

            // 提取图片的拍摄时间/最后修改时间
            // 不能在upload之后执行，因为upload时会将file的流用完
            InputStream inputStream = file.getInputStream();
            Date imageDate = exifUtil.getImageDate(inputStream);
            inputStream.close();
            if (imageDate == null) {
                imageDate = new Date();
            }

            // 将图片文件保存到OSS中，返回图片文件在OSS上的路径
            InputStream inputStream1 = file.getInputStream();
            String imageUrL = aliyunOssUtil.uploadOriginImage(userName, imageName, inputStream1, CannedAccessControlList.PublicRead, null);
            inputStream1.close();

            // 图片压缩，用于缩略图展示
            InputStream inputStream2 = file.getInputStream();
            String compressUrL = aliyunOssUtil.uploadCompressImage(userName, imageName, (float)imageSize, inputStream2, CannedAccessControlList.PublicRead, null);
            inputStream2.close();

            // 创建Image对象，并加入到列表中
            imageList.add(new Image(null, imageName, imageSize, imgSite, imgDesc, imageUrL, compressUrL, imageDate));
        }

        // 在数据库中插入记录
        boolean status = imageService.uploadImage(imageList, userId, albumId, albumName, imgType);

        if(status) {
            json.put("status", "success");
        } else {
            // 上传失败，清除之前上传的文件
            List<String> imageUrls = new ArrayList<>();
            for(Image image : imageList) {
                imageUrls.add(image.getImageUrL());
            }
            aliyunOssUtil.deleteImages(imageUrls);
            json.put("status", "fail");
        }

        return json;
    }

    @RequestMapping("/uploadAI")
    public JSONObject uploadAI(String token, String albumName, Integer albumId, String imgSite, String imgDesc, Integer resNumber, @RequestParam("file")MultipartFile[] multipartFiles ) throws Exception {
        JSONObject json = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user == null) {
            json.put("status", "fail");
            return json;
        }

        // 提取用户名和用户Id
        Integer userId = user.getUserId();
        String userName = user.getUserName();

        // 设置图片的拍摄地点 和 注释
        imgSite = imgSite == null ? "其它" : imgSite;
        imgDesc = imgDesc == null ? "请输入文本" : imgDesc;

        // 将所有要上传的图片信息存入集合
        List<Image> imageList = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            // 获取图片的名称和字节数
            String imageName = file.getOriginalFilename();
            long imageSize = file.getSize();

            // 提取图片的拍摄时间/最后修改时间
            // 不能在upload之后执行，因为upload时会将file的流用完
            InputStream inputStream = file.getInputStream();
            Date imageDate = exifUtil.getImageDate(inputStream);
            inputStream.close();
            if (imageDate == null) {
                imageDate = new Date();
            }

            // 将图片文件保存到OSS中，返回图片文件在OSS上的路径
            InputStream inputStream1 = file.getInputStream();
            String imageUrL = aliyunOssUtil.uploadOriginImage(userName, imageName, inputStream1, CannedAccessControlList.PublicRead, null);
            inputStream1.close();

            // 图片压缩，用于缩略图展示
            InputStream inputStream2 = file.getInputStream();
            String compressUrL = aliyunOssUtil.uploadCompressImage(userName, imageName, (float)imageSize, inputStream2, CannedAccessControlList.PublicRead, null);
            inputStream2.close();

            // 创建Image对象，并加入到列表中
            imageList.add(new Image(null, imageName, imageSize, imgSite, imgDesc, imageUrL, compressUrL, imageDate));
        }

        // 设置标签数量
        int labelCount = resNumber == null ? 1 : resNumber;

        // 使用AI功能打标
        JSONArray data = imageService.uploadImageWithAI(imageList, userId, albumId, albumName, labelCount);

        // 上传失败，清除之前的文件
        if(data == null || data.size() == 0) {
            // 上传失败，清除之前上传的文件
            List<String> imageUrls = new ArrayList<>();
            for(Image image : imageList) {
                imageUrls.add(image.getImageUrL());
            }
            aliyunOssUtil.deleteImages(imageUrls);
            json.put("status", "fail");
            return json;
        }

        // 上传成功，将标签和置信度作为data传递给前端
        json.put("status", "success");
        json.put("data", data);
        return json;
    }

    @RequestMapping("/selectTimeType")
    public JSONObject selectTimeType(String token) {
        JSONObject json = new JSONObject();

        // 通过token解析用户信息
        User user = tokenUtil.jwtParser(token);
        if(user == null) {
            json.put("status", "fail");
            return json;
        }
        Integer userId = user.getUserId();

        // 调用service层查询图片的时间和类型
        AllTimeTypeVO allTimeTypeVO = imageService.selectTimeType(userId);
        json.put("data", allTimeTypeVO);
        json.put("status", "success");
        return json;
    }

    @RequestMapping("/selectAll")
    public JSONObject selectAllImage(String token, Integer currentPage, Integer pageSize) {
        JSONObject json = new JSONObject();

        // 解析token
        User user = tokenUtil.jwtParser(token);
        if(user == null) {
            json.put("status", "fail");
            return json;
        }
        Integer userId = user.getUserId();

        // 调用service层，获得图片
        ImageVO imageVO = imageService.selectAllImage(userId, currentPage, pageSize);
        if(imageVO == null) {
            json.put("status", "fail");
            return json;
        }
        json.put("data", imageVO);
        json.put("status", "success");
        return json;
    }

    @RequestMapping("/selectAllByType")
    public JSONObject selectAllImageByType(String token, String imageType, Integer currentPage, Integer pageSize) {
        JSONObject json = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user == null) {
            json.put("status", "fail");
            return json;
        }

        Integer userId = user.getUserId();
        ImageVO imageVO = imageService.selectImageByType(userId, imageType, currentPage, pageSize);
        if(imageVO == null) {
            json.put("status", "fail");
            return json;
        }
        json.put("data", imageVO);
        json.put("status", "success");
        return json;
    }

    @RequestMapping("/selectAllByTime")
    public JSONObject selectAllImageByTime(String token, String imageDate, Integer currentPage, Integer pageSize) throws ParseException {
        JSONObject json = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user == null) {
            json.put("status", "fail");
            return json;
        }

        Integer userId = user.getUserId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(imageDate);

        ImageVO imageVO = imageService.selectImageByTime(userId, date, currentPage, pageSize);
        if(imageVO == null) {
            json.put("status", "fail");
            return json;
        }
        json.put("data", imageVO);
        json.put("status", "success");
        return json;
    }

    @RequestMapping("/delete")
    public JSONObject deleteImage(String token, @RequestParam("imageId") List<Integer> imageIds) {
        JSONObject json = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user == null) {
            json.put("status", "fail");
            return json;
        }
        Integer userId = user.getUserId();
        imageService.deleteImage(userId, imageIds);
        json.put("status", "success");
        return json;
    }
}