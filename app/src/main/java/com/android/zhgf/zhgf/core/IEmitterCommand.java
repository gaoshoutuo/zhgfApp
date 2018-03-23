package com.android.zhgf.zhgf.core;

import org.json.JSONObject;

/**
 * Created by zhuyt on 2017/11/1.
 */

public interface IEmitterCommand {
    //// TODO: 2017/11/1 doEmitter为WebSocket在接受到事件后触发
    void doEmitter(JSONObject payload);
    //// TODO: 2017/11/1 doSender为发送WebSocket事件，需要程序员在代码中触发
    void doSender(JSONObject payload);
    void doError(int status, String message);
}
