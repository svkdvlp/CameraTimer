package com.svk.cameratimerlib.tasks;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 02-05-2018.
 */

public interface BitmapConversionListener {
    void onError();

    void onSuccess(Bitmap resultBitmap, String savedPath);

    void onLoading();
}

