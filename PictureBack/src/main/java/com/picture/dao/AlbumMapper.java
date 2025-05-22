package com.picture.dao;

import com.picture.domain.Album;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface AlbumMapper {
    /**
     * 创建相册
     * @param album
     */
    @Insert("insert into album values (null,#{albumName},#{albumTheme},#{albumContext},#{albumImg},#{backgroundImage},#{albumMusic},#{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "albumId")
    void addAlbum(Album album);
}
