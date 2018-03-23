package com.android.zhgf.zhgf.JavaBean.comment;

import com.android.zhgf.zhgf.JavaBean.News.PageInfo;

import java.util.List;

/**
 * Created by TZXX001 on 2017/10/11.
 */

public class Msg {
    private PageInfo PageInfo;

    private List<DataList> DataList ;

    public void setPageInfo(PageInfo PageInfo){
        this.PageInfo = PageInfo;
    }
    public PageInfo getPageInfo(){
        return this.PageInfo;
    }
    public void setDataList(List<DataList> DataList){
        this.DataList = DataList;
    }
    public List<DataList> getDataList(){
        return this.DataList;
    }
}
