package com.svk.cameratimerlib.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.svk.cameratimerlib.CameraTimer;
import com.svk.cameratimerlib.R;
import com.svk.cameratimerlib.tasks.BitmapConversionListener;
import com.svk.cameratimerlib.tasks.BitmapConversionTask;

import java.util.ArrayList;


public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CameraActivity";
    public static final String KEY_DATA = "data_Arr";

    Toolbar tb_head;
    CameraView cv_cam;
    RelativeLayout rl_capture,rl_capuredview;
    Button btn_capture,btn_ok,btn_retake;
    ImageView iv_capview;

    int targetSeconds = 0;
    int targetCount = 0;

    ProgressDialog mProgressDialog;

    ArrayList<String> resultImageList;
    Bitmap currentBitmap;
    String currentSavedPath;

    private BitmapConversionListener bmListener = new BitmapConversionListener() {
        @Override
        public void onError() {
            hideLoading();
            Toast.makeText(CameraActivity.this, "Some problem found", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(Bitmap resultBitmap, String savedPath) {
            hideLoading();
            Toast.makeText(CameraActivity.this, "On success" + resultBitmap.getHeight() + " w "+ resultBitmap.getWidth(), Toast.LENGTH_SHORT).show();
            currentBitmap = resultBitmap;
            currentSavedPath = savedPath;
            setupImageDisplay(currentBitmap);
        }

        @Override
        public void onLoading() {
            showLoading();
        }
    };

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
                new BitmapConversionTask(CameraActivity.this,data,getFileName(), bmListener).execute();
            }else{
                Toast.makeText(CameraActivity.this, "No data on capture", Toast.LENGTH_SHORT).show();
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        targetSeconds = getIntent().getIntExtra(CameraTimer.KEY_TIMELIFE,0);
        targetCount = getIntent().getIntExtra(CameraTimer.KEY_IMGCOUNT,5);
        resultImageList = new ArrayList<>();

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
        if (bmListener != null) {
            bmListener = null;
        }
        super.onDestroy();
    }

    private void showLoading() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }


    private void init() {
        btn_capture.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_retake.setOnClickListener(this);

        if (cv_cam != null) {
            cv_cam.addCallback(mCallback);
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);

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
            Toast.makeText(this, "On Take Image", Toast.LENGTH_SHORT).show();
            cv_cam.takePicture();
        }else if(view.getId() == R.id.btn_ok){
            actionForCapturedData();
        }else if(view.getId() == R.id.btn_retake){
            currentSavedPath=null;
            currentBitmap = null;
            setupImageCapture();
        }
    }

    private void actionForCapturedData() {
        if(resultImageList.size() < targetCount){
            resultImageList.add(currentSavedPath);
            if(resultImageList.size() == targetCount){
                Intent resultIntent = new Intent();
                resultIntent.putStringArrayListExtra(KEY_DATA,resultImageList);
                setResult(RESULT_OK,resultIntent);
                finish();
            }
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED,null);
        finish();
    }

    public String getFileName() {
        int imgNameCount = resultImageList.size()+1;
        return "image_"+imgNameCount+".png" ;
    }
}
