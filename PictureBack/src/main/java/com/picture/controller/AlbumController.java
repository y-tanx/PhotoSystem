package com.picture.controller;

import com.alibaba.fastjson.JSONObject;
import com.picture.domain.VO.PartAlbumVO;
import com.picture.domain.User;
import com.picture.service.AlbumService;
import com.picture.utils.RedisUtil;
import com.picture.utils.TokenUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

@RestController     // 标识该类是一个控制器，用于处理前端发来的请求，并返回响应数据（通常是JSON）
@RequestMapping("/album")   // 定义该类控制器的统一请求前缀为/album
public class AlbumController {
    @Resource   // 将对应的Bean注入到变量中，相当于Spring自动new了这个对象
    AlbumService albumService;
    @Resource
    TokenUtil tokenUtil;
    @Resource
    RedisUtil redisUtil;

    @RequestMapping("/selectAlbumName") // 定义具体接口路径
    // 当前端发起请求访问/album/addAlbum这个地址时，就会执行该方法
    public JSONObject selectAlbumName(String token){
        JSONObject jsonObject = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user==null){
            jsonObject.put("status","fail");
            return jsonObject;
        }
        List<PartAlbumVO> partAlbumVOS = albumService.selectAllAlbum(user.getUserId());
        jsonObject.put("status","success");
        jsonObject.put("data", partAlbumVOS);
        return jsonObject;
    }

    @RequestMapping("/addImageToAlbum")
    public JSONObject addImageToAlbum(HttpServletRequest req, String token, @RequestParam("imageId") List<Integer> imageId, Integer albumId ){
        JSONObject jsonObject = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user==null){
            jsonObject.put("status","fail");
            return jsonObject;
        }
        albumService.addImageToAlbum(req,albumId,imageId,user.getUserId());
        jsonObject.put("status","success");
        return jsonObject;
    }

    @RequestMapping("/removeImageFromAlbum")
    public JSONObject removeImageFromAlbum(HttpServletRequest req, String token, @RequestParam("imageId") List<Integer> imageId, Integer albumId ){
        JSONObject jsonObject = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user==null){
            jsonObject.put("status","fail");
            return jsonObject;
        }
        albumService.removeImageFromAlbum(req,albumId,imageId,user.getUserId());
        jsonObject.put("status","success");
        return jsonObject;
    }

    @RequestMapping("/addAlbum")
    public JSONObject addAlbum(HttpServletRequest req,String token,String albumName){
        JSONObject jsonObject = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user==null){
            jsonObject.put("status","fail");
            return jsonObject;
        }
        try {
            albumService.addAlbum(req,albumName,user.getUserId());
            jsonObject.put("status","success");
        } catch (RuntimeException e) {
            if(e.getMessage().equals("相册名已存在")) {
                jsonObject.put("status","exist");
                jsonObject.put("message","相册名已存在");
            } else {
                jsonObject.put("status","fail");
            }
        }
        return jsonObject;
    }

    @RequestMapping("/deleteAlbumByIds")
    public JSONObject deleteAlbumByIds(HttpServletRequest req,String token,@RequestParam("albumIds")List<Integer> albumIds){
        JSONObject jsonObject = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user==null){
            jsonObject.put("status","fail");
            return jsonObject;
        }
        albumService.deleteAlbum(req,albumIds,user.getUserId());
        jsonObject.put("status","success");

        return jsonObject;
    }
    @RequestMapping("/selectAllImage")
    public JSONObject selectAllImage(String token,Integer albumId) throws ParseException {
        JSONObject jsonObject = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user==null){
            jsonObject.put("status","fail");
            return jsonObject;
        }
        JSONObject res = albumService.selectAlbumImage(albumId);

        jsonObject.put("status","success");
        jsonObject.put("data",res);
        System.out.println(res);

        return jsonObject;
    }

    @RequestMapping("/setAlbumCover")
    public JSONObject setAlbumCover(String token,Integer albumId,Integer imageId) throws ParseException {
        JSONObject jsonObject = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user==null||albumId == null||imageId == null){
            jsonObject.put("status","fail");
            return jsonObject;
        }

        albumService.setAlbumCover(albumId,imageId);
        jsonObject.put("status","success");
        return jsonObject;
    }

    @RequestMapping("/share")
    public JSONObject shareAlbum(String token,Integer albumId,Integer shareDay) throws ParseException {
        JSONObject jsonObject = new JSONObject();
        User user = tokenUtil.jwtParser(token);
        if(user==null){
            jsonObject.put("status","fail");
            return jsonObject;
        }
        if(shareDay == 0){
            shareDay = 9999*24*60*60;
        }
        else{
            shareDay = shareDay*24*60*60;
        }

        String shareInfo = user.getUserId()+","+albumId;
        String md5 = DigestUtils.md5Hex(shareInfo);

        redisUtil.set(md5,shareInfo,shareDay);
        jsonObject.put("UrL","?code="+md5);
        return jsonObject;
    }
}
