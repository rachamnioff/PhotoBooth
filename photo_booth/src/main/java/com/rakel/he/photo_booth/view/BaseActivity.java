package com.rakel.he.photo_booth.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.rakel.he.photo_booth.presenter.IPresenter;


public abstract class BaseActivity extends Activity implements IView{
    protected String TAG="PhotoBooth";
    protected IPresenter iPresenter;
    protected ProgressDialog mDialog;

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
            mDialog = new ProgressDialog(this);
            mDialog.setCancelable(false);
        }
        mDialog.setMessage(message);
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
