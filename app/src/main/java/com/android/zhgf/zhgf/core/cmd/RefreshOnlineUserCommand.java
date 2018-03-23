package com.android.zhgf.zhgf.core.cmd;

import org.json.JSONObject;

/**
 * Created by zhuyt on 2017/11/2.
 */

public class RefreshOnlineUserCommand extends BaseCommand{
    public static String TAG = RefreshOnlineUserCommand.class.getSimpleName();

    public static RefreshOnlineUserCommand mInstance=null;
    public static RefreshOnlineUserCommand getInstance(){
        return mInstance;
    }


    private EmitterListener mListener;
    public void setListener(EmitterListener val){
        mListener = val;
    }
    public EmitterListener getListener(){
        return mListener;
    }

    public RefreshOnlineUserCommand(){
        setEmitterType("REFRESHONLINEUSERS");
        mInstance = RefreshOnlineUserCommand.this;
    }
    @Override
    public void doEmitter(JSONObject payload) {
        if (mListener!=null){
            mListener.onEmitter(payload);
        }
    }

    @Override
    public void doError(int status, String message) {
        super.doError(status, message);
    }


}
