package com.android.zhgf.zhgf.JavaBean.News;

/**
 * Created by TZXX001 on 2017/10/11.
 */

public class Root {
    private int status;

    private Msg msg;

    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setMsg(Msg msg){
        this.msg = msg;
    }
    public Msg getMsg(){
        return this.msg;
    }
}
