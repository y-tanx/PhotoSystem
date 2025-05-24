package com.picture.service.Impl;

import com.github.pagehelper.PageHelper;
import com.picture.dao.AlbumMapper;
import com.picture.dao.ImageMapper;
import com.picture.dao.RecycleMapper;
import com.picture.dao.UserMapper;
import com.picture.domain.Image;
import com.picture.domain.VO.AllTimeTypeVO;
import com.picture.domain.VO.ImageVO;
import com.picture.service.ImageService;
import org.apache.tomcat.Jar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private AlbumMapper albumMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RecycleMapper recycleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)   // 事务控制
    public boolean uploadImage(List<Image> imageList, Integer userId, Integer albumId, String albumName, String imageType) {
        if(imageList == null || imageList.size() == 0 || userId == null || albumId == null || albumName == null || imageType == null) {
            return false;
        }

        // 1. 插入image表
        imageMapper.addImages(imageList);

        // 图片id集合
        List<Integer> imageIds = new ArrayList<Integer>();
        for (Image image : imageList) {
            imageIds.add(image.getImageId());
        }

        // 2. 插入到image-type表
        imageMapper.addImageType(imageIds, imageType);

        // 3. 插入到user-image表
        userMapper.addUserImage(userId, imageIds);

        // 4. 插入到album-image表
        albumMapper.addAlbumImage(albumId, imageIds);

        return true;
    }

    @Override
    public AllTimeTypeVO selectTimeType(Integer userId) {
        // 查询用户图片的所有日期
        List<Date> dates = imageMapper.selectAllImageTime(userId);

        // 查询用户图片的所有类型
        List<String> types = imageMapper.selectAllImageType(userId);

        // 封装类型和日期信息
        AllTimeTypeVO allTimeTypeVO = new AllTimeTypeVO(types, dates);
        return allTimeTypeVO;
    }

    @Override
    public ImageVO selectAllImage(Integer userId, Integer currentPage, Integer pageSize) {
        if (userId == null || currentPage == null || pageSize == null) {
            return null;
        }

        PageHelper.startPage(currentPage, pageSize);    // 启用分页
        List<Image> imageList = imageMapper.selectAllImage(userId);

        // 获得图片的URL
        List<String> imageUrLs = new ArrayList<>();
        for (Image image : imageList) {
            imageUrLs.add(image.getImageUrL());
        }
        Integer count = imageMapper.selectImageCount(userId);

        // 封装用户的所有图片对象，图片的URL和图片总数
        ImageVO imageVO = new ImageVO(imageList, imageUrLs, count);
        return imageVO;
    }

    @Override
    public ImageVO selectImageByType(Integer userId, String imageType, Integer currentPage, Integer pageSize) {
        if (userId == null || currentPage == null || pageSize == null) {
            return null;
        }
        PageHelper.startPage(currentPage, pageSize);

        // 按类别查询图片
        List<Image> images = imageMapper.selectImageByType(userId, imageType);
        List<String> imageUrLs = new ArrayList<>();

        // 获得图片的URL
        for (Image image : images) {
            imageUrLs.add(image.getImageUrL());
        }

        // 图片的数量
        Integer count = imageMapper.selectImageCountByType(userId, imageType);
        ImageVO imageVO = new ImageVO(images, imageUrLs, count);
        return imageVO;
    }

    @Override
    public ImageVO selectImageByTime(Integer userId, Date imageDate, Integer currentPage, Integer pageSize) {
        if (userId == null || currentPage == null || pageSize == null) {
            return null;
        }
        PageHelper.startPage(currentPage, pageSize);
        List<Image> images = imageMapper.selectImageByTime(userId, imageDate);
        List<String> imageUrLs = new ArrayList<>();
        for (Image image : images) {
            imageUrLs.add(image.getImageUrL());
        }

        Integer count = imageMapper.selectImageCountByTime(userId, imageDate);
        ImageVO imageVO = new ImageVO(images, imageUrLs, count);
        return imageVO;
    }

    @Override
    public void deleteImage(Integer userId, List<Integer> imageIds) {
        // 在user-image中删除图片
        userMapper.deleteUserImage(userId, imageIds);

        // 在image-type中删除图片
        imageMapper.deleteImageType(imageIds);

        // 在album-image中删除图片
        albumMapper.deleteAlbumImageByImgId(imageIds);

        // 将图片加入到recycle中
        recycleMapper.addImageToRecycle(userId, imageIds);
    }
}
