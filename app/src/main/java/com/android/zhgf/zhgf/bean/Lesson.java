package com.android.zhgf.zhgf.bean;

/**
 * Created by TZXX001 on 2017/11/17.
 */

public class Lesson {

    private String url;

    private String lessonTitle;

    public Lesson (String url,String lessonTitle){
        this.url = url;
        this.lessonTitle = lessonTitle;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }
}
