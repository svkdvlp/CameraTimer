package com.svk.cameratimerlib.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.cameraview.CameraView;
import com.svk.cameratimerlib.CameraTimer;
import com.svk.cameratimerlib.R;
import com.svk.cameratimerlib.model.ImageModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CameraActivity";

    Toolbar tb_head;
    CameraView cv_cam;
    RelativeLayout rl_capture,rl_capuredview;
    Button btn_capture,btn_ok,btn_retake;
    ImageView iv_capview;

    int targetSeconds=0;
    private Handler mBackgroundHandler;

    ArrayList<ImageModel> resultImageList;

    private CameraView.Callback mCallback = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "onPictureTaken " + data.length);

            if(data!=null){
                int maxSize = 816;
                BitmapFactory.Options opt=new BitmapFactory.Options();
                opt.inJustDecodeBounds=true;
                BitmapFactory.decodeByteArray(data, 0, data.length,opt);
                int srcSize=Math.max(opt.outWidth, opt.outHeight);
                System.out.println("out w:"+opt.outWidth+" h:"+opt.outHeight);
                opt.inSampleSize=maxSize <srcSize ? (srcSize/maxSize):1;
                System.out.println("sample size "+opt.inSampleSize);
                opt.inJustDecodeBounds=false;
                Bitmap tmp=BitmapFactory.decodeByteArray(data, 0, data.length,opt);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                tmp.compress(Bitmap.CompressFormat.PNG, 100, out);

                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                setupImageDisplay(decoded);
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        targetSeconds = getIntent().getIntExtra(CameraTimer.KEY_TIMELIFE,0);

        bindViews();

        init();
    }

    @Override
    protected void onPause() {
        cv_cam.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cv_cam.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }

    private void init() {
        btn_capture.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_retake.setOnClickListener(this);
        if (cv_cam != null) {
            cv_cam.addCallback(mCallback);
        }
        setupImageCapture();
    }

    private void bindViews() {
        tb_head = findViewById(R.id.tb_head);
        rl_capture = findViewById(R.id.rl_capture);
        rl_capuredview = findViewById(R.id.rl_capuredview);
        btn_capture = findViewById(R.id.btn_capture);
        btn_ok = findViewById(R.id.btn_ok);
        btn_retake = findViewById(R.id.btn_retake);
        iv_capview = findViewById(R.id.iv_capview);
        cv_cam = findViewById(R.id.cv_cam);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_capture){
            cv_cam.takePicture();
        }else if(view.getId() == R.id.btn_ok){
            setupImageCapture();
        }else if(view.getId() == R.id.btn_retake){
            setupImageCapture();
        }
    }

    private void setupImageCapture() {
        rl_capuredview.setVisibility(View.INVISIBLE);
        rl_capture.setVisibility(View.VISIBLE);
    }

    private void setupImageDisplay(Bitmap bm) {
        iv_capview.setImageBitmap(bm);
        rl_capuredview.setVisibility(View.VISIBLE);
        rl_capture.setVisibility(View.INVISIBLE);
    }
}
