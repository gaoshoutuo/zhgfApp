package com.android.zhgf.zhgf.core;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhuyt on 2017/10/31.
 */

public class StatusEmitter implements Emitter.Listener {
    public static String TAG = StatusEmitter.class.getSimpleName();
    ICommand mCMD;
    public StatusEmitter(ICommand cmd){
        mCMD = cmd;
    }
    @Override
    public void call(Object... args) {
        JSONObject payload = (JSONObject)args[0];
        try {
            Log.e(TAG, "StatusEmitter:::call:: "+payload.getString("message"));
            int status = payload.getInt("status");
            if(status==0){
                mCMD.doEmitter(payload.getJSONObject("data"));
            }else{
                mCMD.doError(status,payload.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
