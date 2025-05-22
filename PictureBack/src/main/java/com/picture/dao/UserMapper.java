package com.picture.dao;
import com.picture.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     *用户id查询,用于账号登录
     * @param user
     * @return
     */
    @Select("select id from user where (email=#{userName} or userName = #{userName})and passWord = #{passWord}")
    Integer selectUserId(User user);
}
