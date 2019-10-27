package com.rakel.he.photo_booth.view;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rakel.he.photo_booth.R;
import com.rakel.he.photo_booth.contacts.PhotoGalleryContacts;
import com.rakel.he.photo_booth.event.SavePhotoEvent;
import com.rakel.he.photo_booth.model.PhotoBean;
import com.rakel.he.photo_booth.presenter.IPresenter;
import com.rakel.he.photo_booth.presenter.PhotoGalleryPresenter;
import com.rakel.he.photo_booth.widget.SquareImageView;
import com.truizlop.sectionedrecyclerview.SectionedSpanSizeLookup;
import com.truizlop.sectionedrecyclerview.SimpleSectionedAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class PhotoGalleryView extends BaseActivity
        implements View.OnClickListener, PhotoGalleryContacts.View{

    private PhotoSectionAdapter mPhotoAdapter;
    private RecyclerView mRecyclerView;
    private SimpleDateFormat mDayFormater,mMonthFormater;
    private RequestOptions mPhotoLoadOptions;

    //group all of photos by month of its created timestamp
    private Map<String,List<PhotoBean>> mPhotoMap;

    //group all the tittle(which is actually the month of create timestamp)
    private List<String> mSectionTittles;

    private int MSG_LOAD_PHOTO_FINISHED=100;

    //used to indicate that data source has changed.need to refresh the page
    private boolean mDataSetChanged=false;

    private int CAMERA_PERMISSION_REQUEST_CODE=1;
    private int STORAGE_PERMISSION_REQUEST_CODE=2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_LOAD_PHOTO_FINISHED) {
                dismissProgress();
                if (mSectionTittles == null || mSectionTittles.size() == 0) {
                    findViewById(R.id.gallery_empty_icon).setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
                mPhotoAdapter.notifyDataSetChanged();
            }
        }
    };

    //get invoked every time when an new photo file is added to the db.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPhotoSaved(SavePhotoEvent event)
    {
        PhotoBean bean=new PhotoBean();
        bean.setCreateTimestamp(event.createTimestamp);
        bean.setName(event.name);
        bean.setFilePath(event.filePath);
        String formatedDate=mMonthFormater.format(event.createTimestamp);
        if(mPhotoMap.containsKey(formatedDate))
            mPhotoMap.get(formatedDate).add(0,bean);
        else
        {
            ArrayList<PhotoBean> photoBeans=new ArrayList<>();
            photoBeans.add(bean);
            mPhotoMap.put(formatedDate,photoBeans);
        }
        mDataSetChanged=true;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        showProgress(getString(R.string.loading));
        requestStoragePermission();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mDataSetChanged)
            mPhotoAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected IPresenter createPresenter() {
        if(iPresenter!=null)
            return iPresenter;
        return new PhotoGalleryPresenter();
    }

    private void loadingPhotoes()
    {
        ((PhotoGalleryPresenter)iPresenter).loadPhotoes();
    }

    private void initView() {
        setContentView(R.layout.activity_photo_gallery);
        ((TextView)findViewById(R.id.tittle_bar_tittle)).setText(R.string.photoes);
        TextView rightMenu=findViewById(R.id.tittle_bar_right_menu);
        rightMenu.setText(R.string.camera);
        rightMenu.setOnClickListener(this);
        findViewById(R.id.tittle_bar_left_menu).setOnClickListener(this);

        //init DateFormater
        mDayFormater=new SimpleDateFormat("yyyy-MM-dd");
        mMonthFormater=new SimpleDateFormat("yyyy-MM");

        int screenWidth=getResources().getDisplayMetrics().widthPixels;
        mPhotoLoadOptions=new RequestOptions()
                .override(screenWidth/4,screenWidth/4);

        //init settings for recyclerView
        mPhotoAdapter=new PhotoSectionAdapter();
        mRecyclerView=findViewById(R.id.gallery_photoes);
        GridLayoutManager layoutManager= new GridLayoutManager(this, 4);
        SectionedSpanSizeLookup lookup = new SectionedSpanSizeLookup(mPhotoAdapter, layoutManager);
        layoutManager.setSpanSizeLookup(lookup);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mPhotoAdapter);

        mPhotoAdapter.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClicked(long section, long position) {
                 gotoDetailPage(section,position);
            }
        });
    }

    private void gotoDetailPage(long section,long position)
    {
        PhotoBean bean=mPhotoMap.get(mSectionTittles.get((int)section)).get((int)position);
        ((PhotoGalleryPresenter)iPresenter).showPhotoDetail(bean.getFilePath(),bean.getName());
    }



    @Override
    public void onClick(View view) {
        int viewId=view.getId();
        if(viewId==R.id.tittle_bar_left_menu)
        {
            finish();
            return;
        }else if(viewId==R.id.tittle_bar_right_menu)
        {
            requestCameraPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==CAMERA_PERMISSION_REQUEST_CODE)
        {
            if(grantResults[0]==PERMISSION_GRANTED)
                takePhoto();
        }else if(requestCode==STORAGE_PERMISSION_REQUEST_CODE)
        {
            if(grantResults[0]==PERMISSION_GRANTED)
            {
                loadingPhotoes();
            }
        }
    }

    private void requestCameraPermission()
    {
        if (Build.VERSION.SDK_INT>22){
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA)!= PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}
                                ,CAMERA_PERMISSION_REQUEST_CODE);
            }else {
                takePhoto();
            }
        }else {
            takePhoto();
        }
    }


    private void requestStoragePermission()
    {
        if (Build.VERSION.SDK_INT>22){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}
                        ,STORAGE_PERMISSION_REQUEST_CODE);
            }else {
                loadingPhotoes();
            }
        }else {
            loadingPhotoes();
        }
    }


    public void takePhoto()
    {
        ((PhotoGalleryPresenter)iPresenter).capturePhoto();
    }


    /*
    this callback method will get invoked once the photo load
    process is over
    */
    @Override
    public void onPhotoesLoaded(List<PhotoBean> beans) {
        if(mSectionTittles==null)
            mSectionTittles=new ArrayList<>();
        if(mPhotoMap==null)
            mPhotoMap=new HashMap<>();
        if(beans==null||beans.size()==0)
        {
            mHandler.sendEmptyMessage(MSG_LOAD_PHOTO_FINISHED);
            return;
        }

        for(PhotoBean bean:beans)
        {
            String month=mMonthFormater.format(new Date(bean.getCreateTimestamp()));
            if(!mSectionTittles.contains(month))
                mSectionTittles.add(month);
            boolean needCreateNewGroup=false;
            if(!mPhotoMap.containsKey(month)||mPhotoMap.get(month)==null)
                needCreateNewGroup=true;
            if(needCreateNewGroup)
            {
                ArrayList<PhotoBean> photoBeans=new ArrayList<>();
                photoBeans.add(bean);
                mPhotoMap.put(month,photoBeans);
            }else
            {
                mPhotoMap.get(month).add(bean);
            }
        }
        mHandler.sendEmptyMessage(MSG_LOAD_PHOTO_FINISHED);

    }

    public class PhotoSectionAdapter extends SimpleSectionedAdapter<PhotoItemViewHolder> {

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
            this.mOnItemClickListener = mOnItemClickListener;
        }

        @Override
        protected String getSectionHeaderTitle(int section) {
            return mSectionTittles ==null? null : mSectionTittles.get(section);
        }

        @Override
        protected int getSectionCount() {
            return mSectionTittles ==null?0: mSectionTittles.size();
        }

        @Override
        protected int getItemCountForSection(int section) {
            if(mSectionTittles==null||mPhotoMap==null)
                return 0;
            return mPhotoMap.get(mSectionTittles.get(section)).size();
        }

        @Override
        protected PhotoItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.layout_recycler_view_section_content, parent, false);
            return new PhotoItemViewHolder(view);
        }


        @Override
        protected void onBindItemViewHolder(PhotoItemViewHolder holder, final int section, final int position) {
            PhotoBean bean=mPhotoMap.get(mSectionTittles.get(section)).get(position);
            holder.nameView.setText(bean.getName());
            holder.timestampView.setText(mDayFormater.format(String.valueOf(bean.getCreateTimestamp())));
            Glide.with(PhotoGalleryView.this).load(bean.getFilePath()).apply(mPhotoLoadOptions).into(holder.imageView);
            ((LinearLayout)holder.nameView.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClickListener!=null)
                    {
                        mOnItemClickListener.onItemClicked(section,position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener
    {
        void onItemClicked(long section,long position);
    }

    private class PhotoItemViewHolder extends RecyclerView.ViewHolder
    {
        public SquareImageView imageView;
        public TextView nameView;
        public TextView timestampView;

        public PhotoItemViewHolder(View view)
        {
            super(view);
            imageView=view.findViewById(R.id.gallery_item_photo);
            nameView=view.findViewById(R.id.gallery_item_name);
            timestampView=view.findViewById(R.id.gallery_item_create_time);
        }
    }

}
