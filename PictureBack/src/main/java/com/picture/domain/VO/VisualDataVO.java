package com.picture.domain.VO;

import com.picture.domain.VisualDataType;

import java.util.List;


public class VisualDataVO {
    public List<VisualDataType> imageType;  // 所有图片的类别-类别数量列表
    public List<String> imageSite;          // 所有图片的拍摄地点
    public List<Integer> SiteNumber;        // 各个地点的数量

    public VisualDataVO() {

    }

    public VisualDataVO(List<VisualDataType> imageType, List<String> imageSite, List<Integer> siteNumber) {
        this.imageType = imageType;
        this.imageSite = imageSite;
        SiteNumber = siteNumber;
    }

    public List<VisualDataType> getImageType() {
        return imageType;
    }

    public void setImageType(List<VisualDataType> imageType) {
        this.imageType = imageType;
    }

    public List<String> getImageSite() {
        return imageSite;
    }

    public void setImageSite(List<String> imageSite) {
        this.imageSite = imageSite;
    }

    public List<Integer> getSiteNumber() {
        return SiteNumber;
    }

    public void setSiteNumber(List<Integer> siteNumber) {
        SiteNumber = siteNumber;
    }

    @Override
    public String toString() {
        return "VisualDataVO{" +
                "imageType=" + imageType +
                ", imageSite=" + imageSite +
                ", SiteNumber=" + SiteNumber +
                '}';
    }
}
