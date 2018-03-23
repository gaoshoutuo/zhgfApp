package com.android.zhgf.zhgf.core.cmd;

import org.json.JSONObject;

/**
 * Created by zhuyt on 2017/11/2.
 */

public class InvisitCommand extends BaseCommand {
    public static String TAG = InvisitCommand.class.getSimpleName();

    public InvisitCommand(){
        setEmitterType("INIST");
    }


    private EmitterListener mListener;
    public void setListener(EmitterListener val){
        mListener = val;
    }
    public EmitterListener getListener(){
        return mListener;
    }


    @Override
    public void doEmitter(JSONObject payload) {
        mListener.onEmitter(payload);
    }
}
