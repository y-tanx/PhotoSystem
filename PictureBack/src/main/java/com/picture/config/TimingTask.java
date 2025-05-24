package com.picture.config;

import com.picture.domain.Recycle;
import com.picture.service.RecycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 定时任务配置：每天凌晨 2 点删除回收站中已过期的图片
 */
@Configuration
@EnableScheduling
public class TimingTask {

    private static final Logger logger = LoggerFactory.getLogger(TimingTask.class);

    @Resource
    private RecycleService recycleService;

    /**
     * 每天凌晨 2 点执行回收站清理任务
     */
    @Scheduled(cron = "0 0 2 * * ?")
    private void configureTasks() {
        List<Recycle> recycles = recycleService.selectAllOverTime();

        if (recycles == null || recycles.isEmpty()) {
            logger.info("回收站无过期图片，无需清理。时间：{}", LocalDateTime.now());
            return;
        }

        // 将过期图片按 userId 分组后批量删除，提高效率
        Map<Integer, List<Integer>> userImageMap = new HashMap<>();
        for (Recycle recycle : recycles) {
            userImageMap
                    .computeIfAbsent(recycle.getUserId(), k -> new ArrayList<>())
                    .add(recycle.getImageId());
        }

        for (Map.Entry<Integer, List<Integer>> entry : userImageMap.entrySet()) {
            Integer userId = entry.getKey();
            List<Integer> imageIds = entry.getValue();
            recycleService.deleteImage(userId, imageIds);
        }

        logger.info("定时任务执行成功，已清理回收站中过期图片。执行时间：{}", LocalDateTime.now());
    }
}
