package com.picture.controller;

import com.alibaba.fastjson.JSONObject;
import com.picture.domain.Image;
import com.picture.domain.User;
import com.picture.service.Impl.ImageServiceImpl;
import com.picture.utils.EXIFUtil;
import com.picture.utils.FileServerUtil;
import com.picture.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 响应前段有关 图片管理 的请求
 */
@RestController     // 标识该类是一个控制器，用于处理前端发来的请求，并返回响应数据
@RequestMapping("/image")   // 前端统一的请求前缀为/image
public class ImageController {

    @Resource
    private TokenUtil tokenUtil;
    @Resource
    private FileServerUtil fileServerUtil;
    @Resource
    private EXIFUtil exifUtil;
    @Resource
    private ImageServiceImpl imageServiceImpl;

    @RequestMapping("/upload")
    public JSONObject upload(String albumName, Integer albumId, String imgSite, String imgType, String imgDesc, String token, @RequestParam("file")MultipartFile[] multipartFiles) throws IOException {
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

            // 将图片文件保存到userName目录下，返回图片文件在Web上的路径
            String imageUrL = fileServerUtil.uploadServe("img", userName, file);
            String imagePath = fileServerUtil.ServPathToAP(imageUrL);   // 这是本地存储路径

            // 提取图片的拍摄时间/最后修改时间
            Date imageDate = exifUtil.getImageDate(new File(imagePath));    // 根据图片路径，获得图片的拍摄时间/最后修改时间
            if(imageDate == null)
            {
                imageDate = new Date();
            }

            // 压缩图片，返回压缩后图片的路径
            String compressUrL = fileServerUtil.CompressImage(imagePath, imageName, (float) imageSize);

            // 创建Image对象，并加入到列表中
            imageList.add(new Image(null, imageName, imageSize, imgSite, imgDesc, imageUrL, compressUrL, imageDate));
        }

        // 在数据库中插入记录
        boolean status = imageServiceImpl.uploadImage(imageList, userId, albumId, albumName, imgType);

        if(status) {
            json.put("status", "success");
        } else {
            // 上传失败，清除之前上传的文件
            for (Image image : imageList) {
                String imageUrL = image.getImageUrL();
                fileServerUtil.deleteServe(imageUrL);
            }
            json.put("status", "fail");
        }

        return json;
    }

}
