package com.android.zhgf.zhgf.core.cmd;

import org.json.JSONObject;

/**
 * Created by zhuyt on 2017/11/2.
 */

public class OfferCommand extends BaseCommand {
    public static String TAG = OfferCommand.class.getSimpleName();

    private EmitterListener mListener;
    public void setListener(EmitterListener val){
        mListener = val;
    }
    public EmitterListener getListener(){
        return mListener;
    }

    public OfferCommand(){
        setEmitterType("OFFER");
    }

    @Override
    public void doEmitter(JSONObject payload) {
        mListener.onEmitter(payload);
    }
}
