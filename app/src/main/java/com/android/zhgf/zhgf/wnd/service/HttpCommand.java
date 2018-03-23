package com.android.zhgf.zhgf.wnd.service;/**
 * Created by Administrator on 2016/4/20.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import android.util.Log;

import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.wnd.global.GlobalHandler;
import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.wnd.incompatible.NotifyIncompatible;


/*******************************************************************************************************
 * Class Name:WndProject - HttpCommand
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title HttpCommand
 * @Package com.jiechu.wnd.service
 * @date 2016/4/20 12:38
 * @Description 与服务器的命令交互
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class HttpCommand extends Service{
    private static final String TAG = HttpCommand.class.getSimpleName();
    private static final String HTTP_URL = "http://wnd.jx96871.cn/";


    /*TODO 向服务端发送GPS坐标信息的时间间隔*/
    public static final int SEND_GIS_TIME_SPEC=1500;
    /*TODO 未取GPS地址未知时重试的次数*/
    public static final int UNKNOW_LOCATION_TRY_MAX = 1000;

    /*TODO Android通知对象管理器*/
    private NotifyIncompatible mNotify;

    private GlobalHandler.GlobalHandlerExt mHandler;
    /*TODO 是否有服务端发送命令过来,默认为false,当命令接收到后，程式修改其值为true，请在用户确认命令后在程式中设置为false*/
    private boolean isHttpCommandReceive=false;

    private LoopService mLooper;
    public LoopService getLooper(){
        return mLooper;
    }
    private Thread mGPSService;
    public Thread getGPSService(){
        return mGPSService;
    }
    boolean isGPSRuning=false;

    @Override
    public void onCreate() {
        super.onCreate();

        //mHandler = ((GlobalHandlerExt)getApplication()).getHandler();
        /**********************************************************************************************
         * TODO Notification处理
         **********************************************************************************************/


        /*****************************************************************************
         * GPS定位启动
         *****************************************************************************/
       /* mGPSService = new Thread(new Runnable() {
            @Override
            public void run() {
                new GPSService((GlobalHandler)getApplication());
                isGPSRuning=true;
            }
        });
        mGPSService.start();*/


    }

    void startGPS(){
        if (mGPSService.isInterrupted()&& null!=mGPSService){
            mGPSService.start();
            isGPSRuning=true;
        }
    }
    void stopGPS(){
        if (!mGPSService.isInterrupted() && null!=mGPSService){
            mGPSService.interrupt();
            isGPSRuning=false;
        }
    }

    public void openActivity(Intent p_Int){
        startActivity(p_Int);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand execute");
        ((AppApplication)getApplication()).getService().addService(HttpCommand.this);
        mNotify = new NotifyIncompatible(getApplicationContext());
        try {
            mNotify.HeighNotify(getResources().getString(R.string.app_name),
                    R.drawable.ic_menu_send,
                    getResources().getString(R.string.notify_content_title),
                    getResources().getString(R.string.notify_content_text));

            //TODO测试mHandler
           /* Thread.sleep(3000);
            if (mHandler==null)mHandler=((GlobalHandler)getApplication()).getHandler();
            mHandler.sendEmptyMessage(300001);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*****************************************************************************
         * 服务端轮循启动
         *****************************************************************************/
        Log.e(TAG, "HttpCommand::onCreate: 启动轮循服务");
        mLooper = new LoopService((AppApplication) getApplication());
        mLooper.reset();
        mLooper.start();

        /*****************************************************************************
         * GPS定位启动
         *****************************************************************************/
        /*mGPSService = new Thread(new Runnable() {
            @Override
            public void run() {
                new GPSService((GlobalHandler)getApplication());
                isGPSRuning=true;
            }
        });
        mGPSService.start();*/


        GPSListener mGPSListener = new GPSListener((AppApplication) getApplication());
        //return super.onStartCommand(intent, START_STICKY, startId);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "HttpCommand::onDestroy: 服务销毁");
        //mNotify = null;
        mLooper.interrupt();
        mLooper=null;
       // ((AppApplication)getApplication()).getService().removeService(HttpCommand.class.getSimpleName());
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {

        return new MyBinder();
    }

    class MyBinder extends Binder{
        /**
         * @ClassName: MyBinder
         * @Method: get
         * @Params -
         * @Return HttpCommand
         * @Description: 获取HttpCommand对象实例
         * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
         * @date 2016/4/21 9:50
         * ${tags}
         */
        HttpCommand get(){
            return HttpCommand.this;
        }
    }

   /* void updateGPSNotify(float x,float y){
        String str_gps = "当前坐标：经度%d1 | 纬度%d2";
        str_gps = str_gps.replace("%d1",""+x);
        str_gps = str_gps.replace("%d2",""+y);
        mNotify.updateHeighNotify(getResources().getString(R.string.notify_content_title), str_gps);
    }*/
}
