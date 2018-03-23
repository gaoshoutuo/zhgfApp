package com.android.zhgf.zhgf.core;

import android.Manifest;
import android.app.Application;
import android.content.Context;

/**
 * Created by zhuyt on 2017/11/1.
 */

public class RuningTime extends Application {
    public static String TAG = RuningTime.class.getSimpleName();
    public static boolean isBusy_Caller=false;
    public static boolean isBusy_Callee=false;
    public static String[] mPermissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String WS_URL = "http://192.168.10.107:8088";
    public static String getWebSocketHostURL(){
        return WS_URL;
    }
    private static String[] Ice_List= new String[]{
            "stun:23.21.150.121",
            "stun:stun.l.google.com:19302"
    };
    public static String[] getIceServers(){
        return Ice_List;
    }

    public static WSClient getWSClient(){
        return WSClient.getInstance();
    }

    public static RTCClient getRTCClient(Context pCtx){
        return RTCClient.getInstance(pCtx);
    }
    public static RTCClient getRTCClient(){
        return RTCClient.getInstance();
    }

    public static Utils getUtils(Context pCtx){
        return Utils.getInstance(pCtx);
    }
}
