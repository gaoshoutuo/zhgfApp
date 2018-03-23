package com.android.zhgf.zhgf.core;

import org.json.JSONObject;

/**
 * Created by zhuyt on 2017/11/2.
 */

public interface ICommand {
    void doEmitter(JSONObject payload);
    void doError(int status, String message);

    public interface EmitterListener{
        void onEmitter(JSONObject payload);
    }
}
