package com.picture.dao;

import com.picture.domain.Image;
import com.picture.domain.VO.ImageVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.Date;
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

    /**
     * 根据用户Id，查询用户所有图片的时间信息
     *
     * @param userId 用户Id
     */
    List<Date> selectAllImageTime(Integer userId);

    /**
     * 根据用户Id，查询用户所有图片的类别信息
     *
     * @param userId 用户Id
     */
    List<String> selectAllImageType(Integer userId);

    /**
     * 查询用户的所有图片
     * @param userId
     * @return
     */
    List<Image> selectAllImage(Integer userId);

    /**
     * 查询用户所有图片的数量
     *
     * @param userId 用户Id
     * @return
     */
    Integer selectImageCount(Integer userId);

    /**
     * 根据用户Id和图片类别查询图片
     *
     * @param userId 用户Id
     * @param imageType 图片类别
     * @return
     */
    List<Image> selectImageByType(Integer userId, String imageType);

    /**
     * 根据用户Id和类别查询的图片数量
     *
     * @param userId 用户Id
     * @param imageType 图片类别
     * @return
     */
    Integer selectImageCountByType(Integer userId, String imageType);

    /**
     * 根据用户Id和时间查询图片
     *
     * @param userId 用户Id
     * @param imageDate 图片日期
     * @return
     */
    List<Image> selectImageByTime(Integer userId, Date imageDate);

    /**
     * 根据用户Id和时间查询的图片数量
     *
     * @param userId 用户Id
     * @param imageDate 图片日期
     * @return
     */
    Integer selectImageCountByTime(Integer userId, Date imageDate);
}
