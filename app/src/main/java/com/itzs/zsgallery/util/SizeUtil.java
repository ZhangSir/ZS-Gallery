package com.itzs.zsgallery.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 尺寸相关的工具类
 * Created by zhangshuo on 2016/1/27.
 */
public class SizeUtil {

    /**
     * 计算itemView的宽度
     * @param context
     * @param columnCount 列数
     * @param outerSpace 外间隙 （左右两边的空隙）
     * @param interSpace 內间隙（两个itemView之间的空隙）
     * @return
     */
    public static int computeItemViewWidth(Context context, int columnCount, int outerSpace, int interSpace){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        return (width - (2 * outerSpace) - ((columnCount - 1) * interSpace))/columnCount;
    }
}
