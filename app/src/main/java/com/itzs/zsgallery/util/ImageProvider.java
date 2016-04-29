package com.itzs.zsgallery.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.itzs.zsgallery.imageloader.downloader.ImageDownloader;
import com.itzs.zsgallery.model.ImageFolderModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 本地图片扫描工具
 * Created by zhangshuo on 2016/1/6.
 */
public class ImageProvider {

    public static final String TAG = ImageProvider.class.getSimpleName();

    private static final String EXTERNAL_MEDIA = "external";

    /**
     * 从MediaStore的File表中取出所有图片
     * @param context
     * @return
     */
    public static HashMap<String, List<String>> loadImagesFromFilesTable(Context context) {
        if(null == context) return null;
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri(EXTERNAL_MEDIA);

        //只查询图片
        Cursor mCursor = resolver.query(uri, null,
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=?",
                new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)}, MediaStore.MediaColumns.DATE_ADDED);

        if(mCursor == null){
            return null;
        }

        HashMap<String, List<String>> mGroupMap = new HashMap<String, List<String>>();

        while (mCursor.moveToNext()) {
            //获取图片的路径
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            //获取该图片的父路径名
            String parentName = new File(path).getParentFile().getName();
            //根据父路径名将图片放入到mGroupMap中
            if (!mGroupMap.containsKey(parentName)) {
                List<String> chileList = new ArrayList<String>();
                chileList.add(ImageDownloader.Scheme.FILE.wrap(path));//将路径地址包装一层scheme file://
                mGroupMap.put(parentName, chileList);
            } else {
                mGroupMap.get(parentName).add(ImageDownloader.Scheme.FILE.wrap(path));
            }
        }

        mCursor.close();
        return mGroupMap;
    }

    /**
     * 从MediaStore的Iamge表中取出所有图片
     * @param context
     * @return
     */
    public static HashMap<String, List<String>> loadImagesFromImageTable(Context context){
        if(null == context) return null;

        ContentResolver mContentResolver = context.getContentResolver();
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        //只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

        if(mCursor == null){
            return null;
        }

        HashMap<String, List<String>> mGroupMap = new HashMap<String, List<String>>();

        while (mCursor.moveToNext()) {
            //获取图片的路径
            String path = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));

            //获取该图片的父路径名
            String parentName = new File(path).getParentFile().getName();


            //根据父路径名将图片放入到mGroupMap中
            if (!mGroupMap.containsKey(parentName)) {
                List<String> chileList = new ArrayList<String>();
                chileList.add("file://" + path);
                mGroupMap.put(parentName, chileList);
            } else {
                mGroupMap.get(parentName).add("file://" + path);
            }
        }
        mCursor.close();
        return mGroupMap;
    }

    /**
     * 将以文件夹分组的图片集合mGroupMap重新格式化为List<ImageFolderModel>集合
     * @param mGroupMap
     * @return
     */
    public static List<ImageFolderModel> formatImageFolder(HashMap<String, List<String>> mGroupMap){
        if(mGroupMap.size() == 0){
            return null;
        }
        List<ImageFolderModel> listFolders = new ArrayList<ImageFolderModel>();

        Iterator<Map.Entry<String, List<String>>> iterator = mGroupMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, List<String>> mapEntry = iterator.next();
            if(null != mapEntry){
                String key = mapEntry.getKey();
                List<String> list = mapEntry.getValue();
                if(null != list && list.size() > 0){
                    ImageFolderModel folderModel = new ImageFolderModel();
                    folderModel.setName(key);
                    folderModel.setCount(list.size());
                    folderModel.setFirstImagePath(list.get(0));
                    listFolders.add(folderModel);
                }else{
                    Log.e(TAG, "-formatImageFolder--mapEntry--value = null");
                }


            }else{
                Log.e(TAG, "-formatImageFolder--mapEntry = null");
            }
        }
        return listFolders;
    }
}
