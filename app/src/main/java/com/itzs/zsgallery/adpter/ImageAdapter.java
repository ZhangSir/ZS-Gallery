package com.itzs.zsgallery.adpter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.itzs.zsgallery.R;
import com.itzs.zsgallery.imageloader.FailReason;
import com.itzs.zsgallery.imageloader.ImageLoader;
import com.itzs.zsgallery.imageloader.ImageLoadingListener;
import com.itzs.zsgallery.imageloader.ImageLoadingProgressListener;
import com.itzs.zsgallery.imageloader.ImageViewAware;

import java.util.List;

/**
 * 大图浏览界面的适配器
 * Created by zhangshuo on 2016/1/27.
 */
public class ImageAdapter extends PagerAdapter implements View.OnClickListener{
    private Context mContext;
    /**用于显示的图片路径列表*/
    private List<String> listPhotos = null;

    private ImageLoader imageLoader;

    private LayoutInflater mInflater;

    private View primaryItem;

    private OnItemClickListener onItemClickListener;

    public ImageAdapter(Context context, List<String> listPaths){
        this.mContext = context;
        this.listPhotos = listPaths;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.imageLoader = ImageLoader.getInstance(this.mContext);
    }

    @Override
    public int getCount() {
        return listPhotos.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
		View view = (View) mInflater.inflate(R.layout.layout_image_item, null);
        ImageView ivPic = (ImageView) view.findViewById(R.id.iv_image_item);

        ivPic.setOnClickListener(this);
        this.imageLoader.displayImage(listPhotos.get(position), new ImageViewAware(ivPic), false, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ((ImageView) view).setImageResource(R.color.backgroud);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ((ImageView) view).setImageBitmap(loadedImage);
                if(null != onItemClickListener){
                    onItemClickListener.onLoadFinish();
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        }, null);
        container.addView(view, 0);
        return view;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        primaryItem = (View) object;
    }

    public View getPrimaryItem(){
        return primaryItem;
    }

    @Override
    public void onClick(View v) {
        if(null != onItemClickListener){
            onItemClickListener.onClick(v);
        }
    }

    /**
     * ImageAdapter中itemView的点击监听器
     * @return
     */
    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    /**
     * 设置ImageAdapter中itemView的点击监听器
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * ImageAdapter中itemView的点击监听器
     */
    public interface OnItemClickListener{
        /**
         * ImageAdapter中itemView的点击监听器
         * @param v
         */
        public void onClick(View v);

        public void onLoadFinish();
    }
}
