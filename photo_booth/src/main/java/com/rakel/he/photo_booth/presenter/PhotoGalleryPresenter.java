package com.rakel.he.photo_booth.presenter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.rakel.he.photo_booth.contacts.PhotoGalleryContacts;
import com.rakel.he.photo_booth.model.PhotoBean;
import com.rakel.he.photo_booth.model.PhotoGalleryModel;
import com.rakel.he.photo_booth.view.CapturePhotoView;
import com.rakel.he.photo_booth.view.PhotoDetailView;
import com.rakel.he.photo_booth.view.PhotoGalleryView;

import java.util.List;

public class PhotoGalleryPresenter extends BasePresenter
        implements PhotoGalleryContacts.Presenter
{
    private PhotoGalleryModel mPhotoModel;
    @Override
    public void loadPhotoes() {
        if(mPhotoModel==null)
        {
            mPhotoModel=new PhotoGalleryModel((Activity)(iView.get()));
        }
        mPhotoModel.loadPhotoes(new PhotoGalleryContacts.Model.ModelListener() {
            @Override
            public void completed(List<PhotoBean> photoes) {
                ((PhotoGalleryView)iView.get()).onPhotoesLoaded(photoes);
            }
        });
    }

    public  void capturePhoto()
    {
        Activity source=((Activity)iView.get());
        Intent intent=new Intent(source, CapturePhotoView.class);
        ((Activity)iView.get()).startActivity(intent);
    }

    @Override
    public void showPhotoDetail(String filePath,String photoName) {
        Activity source=((Activity)iView.get());
        Intent intent=new Intent(source, PhotoDetailView.class);
        intent.putExtra(PhotoGalleryContacts.FILE_PATH,filePath);
        intent.putExtra(PhotoGalleryContacts.PHOTO_NAME,photoName);
        ((Activity)iView.get()).startActivity(intent);
    }
}
