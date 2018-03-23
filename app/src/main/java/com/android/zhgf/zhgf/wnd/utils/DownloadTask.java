package com.android.zhgf.zhgf.wnd.utils;/**
 * Created by Administrator on 2016/4/28.
 */

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Handler;

/*******************************************************************************************************
 * Class Name:WndProject - DownloadTask
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title DownloadTask
 * @Package com.jiechu.wnd.utils
 * @date 2016/4/28 11:29
 * @Description 下载
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class DownloadTask extends Thread implements Runnable{
    private Context mContext;
    private Handler mHandler;

    private OnDownloadListener mListener;

    private String mDownloadURL;

    public DownloadTask(Context p_ctx,Handler p_hnd){
        this.mContext = p_ctx;
        this.mHandler = p_hnd;
    }


    void Execute(String p_url,OnDownloadListener listener){
        mDownloadURL = p_url;
        mListener = listener;
        this.start();
    }

    @Override
    public void run() {
        byte[] mFileBytes = new byte[1];
        try {
            URL m_Url = new URL(this.mDownloadURL);
            HttpURLConnection m_Conn = (HttpURLConnection)m_Url.openConnection();
            m_Conn.setRequestMethod("GET");
            m_Conn.setRequestProperty("Connection", "Keep-Alive");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mListener!=null)mListener.onBegin(mDownloadURL);


        if (mListener!=null)mListener.onEnd(mFileBytes);
    }

    interface OnDownloadListener{
        void onBegin(String p_url);
        void onProgress(long loadedBytes, long totalBytes);
        void onEnd(byte[] p_data);
    }
}
