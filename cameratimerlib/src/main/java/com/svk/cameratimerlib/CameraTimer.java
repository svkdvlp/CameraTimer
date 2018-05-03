package com.svk.cameratimerlib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.svk.cameratimerlib.activity.CameraActivity;

import java.io.Serializable;

import static com.svk.cameratimerlib.utils.BitmapUtils.deleteFilesFromPrivateStorage;

/**
 * Coded by SvK
 */
public class CameraTimer{

    public static final String TAG = "CameraTimer";
    public static final String KEY_TIMELIFE = "data_time";
    public static final String KEY_IMGCOUNT = "img_count";

    private Context mContext;
    private int secondsLife;
    private int countImages;
    private int requestCode;

    private CameraTimer(Context mContext) {
        this.mContext=mContext;
        this.secondsLife=0;
        this.requestCode =-1;
        deleteFilesFromPrivateStorage(mContext);
    }

    public static CameraTimer with(Context ctx){
        return new CameraTimer(ctx);
    }

    public CameraTimer timeLife(int seconds){
        this.secondsLife = seconds;
        return this;
    }

    public CameraTimer requireImageCount(int count){
        this.countImages = count;
        return this;
    }

    public void startCameraActivity(int reqCode){
        this.requestCode = reqCode;

        if(mContext!=null && !(mContext instanceof Activity)){
            throw new IllegalArgumentException("Context must be an activity");
        }else if(secondsLife<=0){
            throw new IllegalArgumentException("TimeLife must be bigger than zero value");
        }else if(countImages<=0){
            throw new IllegalArgumentException("Image count must be bigger than zero value");
        }

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent camIntent = new Intent((Activity)mContext,CameraActivity.class);
            camIntent.putExtra(KEY_TIMELIFE,secondsLife);
            camIntent.putExtra(KEY_IMGCOUNT,countImages);
            ((Activity)mContext).startActivityForResult(camIntent, requestCode);
        }else{
            Toast.makeText(mContext, "Camera access permission is required", Toast.LENGTH_SHORT).show();
        }
    }
}
