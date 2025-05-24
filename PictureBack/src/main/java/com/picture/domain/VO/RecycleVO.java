package com.picture.domain.VO;

public class RecycleVO {
    private Integer imageId;    // 图片ID
    private String imageName;   // 图片名
    private String compressUrL; // 压缩的URL
    private Integer day;        // 删除日期

    @Override
    public String toString() {
        return "RecycleVO{" +
                "imageId=" + imageId +
                ", imageName='" + imageName + '\'' +
                ", compressUrL='" + compressUrL + '\'' +
                ", day=" + day +
                '}';
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

    public String getCompressUrL() {
        return compressUrL;
    }

    public void setCompressUrL(String compressUrL) {
        this.compressUrL = compressUrL;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public RecycleVO() {

    }

    public RecycleVO(Integer imageId, String imageName, String compressUrL, Integer day) {
        this.imageId = imageId;
        this.imageName = imageName;
        this.compressUrL = compressUrL;
        this.day = day;
    }
}
