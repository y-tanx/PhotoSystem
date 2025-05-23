package com.picture.dao;

import com.picture.domain.Album;
import com.picture.domain.VO.PartAlbumVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AlbumMapper {
    /**
     * 创建相册
     * @param album
     */
    @Insert("insert into album values (null,#{albumName},#{albumTheme},#{albumContext},#{albumImg},#{backgroundImage},#{albumMusic},#{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "albumId")
    void addAlbum(Album album);
    /**
     * 根据id删除相册
     * @param albumIds
     */
    void deleteAlbum(@Param("list") List<Integer> albumIds);

    /**
     * 根据相册id删除中间表
     * @param albumIds
     */
    void deleteAlbumImageByAlbum(@Param("list") List<Integer> albumIds);
    /**
     * 查询用户相册部分信息
     * @param userId
     * @return
     */
    @Select(" select album.id,albumName,albumImg,count(DISTINCT album_image.imageId) as imageNumber from album  left join album_image on album_image.albumId = album.id  where album.userId=#{userId}  group by album.id")
    @Results(id="AlbumResultMap" ,value = {
            @Result(property = "albumId",column = "id"),
    })
    List<PartAlbumVO> selectAllAlbum(Integer userId);

}
