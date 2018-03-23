package com.android.zhgf.zhgf.core;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by zhuyt on 2017/11/2.
 */

public class Utils {

    private static Utils mInstance=null;
    public static Utils getInstance(Context pCtx){
        if(mInstance==null){
            mInstance = new Utils(pCtx);
        }
        return mInstance;
    }

    private Context mCtx;
    public Utils(Context pCtx){
        mCtx = pCtx;
    }

    private int currVolume = 0;

    public void setMicMetu(){

    }
    /**
     * 打开扬声器
     */
    public void openSpeaker() {
        try{
            AudioManager audioManager = (AudioManager) mCtx.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if(!audioManager.isSpeakerphoneOn()) {
                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
                //audioManager.setMode(AudioManager.MODE_IN_CALL);

                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 关闭扬声器
     */
    public void closeSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) mCtx.getSystemService(Context.AUDIO_SERVICE);
            if(audioManager != null) {
                if(audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void playMusic(String mediaURL){

    }
}
