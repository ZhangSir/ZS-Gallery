package com.itzs.zsgallery.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SystemUtil {
	private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
    private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";

    /**
     * 屏幕方向是否是竖向
     * @param res
     * @return
     */
    public static boolean isInPortrait(Resources res){
    	return (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    @TargetApi(14)
    public static int getActionBarHeight(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
            result = context.getResources().getDimensionPixelSize(tv.resourceId);
        }
        return result;
    }

    @TargetApi(14)
    public static int getNavigationBarHeight(Context context) {
        Resources res = context.getResources();
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (!ViewConfiguration.get(context).hasPermanentMenuKey()) {
                String key;
                if (isInPortrait(res)) {
                    key = NAV_BAR_HEIGHT_RES_NAME;
                } else {
                    key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
                }
                return getInternalDimensionSize(res, key);
            }
        }
        return result;
    }

    @TargetApi(14)
    public static int getNavigationBarWidth(Context context) {
        Resources res = context.getResources();
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (!ViewConfiguration.get(context).hasPermanentMenuKey()) {
                return getInternalDimensionSize(res, NAV_BAR_WIDTH_RES_NAME);
            }
        }
        return result;
    }

    public static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }
    
    /**
     * 设置MIUI的状态栏
     * @param context
     * @param isBlack
     */
    public static void setMIUIStatusBar(Activity context,boolean isBlack) {
		Window window = context.getWindow();
		Class clazz = window.getClass();
		try {
			int tranceFlag = 0;
			int darkModeFlag = 0;
			Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
			Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
			tranceFlag = field.getInt(layoutParams);
			field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
			darkModeFlag = field.getInt(layoutParams);
			Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
			if (isBlack) {
				// 状态栏透明且黑色字体
				extraFlagField.invoke(window, tranceFlag | darkModeFlag, tranceFlag | darkModeFlag);
			} else {
				// 清除黑色
				extraFlagField.invoke(window, 0, darkModeFlag);
			}
		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
		} catch (IllegalAccessException e) {
//			e.printStackTrace();
		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
		} catch (InvocationTargetException e) {
//			e.printStackTrace();
		}
	}

    @SuppressLint("NewApi")
    public static float getSmallestWidthDp(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        } else {
            // TODO this is not correct, but we don't really care pre-kitkat
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        }
        float widthDp = metrics.widthPixels / metrics.density;
        float heightDp = metrics.heightPixels / metrics.density;
        return Math.min(widthDp, heightDp);
    }

    /**
     * Should a navigation bar appear at the bottom of the screen in the current
     * device configuration? A navigation bar may appear on the right side of
     * the screen in certain configurations.
     *
     * @return True if navigation should appear at the bottom of the screen, False otherwise.
     */
    public static boolean isNavigationAtBottom(Activity activity) {
        return (getSmallestWidthDp(activity) >= 600 || isInPortrait(activity.getResources()));
    }

    /**
     * Get the height of the system status bar.
     *
     * @return The height of the status bar (in pixels).
     */
    public static int getStatusBarHeight(Context context) {
        return getInternalDimensionSize(context.getResources(), STATUS_BAR_HEIGHT_RES_NAME);
    }

    /**
     * Does this device have a system navigation bar?
     *
     * @return True if this device uses soft key navigation, False otherwise.
     */
    public static boolean hasNavigtionBar(Context context) {
        return (getNavigationBarHeight(context) > 0);
    }

    
    /**
     * 在系统版本为4.4及以上时，设置透明状态栏，所以需要设置自定义actionBar的paddingTop为状态栏的高度
     * @param actionBar
     */
    public static void setStatusBar(Context context, View actionBar){
    	if (android.os.Build.VERSION.SDK_INT > 18) {
            //设置根布局的内边距
    		actionBar.setPadding(
    				actionBar.getPaddingLeft(), 
    				actionBar.getPaddingTop() + SystemUtil.getStatusBarHeight(context), 
    				actionBar.getPaddingRight(), 
    				actionBar.getPaddingBottom());
        }
    }
    
    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getDisplayWidth(Context context){
    	DisplayMetrics dm = new DisplayMetrics();
    	((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
    	return dm.widthPixels;
    }
    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getDisplayHeight(Context context){
    	DisplayMetrics dm = new DisplayMetrics();
    	((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
    	return dm.heightPixels;
    }
}