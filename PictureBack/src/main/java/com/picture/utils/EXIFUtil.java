package com.picture.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
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
    public static Date getImageDate(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("文件不存在或无效：" + file);
        }

        try {
            // 读取 EXIF 元数据
            Metadata metadata = ImageMetadataReader.readMetadata(file);
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

        // 尝试使用文件属性中的创建时间或最后修改时间
        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            if (attrs.creationTime() != null) {
                return new Date(attrs.creationTime().toMillis());
            } else {
                return new Date(attrs.lastModifiedTime().toMillis());
            }
        } catch (IOException ioException) {
            System.err.println("读取文件属性失败：" + ioException.getMessage());
        }

        return null;
    }
}
