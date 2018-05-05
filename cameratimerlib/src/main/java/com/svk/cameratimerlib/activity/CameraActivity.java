package com.svk.cameratimerlib.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.svk.cameratimerlib.CameraTimer;
import com.svk.cameratimerlib.R;
import com.svk.cameratimerlib.tasks.BitmapConversionListener;
import com.svk.cameratimerlib.tasks.BitmapConversionTask;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CameraActivity";
    public static final String KEY_DATA = "data_Arr";

    Toolbar tb_head;
    CameraView cv_cam;
    RelativeLayout rl_capture,rl_capuredview;
    Button btn_capture,btn_ok,btn_retake;
    TextView tv_timer,tv_counter;
    ImageView iv_capview;

    int targetSeconds = 0;
    int targetCount = 0;
    long targetMillis,elapsedMillis=0;

    ProgressDialog mProgressDialog;

    ArrayList<String> resultImageList;
    Bitmap currentBitmap;
    String currentSavedPath;

    CountDownTimer mTimer;

    private int mCurrentFlash;

    private BitmapConversionListener bmListener = new BitmapConversionListener() {
        @Override
        public void onError() {
            hideLoading();
            Toast.makeText(CameraActivity.this, "Some problem found", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(Bitmap resultBitmap, String savedPath) {
            hideLoading();
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

    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON

    };


    private static final int[] FLASH_ICONS = {
            R.drawable.ic_action_flashauto,
            R.drawable.ic_action_flashoff,
            R.drawable.ic_action_flashon

    };

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
        if(mTimer!=null){
            mTimer.cancel();
            mTimer = null;
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
        setSupportActionBar(tb_head);

        btn_capture.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_retake.setOnClickListener(this);


        if (cv_cam != null) {
            cv_cam.addCallback(mCallback);
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);

        tv_counter.setText(formatCounterString(resultImageList.size()));

        setupImageCapture();

        startTimer(targetSeconds,1000);
    }

    private void bindViews() {
        tb_head = (Toolbar) findViewById(R.id.tb_head);
        rl_capture = (RelativeLayout) findViewById(R.id.rl_capture);
        rl_capuredview = (RelativeLayout) findViewById(R.id.rl_capuredview);
        btn_capture = (Button) findViewById(R.id.btn_capture);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_retake = (Button) findViewById(R.id.btn_retake);
        iv_capview = (ImageView) findViewById(R.id.iv_capview);
        cv_cam = (CameraView) findViewById(R.id.cv_cam);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        tv_counter = (TextView) findViewById(R.id.tv_counter);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.switch_flash).setIcon(FLASH_ICONS[0]);
        cv_cam.setFlash(FLASH_OPTIONS[0]);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.switch_flash) {
            flashLightToggle(item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_capture){
            Toast.makeText(this, "On Take Image", Toast.LENGTH_SHORT).show();
            cv_cam.takePicture();
        }else if(view.getId() == R.id.btn_ok){
            actionForCapturedData();
        }else if(view.getId() == R.id.btn_retake){
            currentSavedPath = null;
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
            tv_counter.setText(formatCounterString(resultImageList.size()));
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

    public void startTimer(final int tsecs, final long tick) {

        targetMillis = tsecs*1000;

        mTimer = new CountDownTimer(targetMillis, tick) {

            public void onTick(long millisUntilFinished) {
                targetMillis = millisUntilFinished;
                elapsedMillis = (targetSeconds*1000) - millisUntilFinished;

                if(rl_capture !=null && rl_capture.getVisibility() == View.VISIBLE){
                    tv_timer.setText(formatTimeString(targetMillis));
                }
            }

            public void onFinish() {
                if(rl_capture !=null && rl_capture.getVisibility() == View.VISIBLE){
                    tv_timer.setText("0:00");
                }
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }.start();
    }

    public String formatTimeString(long ms){
        return String.format(Locale.getDefault(),
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(ms) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(ms) % TimeUnit.MINUTES.toSeconds(1));
    }

    public String formatCounterString(int count){
        String str = String.valueOf(count) + "/" + targetCount;
        return str;
    }



    private void flashLightToggle(MenuItem item) {

        if (cv_cam != null) {
            mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
            item.setIcon(FLASH_ICONS[mCurrentFlash]);
            cv_cam.setFlash(FLASH_OPTIONS[mCurrentFlash]);
        }
    }
}
