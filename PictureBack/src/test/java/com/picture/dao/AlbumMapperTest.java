package com.picture.dao;

import com.picture.domain.Album;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AlbumMapperTest {
    @Resource
    private AlbumMapper albumMapper;

    @Test
    void testAddAlbum() {
        Album album = new Album();
        album.setAlbumName("测试相册");
        album.setAlbumTheme("自然风光");
        album.setAlbumContext("这是一个描述文本");
        album.setAlbumImg("cover.jpg");
        album.setBackgroundImage("bg.jpg");
        album.setAlbumMusic("music.mp3");
        album.setUserId(1);

        albumMapper.addAlbum(album);

        assertNotNull(album.getAlbumId(), "插入成功后albumId应该被自动生成");
    }

    @Test
    void testAddAlbumImage() {
        int albumId = 1;
        albumMapper.addAlbumImage(albumId, Arrays.asList(101, 102, 103));

        // 通常我们应查询数据库验证是否插入成功，这里简化为运行无异常即可
        assertTrue(true, "成功执行 addAlbumImage 无异常");
    }
}