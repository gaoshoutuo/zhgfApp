package com.android.zhgf.zhgf.JavaBean;

/**
 * Created by TZXX001 on 2017/9/29.
 */

public class NewsDetailRoot {
    private int status;

    private NewsDetail newsDetail;

    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setNewsDetail(NewsDetail newsDetail){
        this.newsDetail = newsDetail;
    }
    public NewsDetail getNewsDetail(){
        return this.newsDetail;
    }
}
