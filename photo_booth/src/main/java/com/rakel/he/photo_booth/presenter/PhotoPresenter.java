package com.rakel.he.photo_booth.presenter;

import android.app.Activity;

import com.rakel.he.photo_booth.contacts.PhotoContacts;
import com.rakel.he.photo_booth.model.PhotoBean;
import com.rakel.he.photo_booth.model.PhotoModel;
import com.rakel.he.photo_booth.view.PhotoGalleryView;

import java.util.List;

public class PhotoPresenter extends BasePresenter
        implements PhotoContacts.Presenter
{
    private PhotoModel mPhotoModel;
    @Override
    public void loadPhotoes() {
        if(mPhotoModel==null)
        {
            mPhotoModel=new PhotoModel((Activity)(iView.get()));
        }
        mPhotoModel.loadPhotoes(new PhotoContacts.Model.ModelListener() {
            @Override
            public void completed(List<PhotoBean> photoes) {
                ((PhotoGalleryView)iView.get()).onPhotoesLoaded(photoes);
            }
        });
    }
}
