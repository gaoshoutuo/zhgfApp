package com.android.zhgf.zhgf.bean;

import android.util.Log;

import com.android.zhgf.zhgf.JavaBean.News.DataList;
import com.android.zhgf.zhgf.JavaBean.News.Msg;
import com.android.zhgf.zhgf.JavaBean.News.PageInfo;
import com.android.zhgf.zhgf.JavaBean.News.Root;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZXX001 on 2017/9/20.
 */

public class NewsInfoEntity implements Serializable{
    /** ID */
    private String catid;
    /** 新闻ID */
    private String newsId;
    /** 标题 */
    private String title;
    /** 新闻源 */
    private String source;
    /** 新闻源地址 URL */
    private String source_url;
    /** 发布时间 */
    private Long publishTime;
    /** 评论数量 */
    private String commentNum;
    /** 点击数量 */
    private String hits;

    public String getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }

    /** 图片1 URL */
    private String picOne;
    /** 图片2 URL */
    private String picTwo;
    /** 图片3 URL */
    private String picThr;
    /** 图片 列表 */
    private List<String> picList;

    // 新闻详细
    private String description;

    private String username;

    private String inputtime;

    private String updatetime;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInputtime() {
        return inputtime;
    }

    public void setInputtime(String inputtime) {
        this.inputtime = inputtime;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.inputtime = updatetime;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public String getCatId() {
        return catid;
    }

    public void setCatId(String catid) {
        this.catid = catid;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public String getPicOne() {
        return picOne;
    }

    public void setPicOne(String picOne) {
        this.picOne = picOne;
    }

    public String getPicTwo() {
        return picTwo;
    }

    public void setPicTwo(String picTwo) {
        this.picTwo = picTwo;
    }

    public String getPicThr() {
        return picThr;
    }

    public void setPicThr(String picThr) {
        this.picThr = picThr;
    }

    public List<String> getPicList() {
        return picList;
    }

    public void setPicList(List<String> picList) {
        this.picList = picList;
    }

    // 新闻资讯json解析
    public ArrayList<NewsInfoEntity> getNewsList(String jsonString) {
        ArrayList<NewsInfoEntity> newsInfoEntity = new ArrayList<NewsInfoEntity>();
        ArrayList<DataList> newsList = new ArrayList<DataList>();
        Msg msg = new Msg();
        Root root = new Gson().fromJson(jsonString,Root.class);
        if(root != null){
            msg = root.getMsg();
            if(msg != null){
                newsList = (ArrayList<DataList>) msg.getDataList();
                if(newsList != null){
                    for(int i = 0;i < newsList.size();i++){
                        DataList dataList = newsList.get(i);
                        List<String> url_list = new ArrayList<String>();
                        NewsInfoEntity newsInfo = new NewsInfoEntity();
                        newsInfo.setCatId(dataList.getCatid());
                        newsInfo.setNewsId(dataList.getId());
                        newsInfo.setDescription(dataList.getDescription());
                        newsInfo.setInputtime(dataList.getInputtime());
                        newsInfo.setTitle(dataList.getTitle());
                        newsInfo.setSource_url(dataList.getUrl());
                        newsInfo.setCommentNum(dataList.getComment_count());
                        newsInfo.setHits(dataList.getHits());
                        newsInfo.setUsername(dataList.getUsername());
                        if (!(dataList.getThumb().equals(""))) {
                            url_list.add(dataList.getThumb());
                            newsInfo.setPicList(url_list);
                        }
                        newsInfoEntity.add(newsInfo);
                    }

                }
            }
        //拿到数组
        /*JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("DataList");*/
        //for (JsonElement news : jsonArray) {

        }
        return newsInfoEntity;
    }
    // 取得分页信息
    public PageInfo getPageInfo(String jsonString){
        PageInfo pageInfo = new PageInfo();
        Msg msg = new Msg();
        Root root = new Gson().fromJson(jsonString,Root.class);
        if(root == null){return null;}
        msg = root.getMsg();
        pageInfo = msg.getPageInfo();
        return pageInfo;

    }
}
