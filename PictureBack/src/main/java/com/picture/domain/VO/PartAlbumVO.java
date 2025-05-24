package com.picture.domain.VO;

public class PartAlbumVO {
    private Integer albumId;    // 相册ID
    private String albumName;   // 相册名
    private String albumImg;    // 相册封面
    private Integer imageNumber;    // 相册包含的图片数量

    public PartAlbumVO(){

    }
    public PartAlbumVO(Integer albumId, String albumName, String albumImg, Integer imageNumber) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.albumImg = albumImg;
        this.imageNumber = imageNumber;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumImg() {
        return albumImg;
    }

    public void setAlbumImg(String albumImg) {
        this.albumImg = albumImg;
    }

    public Integer getImageNumber() {
        return imageNumber;
    }

    public void setImageNumber(Integer imageNumber) {
        this.imageNumber = imageNumber;
    }

    @Override
    public String toString() {
        return "PartAlbumVO{" +
                "albumId=" + albumId +
                ", albumName='" + albumName + '\'' +
                ", albumImg='" + albumImg + '\'' +
                ", imageNumber=" + imageNumber +
                '}';
    }
}