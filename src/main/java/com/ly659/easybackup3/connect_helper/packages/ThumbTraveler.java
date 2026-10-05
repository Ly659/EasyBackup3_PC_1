package com.ly659.easybackup3.connect_helper.packages;

import com.ly659.easybackup3.Enums;

import java.io.*;

/**
 * 此类为传输缩略图数据的封装类。
 * 创建对象时，可以指定图片信息，也可以只指定图片数据。
 */
public class ThumbTraveler implements Serializable {
    // 原图信息
    private String name;
    private Enums type;
    private final String id;          // 此缩略图的唯一标识符
    // 缩略图数据
    private final byte[] image;


    public ThumbTraveler(byte[] image, String id) {
        this.image = image;
        this.id = id;
    }

    public ThumbTraveler(String name, Enums type, byte[] image, String id) {
        this.name = name;
        this.type = type;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取缩略图的文件类型。
     * @return 文件类型
     */
    public Enums getType() {
        return type;
    }

    public File saveImage(String path) throws IOException {
        File imageFile = new File(path + "/" + id);
        // 新建文件
        if (!imageFile.exists()) {
            if (!imageFile.createNewFile())
                throw new IOException("Unable to create file : " + imageFile.getAbsolutePath());
        }

        // 写入文件
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(imageFile))) {
            dos.write(image);
            dos.flush();
        }

        return imageFile;
    }

    public String getId() {
        return id;
    }






}
