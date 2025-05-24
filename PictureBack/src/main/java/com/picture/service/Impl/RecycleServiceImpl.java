package com.picture.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.picture.dao.AlbumMapper;
import com.picture.dao.ImageMapper;
import com.picture.dao.RecycleMapper;
import com.picture.dao.UserMapper;
import com.picture.domain.Image;
import com.picture.domain.Recycle;
import com.picture.domain.VO.RecycleVO;
import com.picture.service.RecycleService;
import com.picture.utils.FileServerUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecycleServiceImpl implements RecycleService {
    @Resource
    private RecycleMapper recycleMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private AlbumMapper albumMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private FileServerUtil fileServerUtil;

    @Override
    public JSONObject selectAll(Integer userId) {
        JSONObject res = new JSONObject();

        // 调用DAO层，查询回收站中的图片信息
        List<RecycleVO> images = recycleMapper.selectAll(userId);

        // 图片URL列表
        List<String> imageUrLs = new ArrayList<String>();
        for (RecycleVO image : images) {
            imageUrLs.add(image.getCompressUrL());
        }

        res.put("images", images);
        res.put("previewImageUrL", imageUrLs);
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoverImage(Integer userId, List<Integer> imageIds) {
        // 在recycle表中删除图片记录
        recycleMapper.deleteImageFromRecycle(userId, imageIds);

        // 在user-image表中添加图片记录
        userMapper.addUserImage(userId, imageIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(Integer userId, List<Integer> imageIds) {
        List<Image> images = imageMapper.selectImageByIds(imageIds);

        // 从recycle表中删除
        recycleMapper.deleteImageFromRecycle(userId, imageIds);

        // 从image表中删除
        imageMapper.deleteImage(imageIds);

        // 从image-type表中删除
        imageMapper.deleteImageType(imageIds);

        // 从album-image表中删除
        albumMapper.deleteAlbumImageByImgId(imageIds);

        // 从磁盘中删除图片
        for(Image image : images){
            fileServerUtil.deleteServe(image.getImageUrL());
            fileServerUtil.deleteServe(image.getCompressUrL());
        }
    }

    @Override
    public List<Recycle> selectAllOverTime() {
        List<Recycle> recycles = recycleMapper.selectAllOverTime();

        return recycles;
    }

}
