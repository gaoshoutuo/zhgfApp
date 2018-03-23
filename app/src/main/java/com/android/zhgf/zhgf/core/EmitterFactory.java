package com.android.zhgf.zhgf.core;

import java.util.HashMap;

/**
 * Created by zhuyt on 2017/11/1.
 */

public class EmitterFactory {
    public static String TAG = EmitterFactory.class.getSimpleName();
    private static EmitterFactory mInstance=null;
    public static EmitterFactory getInstance(){
        if(mInstance==null){
            mInstance = new EmitterFactory();
        }
        return mInstance;
    }

    private HashMap<Enum,String> mEmitterLables = new HashMap<>();
    public String getLable(Enum e){
        return mEmitterLables.get(e);
    }
    public void addLable(Enum e,String lable){
        mEmitterLables.put(e,lable);
    }
    public void removeLable(Enum e){
        mEmitterLables.remove(e);
    }


    private HashMap<Enum,IEmitterCommand> mEmitterCommandMap = new HashMap<>();

    public IEmitterCommand get(EVENTS lable){
        return mEmitterCommandMap.get(lable);
    }

    public void add(Enum lable,IEmitterCommand cmd){
        mEmitterCommandMap.put(lable,cmd);
    }

    public void remove(Enum lable){
        mEmitterCommandMap.remove(lable);
    }

    public int size(){
        return mEmitterCommandMap.size();
    }

    public enum EVENTS{
        EVENT_LOGIN,
        EVENT_LOGOUT,
        EVENT_INVISIT,
        EVENT_ACCEPT,
        EVENT_REJECT,
        EVENT_OFFER,
        EVENT_ANSWER,
        EVENT_CANDIDATE,
        EVENT_HANGUP,

    }
}
