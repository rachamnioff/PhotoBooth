package com.rakel.he.photo_booth.presenter;

import com.rakel.he.photo_booth.view.IView;

public interface IPresenter {
    void register(IView view);
    void unRegister();
}
