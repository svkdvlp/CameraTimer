package com.svk.cameratimerlib.tasks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.svk.cameratimerlib.utils.BitmapUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.Result;

/**
 * Created by Administrator on 02-05-2018.
 */

public class BitmapConversionTask extends AsyncTask<String, Void, String> {

    private Bitmap resultBitmap;
    private String resultPath;
    private byte[] data;
    private String currentImageName;
    private BitmapConversionListener listener;
    private Context ctx;

    public BitmapConversionTask(Context ctx ,byte[] data , String fname,BitmapConversionListener mListener) {
        this.listener = mListener;
        this.currentImageName=fname;
        this.data = data;
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        resultBitmap = getResizedBitmap();
        //resultBitmap = BitmapUtils.getScaledDownBitmap()
        resultPath = BitmapUtils.saveToInternalStorage(ctx,resultBitmap,currentImageName);


        if(resultBitmap != null && resultPath != null){
            return "1";
        }else{
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if(result!=null) {
            listener.onSuccess(resultBitmap, resultPath);
        }else {
            listener.onError();
        }
    }

    @Override
    protected void onPreExecute() {
        listener.onLoading();
    }

    private Bitmap getResizedBitmap(){
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
            return BitmapUtils.getScaledDownBitmap(decoded,800,false);
        }
        return null;
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
