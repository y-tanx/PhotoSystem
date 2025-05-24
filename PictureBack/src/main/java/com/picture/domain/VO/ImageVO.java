package com.picture.domain.VO;

import com.picture.domain.Image;

import java.util.List;

public class ImageVO {
    private List<Image> images;     // 图片对象列表
    private List<String> previewImageUrL;   // 原图片的URL
    private Integer totalCount;     // 图片总数

    @Override
    public String toString() {
        return "ImageVO{" +
                "images=" + images +
                ", previewImageUrL=" + previewImageUrL +
                ", totalCount=" + totalCount +
                '}';
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<String> getPreviewImageUrL() {
        return previewImageUrL;
    }

    public void setPreviewImageUrL(List<String> previewImageUrL) {
        this.previewImageUrL = previewImageUrL;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
    public ImageVO(){}
    public ImageVO(List<Image> images, List<String> previewImageUrL, Integer totalCount) {
        this.images = images;
        this.previewImageUrL = previewImageUrL;
        this.totalCount = totalCount;
    }
}
