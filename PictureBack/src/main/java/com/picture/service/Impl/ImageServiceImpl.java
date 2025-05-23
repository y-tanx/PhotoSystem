package com.picture.service.Impl;

import com.picture.dao.AlbumMapper;
import com.picture.dao.ImageMapper;
import com.picture.dao.UserMapper;
import com.picture.domain.Image;
import com.picture.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private AlbumMapper albumMapper;
    @Resource
    private UserMapper userMapper;

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
}
