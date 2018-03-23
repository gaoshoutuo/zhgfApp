package com.android.zhgf.zhgf.bean;

import android.graphics.Bitmap;

/**
 * Created by colorful on 2017-10-19.
 */

public class GridPicture {
    private String url;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GridPicture(String url) {
        this.url=url;
    }

}
