package com.picture.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 将用户id-图片id插入到user-image表中
     *
     * @param userId 用户Id
     * @param imageIds 图片的Id列表
     */
    void addUserImage(Integer userId, List<Integer> imageIds);
}
