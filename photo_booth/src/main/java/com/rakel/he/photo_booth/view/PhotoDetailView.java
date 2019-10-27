package com.rakel.he.photo_booth.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.rakel.he.photo_booth.R;
import com.rakel.he.photo_booth.contacts.PhotoGalleryContacts;
import com.rakel.he.photo_booth.presenter.IPresenter;

public class PhotoDetailView extends  BaseActivity{
    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView()
    {
        String filePath=getIntent().getStringExtra(PhotoGalleryContacts.FILE_PATH);
        String photoName=getIntent().getStringExtra(PhotoGalleryContacts.PHOTO_NAME);
        setContentView(R.layout.activity_photo_detail);
        ((TextView)findViewById(R.id.tittle_bar_tittle)).setText(photoName);
        findViewById(R.id.tittle_bar_right_menu).setVisibility(View.GONE);
        findViewById(R.id.tittle_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView imageView=findViewById(R.id.photo_detail);
        Glide.with(this).load(filePath).into(imageView);
    }
}
