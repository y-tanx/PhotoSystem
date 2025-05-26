package com.picture.domain;

public class VisualDataSite {
    private String imageSite; // 图片拍摄地点
    private Integer number;   // 该地点的数量

    public VisualDataSite(){}
    public VisualDataSite(String imageSite, Integer number) {
        this.imageSite = imageSite;
        this.number = number;
    }

    public String getImageSite() {
        return imageSite;
    }

    public void setImageSite(String imageSite) {
        this.imageSite = imageSite;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "VisualDataSite{" +
                "imageSite='" + imageSite + '\'' +
                ", number=" + number +
                '}';
    }
}
