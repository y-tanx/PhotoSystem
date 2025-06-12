package com.picture.service;
import com.alibaba.fastjson.JSONObject;
import com.picture.domain.VO.PartAlbumVO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

@Service
public interface AlbumService {
    /**
     * 添加一个新相册，并记录日志
     *
     * @param req        Http 请求对象（用于记录操作信息）
     * @param albumName  相册名称
     * @param userId     用户 ID
     */
    void addAlbum(HttpServletRequest req,String albumName, Integer userId);
    /**
     * 设置相册封面为指定图片
     *
     * @param albumId 相册 ID
     * @param imageId 图片 ID
     */
    void setAlbumCover(Integer albumId,Integer imageId);
    /**
     * 查询用户的所有相册简要信息
     *
     * @param userId 用户 ID
     * @return 返回包含相册 ID、封面、名称等信息的列表
     */
    List<PartAlbumVO> selectAllAlbum(Integer userId);
    /**
     * 从相册中移除图片，并记录操作日志
     *
     * @param req     请求对象
     * @param albumId 相册 ID
     * @param imageId 图片 ID 列表
     * @param userId  用户 ID
     */
    void removeImageFromAlbum(HttpServletRequest req,Integer albumId, List<Integer> imageId,Integer userId);
    /**
     * 将图片添加到相册，并记录操作日志
     *
     * @param req     请求对象
     * @param albumId 相册 ID
     * @param imageId 图片 ID 列表
     * @param userId  用户 ID
     */
    void addImageToAlbum(HttpServletRequest req,Integer albumId,List<Integer> imageId,Integer userId);
    /**
     * 删除相册及其关联图片，并记录日志（带事务控制）
     *
     * @param req      请求对象
     * @param albumIds 要删除的相册 ID 列表
     * @param userId   用户 ID
     */
    void deleteAlbum(HttpServletRequest req,List<Integer> albumIds, Integer userId);
    /**
     * 查询某个相册中所有图片，按日期分组，并生成预览 URL 列表
     *
     * @param albumId 相册 ID
     * @return 返回一个 JSONObject，包含按时间分组的图片列表和预览图 URL 列表
     * @throws ParseException 日期转换异常
     */
    JSONObject selectAlbumImage(Integer albumId) throws ParseException;
}
