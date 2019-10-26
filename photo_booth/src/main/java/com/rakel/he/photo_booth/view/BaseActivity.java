package com.rakel.he.photo_booth.view;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.rakel.he.photo_booth.presenter.IPresenter;

public abstract class BaseActivity extends Activity implements IView{

    protected IPresenter iPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindView();
    }

    @Override
    public void bindView() {
        iPresenter = createPresenter();
        iPresenter.register(this);
    }

    @Override
    public void unbindView() {
        iPresenter.unRegister();
    }

    protected abstract IPresenter createPresenter();

}
