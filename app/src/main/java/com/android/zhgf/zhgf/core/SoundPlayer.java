package com.android.zhgf.zhgf.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.android.zhgf.zhgf.R;

import java.util.HashMap;

/**
 * Created by zhuyt on 2017/11/8.
 */

public class SoundPlayer {
    public static String TAG = SoundPlayer.class.getSimpleName();

    private static SoundPlayer mInstance;
    public static SoundPlayer getmInstance(Context pCtx){
        if (mInstance == null) {
            mInstance = new SoundPlayer(pCtx);
        }
        return mInstance;
    }

    AudioManager audioManager = null;

    int iStreamID=-1;
    SoundPool mSP = null;
    HashMap<Integer, Integer> soundPoolMap = new HashMap<>();
    public SoundPlayer(Context pCtx){
        audioManager = (AudioManager) pCtx.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder tSoundBuilder = new SoundPool.Builder();
            tSoundBuilder.setMaxStreams(5);
            mSP = tSoundBuilder.build();

        }else{
            mSP = new SoundPool(3,AudioManager.STREAM_MUSIC,100);
        }

        soundPoolMap.put(0,mSP.load(pCtx,R.raw.sound1,1));
        soundPoolMap.put(1,mSP.load(pCtx,R.raw.sound2,1));
        soundPoolMap.put(2,mSP.load(pCtx,R.raw.sound3,1));
        soundPoolMap.put(3,mSP.load(pCtx,R.raw.sound6,1));
        soundPoolMap.put(4,mSP.load(pCtx,R.raw.sound8,1));
    }


    public int play(int soundID){
        iStreamID = mSP.play(soundPoolMap.get(soundID),1,1,0,0,1);
        return iStreamID;
    }

    public void stop(int pSid){
        mSP.stop(pSid);
    }

    public void pause(int pSid){
        mSP.pause(pSid);
    }


    /**
     * 切换麦克风和话筒
     * @param on
     */
    private synchronized void setSpeakerphoneOn(boolean on) {
        if(on) {
            audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_NORMAL);
        } else {
            audioManager.setMode(AudioManager.STREAM_VOICE_CALL);
            audioManager.setSpeakerphoneOn(false);//关闭扬声器
            audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
            //       把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
    }

}
