package com.android.zhgf.zhgf.bean;

import java.io.Serializable;

/**
 * Created by TZXX001 on 2017/9/26.
 */

public class StudyInfoEntity implements Serializable {

    /** ID */
    private String idStr;
    /** 学习内容标题 */
    private String titleStr;

    private String testSorceStr;

    private int buttonTypeInt;

    public String getTestSorceStr() {
        return testSorceStr;
    }

    public void setTestSorceStr(String testSorceStr) {
        this.testSorceStr = testSorceStr;
    }

    public int getButtonTypeInt() {
        return buttonTypeInt;
    }

    public void setButtonTypeInt(int buttonTypeInt) {
        this.buttonTypeInt = buttonTypeInt;
    }

    public String getTitleStr() {
        return titleStr;
    }

    public void setTitleStr(String titleStr) {
        this.titleStr = titleStr;
    }

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }


}
