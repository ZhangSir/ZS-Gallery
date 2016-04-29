package com.itzs.zsgallery;

import android.app.Activity;
import android.app.Application;

import com.itzs.zsgallery.exception.UnCEHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangshuo on 2016/1/11.
 */
public class GalleryApplication extends Application{
    /** 记录程序中打开过的所有Activity */
    private List<Activity> activitys = new ArrayList<Activity>();
    /** 程序未捕获异常处理器 */
    private UnCEHandler unCEHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        /* 设置自己的未捕获异常处理器为程序的默认未捕获异常处理器 */
        unCEHandler = new UnCEHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(unCEHandler);
    }

    /**
     * 添加指定Activity到记录列表
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        activitys.add(activity);
    }

    /**
     * 从记录列表移除指定Activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        activitys.remove(activity);
    }

    /**
     * 关闭程序
     */
    public void closeProgress() {

        // 先关闭程序中多有的Activity
        for (Activity activity : activitys) {
            if (null != activity) {
                activity.finish();
            }
        }

        // 杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());

    }
}
