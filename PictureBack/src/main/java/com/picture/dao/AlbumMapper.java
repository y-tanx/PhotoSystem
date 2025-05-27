package com.picture.dao;

import com.picture.domain.Album;
import com.picture.domain.Image;
import com.picture.domain.VO.PartAlbumVO;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface AlbumMapper {
    /**
     * 创建相册
     * @param album
     */
    void addAlbum(Album album);

    /**
     * 相册名查询，防止重复创建
     * @param albumName,userId
     * @return
     */
    @Select("select albumName from album where albumName = #{albumName} and userId = #{userId}")
    String selectAlbumByName(@Param("albumName") String albumName, @Param("userId") Integer userId);

    @Update("update album set albumImg = (select compressUrL from image where image.id=#{imageId}) where album.id =#{albumId} ")
    void uploadAlbum(Integer albumId,Integer imageId);

    @Select("select albumName from album where id=#{albumId}")
    String selectAlbum(Integer albumId);
    /**
     * 将相册Id-图片Id 添加到album-image表中
     *
     * @param albumId 相册Id
     * @param imageIds 要上传的图片Id
     */
    void addAlbumImage(int albumId, List<Integer> imageIds);
    /**
     * 从album-image表中删除相册的一组图片
     *
     * @param imageIds 要删除的图片Id
     */
    void deleteAlbumImageByImgId(List<Integer> imageIds);

    /**
     * 根据相册id删除中间表
     * @param albumIds
     */
    void deleteAlbumImageByAlbum(@Param("list") List<Integer> albumIds);
    /**
     * 移除相册图片中间表
     * @param imageIds
     */
    void removeAlbumImage(int albumId,@Param("list") List<Integer> imageIds);

    /**
     * 根据id删除相册
     * @param albumIds
     */
    void deleteAlbum(@Param("list") List<Integer> albumIds);

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

    /**
     * 根据相册id查询所有图片
     * @param albumId
     * @return
     */
    @Select("select id as imageId ,compressUrl,imageUrL,imageDate,imageName, imageSite,imageDesc from image  where id in (select imageId from album_image where albumId=#{albumId}) ")
    List<Image> selectAlbumImage(Integer albumId);

    /**
     * 根据相册id查询所有时间段
     * @param albumId
     * @return
     */
    @Select("select imageDate from image where id in (select imageId from album_image where albumId=#{albumId}) group by imageDate order by imageDate desc ")
    List<Date> selectAlbumImageTime(Integer albumId);
}