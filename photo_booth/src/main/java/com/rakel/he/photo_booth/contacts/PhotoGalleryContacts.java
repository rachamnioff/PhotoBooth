package com.rakel.he.photo_booth.contacts;

import com.rakel.he.photo_booth.model.PhotoBean;

import java.util.List;

public interface PhotoGalleryContacts {
    String FILE_PATH="FilePath";
    String PHOTO_NAME="PhotoName";

    interface View {
        void onPhotoesLoaded(List<PhotoBean> beans);
    }

    interface Presenter {
        void loadPhotoes();
        void showPhotoDetail(String filePath,String photoName);
        void capturePhoto();
    }

    interface Model {

        void loadPhotoes(ModelListener modelListener);

        interface ModelListener {

            void completed(List<PhotoBean> photoes);

        }

    }
}