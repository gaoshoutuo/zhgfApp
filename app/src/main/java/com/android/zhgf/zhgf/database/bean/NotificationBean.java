package com.android.zhgf.zhgf.database.bean;

/**
 * Created by 71568 on 2017/11/25.
 */

public class NotificationBean {
    private int newsId;
    private int catId;
    private String menuName;
    private String title;

    public NotificationBean(int newsId, int catId, String menuName, String title) {
        this.newsId = newsId;
        this.catId = catId;
        this.menuName = menuName;
        this.title = title;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
