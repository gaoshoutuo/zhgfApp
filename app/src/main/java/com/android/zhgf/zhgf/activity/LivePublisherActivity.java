package com.android.zhgf.zhgf.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.wnd.global.GlobalHandler;
import com.android.zhgf.zhgf.wnd.iface.INetwork;
import com.android.zhgf.zhgf.wnd.iface.impl.User;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;

import cn.nodemedia.LivePublisher;
import cn.nodemedia.LivePublisher.LivePublishDelegate;

public class LivePublisherActivity extends Activity implements OnClickListener, LivePublishDelegate {
    private static final String TAG = LivePublisherActivity.class.getSimpleName();
    private SurfaceView sv;
    private Button micBtn, swtBtn, videoBtn, flashBtn, camBtn;
    private boolean isStarting = false;
    private boolean isMicOn = true;
    private boolean isCamOn = true;
    private boolean isFlsOn = true;

    private Button capBtn;


    private User mUser=null;
    private String sPhonenumber;
    private String PUBLISH_REAL_URL="rtmp://wnnd.jx96871.cn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livepublish);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mUser = new User(this);

        sPhonenumber = (String)mUser.getCache(User.CACHE_KEY_USER_PHONE);
//        if (sPhonenumber==""){
//            finish();
//        }

        String sRTMPURL = ((AppApplication)getApplication()).getConfigures().GetLiveURL();
        PUBLISH_REAL_URL = sRTMPURL+sPhonenumber;
        //PUBLISH_REAL_URL = sRTMPURL+"test";//测试用的
        /*PUBLISH_REAL_URL="rtmp://192.168.168.104/live/"+sPhonenumber;*/ //测试用的
        isStarting = false;
        sv = (SurfaceView) findViewById(R.id.cameraView);
        micBtn = (Button) findViewById(R.id.button_mic);
        swtBtn = (Button) findViewById(R.id.button_sw);
        videoBtn = (Button) findViewById(R.id.button_video);
        flashBtn = (Button) findViewById(R.id.button_flash);
        camBtn = (Button) findViewById(R.id.button_cam);
        capBtn = (Button) findViewById(R.id.pub_cap_button);

        micBtn.setOnClickListener(this);
        swtBtn.setOnClickListener(this);
        videoBtn.setOnClickListener(this);
        flashBtn.setOnClickListener(this);
        camBtn.setOnClickListener(this);
        capBtn.setOnClickListener(this);
        capBtn.setVisibility(View.GONE);
        LivePublisher.init(this); // 1.初始化
        LivePublisher.setDelegate(this); // 2.设置事件回调

        /**
         * 设置输出音频参数 码率 32kbps 使用HE-AAC ,部分服务端不支持HE-AAC,会导致发布失败
         */
        LivePublisher.setAudioParam(32 * 1000, LivePublisher.AAC_PROFILE_HE);

        /**
         * 设置输出视频参数 宽 640 高 360 fps 15 码率 300kbps 以下建议分辨率及比特率 不用超过1280x720
         * 320X180@15 ~~ 200kbps 480X272@15 ~~ 250kbps 568x320@15 ~~ 300kbps
         * 640X360@15 ~~ 400kbps 720x405@15 ~~ 500kbps 854x480@15 ~~ 600kbps
         * 960x540@15 ~~ 700kbps 1024x576@15 ~~ 800kbps 1280x720@15 ~~ 1000kbps
         * 使用main profile
         */
        /*LivePublisher.setVideoParam(640, 360, 15, 400 * 1000, LivePublisher.AVC_PROFILE_BASELINE);*/
        LivePublisher.setVideoParam(960, 540, 15, 400 * 1000, LivePublisher.AVC_PROFILE_BASELINE);



        /**
         * 是否开启背景噪音抑制
         */
        LivePublisher.setDenoiseEnable(true);

        /**
         * 开始视频预览， cameraPreview ： 用以回显摄像头预览的SurfaceViewd对象，如果此参数传入null，则只发布音频
         * interfaceOrientation ： 程序界面的方向，也做调整摄像头旋转度数的参数， camId：
         * 摄像头初始id，LivePublisher.CAMERA_BACK 后置，LivePublisher.CAMERA_FRONT 前置
         */
        /*LivePublisher.startPreview(sv, getWindowManager().getDefaultDisplay().getRotation(), LivePublisher.CAMERA_FRONT);*/ // 5.开始预览
        LivePublisher.startPreview(sv, getWindowManager().getDefaultDisplay().getRotation(), LivePublisher.CAMERA_FRONT);

        if((boolean) getIntent().getSerializableExtra("isOpenVideo")){
            Log.e(TAG, "onClick: 正在发布："+PUBLISH_REAL_URL);
            LivePublisher.startPublish(PUBLISH_REAL_URL);
            SendOpenVideoCommand(1);
        }
        // 如果传null
        // 则只发布音频
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 注意：如果你的业务方案需求只做单一方向的视频直播，可以不处理这段

