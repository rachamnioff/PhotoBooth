package com.rakel.he.photo_booth.contacts;

import android.content.Context;

import com.rakel.he.photo_booth.model.PhotoBean;

import java.util.List;

public interface PhotoContacts {

    interface View {
        void onPhotoesLoaded(List<PhotoBean> beans);
    }

    interface Presenter {
        void loadPhotoes();
    }

    interface Model {

        void loadPhotoes(ModelListener modelListener);

        interface ModelListener {

            void completed(List<PhotoBean> photoes);

        }

    }
}