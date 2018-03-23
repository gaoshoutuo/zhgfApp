package com.android.zhgf.zhgf.core.cmd;

import android.util.Log;

import com.android.zhgf.zhgf.core.RuningTime;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhuyt on 2017/11/2.
 */

public class LoginCommand extends BaseCommand {
    public static String TAG = LoginCommand.class.getSimpleName();



    private EmitterListener mListener;
    public void setListener(EmitterListener val){
        mListener = val;
    }
    public EmitterListener getListener(){
        return mListener;
    }

    public LoginCommand(){
        setEmitterType("LOGIN");
    }

    @Override
    public void doEmitter(JSONObject payload) {
        try {
            RuningTime.getWSClient().setUsername(payload.getString("username"));
            RuningTime.getWSClient().setSocketID(payload.getString("socket_id"));
            RuningTime.getWSClient().setRoomname(payload.getString("room_name"));
            if (mListener!=null){
                mListener.onEmitter(payload);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void doError(int status, String message) {
        Log.e(TAG, "LoginCommand:::doError:: 服务器返回错误【" + status + "】"+message);
    }
}
