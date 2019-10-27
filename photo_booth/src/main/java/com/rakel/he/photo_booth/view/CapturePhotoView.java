package com.rakel.he.photo_booth.view;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Mode;
import com.rakel.he.photo_booth.R;
import com.rakel.he.photo_booth.contacts.CameraContacts;
import com.rakel.he.photo_booth.presenter.CapturePhotoPresenter;
import com.rakel.he.photo_booth.presenter.IPresenter;

import java.io.File;

public class CapturePhotoView extends BaseActivity implements CameraContacts.View {
    private CameraView mCameraView;
    private String PHOTO_DIR="photo_booth";
    private String mFilePathForCapturePhoto;
    private PopupWindow mPopWindow;
    @Override
    public void capturePhoto(CapturePhotoListener callBack) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected IPresenter createPresenter() {
        return new CapturePhotoPresenter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.open();
    }

    private void initView()
    {
        setContentView(R.layout.activity_capture_photo);
        ((TextView)findViewById(R.id.tittle_bar_tittle)).setText(R.string.camera);
        findViewById(R.id.tittle_bar_right_menu).setVisibility(View.GONE);
        findViewById(R.id.tittle_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.capture_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mCameraView.takePicture();
            }
        });
        initCameraView();
    }

    private void showNameInputDialog()
    {
        mPopWindow=new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setTouchable(false);
        mPopWindow.setOutsideTouchable(false);
        View view= LayoutInflater.from(this).inflate(R.layout.layout_popup_input,null);
        mPopWindow.setContentView(view);
        final EditText editText=view.findViewById(R.id.input_photo_name);
        view.findViewById(R.id.input_confirm_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String inputName=editText.getText().toString();
                if(TextUtils.isEmpty(inputName))
                {
                    showToast(getString(R.string.err_input_name_empty));
                    return;
                }
                ((CapturePhotoPresenter)iPresenter).savePhoto(mFilePathForCapturePhoto,inputName);
            }
        });
        mPopWindow.showAtLocation(findViewById(R.id.tittle_bar_tittle), Gravity.CENTER_VERTICAL,0,0);
    }


    private void initCameraView()
    {
        mCameraView=findViewById(R.id.capture_camera_view);
        mCameraView.setMode(Mode.PICTURE);
        final File dir=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+PHOTO_DIR);
        if(!dir.exists())
            dir.mkdirs();
        mCameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
                final String fileName=dir.getAbsolutePath()+File.pathSeparator
                        +System.currentTimeMillis()+".jpg";
                result.toFile(new File(fileName), new FileCallback() {
                    @Override
                    public void onFileReady(@Nullable File file) {
                        mFilePathForCapturePhoto=file.getAbsolutePath();
                        showNameInputDialog();
                    }
                });
            }
        });
    }

}
