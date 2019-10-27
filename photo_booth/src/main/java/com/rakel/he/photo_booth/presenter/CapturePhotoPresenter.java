package com.rakel.he.photo_booth.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.rakel.he.photo_booth.contacts.CameraContacts;
import com.rakel.he.photo_booth.event.SavePhotoEvent;
import com.rakel.he.photo_booth.model.CapturePhotoModel;

import org.greenrobot.eventbus.EventBus;

public class CapturePhotoPresenter extends BasePresenter implements CameraContacts.Presenter {
    @Override
    public void savePhoto(String filePath, final String photoName) {
        Activity context= (Activity) iView.get();
        if(TextUtils.isEmpty(filePath)||TextUtils.isEmpty(photoName))
        {
            iView.get().showToast("empty photo file path or name");
            return;
        }
        iView.get().showProgress(null);
        CapturePhotoModel model=new CapturePhotoModel(context);
        model.savePhoto(filePath, photoName, new CameraContacts.Model.SavePhotoListener() {
            @Override
            public void onPhotoSaved(int code, String photoName,String filePath) {
                iView.get().dismissProgress();
                if(code>0)
                {
                    iView.get().showToast("photo saved");
                    SavePhotoEvent event=new SavePhotoEvent();
                    event.filePath=filePath;
                    event.name=photoName;
                    event.createTimestamp=System.currentTimeMillis();
                    EventBus.getDefault().post(event);
                    iView.get().goBack();
                }else
                {
                    iView.get().showToast("failed to save photo");
                    iView.get().goBack();
                }
            }
        });
    }
}
