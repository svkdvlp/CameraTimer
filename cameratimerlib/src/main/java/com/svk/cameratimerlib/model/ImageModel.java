package com.svk.cameratimerlib.model;

public class ImageModel {
    public int id;
    public String imgData;

    public ImageModel(int id, String imgData) {
        this.id = id;
        this.imgData = imgData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgData() {
        return imgData;
    }

    public void setImgData(String imgData) {
        this.imgData = imgData;
    }
}
