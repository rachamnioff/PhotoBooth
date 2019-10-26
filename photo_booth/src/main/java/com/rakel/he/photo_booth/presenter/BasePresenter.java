package com.rakel.he.photo_booth.presenter;

import com.rakel.he.photo_booth.view.IView;

import java.lang.ref.WeakReference;

public class BasePresenter implements IPresenter {

    protected WeakReference iView;

    @Override
    public void register(IView view) {
        iView = new WeakReference(view);
    }

    @Override

    public void unRegister() {
        iView.clear();
    }
}
