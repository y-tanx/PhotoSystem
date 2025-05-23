package com.picture.service.Impl;

import com.picture.dao.AlbumMapper;
import com.picture.domain.Album;
//import com.picture.domain.Image;
import com.picture.domain.Operation;
//import com.picture.domain.VO.AlbumImageVO;
import com.picture.domain.VO.PartAlbumVO;
import com.picture.service.AlbumService;
import com.picture.service.RecordService;
import com.picture.utils.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {
    @Resource
    AlbumMapper albumMapper;
    @Resource
    RecordService recordService;
    @Resource
    DateUtil dateUtil;

    // 默认相册封面图片路径
    private String defaultAlbum = "/static/album/albumImg.png";

    @Override
    @Transactional(rollbackFor = Exception.class)   // 事务注解: 在方法执行时，Spring会开启一个数据库事务
    public void addAlbum(HttpServletRequest req, String albumName, Integer userId) {
        Album album = new Album();
        album.setAlbumImg(defaultAlbum);
        album.setUserId(userId);
        album.setAlbumName(albumName);
        System.out.println(album);

        // 将相册信息写入数据库
        albumMapper.addAlbum(album);

        // 添加操作记录，创建相册
        recordService.addRecord(req, Operation.createAlbum.getName()+"\""+albumName+"\"", 1,userId);
    }

    @Override
    public List<PartAlbumVO> selectAllAlbum(Integer userId) {
        if(userId==null){
            return null;
        }
        List<PartAlbumVO> partAlbumVO = albumMapper.selectAllAlbum(userId); // 返回所有相册基本信息(封面,名字,图片数量)
        return partAlbumVO;
    }
}
