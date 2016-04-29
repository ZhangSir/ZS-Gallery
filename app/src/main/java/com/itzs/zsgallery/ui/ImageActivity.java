package com.itzs.zsgallery.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;

import com.itzs.zsgallery.BaseActivity;
import com.itzs.zsgallery.R;
import com.itzs.zsgallery.adpter.ImageAdapter;
import com.itzs.zsgallery.imageloader.ImageLoader;
import com.itzs.zsgallery.imageloader.ImageViewAware;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ImageActivity extends BaseActivity {

    public static final String TAG = ImageActivity.class.getSimpleName();

    /**Intent传值标示，标示图片列表*/
    public static final String FLAG_INTENT_LIST_PHOTOS = "listPhotos";

    /**Intent传值标示,标示首次进入该界面，ViewPager应该显示listPhotos中第几项*/
    public static final String FLAG_INTENT_START_INDEX = "startIndex";

    /**Intent传值标示,标示Transition动画起始Image的大小，方便从缓存中获取*/
    public static final String FLAG_INTENT_FROM_TARGET_SIZE = "target_size";

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private Toolbar toolbar;

    private ViewPager mViewPager;

    /**用于Activity间进行Transition的ImageView*/
    private ImageView ivTransition;

    private ImageAdapter imageAdapter;

    private List<String> listPhotos;

    /**记录首次进入该界面，ViewPager应该显示listPhotos中第几项*/
    private int startIndex = 0;

    private boolean mVisible;

    private int[] targetSize;

    /**标记是否正在Transition，是的话禁止返回键*/
    private boolean isTransiting = false;

    private final Handler mHideHandler = new Handler();

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent it = getIntent();
        if(it.hasExtra(FLAG_INTENT_LIST_PHOTOS)){
            this.listPhotos = (List<String>) it.getSerializableExtra(FLAG_INTENT_LIST_PHOTOS);
            this.startIndex = it.getIntExtra(FLAG_INTENT_START_INDEX, 0);
        }
        if(null == this.listPhotos || this.listPhotos.size() == 0){
            this.finish();
            return;
        }

        if(it.hasExtra(FLAG_INTENT_FROM_TARGET_SIZE)){
            this.targetSize = it.getIntArrayExtra(FLAG_INTENT_FROM_TARGET_SIZE);
        }

        mVisible = true;

        this.initView();
        this.initViewData();
        this.initListener();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_image);
        ivTransition = (ImageView) findViewById(R.id.iv_image_transition);

        String currentPath = listPhotos.get(startIndex);
        String fileName = currentPath.substring(currentPath.lastIndexOf(File.separator), currentPath.length());
        getSupportActionBar().setTitle(fileName);

        if(Build.VERSION.SDK_INT >= 21){
            //动态设置共享视图
            ivTransition.setTransitionName(getString(R.string.transition_name1));

            addTransitionListener();
        }
        ImageViewAware aware = new ImageViewAware(ivTransition);
        aware.setTargetSize(targetSize);
        ImageLoader.getInstance(this).displayImage(listPhotos.get(startIndex), aware, false, null, null);
    }

    private void initViewData(){
        imageAdapter = new ImageAdapter(this, this.listPhotos);
    }

    private void initListener(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }

            @Override
            public void onLoadFinish() {

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("onPageScrolled-->", "" + position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("onPageSelected-->", "" + position);
                if (null != DetailGalleryActivity.instance) {
                    DetailGalleryActivity.instance.scrollToPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("onPageStateChanged-->", "" + state);
                if (ivTransition.getVisibility() == View.VISIBLE) {
                    ivTransition.setImageDrawable(null);
                    ivTransition.setVisibility(View.GONE);
                }
            }
        });

    }

    /**
     * Try and add a {@link Transition.TransitionListener} to the entering shared element
     * {@link Transition}. We do this so that we can load the full-size image after the transition
     * has completed.
     *
     * @return true if we were successful in adding a listener to the enter transition
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener(){
        final Transition transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    //the transition has ended
                    initViewPagerData();
                    isTransiting = false;
                    if(null != DetailGalleryActivity.instance){
                        DetailGalleryActivity.instance.clearTransitionNameToPosition(startIndex);
                    }
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }
                @Override
                public void onTransitionStart(Transition transition) {
                    // No-op
                    isTransiting = true;
                }
                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    isTransiting = false;
                    transition.removeListener(this);
                }
                @Override
                public void onTransitionPause(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // No-op
                }
            });
            return true;
        }
        // If we reach here then we have not added a listener
        return false;
    }

    private void initViewPagerData(){
        mViewPager.setAdapter(imageAdapter);
        mViewPager.setCurrentItem(startIndex);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if(isTransiting) return;

        View currentView = imageAdapter.getPrimaryItem();
        ImageView imageView = (ImageView) currentView.findViewById(R.id.iv_image_item);
        ViewCompat.setTransitionName(imageView, getString(R.string.transition_name1));

        if(null != DetailGalleryActivity.instance){
            DetailGalleryActivity.instance.setTrasitionNameToPosition(mViewPager.getCurrentItem());
        }

        super.onBackPressed();
    }
}
