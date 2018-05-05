package com.svk.camtimer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.svk.cameratimerlib.CameraTimer;
import com.svk.cameratimerlib.activity.CameraActivity;
import com.svk.cameratimerlib.model.ImageModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";

    public final static int REQ_CODE = 34342;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void startCamActivity(View v){

        //Lib
        CameraTimer.with(this)
                .timeLife(120)
                .requireImageCount(5)
                .startCameraActivity(REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE){
            if(resultCode == Activity.RESULT_OK){
                ArrayList<String> list = (ArrayList<String>)data.getSerializableExtra(CameraActivity.KEY_DATA);

                for (int i = 0; i < list.size() ; i++) {
                    Log.d(TAG, "onActivityResult : poisition "+ (i+1) + " : "+list.get(i));
                }
                Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();

            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
