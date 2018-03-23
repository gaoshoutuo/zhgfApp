package com.android.zhgf.zhgf.wnd.utils;/**
 * Created by Administrator on 2016/4/27.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.Toast;

import cn.nodemedia.LivePublisher;
import cn.nodemedia.LivePublisher.LivePublishDelegate;

/*******************************************************************************************************
 * Class Name:WndProject - RtmpVideo
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title RtmpVideo
 * @Package com.jiechu.wnd.utils
 * @date 2016/4/27 19:11
 * @Description
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class RtmpVideo {
    private static final String TAG = RtmpVideo.class.getSimpleName();
    public static final int BUFFER_TIME = 2000;
    public static final int BUFFER_TIME_MAX = 10000;

    /**
     * @Field Context m_ctx
     * */
    public   Context m_ctx= null;
    public SurfaceView m_sfv;


    boolean isStarting=false;
    public RtmpVideo(Context p_ctx,SurfaceView p_sfv){
        m_ctx = p_ctx;
        m_sfv = p_sfv;

        LivePublisher.init(p_ctx);
        LivePublisher.setDelegate(new LivePublishDelegate());
        LivePublisher.setAudioParam(32 * 1000, LivePublisher.AAC_PROFILE_HE);
        LivePublisher.setVideoParam(640, 360, 15, 400 * 1000, LivePublisher.AVC_PROFILE_MAIN);
        LivePublisher.setDenoiseEnable(true);
        /*LivePublisher.startPreview(m_sfv, ((Activity) m_ctx).getWindowManager().getDefaultDisplay().getRotation(), LivePublisher.CAMERA_BACK);*/
        //LivePublisher.startPreview(m_sfv, 270, LivePublisher.CAMERA_BACK);

        Log.e("VideoChat.Live", this.toString());

    }

    public void start(String p_url){
        LivePublisher.startPreview(m_sfv, ((Activity) m_ctx).getWindowManager().getDefaultDisplay().getRotation(), LivePublisher.CAMERA_BACK);
        LivePublisher.startPublish(p_url);
        LivePublisher.setFlashEnable(true);
    }

    public void stop(){
        LivePublisher.setFlashEnable(false);
        LivePublisher.stopPreview();
        LivePublisher.stopPublish();
    }




    class LivePublishDelegate implements LivePublisher.LivePublishDelegate{

        @Override
        public void onEventCallback(int e, String arg1) {
            // TODO Auto-generated method stub
            handler_publisher.sendEmptyMessage(e);
        }

    }


    /**
     * 视频发布的处理
     * */
    private Handler handler_publisher = new Handler() {
        // 回调处理
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2000:
                    Toast.makeText(m_ctx, "正在发布视频", Toast.LENGTH_SHORT).show();
                    break;
                case 2001:
                    Toast.makeText(m_ctx, "视频发布成功", Toast.LENGTH_SHORT).show();
                    isStarting = true;
                    break;
                case 2002:
                    Toast.makeText(m_ctx, "视频发布失败", Toast.LENGTH_SHORT).show();
                    break;
                case 2004:
                    Toast.makeText(m_ctx, "视频发布结束", Toast.LENGTH_SHORT).show();
                    isStarting = false;
                    break;
                case 2005:
                    Toast.makeText(m_ctx, "网络异常,发布中断", Toast.LENGTH_SHORT).show();
                    break;
                case 2100:
                    // mic off
                    Toast.makeText(m_ctx, "麦克风静音", Toast.LENGTH_SHORT).show();
                    break;
                case 2101:
                    // mic on
                    Toast.makeText(m_ctx, "麦克风恢复", Toast.LENGTH_SHORT).show();
                    break;
                case 2102:
                    // camera off

                    Toast.makeText(m_ctx, "摄像头传输关闭", Toast.LENGTH_SHORT).show();
                    break;
                case 2103:
                    // camera on
                    Toast.makeText(m_ctx, "摄像头传输打开", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
