package com.itzs.zsgallery.exception;

import android.os.Looper;
import android.widget.Toast;

import com.itzs.zsgallery.GalleryApplication;

/**
 * Created by zhangshuo on 2016/1/11.
 */
public class UnCEHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = UnCEHandler.class.getSimpleName();

    /**
     * 系统默认的UncaughtException的处理器
     */
    private Thread.UncaughtExceptionHandler defaultUnCEHandler;

    private GalleryApplication application;

    public UnCEHandler(GalleryApplication application) {
        this.application = application;
        //获取系统默认的UncaughtException的处理器
        defaultUnCEHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * 通过实现UncaughtExceptionHandler接口的该方法，取得抛出异常的线程和异常
     *
     * @param thread    抛出异常的线程
     * @param throwable 抛出的异常
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (null != throwable) {
            throwable.printStackTrace();
        }
        if (!handleUncaughtException(throwable) && null != defaultUnCEHandler) {
            //如果用户没有处理异常，则交给系统处理
            defaultUnCEHandler.uncaughtException(thread, throwable);
        } else {

        }

    }

    /**
     * 自己处理未捕获的异常
     *
     * @param throwable
     * @return true, 如果自己处理了异常；否则返回false
     */
    private boolean handleUncaughtException(Throwable throwable) {
        if (null == throwable) {
            return false;
        }
        new Thread() {

            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(application, "程序开小差，正在退出", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

        }.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//		/*彻底退出崩溃程序，并使用PendingIntent和AlarmManager实现程序重启*/
//		Intent intent = new Intent(application, MainActivity.class);
//		PendingIntent restartIntent = PendingIntent.getActivity(application, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
//		AlarmManager am = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
//		am.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);//1秒后重启程序
        application.closeProgress();

        return true;
    }
}