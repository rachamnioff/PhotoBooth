package com.rakel.he.photo_booth.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.rakel.he.photo_booth.presenter.IPresenter;

import cn.pedant.SweetAlert.SweetAlertDialog;

public abstract class BaseActivity extends Activity implements IView{

    protected IPresenter iPresenter;
    protected SweetAlertDialog mDialog;

    public void goBack()
    {
        finish();
    }

    public void showToast(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_LONG);
    }

    public void dismissProgress()
    {
        if(mDialog!=null)
        {
            mDialog.dismiss();
        }
    }
    public void showProgress(String message)
    {
        if(mDialog==null) {
            mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            mDialog.setCancelable(false);
        }
        mDialog.setTitleText(message);
        mDialog.show();
    }
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
