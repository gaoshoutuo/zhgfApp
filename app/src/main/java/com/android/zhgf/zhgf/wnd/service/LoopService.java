package com.android.zhgf.zhgf.wnd.service;/**
 * Created by Administrator on 2016/4/30.
 */

import android.content.Context;

import android.os.Handler;
import android.util.Log;

import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.wnd.global.GlobalHandler;
import com.android.zhgf.zhgf.wnd.iface.INetwork;
import com.android.zhgf.zhgf.wnd.iface.impl.User;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;

/*******************************************************************************************************
 * Class Name:WndProject - LoopService
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title LoopService
 * @Package com.jiechu.wnd.service
 * @date 2016/4/30 22:50
 * @Description 向服务器轮循有没有命令进来
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class LoopService extends Thread implements Runnable{
    private static final String TAG = LoopService.class.getSimpleName();
    
    
    /**
    * String HTTP_LOOP_URL default "http://wnd.jx96871.cn/AppMobile/RequestOpenVideo";
    */
    private static String HTTP_LOOP_URL = "http://wnd.jx96871.cn/AppMobile/RequestOpenVideo";


    /**
    * int HTTP_LOOP_SLEEP_TIME default 5000;
    */
    private static int HTTP_LOOP_SLEEP_TIME = 10000;

    /**
    * Context m_ctx  default null;
    */
    private static AppApplication m_ctx  = null;
    /**
    * NetworkUtil mNet default null;
    */
    private static NetworkUtil mNet = null;

    /**
    * boolean isReceiveCommandAndExecute  default false;
    */
    private static boolean isReceiveCommandAndExecute  = false;

    /**
    * boolean isExitLoop  default false;
    */
    private static boolean isExitLoop  = false;

    private static Handler mGd;

    public LoopService(AppApplication p_ctx){
        m_ctx = p_ctx;
        mGd = m_ctx.getHandler();
        mNet = new NetworkUtil();
        HTTP_LOOP_URL = ((AppApplication)p_ctx).getConfigures().GetGPSURL("RequestOpenVideo");
        //HTTP_LOOP_URL = "http://192.168.10.188:8090/AppMobile/RequestOpenVideo";
    }


    public void reset() {
        if (!this.isInterrupted()) {
            this.interrupt();
        }
        isReceiveCommandAndExecute=false;
        isExitLoop=false;
    }



    @Override
    public void run() {
        while (!isExitLoop){
            Log.e(TAG, "LoopService::run: 服务器正在轮循中...............................................");
            try {
                Thread.sleep(HTTP_LOOP_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String mPhoneNumber="";
            if (m_ctx!=null) {
                try {
                    mPhoneNumber = (String) m_ctx.getUser().getCache(User.CACHE_KEY_USER_PHONE);
                }catch (Exception e){}
                if (mPhoneNumber==""){
                    isExitLoop = true;
                    return;
                }
            }

            try{
                mNet.HttpSendGetData(HTTP_LOOP_URL, "clientmb="+mPhoneNumber, new INetwork.OnNetworkStatusListener() {
                    @Override
                    public void onConnected(boolean isSuccess) {

                    }

                    @Override
                    public void onDisconnected(boolean isSuccess) {

                    }

                    @Override
                    public void onGetResult(boolean isSuccess, Object pData) {
                        NetworkUtil.HttpStatus mStatus = (NetworkUtil.HttpStatus)pData;
                        if (mStatus.getStatusCode()==200){
                            String result = (String) mStatus.getData();
                            Log.e(TAG, "onGetResult: LoopService返回的值："+result+"|isReceiveCommandAndExecute="+isReceiveCommandAndExecute);
                            if (Integer.parseInt(result)==1&&isReceiveCommandAndExecute==false){
                                Log.e(TAG, "onGetResult: 进来了");
                                mGd.obtainMessage(10086).sendToTarget();
                                isReceiveCommandAndExecute=true;
                            /*reset();*/
                            }else{
                                isReceiveCommandAndExecute=false;
                            }
                        }
                    }

                    @Override
                    public void onPostResult(boolean isSuccess, Object pData) {

                    }

                    @Override
                    public void onError(int errCode, String errMessage) {

                    }
                });
            }catch (Exception e){
                Log.e(TAG, "LoopService:"+e.getMessage());
            }
        }
    }


    @Override
    protected void finalize() throws Throwable {
        mNet = null;
        super.finalize();
    }
}
