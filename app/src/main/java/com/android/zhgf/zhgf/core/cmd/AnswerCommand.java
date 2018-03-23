package com.android.zhgf.zhgf.core.cmd;

import com.android.zhgf.zhgf.core.ICommand;

import org.json.JSONObject;

/**
 * Created by zhuyt on 2017/11/2.
 */

public class AnswerCommand extends BaseCommand{
    private ICommand.EmitterListener mListener;
    public void setListener(ICommand.EmitterListener val){
        mListener = val;
    }
    public ICommand.EmitterListener getListener(){
        return mListener;
    }

    public AnswerCommand(){
        setEmitterType("ANSWER");
    }

    @Override
    public void doEmitter(JSONObject payload) {
        mListener.onEmitter(payload);
    }
}
