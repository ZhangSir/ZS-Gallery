package com.itzs.zsgallery.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.itzs.zsgallery.BaseActivity;
import com.itzs.zsgallery.R;
import com.itzs.zsgallery.adpter.DetailGalleryAdpter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DetailGalleryActivity extends BaseActivity {
    public static final String TAG = DetailGalleryActivity.class.getSimpleName();

    /**
     * Intent 参数传递标示，传递图片列表数据
     */
    public static final String INTENT_FLAG_LIST_PHOTOS = "list_photo";

    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;
    private DetailGalleryAdpter galleryAdpter;

    /**
     * 保存图片路径的列表
     */
    private List<String> listPhotos = null;

    public static DetailGalleryActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_gallery);
        this.instance = this;
        this.initData();
        this.initView();
        this.initViewData();
        this.initListener();
    }

    @Override
    protected void onDestroy() {
        this.instance = null;
        super.onDestroy();
    }

    private void initData(){
        Intent intent = this.getIntent();
        this.listPhotos = (List<String>) intent.getSerializableExtra(INTENT_FLAG_LIST_PHOTOS);
        if(null == listPhotos){
            listPhotos = new ArrayList<String>();
        }
    }

    private void initView(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        setTranslucentStatusBar();

        mRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerview_detail_gallery);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        fab = (FloatingActionButton) findViewById(R.id.fab);

    }

    private void initViewData(){
        galleryAdpter = new DetailGalleryAdpter(this, listPhotos);
        galleryAdpter.setColumnCount(2);
        mRecyclerView.setAdapter(galleryAdpter);

    }

    private void initListener(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        galleryAdpter.setListener(new DetailGalleryAdpter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                setTrasitionNameToPosition(position);
                ImageView imageView = (ImageView) v.findViewById(R.id.iv_detail_gallery_item);
                Intent it = new Intent();
                it.setClass(DetailGalleryActivity.this, ImageActivity.class);
                it.putExtra(ImageActivity.FLAG_INTENT_LIST_PHOTOS, (Serializable) listPhotos);
                it.putExtra(ImageActivity.FLAG_INTENT_START_INDEX, position);
                it.putExtra(ImageActivity.FLAG_INTENT_FROM_TARGET_SIZE, new int[]{imageView.getWidth(), imageView.getHeight()});
//                startActivity(it);

                if (Build.VERSION.SDK_INT >= 21) {
                    // shareView: 需要共享的视图
                    // shareName: 设置的android:transitionName=shareName
//                    如果不想使用transition可以设置options bundle为null。
//                    如果需要逆转该过渡动画，使用Activity.finishAfterTransition()方法代替Activity.finish()
                    ActivityOptions option = ActivityOptions.makeSceneTransitionAnimation(DetailGalleryActivity.this, imageView, getString(R.string.transition_name1));
                    startActivity(it, option.toBundle());
                } else {
                    //让新的Activity从一个小的范围扩大到全屏
                    ActivityOptionsCompat options =
                            ActivityOptionsCompat.makeScaleUpAnimation(v, //The View that the new activity is animating from
                                    (int) v.getWidth() / 2, (int) v.getHeight() / 2, //拉伸开始的坐标
                                    v.getWidth(), v.getHeight());//拉伸开始的区域大小，这里用（0，0）表示从无到全屏
                    ActivityCompat.startActivity(DetailGalleryActivity.this, it, options.toBundle());
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_detail_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void smoothScrollToPosition(int position){
        mRecyclerView.smoothScrollToPosition(position);
    }

    public void scrollToPosition(int position){
        mRecyclerView.scrollToPosition(position);
    }

    /**
     * 设置RecycleView列表中指定子View的TransitionName属性
     * @param position
     */
    public void setTrasitionNameToPosition(int position){
        if(null != galleryAdpter.getListHolders()){
            for(int i = 0; i < galleryAdpter.getListHolders().size(); i++ ){
                DetailGalleryAdpter.MyViewHolder myViewHolder = galleryAdpter.getListHolders().get(i);
                if(null != myViewHolder && myViewHolder.position == position){
                    Log.e("clearHolder:" + i, myViewHolder.toString());
                    ViewCompat.setTransitionName(myViewHolder.ivPic, getString(R.string.transition_name1));
                    return;
                }
            }
        }
//        DetailGalleryAdpter.MyViewHolder holder = (DetailGalleryAdpter.MyViewHolder)(mRecyclerView.findViewHolderForLayoutPosition(position));
//        Log.e("setHolder:" + position, holder.toString());
//        ViewCompat.setTransitionName(holder.ivPic, getString(R.string.transition_name1));
    }

    /**
     * 清除RecycleView列表中所有子View的TransitionName属性
     */
    public void clearTransitionName(){
        if(null != galleryAdpter.getListHolders()){
            for(int i = 0; i < galleryAdpter.getListHolders().size(); i++ ){
                DetailGalleryAdpter.MyViewHolder myViewHolder = galleryAdpter.getListHolders().get(i);
                if(null != myViewHolder){
                    Log.e("clearHolder:" + i, myViewHolder.toString());
                    ViewCompat.setTransitionName(myViewHolder.ivPic, null);
                }
            }
        }
    }

}
