package com.android.zhgf.zhgf.JavaBean;
import java.util.List;
/**
 * Created by TZXX001 on 2017/9/20.
 */

public class TitleMsg {
    private int status;

    private List<CategoryList> msg ;

    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setMsg(List<CategoryList> msg){
        this.msg = msg;
    }
    public List<CategoryList> getMsg(){
        return this.msg;
    }
}
