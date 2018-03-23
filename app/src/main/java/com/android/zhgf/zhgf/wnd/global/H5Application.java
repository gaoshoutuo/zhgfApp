package com.android.zhgf.zhgf.wnd.global;

import android.app.Application;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.zhgf.zhgf.wnd.service.GPSListener;
import com.android.zhgf.zhgf.wnd.service.LoopService;

/**
 * Created by zhuyt on 2016/9/4.
 */
public class H5Application extends Application {
    private static String TAG = H5Application.class.getSimpleName();
    private GlobalHandlerExt mHandler;
    private LoopService mLooper;
    private GPSListener mGps;
    public H5Application(){

        mHandler = new GlobalHandlerExt();
        //mLooper = new LoopService(getApplicationContext());
        //mGps = new GPSListener(H5Application.this);


    }

    public GlobalHandlerExt getHandler(){
        return mHandler;
    }

    class GlobalHandlerExt extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 666:
                    Location l = (Location)msg.obj;
                    Log.e(TAG, "handleMessage:GPS坐标取到了 "+l.getLatitude()+"|"+l.getLongitude());
                    break;
                default:
                    break;
            }
        }
    }
}
