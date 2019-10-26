package com.rakel.he.photo_booth.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rakel.he.photo_booth.R;
import com.rakel.he.photo_booth.contacts.PhotoContacts;
import com.rakel.he.photo_booth.model.PhotoBean;
import com.rakel.he.photo_booth.presenter.IPresenter;
import com.rakel.he.photo_booth.presenter.PhotoPresenter;
import com.rakel.he.photo_booth.widget.SquareImageView;
import com.truizlop.sectionedrecyclerview.SectionedSpanSizeLookup;
import com.truizlop.sectionedrecyclerview.SimpleSectionedAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PhotoGalleryView extends BaseActivity
        implements View.OnClickListener,PhotoContacts.View{
    private SweetAlertDialog mDialog;
    private PhotoSectionAdapter mPhotoAdapter;
    private RecyclerView mRecyclerView;
    private SimpleDateFormat mDayFormater,mMonthFormater;
    private RequestOptions mPhotoLoadOptions;

    //group all of photos by month of its created timestamp
    private Map<String,List<PhotoBean>> mPhotoMap;

    //group all the tittle(which is actually the month of create timestamp)
    private List<String> mSectionTittles;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        showDialog(getString(R.string.loading));
        loadingPhotoes();
    }

    @Override
    protected IPresenter createPresenter() {
        if(iPresenter!=null)
            return iPresenter;
        return new PhotoPresenter();
    }

    private void loadingPhotoes()
    {
        ((PhotoPresenter)iPresenter).loadPhotoes();
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
    }

    private void showDialog(String message)
    {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mDialog.setTitleText(message);
        mDialog.setCancelable(false);
        mDialog.show();
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
            takePhoto();
        }
    }

    public void takePhoto()
    {

    }


    @Override
    public void onPhotoesLoaded(List<PhotoBean> beans) {
        if(beans==null||beans.size()==0)
        {
            findViewById(R.id.gallery_empty_icon).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            return;
        }

        if(mSectionTittles==null)
            mSectionTittles=new ArrayList<>();
        if(mPhotoMap==null)
            mPhotoMap=new HashMap<>();
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
        mPhotoAdapter.notifyDataSetChanged();

    }

    public class PhotoSectionAdapter extends SimpleSectionedAdapter<PhotoItemViewHolder> {

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
        protected void onBindItemViewHolder(PhotoItemViewHolder holder, int section, int position) {
            PhotoBean bean=mPhotoMap.get(mSectionTittles.get(section)).get(position);
            holder.nameView.setText(bean.getName());
            holder.timestampView.setText(mDayFormater.format(String.valueOf(bean.getCreateTimestamp())));
            Glide.with(PhotoGalleryView.this).load(bean.getFilePath()).apply(mPhotoLoadOptions).into(holder.imageView);
        }
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
