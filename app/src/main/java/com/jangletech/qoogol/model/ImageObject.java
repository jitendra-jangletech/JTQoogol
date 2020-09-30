package com.jangletech.qoogol.model;

import android.net.Uri;


public class ImageObject {
    private String name;

    public ImageObject(String name) {
        this.name = name;
    }

    private Uri imageUri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
