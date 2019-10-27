package com.rakel.he.photo_booth.view;

public interface IView {
    void bindView();
    void unbindView();
    void showProgress(String msg);
    void dismissProgress();
    void showToast(String msg);
    void goBack();
}