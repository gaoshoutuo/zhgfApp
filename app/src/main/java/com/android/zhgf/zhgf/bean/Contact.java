package com.android.zhgf.zhgf.bean;

/**
 * Created by TZXX001 on 2017/9/18.
 */

public class Contact {
    private String aName;
    private int aIcon;

    public Contact() {
    }

    public Contact(String aName,  int aIcon) {
        this.aName = aName;
        this.aIcon = aIcon;
    }

    public String getaName() {
        return aName;
    }

    public int getaIcon() {
        return aIcon;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public void setaIcon(int aIcon) {
        this.aIcon = aIcon;
    }
}
