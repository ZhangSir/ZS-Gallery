package com.itzs.zsgallery;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.Toast;

import com.itzs.zsgallery.util.SystemUtil;

/**
 * Activity基类
 * Created by zhangshuo on 2016/1/11.
 */
public class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;

    protected GalleryApplication application = null;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        if(null == application){
            application = (GalleryApplication) this.getApplicationContext();
        }
        //记录当前Activity到缓存中
        application.addActivity(this);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        setTranslucentStatusBar();
    }

    @Override
    protected void onDestroy() {
        if(null == application){
            application = (GalleryApplication) this.getApplicationContext();
        }
        //从缓存中移除当前Activity
        application.removeActivity(this);
        super.onDestroy();
    }

    /**
     * 设置状态栏样式
     */
    protected void setTranslucentStatusBar() {
        if (android.os.Build.VERSION.SDK_INT > 18 && null != toolbar) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            mlp.setMargins(mlp.leftMargin, SystemUtil.getStatusBarHeight(this), mlp.rightMargin, mlp.bottomMargin);
            toolbar.setLayoutParams(mlp);
        }
    }

    /**
     * Toast短提醒
     *
     * @param text
     *            String字符串
     */
    protected void shortMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toast短提醒
     *
     * @param id
     *            String字符串ID
     */
    protected void shortMessage(int id) {
        Toast.makeText(this, getString(id), Toast.LENGTH_SHORT).show();
    }

}
