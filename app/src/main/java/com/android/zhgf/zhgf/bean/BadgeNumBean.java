package com.android.zhgf.zhgf.bean;

import java.io.Serializable;

/**
 * Created by 71568 on 2017/12/4.
 */

public class BadgeNumBean implements Serializable {
    int catId;
    int notReadCount;

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public int getNotReadCount() {
        return notReadCount;
    }

    public void setNotReadCount(int notReadCount) {
        this.notReadCount = notReadCount;
    }
}