        // 如果程序UI没有锁定屏幕方向，旋转手机后，请把新的界面方向传入，以调整摄像头预览方向
        LivePublisher.setCameraOrientation(getWindowManager().getDefaultDisplay().getRotation());

        // 还没有开始发布视频的时候，可以跟随界面旋转的方向设置视频与当前界面方向一致，但一经开始发布视频，是不能修改视频发布方向的了
        // 请注意：如果视频发布过程中旋转了界面，停止发布，再开始发布，是不会触发"onConfigurationChanged"进入这个参数设置的
        if (!isStarting) {
            switch (getWindowManager().getDefaultDisplay().getRotation()) {
                case Surface.ROTATION_0:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_LANDSCAPE);
                    break;
                case Surface.ROTATION_180:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT_REVERSE);
                    break;
                case Surface.ROTATION_270:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_LANDSCAPE_REVERSE);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LivePublisher.stopPreview();
        LivePublisher.stopPublish();
        SendOpenVideoCommand(0);
    }

    @Override
    public void onClick(View arg0) {
        Log.e(TAG, "onClick: Pushlish Click;");
        switch (arg0.getId()) {
            case R.id.pub_cap_button:
                String capFilePath = Environment.getExternalStorageDirectory().getPath() + "/pub_cap.jpg";
                if (LivePublisher.capturePicture(capFilePath)) {
                    Toast.makeText(LivePublisherActivity.this, "截图保存到 " + capFilePath, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LivePublisherActivity.this, "截图保存失败", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.button_mic:
                if (isStarting) {
                    isMicOn = !isMicOn;
                    LivePublisher.setMicEnable(isMicOn); // 设置是否打开麦克风
                    if (isMicOn) {
                        handler.sendEmptyMessage(3101);
                    } else {
                        handler.sendEmptyMessage(3100);
                    }
                }
                break;
            case R.id.button_sw:
                LivePublisher.switchCamera();// 切换前后摄像头
                LivePublisher.setFlashEnable(false);// 关闭闪光灯,前置不支持闪光灯
                isFlsOn = false;
                flashBtn.setBackgroundResource(R.mipmap.ic_flash_off);
                break;
            case R.id.button_video:
                if (isStarting) {
                    LivePublisher.stopPublish();// 停止发布
                    SendOpenVideoCommand(0);
                } else {
                    /**
                     * 设置视频发布的方向，此方法为可选，如果不调用，则输出视频方向跟随界面方向，如果特定指出视频方向，
                     * 在startPublish前调用设置 videoOrientation ： 视频方向 VIDEO_ORI_PORTRAIT
                     * home键在 下 的 9:16 竖屏方向 VIDEO_ORI_LANDSCAPE home键在 右 的 16:9 横屏方向
                     * VIDEO_ORI_PORTRAIT_REVERSE home键在 上 的 9:16 竖屏方向
                     * VIDEO_ORI_LANDSCAPE_REVERSE home键在 左 的 16:9 横屏方向
                     */
                    // LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT);

                    /**
                     * 设置发布模式
                     * 参考 rtmp_specification_1.0.pdf 7.2.2.6. publish
                     * LivePublisher。PUBLISH_TYPE_LIVE			'live' 发布类型
                     * LivePublisher。PUBLISH_TYPE_RECORD		'record' 发布类型
                     * LivePublisher。PUBLISH_TYPE_APPEND		'append' 发布类型
                     */
                    /*LivePublisher.setPublishType(LivePublisher.PUBLISH_TYPE_LIVE);*/
                    LivePublisher.setPublishType(LivePublisher.PUBLISH_TYPE_LIVE);


                    /**
                     * 开始视频发布 rtmpUrl rtmp流地址
                     */
                    /*String pubUrl = "rtmp://112.11.127.18:110/live/13567345583";
                    String pubUrl = "rtmp://127.0.0.1/live/13567345583";*/
                    /*LivePublisher.startPublish(pubUrl);*/
                    Log.e(TAG, "onClick: 正在发布："+PUBLISH_REAL_URL);
                    LivePublisher.startPublish(PUBLISH_REAL_URL);
                    SendOpenVideoCommand(1);
                }
                break;
            case R.id.button_flash:
                int ret = -1;
                if (isFlsOn) {
                    ret = LivePublisher.setFlashEnable(false);
                } else {
                    ret = LivePublisher.setFlashEnable(true);
                }
                if (ret == -1) {
                    // 无闪光灯,或处于前置摄像头,不支持闪光灯操作
                } else if (ret == 0) {
                    // 闪光灯被关闭
                    flashBtn.setBackgroundResource(R.mipmap.ic_flash_off);
                    isFlsOn = false;
                } else {
                    // 闪光灯被打开
                    flashBtn.setBackgroundResource(R.mipmap.ic_flash_on);
                    isFlsOn = true;
                }
                break;
            case R.id.button_cam:
                if (isStarting) {
                    isCamOn = !isCamOn;
                    LivePublisher.setCamEnable(isCamOn);
                    if (isCamOn) {
                        handler.sendEmptyMessage(3103);
                    } else {
                        handler.sendEmptyMessage(3102);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onEventCallback(int event, String msg) {
        handler.sendEmptyMessage(event);
    }

    private Handler handler = new Handler() {
        // 回调处理
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2000:
                    Toast.makeText(LivePublisherActivity.this, "正在发布视频", Toast.LENGTH_SHORT).show();
                    break;
                case 2001:
                    Toast.makeText(LivePublisherActivity.this, "视频发布成功", Toast.LENGTH_SHORT).show();
                    videoBtn.setBackgroundResource(R.mipmap.ic_video_start);
                    isStarting = true;
                    break;
                case 2002:
                    Toast.makeText(LivePublisherActivity.this, "视频发布失败", Toast.LENGTH_SHORT).show();
                    break;
                case 2004:
                    Toast.makeText(LivePublisherActivity.this, "视频发布结束", Toast.LENGTH_SHORT).show();
                    videoBtn.setBackgroundResource(R.mipmap.ic_video_stop);
                    isStarting = false;
                    break;
                case 2005:
                    Toast.makeText(LivePublisherActivity.this, "网络异常,发布中断", Toast.LENGTH_SHORT).show();
                    break;
                case 2100:
                    //发布端网络阻塞，已缓冲了2秒的数据在队列中
                    Toast.makeText(LivePublisherActivity.this, "网络阻塞，发布卡顿", Toast.LENGTH_SHORT).show();
                    break;
                case 2101:
                    //发布端网络恢复畅通
                    Toast.makeText(LivePublisherActivity.this, "网络恢复，发布流畅", Toast.LENGTH_SHORT).show();
                    break;


                case 3100:
                    // mic off
                    micBtn.setBackgroundResource(R.mipmap.ic_mic_off);
                    Toast.makeText(LivePublisherActivity.this, "麦克风静音", Toast.LENGTH_SHORT).show();
                    break;
                case 3101:
                    // mic on
                    micBtn.setBackgroundResource(R.mipmap.ic_mic_on);
                    Toast.makeText(LivePublisherActivity.this, "麦克风恢复", Toast.LENGTH_SHORT).show();
                    break;
                case 3102:
                    // camera off
                    camBtn.setBackgroundResource(R.mipmap.ic_cam_off);
                    Toast.makeText(LivePublisherActivity.this, "摄像头传输关闭", Toast.LENGTH_SHORT).show();
                    break;
                case 3103:
                    // camera on
                    camBtn.setBackgroundResource(R.mipmap.ic_cam_on);
                    Toast.makeText(LivePublisherActivity.this, "摄像头传输打开", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    void SendOpenVideoCommand(final int video_open){
        Log.w(TAG, "SendOpenVideoCommand:  带我装B带我飞吧");
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                /*String HttpURL = "http://wnd.jx96871.cn/AppMobile/AppVideoOpen?";*/
                String HttpURL = ((AppApplication)getApplication()).getConfigures().GetLOGINURL("AppVideoOpen");
                //String HttpURL = "http://192.168.10.188:8090/AppMobile/AppVideoOpen";
                //String m_phone = (String)mUser.getUserCacheDataCheckAndToLogin("PhoneNumber");
                String m_phone = (String)mUser.getCache(User.CACHE_KEY_USER_PHONE);
                //String m_phone = "";
                String params = "Account="+m_phone;
                if (video_open>0) {
                    params+="&VideoStats=1";
                }
                //String ResultString = new NetWorkUtils().HttpSendGetData(HttpURL,params);
                (new NetworkUtil()).HttpSendGetData(HttpURL, params, new INetwork.OnNetworkStatusListener() {
                    @Override
                    public void onConnected(boolean isSuccess) {

                    }

                    @Override
                    public void onDisconnected(boolean isSuccess) {

                    }

                    @Override
                    public void onGetResult(boolean isSuccess, Object pData) {
                        NetworkUtil.HttpStatus mHttp = (NetworkUtil.HttpStatus) pData;
                        if (mHttp.getStatusCode() == 200) {
                            Log.e(TAG, TAG + "->onGetResult: 向服务器注册视频直播事件成功");
                            Log.w(TAG, "onGetResult: "+mHttp.getData());
                            //((AppApplication) LivePublisherActivity.this.getApplication()).getHandler().sendEmptyMessage(400001);
                            Handler handler = ((AppApplication)getApplication()).getHandler();
                            handler.sendEmptyMessage(400001);
                        } else {
                            Log.e(TAG,  TAG + "->onGetResult: 向服务器注册视频直播事件失败");
                        }
                    }

                    @Override
                    public void onPostResult(boolean isSuccess, Object pData) {

                    }

                    @Override
                    public void onError(int errCode, String errMessage) {
                        Log.w(TAG, "onError: 是不是出错了呀");
                    }
                });
            }
        }).start();
    }
}
