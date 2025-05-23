package com.picture.domain;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 照片实体类
 */
public class Image {
    private Integer imageId;    // 图片ID
    private String imageName;   // 图片名
    private Long imageSize;   // 图片大小
    private String imageSite;   // 图片拍摄地址
    private String imageDesc;   // 图片注释
    private String imageUrL;    // 原图片的URL
    private String compressUrL; // 压缩图片的URL
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField(format = "yyyy-MM-dd")
    private Date imageDate;     // 图片拍摄时间

    @Override
    public String toString() {
        return "Image{" +
                "imageId=" + imageId +
                ", imageName='" + imageName + '\'' +
                ", imageSize=" + imageSize +
                ", imageSite='" + imageSite + '\'' +
                ", imageUrL='" + imageUrL + '\'' +
                ", imageDesc='" + imageDesc + '\'' +
                ", compressUrL='" + compressUrL + '\'' +
                ", imageDate=" + imageDate +
                '}';
    }

    public String getImageDesc() {
        return imageDesc;
    }

    public void setImageDesc(String imageDesc) {
        this.imageDesc = imageDesc;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Long getImageSize() {
        return imageSize;
    }

    public void setImageSize(Long imageSize) {
        this.imageSize = imageSize;
    }

    public String getImageSite() {
        return imageSite;
    }

    public void setImageSite(String imageSite) {
        this.imageSite = imageSite;
    }

    public String getImageUrL() {
        return imageUrL;
    }

    public void setImageUrL(String imageUrL) {
        this.imageUrL = imageUrL;
    }

    public String getCompressUrL() {
        return compressUrL;
    }

    public void setCompressUrL(String compressUrL) {
        this.compressUrL = compressUrL;
    }

    public Date getImageDate() {
        return imageDate;
    }

    public void setImageDate(Date imageDate) {
        this.imageDate = imageDate;
    }

    public Image(Integer imageId, String imageName, Long imageSize, String imageSite, String imageDesc, String imageUrL, String compressUrL, Date imageDate) {
        this.imageId = imageId;
        this.imageName = imageName;
        this.imageSize = imageSize;
        this.imageSite = imageSite;
        this.imageUrL = imageUrL;
        this.imageDesc = imageDesc;
        this.compressUrL = compressUrL;
        this.imageDate = imageDate;
    }

    public Image() {

    }
}