package com.android.zhgf.zhgf.bean;

import com.android.zhgf.zhgf.JavaBean.comment.DataList;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by TZXX001 on 2017/10/24.
 */

public class NewsCommentEntity {
    /** ID */
    private String id;
    /** commentid */
    private String commentidStr;
    /** userIdStr */
    private String userIdStr;
    /** userNameStr */
    private String userNameStr;
    /** lastupdateStr */
    private String lastupdateStr;
    /** ipStr */
    private String ipStr;
    /** contentStr */
    private String contentStr;
    /** ipnameStr */
    private String ipnameStr;
    /** ipuserStr */
    private String ipuserStr;

    private String directionStr;

    public String getDirectionStr() {
        return directionStr;
    }

    public void setDirectionStr(String directionStr) {
        this.directionStr = directionStr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommentidStr() {
        return commentidStr;
    }

    public void setCommentidStr(String commentidStr) {
        this.commentidStr = commentidStr;
    }

    public String getUserIdStr() {
        return userIdStr;
    }

    public void setUserIdStr(String userIdStr) {
        this.userIdStr = userIdStr;
    }

    public String getUserNameStr() {
        return userNameStr;
    }

    public void setUserNameStr(String userNameStr) {
        this.userNameStr = userNameStr;
    }

    public String getLastupdateStr() {
        return lastupdateStr;
    }

    public void setLastupdateStr(String lastupdateStr) {
        this.lastupdateStr = lastupdateStr;
    }

    public String getIpStr() {
        return ipStr;
    }

    public void setIpStr(String ipStr) {
        this.ipStr = ipStr;
    }

    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    public String getIpnameStr() {
        return ipnameStr;
    }

    public void setIpnameStr(String ipnameStr) {
        this.ipnameStr = ipnameStr;
    }

    public String getIpuserStr() {
        return ipuserStr;
    }

    public void setIpuserStr(String ipuserStr) {
        this.ipuserStr = ipuserStr;
    }
    public ArrayList<NewsCommentEntity> getNewsCommentList(String jsonString){
        ArrayList<NewsCommentEntity> newsCommentEntities = new ArrayList<NewsCommentEntity>();
        ArrayList<DataList> newsCommentList = new ArrayList<DataList>();
        com.android.zhgf.zhgf.JavaBean.comment.Root root = new Gson().fromJson(jsonString,com.android.zhgf.zhgf.JavaBean.comment.Root.class);
        if (root == null){
            return null;
        }
        com.android.zhgf.zhgf.JavaBean.comment.Msg msg = root.getMsg();
        if (msg == null){
            return null;
        }
        newsCommentList = (ArrayList<DataList>) msg.getDataList();
        if(newsCommentList != null){
            for(int i = 0;i < newsCommentList.size();i++){
                DataList dataList = newsCommentList.get(i);
                NewsCommentEntity newsCommentEntity = new NewsCommentEntity();
                newsCommentEntity.setId(dataList.getId());
                newsCommentEntity.setCommentidStr(dataList.getCommentid());
                newsCommentEntity.setContentStr(dataList.getContent());
                newsCommentEntity.setIpnameStr(dataList.getIpname());
                newsCommentEntity.setIpStr(dataList.getIp());
                newsCommentEntity.setIpuserStr(dataList.getIpuser());
                newsCommentEntity.setLastupdateStr(dataList.getLastupdate());
                newsCommentEntity.setUserIdStr(dataList.getUserid());
                newsCommentEntity.setUserNameStr(dataList.getUsername());
                newsCommentEntity.setDirectionStr(dataList.getDirection());
                newsCommentEntities.add(newsCommentEntity);
            }
        }

        return newsCommentEntities;
    }
}
