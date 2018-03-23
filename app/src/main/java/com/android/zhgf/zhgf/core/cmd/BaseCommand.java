package com.android.zhgf.zhgf.core.cmd;

import android.util.Log;

import com.android.zhgf.zhgf.core.ICommand;
import com.android.zhgf.zhgf.core.WSClient;

import org.json.JSONObject;

/**
 * Created by zhuyt on 2017/11/2.
 */

public class BaseCommand implements ICommand {
    public static String TAG = BaseCommand.class.getSimpleName();

    protected String EmitterType;
    protected void setEmitterType(String val){
        EmitterType = val;
    }
    protected String getEmitterType(){
        return EmitterType;
    }


    @Override
    public void doEmitter(JSONObject payload) {

    }

    @Override
    public void doError(int status, String message) {

    }

    public void SendMessage(String type,JSONObject payload){
        Log.e(TAG, "BaseCommand:::SendMessage:: 正在向服务器发送信息::::类型："+type);
        WSClient.getInstance().SendMessage(type,payload);
    }
}
