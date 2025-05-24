package com.picture.dao;

import com.picture.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    /**
     * 从user-image表中删除用户的一组图片
     *
     * @param userId 用户Id
     * @param imageIds 要删除的图片的Id
     */
    void deleteUserImage(Integer userId, List<Integer> imageIds);

    /**
     *用户id查询,用于账号登录
     * @param user
     * @return
     */
    @Select("select id from user where (email=#{userName} or userName = #{userName})and passWord = #{passWord}")
    Integer selectUserId(User user);
}
