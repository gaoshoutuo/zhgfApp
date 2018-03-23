package com.android.zhgf.zhgf.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 71568 on 2017/12/11.
 */

public class IntelligenceHistoryBean implements Serializable {
    //既然如此不如直接去去map
        private String totalid;
    //一个id
        private String pid;
    //另一个 id
        private String uploadtime;
    //上传时间
        private String qbfl;
    //上传者手机号码
        private String wbnr;
    //上传内容 照片 音频文件 视频文件 文件大小
        private String zp;
        private String ypwj;
        private String spwj;
        private double wjdx;
        private String x;
    //经纬度  然后后面不用管了

        private String y;
        private int isenabled;
        private Date CreateDate;
        private String CreateUserId;
        private String CreateUserName;
        private String ModifyDate;
        private String ModifyUserId;
        private String ModifyUserName;

    public IntelligenceHistoryBean(String totalid, String uploadtime, String qbfl, String wbnr, String zp, String ypwj, String spwj, String x, String y) {
        this.totalid = totalid;
        this.uploadtime = uploadtime;
        this.qbfl = qbfl;
        this.wbnr = wbnr;
        this.zp = zp;
        this.ypwj = ypwj;
        this.spwj = spwj;
        this.x = x;
        this.y = y;
    }

    public void setTotalid(String totalid) {
            this.totalid = totalid;
        }
        public String getTotalid() {
            return totalid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }
        public String getPid() {
            return pid;
        }

        public void setUploadtime(String uploadtime) {
            this.uploadtime = uploadtime;
        }
        public String getUploadtime() {
            return uploadtime;
        }

        public void setQbfl(String qbfl) {
            this.qbfl = qbfl;
        }
        public String getQbfl() {
            return qbfl;
        }

        public void setWbnr(String wbnr) {
            this.wbnr = wbnr;
        }
        public String getWbnr() {
            return wbnr;
        }

        public void setZp(String zp) {
            this.zp = zp;
        }
        public String getZp() {
            return zp;
        }

        public void setYpwj(String ypwj) {
            this.ypwj = ypwj;
        }
        public String getYpwj() {
            return ypwj;
        }

        public void setSpwj(String spwj) {
            this.spwj = spwj;
        }
        public String getSpwj() {
            return spwj;
        }

        public void setWjdx(double wjdx) {
            this.wjdx = wjdx;
        }
        public double getWjdx() {
            return wjdx;
        }

        public void setX(String x) {
            this.x = x;
        }
        public String getX() {
            return x;
        }

        public void setY(String y) {
            this.y = y;
        }
        public String getY() {
            return y;
        }

        public void setIsenabled(int isenabled) {
            this.isenabled = isenabled;
        }
        public int getIsenabled() {
            return isenabled;
        }

        public void setCreateDate(Date CreateDate) {
            this.CreateDate = CreateDate;
        }
        public Date getCreateDate() {
            return CreateDate;
        }

        public void setCreateUserId(String CreateUserId) {
            this.CreateUserId = CreateUserId;
        }
        public String getCreateUserId() {
            return CreateUserId;
        }

        public void setCreateUserName(String CreateUserName) {
            this.CreateUserName = CreateUserName;
        }
        public String getCreateUserName() {
            return CreateUserName;
        }

        public void setModifyDate(String ModifyDate) {
            this.ModifyDate = ModifyDate;
        }
        public String getModifyDate() {
            return ModifyDate;
        }

        public void setModifyUserId(String ModifyUserId) {
            this.ModifyUserId = ModifyUserId;
        }
        public String getModifyUserId() {
            return ModifyUserId;
        }

        public void setModifyUserName(String ModifyUserName) {
            this.ModifyUserName = ModifyUserName;
        }
        public String getModifyUserName() {
            return ModifyUserName;
        }

    }

