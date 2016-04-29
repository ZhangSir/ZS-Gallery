package com.itzs.zsgallery.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.itzs.zsgallery.R;
import com.itzs.zsgallery.imageloader.ImageLoader;
import com.itzs.zsgallery.imageloader.ImageViewAware;
import com.itzs.zsgallery.util.DensityUtil;
import com.itzs.zsgallery.util.SizeUtil;

import java.util.List;

/**
 * 详细图库界面（展示图片列表）
 * Created by zhangshuo on 2016/1/6.
 */
public class DetailGalleryAdpter extends RecyclerView.Adapter<DetailGalleryAdpter.MyViewHolder> {

    private Context mContext;
    /**用于显示的图片路径列表*/
    private List<String> listPhotos = null;

    private ImageLoader imageLoader;
    /**列表中item被点击的监听器*/
    private OnItemClickListener listener;

    /**
     * 每个item的宽度（高度=宽度）
     */
    private int itemWidth = 0;

    /**
     * 列数
     */
    private int columnCount = 1;

    private ViewGroup.LayoutParams lp = null;

    public DetailGalleryAdpter(Context context, List<String> list){
        this.mContext = context;
        this.listPhotos = list;
        this.imageLoader = ImageLoader.getInstance(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(
                LayoutInflater.from(mContext)
                        .inflate(R.layout.layout_detail_gallery_item, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.position = position;
        this.imageLoader.displayImage(listPhotos.get(position), new ImageViewAware(holder.ivPic), null, null);
    }

    @Override
    public int getItemCount() {
        return listPhotos.size();
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        itemWidth = SizeUtil.computeItemViewWidth(this.mContext, this.columnCount, (int) DensityUtil.dip2px(this.mContext, 8), (int) DensityUtil.dip2px(this.mContext, 16));
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivPic;
        public int position;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivPic = (ImageView) itemView.findViewById(R.id.iv_detail_gallery_item);
            if(itemWidth > 0){
                if(null == lp){
                    lp = ivPic.getLayoutParams();
                    lp.width = itemWidth;
                    lp.height = itemWidth;
                }
                ivPic.setLayoutParams(lp);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(null != listener){
                listener.onItemClick(v, position);
            }
        }
    }

    /**
     * 列表中item被点击的监听器
     * @param listener
     */
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 列表中item被点击的监听器
     */
    public interface OnItemClickListener{
        /**
         * item被点击的回调方法
         * @param v
         * @param position
         */
        public void onItemClick(View v, int position);
    }
}
