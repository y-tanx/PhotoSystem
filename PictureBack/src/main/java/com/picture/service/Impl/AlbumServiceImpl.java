package com.picture.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.picture.dao.AlbumMapper;
import com.picture.domain.Album;
import com.picture.domain.Image;
import com.picture.domain.Operation;
import com.picture.domain.VO.AlbumImageVO;
import com.picture.domain.VO.PartAlbumVO;
import com.picture.service.AlbumService;
import com.picture.utils.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {
    @Resource
    AlbumMapper albumMapper;
    @Resource
    DateUtil dateUtil;

    // 默认相册封面图片路径
    private String defaultAlbum = "/static/album/albumImg.png";

    @Override
    @Transactional(rollbackFor = Exception.class)   // 事务注解: 在方法执行时，Spring会开启一个数据库事务
    public void addAlbum(HttpServletRequest req, String albumName, Integer userId) {
        // 检查相册名是否已存在
        String existingAlbum = albumMapper.selectAlbumByName(albumName, userId);
        if(existingAlbum != null) {
            throw new RuntimeException("相册名已存在");
        }

        Album album = new Album();
        album.setAlbumImg(defaultAlbum);
        album.setUserId(userId);
        album.setAlbumName(albumName);
        System.out.println(album);

        // 将相册信息写入数据库
        albumMapper.addAlbum(album);
    }



    @Override
    public List<PartAlbumVO> selectAllAlbum(Integer userId) {
        if(userId==null){
            return null;
        }
        List<PartAlbumVO> partAlbumVO = albumMapper.selectAllAlbum(userId); // 返回所有相册基本信息(封面,名字,图片数量)
        return partAlbumVO;
    }

    @Override
    public void addImageToAlbum(HttpServletRequest req,Integer albumId, List<Integer> imageId,Integer userId) {
        // 将图片上传到相册中
        albumMapper.addAlbumImage(albumId,imageId);

        // 获取相册名称，用于日志记录
        String albumName = albumMapper.selectAlbum(albumId);
    }

    @Override
    public void removeImageToAlbum(HttpServletRequest req,Integer albumId, List<Integer> imageId,Integer userId) {
        System.out.println(albumId);
        System.out.println(imageId);

        // 删除相册中指定的照片
        albumMapper.removeAlbumImage(albumId,imageId);

        // 获取相册名称，用于日志记录
        String albumName = albumMapper.selectAlbum(albumId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlbum(HttpServletRequest req,List<Integer> albumIds,Integer userId) {
        // 删除相册记录
        albumMapper.deleteAlbum(albumIds);

        // 删除与相册关联的图片
        albumMapper.deleteAlbumImageByAlbum(albumIds);
    }

    @Override
    public void setAlbumCover( Integer albumId, Integer imageId) {
        albumMapper.uploadAlbum(albumId,imageId);   // 设置相册的封面图片
    }

    @Override
    public JSONObject selectAlbumImage(Integer albumId) throws ParseException {
        List<Image> images = albumMapper.selectAlbumImage(albumId); // 相册中所有的图片
        List<Date> dates = albumMapper.selectAlbumImageTime(albumId);   // 图片的所有日期

        List<AlbumImageVO> albumImageVOS = new ArrayList<>();
        List<String> previewList = new ArrayList<>();

        // 按照日期将图片分组
        for(int i=0;i<dates.size();i++){
            List<Image> img = new ArrayList<>();
            for(int j=0;j<images.size();j++){
                // 日期为空 或 当前照片的日期等于当前遍历的日期
                if(dates.get(i)==null||dates.get(i).equals(images.get(j).getImageDate())){
                    img.add(images.get(j));  // 添加到该日期分组中
                    previewList.add(images.get(j).getImageUrL());    // 添加到预览图列表中

                    // 从images中移除第j个image
                    images.remove(j);
                    j--;
                }
            }

            // 构造日期字符串
            String time = "";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            if(dates.get(i)!=null) {
                String format = dateFormat.format(dates.get(i));
                String week = dateUtil.getWeek(dates.get(i));    // 星期
                time = format+week;
            }
            else{
                time="其它时间";
            }
            // 将当前遍历的日期 和 该日期对应的一组图片 封装到albumImageVO中，用于传递给前端
            albumImageVOS.add(new AlbumImageVO(time,img));
        }

        // 将分组后的图片信息和预览图URL封装为JSON返回
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("images",albumImageVOS);
        jsonObject.put("previewImageUrL",previewList);
        return jsonObject;
    }
}
