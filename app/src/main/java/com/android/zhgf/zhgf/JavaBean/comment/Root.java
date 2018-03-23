package com.android.zhgf.zhgf.JavaBean.comment;


/**
 * Created by TZXX001 on 2017/10/11.
 */

public class Root {
    private int status;

    private com.android.zhgf.zhgf.JavaBean.comment.Msg msg;

    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setMsg(com.android.zhgf.zhgf.JavaBean.comment.Msg msg){
        this.msg = msg;
    }
    public Msg getMsg(){
        return this.msg;
    }
}
