package com.itzs.zsgallery.model;

import java.io.Serializable;

/**
 * 图片文件夹模型(包含文件夹中第一张图片的路径)
 * Created by zhangshuo on 2016/1/6.
 */
public class ImageFolderModel implements Serializable{
    /**
     * 文件夹名
     */
    private String name;
    /**
     * 文件夹中的图片数
     */
    private int count;
    /**
     * 文件夹的第一张图片路径
     */
    private String firstImagePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    @Override
    public String toString() {
        return "ImageFolderModel{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", firstImagePath='" + firstImagePath + '\'' +
                '}';
    }
}
