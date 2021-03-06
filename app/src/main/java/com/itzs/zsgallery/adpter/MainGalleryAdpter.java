package com.itzs.zsgallery.adpter;

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itzs.zsgallery.model.ImageFolderModel;
import com.itzs.zsgallery.R;
import com.itzs.zsgallery.imageloader.ImageLoader;
import com.itzs.zsgallery.imageloader.ImageViewAware;
import com.itzs.zsgallery.ui.MainGalleryFragment;
import com.itzs.zsgallery.util.DensityUtil;
import com.itzs.zsgallery.util.SizeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 主图库列表（展示所有包含图片的文件夹）
 * Created by zhangshuo on 2016/1/6.
 */
public class MainGalleryAdpter extends RecyclerView.Adapter<MainGalleryAdpter.MyViewHolder> {

    public static final String TAG = MainGalleryFragment.class.getSimpleName();

    private Context mContext;
    /**用于显示的图片文件夹对象列表*/
    private List<ImageFolderModel> listFolders = null;

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

    public MainGalleryAdpter(Context context, List<ImageFolderModel> list){
        this.mContext = context;
        this.listFolders = list;
        this.imageLoader = ImageLoader.getInstance(mContext);

        if(null == listFolders){
            this.listFolders = new ArrayList<ImageFolderModel>();
        }
    }

    /**
     * 刷新数据
     * @param list
     */
    public void refreshData(List<ImageFolderModel> list){
        this.listFolders = list;
        if(null == listFolders){
            this.listFolders = new ArrayList<ImageFolderModel>();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(
                LayoutInflater.from(mContext)
                        .inflate(R.layout.layout_main_gallery_item, null));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageFolderModel folderModel = listFolders.get(position);
        holder.position = position;
        holder.tvName.setText(folderModel.getName());
        holder.tvNum.setText(String.valueOf(folderModel.getCount()));

        this.imageLoader.displayImage(folderModel.getFirstImagePath(), new ImageViewAware(holder.ivPic), null, null);

    }

    @Override
    public int getItemCount() {
        return listFolders.size();
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        itemWidth = SizeUtil.computeItemViewWidth(this.mContext, this.columnCount, (int)DensityUtil.dip2px(this.mContext, 8), (int)DensityUtil.dip2px(this.mContext,16));
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivPic;
        TextView tvNum;
        TextView tvName;
        int position;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivPic = (ImageView) itemView.findViewById(R.id.iv_main_gallery_item);
            tvNum = (TextView) itemView.findViewById(R.id.tv_main_gallery_item_num);
            tvName = (TextView) itemView.findViewById(R.id.tv_main_gallery_item_name);

            if(itemWidth > 0){
                if(null == lp){
                    lp = ivPic.getLayoutParams();
                    lp.width = itemWidth;
                    lp.height = itemWidth;
                }
                ivPic.setLayoutParams(lp);
            }

            ivPic.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e(TAG, "click");
            Log.e(TAG, "listener:" + listener);
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
