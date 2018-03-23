package com.android.zhgf.zhgf.core.cmd;

import com.android.zhgf.zhgf.core.ICommand;

import org.json.JSONObject;

/**
 * Created by zhuyt on 2017/11/8.
 */

public class OffLineCommand extends BaseCommand {

    public static String TAG = OfferCommand.class.getSimpleName();

    private ICommand.EmitterListener mListener;
    public void setListener(ICommand.EmitterListener val){
        mListener = val;
    }
    public ICommand.EmitterListener getListener(){
        return mListener;
    }

    public OffLineCommand(){
        setEmitterType("OFFLINE");
    }

    @Override
    public void doEmitter(JSONObject payload) {
        super.doEmitter(payload);
    }
}
