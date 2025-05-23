package com.picture.dao;

import com.picture.domain.Album;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface AlbumMapper {
    /**
     * 创建相册
     * @param album 新相册
     */
    void addAlbum(Album album);
    /**
     * 将相册Id-图片Id 添加到album-image表中
     *
     * @param albumId 相册Id
     * @param imageIds 图片Id列表
     */
    void addAlbumImage(int albumId, List<Integer> imageIds);

}
