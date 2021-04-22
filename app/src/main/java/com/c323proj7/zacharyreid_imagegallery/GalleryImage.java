package com.c323proj7.zacharyreid_imagegallery;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;

import java.io.Serializable;

public class GalleryImage implements Serializable {
    private String farm;
    private String id;
    private String server;
    private String secret;
    private String stringURL;
    private Bitmap bitmap;
    private String title;
    private boolean saved = false;
    private boolean defaultMode = false;
    private final String DEFAULT_TITLE = "NO IMAGE";
    private final int DEFAULT_IMAGE = R.drawable.default_image;

    public GalleryImage(String farm, String id, String server, String secret, String title) {
        this.farm = farm;
        this.id = id;
        this.server = server;
        this.secret = secret;
        this.stringURL = "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+".jpg";
        this.title = title;

        if(farm.equals("") || id.equals("") || server.equals("") || secret.equals("")) {
            defaultMode = true;
        }
    }

    public GalleryImage() {
        this.defaultMode = true;
    }

    public String getFarm() { return farm; }

    public String getID() { return id; }

    public String getServer() { return server; }

    public String getSecret() { return secret; }

    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }

    public Bitmap getBitmap() { return bitmap; }

    public boolean isDefaultMode() { return defaultMode; }

    public String getStringURL() { return stringURL; }

    public String getTitle() { return title; }

    public void saved() { saved = true; }

    public boolean isSaved() { return saved; }

    public String getDefaultTitle() { return DEFAULT_TITLE; }

    public int getDefaultImage() { return DEFAULT_IMAGE; }

}
