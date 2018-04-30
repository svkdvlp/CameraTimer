package com.svk.cameratimerlib.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.svk.cameratimerlib.CameraTimer;
import com.svk.cameratimerlib.R;
import com.svk.cameratimerlib.model.ImageModel;

import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {

    CameraTimer cTimerData;
    int targetSeconds=0;
    ArrayList<ImageModel> resultImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        cTimerData = (CameraTimer)getIntent().getSerializableExtra(CameraTimer.KEY_CT);
        targetSeconds = cTimerData.getSecondsLife();

    }
}
