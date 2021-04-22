package com.c323proj7.zacharyreid_imagegallery;

import java.io.Serializable;
import java.util.ArrayList;

public class GalleryArrayList extends ArrayList<GalleryImage> implements Serializable {
    private ArrayList<GalleryImage> gallery;

    public GalleryArrayList() {
        gallery = new ArrayList<>();
    }
}
