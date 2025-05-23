package com.picture.service;

import com.picture.domain.Image;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ImageService {
    /**
     * 将图片信息添加到数据库中
     * 上传图片需要在image, image-type, user-image, album-image中添加记录
     *
     * @param imageList 要上传的图片列表
     * @param userId    用户Id
     * @param albumId   相册Id
     * @param albumName 相册名
     * @param imageType 图片类型
     * @return 是否插入成功
     */
    boolean uploadImage(List<Image> imageList, Integer userId, Integer albumId, String albumName, String imageType);
}
