package com.picture.dao;

import com.picture.domain.Recycle;
import com.picture.domain.VO.RecycleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecycleMapper {
    /**
     * 将图片添加到recycle表中
     *
     * @param userId 用户Id
     * @param imageIds 图片Id
     */
    void addImageToRecycle(Integer userId, List<Integer> imageIds);

    /**
     * 查询用户回收站中的所有图片
     *
     * @param userId 用户Id
     */
    List<RecycleVO> selectAll(Integer userId);

    /**
     * 从回收站中删除图片
     *
     * @param userId 用户Id
     * @param imageIds 要删除的图片
     */
    void deleteImageFromRecycle(Integer userId, List<Integer> imageIds);

    /**
     * 查询回收站中超过30天的图片
     *
     * @return
     */
    @Select("select * from  recycle where datediff( date_format(now(),'%Y-%m-%d'),recycleDate) > 30")
    List<Recycle> selectAllOverTime();
}
