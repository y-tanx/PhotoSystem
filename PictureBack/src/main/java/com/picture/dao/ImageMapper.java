package com.picture.dao;

import com.picture.domain.Image;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface ImageMapper {
    /**
     * 将图片信息插入到image表中
     *
     * @param imageList 图片列表
     */
    void addImages(List<Image> imageList);

    /**
     * 将图片-类型信息插入到imgae-type表中
     *
     * @param imageIds 图片id
     * @param imageType 图片类型
     */
    void addImageType(List<Integer> imageIds, String imageType);
}
