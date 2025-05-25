package com.picture.domain.VO;

import com.picture.domain.Image;

import java.util.List;

public class AlbumImageVO {
    private String time;    // 拍摄时间
    List<Image> image;      // 该时间对应的图片

    public AlbumImageVO(){}
    public AlbumImageVO(String time, List<Image> image) {
        this.time = time;
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Image> getImage() {
        return image;
    }

    public void setImage(List<Image> image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "AlbumImageVO{" +
                "time='" + time + '\'' +
                ", images=" + image +
                '}';
    }
}
