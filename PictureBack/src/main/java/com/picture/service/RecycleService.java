package com.picture.service;

import com.alibaba.fastjson.JSONObject;
import com.picture.domain.Recycle;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RecycleService {
    /**
     * 查询用户回收站中的所有图片
     *
     * @param userId 用户Id
     * @return
     */
    JSONObject selectAll(Integer userId);

    /**
     * 从回收站中恢复图片
     *
     * @param userId 用户Id
     * @param imageIds 要恢复的图片Id
     */
    void recoverImage(Integer userId, List<Integer> imageIds);

    /**
     * 从回收站中彻底删除图片
     *
     * @param userId 用户Id
     * @param imageIds 图片Id
     */
    void deleteImage(Integer userId, List<Integer> imageIds);

    /**
     * 查询所有已超时（过期）未恢复的回收站记录
     * 用于后台定时任务或系统清理逻辑
     * @return 回收站中过期图片的列表
     */
    List<Recycle> selectAllOverTime();
}
