package com.android.zhgf.zhgf.JavaBean.News;

/**
 * Created by TZXX001 on 2017/10/11.
 */

public class PageInfo {
    private String TotalRecord;

    private int TotalPage;

    private int CurrPage;

    private String PageSize;

    public void setTotalRecord(String TotalRecord){
        this.TotalRecord = TotalRecord;
    }
    public String getTotalRecord(){
        return this.TotalRecord;
    }
    public void setTotalPage(int TotalPage){
        this.TotalPage = TotalPage;
    }
    public int getTotalPage(){
        return this.TotalPage;
    }
    public void setCurrPage(int CurrPage){
        this.CurrPage = CurrPage;
    }
    public int getCurrPage(){
        return this.CurrPage;
    }
    public void setPageSize(String PageSize){
        this.PageSize = PageSize;
    }
    public String getPageSize(){
        return this.PageSize;
    }
}
