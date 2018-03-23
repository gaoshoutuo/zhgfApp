package com.android.zhgf.zhgf.wnd.iface;/**
 * Created by Administrator on 2016/4/15.
 */

import android.content.Context;

import java.util.Map;

/*******************************************************************************************************
 * Class Name:WndProject - INetwork
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title INetwork
 * @Package com.jiechu.wnd.iface
 * @date 2016/4/15 20:54
 * @Description 创建一个网络操作与通信的工具类
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public interface INetwork {

    String getLanIP(Context pContext);
    String getInternetIP();
    String getVPNIP();
    boolean checkEnable(Context pContext);

    void HttpSendGetData(String pURL, String pParams, OnNetworkStatusListener listener);
    void HttpSendPostData(String pURL, Map<String, Object> pParams, OnNetworkStatusListener listener);
    void HttpSendPostDataByFormEncode(String pURL, Map<String, Object> pParams, OnNetworkStatusListener listener);

    public interface OnNetworkStatusListener{
        /*@Deprecated*/
        void onConnected(boolean isSuccess);
        /*@Deprecated*/
        void onDisconnected(boolean isSuccess);

        void onGetResult(boolean isSuccess, Object pData);
        void onPostResult(boolean isSuccess, Object pData);

        void onError(int errCode, String errMessage);
    }

    public interface  OnProgressListener{
        void onProgress(long loadedBytes, long totalBytes);
    }

}
