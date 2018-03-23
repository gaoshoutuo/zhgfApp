package com.android.zhgf.zhgf.bean;

import com.android.zhgf.zhgf.JavaBean.NewsDetail;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TZXX001 on 2017/9/29.
 */

public class NewsDetailEntity implements Serializable {

    /** ID */
    private String id;
    /** catID */
    private String catId;
    /** 标题 */
    private String title;
    /** 内容 */
    private String content;
    /** 更新时间 */
    private Long inputTime;
    /** 评论数量 */
    private Integer commentNum;
    /** 图片1 URL */
    private String picOne;
    /** 图片2 URL */
    private String picTwo;
    /** 图片3 URL */
    private String picThr;
    /** 图片 列表 */
    private List<String> picList;
    /** 新闻源  */
    private String copyFrom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getInputTime() {
        return inputTime;
    }

    public void setInputTime(Long inputTime) {
        this.inputTime = inputTime;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
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

    public String getCopyFrom() {
        return copyFrom;
    }

    public void setCopyFrom(String copyFrom) {
        this.copyFrom = copyFrom;
    }
    // 新闻详细内容json解析
    public NewsDetailEntity getNewsDetail(String jsonString) {
        //
        JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        String str = jsonObject.getAsJsonObject("msg").toString();
        Gson gson = new Gson();
        NewsDetail newsDetailbean = gson.fromJson(str, NewsDetail.class);
        NewsDetailEntity newsDetail = new NewsDetailEntity();
        if(newsDetailbean != null){
            List<String> url_list = new ArrayList<String>();
            newsDetail.setCatId(newsDetailbean.getCatid());
            newsDetail.setId(newsDetailbean.getId());
            newsDetail.setTitle(newsDetailbean.getTitle());
            //匹配img标签的正则表达式 删除img标签
            String regxpForImgTag = "<img\\s[^>]+/>";
            Pattern pattern = Pattern.compile(regxpForImgTag);
            String result = newsDetailbean.getContent();
            /*Matcher matcher = pattern.matcher(result);
            while (matcher.find()) {
                String temp = matcher.group();
                String tempUrl = temp.substring(temp.indexOf("src=") + 5);
                String urlResult = "<img alt=\"\" src=\"" + "http://img1.gtimg.com/news/pics/hv1/197/152/2205/143419082.jpg" + "\" style=\"width: 600px; height: 399px;\" />";
                result = result.replace(temp, urlResult);
            }*/
            newsDetail.setContent(result);
            //newsDetail.setContent(newsDetailbean.getContent());
            newsDetail.setCopyFrom(newsDetailbean.getCopyfrom());
            newsDetail.setInputTime(Long.parseLong(newsDetailbean.getInputtime()));
            if (!(newsDetailbean.getThumb().equals(""))) {
                url_list.add(newsDetailbean.getThumb());
                newsDetail.setPicList(url_list);
            }
        }
        return newsDetail;
    }

    /*// 新闻资讯json解析
    public NewsDetail getNewsDetail(String jsonString) {
        ArrayList<NewsDetailEntity> newsDetailList = new ArrayList<NewsDetailEntity>();
        Gson gson = new Gson();
        NewsDetailRoot newsDetailRoot = gson.fromJson(jsonString, NewsDetailRoot.class);

        return newsDetailRoot.getNewsDetail();
    }*/
}
