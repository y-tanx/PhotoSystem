package com.picture.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import java.io.InputStream;
import jdk.internal.util.xml.impl.Input;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

@Component
public class EXIFUtil {

    /**
     * 获取照片的拍摄时间
     * 优先 EXIF 信息，其次为文件创建时间
     */
    public static Date getImageDate(InputStream inputStream) throws IOException {

        try {
            // 读取 EXIF 元数据
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            if (directory != null) {
                Date date = directory.getDateOriginal(); // 获取 DateTimeOriginal
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            System.err.println("读取 EXIF 信息失败：" + e.getMessage());
        }

        return null;
    }
}
