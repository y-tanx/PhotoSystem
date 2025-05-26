package com.picture.dao;

import com.picture.domain.VisualDataSite;
import com.picture.domain.VisualDataType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VisualMapper {
    /**
     * 查询用户所有图片的类别信息
     *
     * @param userId
     * @return
     */
    List<VisualDataType> selectVisualDataType(Integer userId);

    /**
     * 查询用户所有图片的拍摄地点信息
     *
     * @param userId
     * @return
     */
    List<VisualDataSite> selectVisualDataSite(Integer userId);

    /**
     * 查询图片的总大小
     *
     * @param userId 用户Id
     * @return
     */
    Integer selectImageTotalSize(Integer userId);
}
