package com.itzs.zsgallery;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Fragment的基类
 * Created by zhangshuo on 2016/1/11.
 */
public class BaseFragment extends Fragment {

    protected GalleryApplication application = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(null == application){
            application = (GalleryApplication) context.getApplicationContext();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Toast短提醒
     *
     * @param text
     *            String字符串
     */
    protected void shortMessage(String text) {
        if (this.isDetached())
            return;
        try {
            Toast.makeText(application, text, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Toast短提醒
     *
     * @param id
     *            String字符串ID
     */
    protected void shortMessage(int id) {
        if (this.isDetached())
            return;
        try {
            Toast.makeText(application, getString(id), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
