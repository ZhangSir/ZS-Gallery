package com.itzs.zsgallery.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itzs.zsgallery.BaseFragment;
import com.itzs.zsgallery.R;
import com.itzs.zsgallery.util.ImageProvider;
import com.itzs.zsgallery.adpter.MainGalleryAdpter;
import com.itzs.zsgallery.model.ImageFolderModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainGalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainGalleryFragment extends BaseFragment {
    public static final String TAG = MainGalleryFragment.class.getSimpleName();
    /**请求写外置存储的权限的code*/
    private final int RequestCode_WriteExternalStoragePermission = 101;
    /**异步加载请求的回调标示*/
    private static final int FLAG_LOAD_SUCCESS = 1001;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;
    private MainGalleryAdpter galleryAdpter;

    private HashMap<String, List<String>> imageGroupMap = null;

    private List<ImageFolderModel> listFolders = null;

    private OnFragmentInteractionListener mListener;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FLAG_LOAD_SUCCESS:
                    //图片列表加载结束
                    if (null == listFolders) {
                        listFolders = new ArrayList<ImageFolderModel>();
                    }
                    galleryAdpter.refreshData(listFolders);
                    galleryAdpter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public MainGalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainGalleryFragment newInstance(String param1, String param2) {
        MainGalleryFragment fragment = new MainGalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_gallery, container, false);

        this.initView(view);
        this.initViewData();
        this.initListener();

        return view;
    }

    private void initView(View view){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_main_gallery);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initViewData() {
        if (checkPermission()) {
            startLoadingThread();
        }
        if (null == listFolders) {
            listFolders = new ArrayList<ImageFolderModel>();
        }
        galleryAdpter = new MainGalleryAdpter(getActivity(), listFolders);
        galleryAdpter.setColumnCount(2);
        mRecyclerView.setAdapter(galleryAdpter);
    }

    private void initListener(){
        galleryAdpter.setListener(new MainGalleryAdpter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent it = new Intent(getActivity(), DetailGalleryActivity.class);
                it.putExtra(DetailGalleryActivity.INTENT_FLAG_LIST_PHOTOS, (Serializable) imageGroupMap.get(listFolders.get(position).getName()));
                startActivity(it);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * 检查是否授予读取外置存储的权限
     * @return
     */
    private boolean checkPermission(){
        //判断权限
        int hasWriteExternalStoragePermission = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED){
            //已授予权限
            return true;
        }else{
            //未授予权限,弹出Dialog让用户手动授予权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode_WriteExternalStoragePermission);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == RequestCode_WriteExternalStoragePermission){
            //获取写外置存储的权限
            for (int i = 0; i < permissions.length; i++){
                if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        //用户授予权限
                        startLoadingThread();
                    }else{
                        //用户拒绝授权
                        shortMessage("读取外置存储的权限被拒绝");
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                    .setMessage("该相册需要赋予访问存储的权限，不开启将无法正常工作！")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getActivity().finish();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getActivity().finish();
                                        }
                                    }).create();
                            dialog.show();
                            return;
                        }
//                        getActivity().finish();
                    }
                }
            }

        }
    }

    /**
     * 启动异步加载图片列表的线程
     */
    private void startLoadingThread(){
        new MyThread().start();
    }

    /**
     * 加载图片列表的异步线程
     */
  class MyThread extends Thread{
      @Override
      public void run() {
          imageGroupMap = ImageProvider.loadImagesFromFilesTable(getActivity());
          listFolders = ImageProvider.formatImageFolder(imageGroupMap);

//          Message msg = mHandler.obtainMessage(FLAG_LOAD_SUCCESS);
//          mHandler.sendMessage(msg);
          mHandler.sendEmptyMessage(FLAG_LOAD_SUCCESS);
      }
  }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
