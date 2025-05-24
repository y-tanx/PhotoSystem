package com.picture.service;

import com.picture.domain.Image;
import com.picture.domain.VO.AllTimeTypeVO;
import com.picture.domain.VO.ImageVO;

import java.util.Date;
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

    /**
     * 获得用户所有图片的时间和类别信息
     *
     * @param userId 用户Id
     * @return 传输给用户
     */
    AllTimeTypeVO selectTimeType(Integer userId);

    /**
     * 按页获得用户的所有图片
     *
     * @param userId 用户Id
     * @param currentPage 当前页
     * @param pageSize 每页可以显示的照片数量
     * @return 用户的所有图片
     */
    ImageVO selectAllImage(Integer userId, Integer currentPage, Integer pageSize);


    /**
     * 按页获得用户指定类别的图片
     *
     * @param userId 用户Id
     * @param imageType 图片类别
     * @param currentPage 当前页码
     * @param pageSize 每页可以展示的图片数量
     * @return
     */
    ImageVO selectImageByType(Integer userId, String imageType, Integer currentPage, Integer pageSize);

    /**
     * 按页获得用户指定日期的图片
     *
     * @param userId 用户Id
     * @param imageDate 图片日期
     * @param currentPage 当前页码
     * @param pageSize 每页可以展示的图片数量
     * @return
     */
    ImageVO selectImageByTime(Integer userId, Date imageDate, Integer currentPage, Integer pageSize);


}
