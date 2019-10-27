package com.rakel.he.photo_booth.contacts;

public class CameraContacts {
    public interface View {
        void capturePhoto(CapturePhotoListener callBack);

        interface CapturePhotoListener
        {
            void onPhotoCaptured(String filePath,String name);
        }
    }

    public interface Presenter
    {
        void savePhoto(String filePath,String name);
    }

    public interface Model
    {
        void savePhoto(String filePath,String name,SavePhotoListener callback);
        interface SavePhotoListener
        {
            void onPhotoSaved(int code,String photoName,String filePath);
        }
    }
}
