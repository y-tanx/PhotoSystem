package com.picture.dao;

import com.picture.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> selectAllUser();
    /**
     * 用户注册
     * @param user
     * @return
     */
    @Insert("insert into user values (null,#{userName},#{passWord},#{sex},#{email},#{phone},#{city},#{birthday},#{capacity},#{avatar})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int add(User user);
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

    /**
     * 用户查询
     * @param userId
     * @return
     */
    @Select("select * from user where id=#{userId}")
    @ResultType(User.class)
    @Results(id="UserResultMap" ,value = {
            @Result(property = "userId",column = "id"),
    })
    User selectUserById(int userId);

    /**
     * 用户账号查询，防止重复注册
     * @param UserName
     * @return
     */
    @Select("select userName from user where userName = #{userName}")
    String selectUserName(String UserName);

    /**
     * 根据userName更改用户的密码
     *
     * @param userName
     */
    @Update("update user set password = #{password} where userName = #{userName}")
    void resetPasswordByUserName(String userName, String password);
}
