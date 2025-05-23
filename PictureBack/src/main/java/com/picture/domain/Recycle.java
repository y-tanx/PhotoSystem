package com.picture.domain;

import java.util.Date;

public class Recycle {
    private Integer id;         // 事务ID
    private Integer userId;     // 用户ID
    private Integer imageId;    // 图片ID
    private Date recycleDate;   // 回收日期

    public Recycle() {
    }

    @Override
    public String toString() {
        return "Recycle{" +
                "id=" + id +
                ", userId=" + userId +
                ", imageId=" + imageId +
                ", recycleDate=" + recycleDate +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Date getRecycleDate() {
        return recycleDate;
    }

    public void setRecycleDate(Date recycleDate) {
        this.recycleDate = recycleDate;
    }

    public Recycle(Integer id, Integer userId, Integer imageId, Date recycleDate) {
        this.id = id;
        this.userId = userId;
        this.imageId = imageId;
        this.recycleDate = recycleDate;
    }
}
