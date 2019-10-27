package com.rakel.he.photo_booth.model;

import android.content.Context;

import com.rakel.he.photo_booth.contacts.CameraContacts;

public class CapturePhotoModel extends  IModel implements CameraContacts.Model {
    public CapturePhotoModel(Context context)
    {
        super(context);
    }


    @Override
    public void savePhoto(String filePath,String name,SavePhotoListener callback) {
        PhotoBean photoBean=new PhotoBean();
        photoBean.setFilePath(filePath);
        photoBean.setName(name);
        photoBean.setCreateTimestamp(System.currentTimeMillis());
        long code=liteOrm.save(photoBean);
        if(callback!=null)
        {
            callback.onPhotoSaved((int)code,name,filePath);
        }
    }
}
