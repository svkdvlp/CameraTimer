package com.svk.cameratimerlib.model;

import android.os.Parcelable;

import java.io.Serializable;

public class ImageModel implements Serializable{

    public String imgData;

    public ImageModel() {
    }

    public String getImgData() {
        return imgData;
    }

    public void setImgData(String imgData) {
        this.imgData = imgData;
    }
}
