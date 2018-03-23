package com.android.zhgf.zhgf.emit;

import android.util.Log;

import com.android.zhgf.zhgf.core.IEmitterCommand;

import org.json.JSONObject;

/**
 * Created by zhuyt on 2017/11/1.
 */

public class BaseEmitterCommand implements IEmitterCommand {
    public static String TAG = BaseEmitterCommand.class.getSimpleName();
    EmitterListener mListener = null;
    protected String EmitType = "";

    //构造
    public BaseEmitterCommand(EmitterListener em){
        mListener = em;
    }

    @Override
    public void doEmitter(JSONObject payload) {
        mListener.onEmitter(payload);
    }

    @Override
    public void doSender(JSONObject payload) {
        if (EmitType!="") {
            //RuningTime.getWSClient().SendToServer(EmitType,payload);
        }else{
            Log.e(TAG, "LoginEmitterCommand:::doError:: 未知的Emit类型");
        }
    }

    @Override
    public void doError(int status, String message) {
        Log.e(TAG, "LoginEmitterCommand:::doError:: 错误代码：" + status + " &服务器返回信息：" + message);
    }

    public interface EmitterListener{
        void onEmitter(JSONObject payload);
    }
}
