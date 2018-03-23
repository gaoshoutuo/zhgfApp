package com.android.zhgf.zhgf.activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.zhgf.zhgf.core.ICommand;
import com.android.zhgf.zhgf.core.RTCClient;
import com.android.zhgf.zhgf.core.RuningTime;
import com.android.zhgf.zhgf.core.Utils;
import com.android.zhgf.zhgf.core.WSClient;
import com.android.zhgf.zhgf.core.cmd.AcceptCommand;
import com.android.zhgf.zhgf.core.cmd.AnswerCommand;
import com.android.zhgf.zhgf.core.cmd.CancleCommand;
import com.android.zhgf.zhgf.core.cmd.CandidateCommand;
import com.android.zhgf.zhgf.core.cmd.HangupCommand;
import com.android.zhgf.zhgf.core.cmd.OffLineCommand;
import com.android.zhgf.zhgf.core.cmd.OfferCommand;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import com.android.zhgf.zhgf.R;
import java.util.LinkedList;

/**
 * Created by zhuyt on 2017/11/1.
 * 视频通话主呼叫端
 */

public class VideoCaller extends MPermissionActivity{
    public static String TAG = VideoCaller.class.getSimpleName();

    public static VideoCaller mInstance=null;

    WSClient.OnlineUser mClientBUser=null;

    String strFromUserName="";
    String strFormUserSocketID="";
    String strToUserName="";
    String strToUserSocket="";

    GLSurfaceView glDisplay;
    VideoRenderer.Callbacks mLocalRender;
    VideoRenderer.Callbacks mRemoteRender;

    VideoRendererGui.ScalingType SCALE_ASPECT_FILL = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;


    LinearLayout ChattingOfferFace;
    LinearLayout OfferFace;
    ImageView btnCancle;
    ImageView btnHangup;
    ImageView btnMetWave;
    boolean blIsMetWaveFlag=false;
    ImageView btnMicphone;
    ImageView btnSwitchCamera;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_caller);
        mClientBUser = (WSClient.OnlineUser) getIntent().getSerializableExtra("ClientB");

        ChattingOfferFace = (LinearLayout) findViewById(R.id.ChattingOfferFace);
        OfferFace = (LinearLayout) findViewById(R.id.OfferFace);
        ChattingOfferFace.setVisibility(View.GONE);
        OfferFace.setVisibility(View.VISIBLE);

        requestPermission(RuningTime.mPermissions,0x001);

        RuningTime.isBusy_Caller=true;
        mInstance = VideoCaller.this;
    }

    @Override
    public void permissionSuccess(int requestCode) {
        initData();
        initEvent();
    }


    void initEvent(){
        btnSwitchCamera = (ImageView) findViewById(R.id.btnSwitchCamera);
        btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RTCClient.getInstance().switchCamera();
            }
        });

        btnHangup = (ImageView) findViewById(R.id.btnHangup);
        btnHangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    JSONObject msg = new JSONObject();
                    msg.put("username",RuningTime.getWSClient().getUsername());
                    msg.put("socket_id",RuningTime.getWSClient().getSocketID());
                    msg.put("room_name",RuningTime.getWSClient().getRoomname());
                    msg.put("to_username",mClientBUser.getUsername());
                    msg.put("to_socket",mClientBUser.getSocketID());
                    msg.put("to_room",mClientBUser .getRoomname());
                    new HangupCommand().SendMessage("HANGUP",msg);
                    VideoCaller.this.finish();
                    Toast.makeText(VideoCaller.this,"您结束了本次通话",Toast.LENGTH_SHORT).show();
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });

        btnCancle = (ImageView) findViewById(R.id.btnCancle);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    JSONObject msg = new JSONObject();
                    msg.put("username",RuningTime.getWSClient().getUsername());
                    msg.put("socket_id",RuningTime.getWSClient().getSocketID());
                    msg.put("room_name",RuningTime.getWSClient().getRoomname());
                    msg.put("to_username",mClientBUser.getUsername());
                    msg.put("to_socket",mClientBUser.getSocketID());
                    msg.put("to_room",mClientBUser .getRoomname());
                    new CancleCommand().SendMessage("CANCLE",msg);
                    VideoCaller.this.finish();
                    Toast.makeText(VideoCaller.this,"您已经取消了本次通话",Toast.LENGTH_SHORT).show();
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });

    }
    boolean isMetWave=true;
    void initData(){
        glDisplay = (GLSurfaceView) findViewById(R.id.glDisplay);
        VideoRendererGui.setView(glDisplay, new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "VideoCaller:::run:: VideoRendererGui设置GLSurfaceview完成!");
            }
        });


        mRemoteRender = VideoRendererGui.create(0,0,100,100, SCALE_ASPECT_FILL,false);
        mLocalRender =  VideoRendererGui.create(0,0,100,100, SCALE_ASPECT_FILL,false);


        LinkedList<PeerConnection.IceServer> mIceServers=new LinkedList<>();
        String[] icelist = RuningTime.getIceServers();
        for (int i=0;i<icelist.length;i++){
            mIceServers.add(new PeerConnection.IceServer(icelist[i]));
        }
        RuningTime.getRTCClient(VideoCaller.this).setIceServers(mIceServers);
        RuningTime.getRTCClient().setListener(new VideoListener());
        RuningTime.getRTCClient().CreateLocalStream();
        RuningTime.getRTCClient().CreateConnection();



        //当对方不在线时,关闭已方Activity
        RuningTime.getWSClient().addCommand("OFFLINE",new OffLineCommand());
        ((OffLineCommand)RuningTime.getWSClient().getCommand("OFFLINE")).setListener(new ICommand.EmitterListener() {
            @Override
            public void onEmitter(JSONObject payload) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(RuningTime.isBusy_Caller==true && VideoCaller.mInstance!=null){
                            VideoCaller.mInstance.finish();
                            RuningTime.isBusy_Caller=false;
                        }

                        if (RuningTime.isBusy_Callee==true && VideoCallee.mInstance!=null){
                            VideoCallee.mInstance.finish();
                            RuningTime.isBusy_Callee=false;
                        }
                    }
                });
            }
        });





        /**
         * 主动发送端
         * 在这里需要发送一个邀请给客户端B，然后等待客户端B的响应
         * 1.装配一个ICommand[RefreshOnlineUserCommand]
         * 2.定义他的回调
         * 3.通知客户端B可以设频通话了
         * */
        {
            JSONObject msg = new JSONObject();

            try {
                msg.put("username",RuningTime.getWSClient().getUsername());
                msg.put("socket_id",RuningTime.getWSClient().getSocketID());
                msg.put("to_username",mClientBUser.getUsername());
                msg.put("to_socket",mClientBUser.getSocketID());
                Log.e(TAG, "VideoCaller:::initData:: 通过服务器向另一端用户发起视频请求");
                RuningTime.getWSClient().getInstance().SendMessage("INVISITVIDEO",msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        //ICE信息交换
        RuningTime.getWSClient().addCommand("CANDIDATE",new CandidateCommand());
        ((CandidateCommand)RuningTime.getWSClient().getCommand("CANDIDATE")).setListener(new ICommand.EmitterListener() {
            @Override
            public void onEmitter(JSONObject payload) {
                try{
                    Log.e(TAG, "VideoCaller:::onEmitter:: 设置IceCandidate:::"+payload.getString("id"));
                    IceCandidate ice = new IceCandidate(payload.getString("id"),payload.getInt("lable"),payload.getString("sdp"));
                    RTCClient.getInstance().setCandidate(ice);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });

        //Answer
        RuningTime.getWSClient().addCommand("ANSWER",new AnswerCommand());
        ((AnswerCommand)RuningTime.getWSClient().getCommand("ANSWER")).setListener(new ICommand.EmitterListener() {
            @Override
            public void onEmitter(JSONObject payload) {
                Log.e(TAG, "VideoCaller:::onEmitter:: 处理远程发送过来的Answer信息");
                try {
                    RuningTime.getRTCClient().setRemoteDescription(new SessionDescription(
                            SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                            payload.getString("sdp")
                    ));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        //接收视频请求
        RuningTime.getWSClient().addCommand("ACCEPTVIDEO",new AcceptCommand());
        ((AcceptCommand)RuningTime.getWSClient().getCommand("ACCEPTVIDEO")).setListener(new ICommand.EmitterListener() {
            @Override
            public void onEmitter(JSONObject payload) {
                Log.e(TAG, "VideoCaller:::onEmitter:: 对方同意了你视频通话后的一些处理");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ChattingOfferFace.setVisibility(View.VISIBLE);
                        OfferFace.setVisibility(View.GONE);


                        //开启/关闭静音
                        btnMetWave = (ImageView) findViewById(R.id.btnMetWave);
                        btnMetWave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isMetWave = isMetWave?false:true;
                                RuningTime.getRTCClient().setWaveMet(isMetWave);


                                /*if (blIsMetWaveFlag==false) {
                                    Log.e(TAG, "VideoCaller:::onClick:: 打开扬声器");
                                    Utils.getInstance(VideoCaller.this).openSpeaker();
                                    blIsMetWaveFlag=true;
                                }else{
                                    Log.e(TAG, "VideoCaller:::onClick:: 关闭扬声器");
                                    Utils.getInstance(VideoCaller.this).closeSpeaker();
                                    blIsMetWaveFlag=false;
                                }*/
                            }
                        });

                        //开启/关闭扬声器
                        btnMicphone = (ImageView) findViewById(R.id.btnMicphone);
                        btnMicphone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (blIsMetWaveFlag==false) {
                                    Log.e(TAG, "VideoCaller:::onClick:: 打开扬声器");
                                    Utils.getInstance(VideoCaller.this).openSpeaker();
                                    blIsMetWaveFlag=true;
                                }else{
                                    Log.e(TAG, "VideoCaller:::onClick:: 关闭扬声器");
                                    Utils.getInstance(VideoCaller.this).closeSpeaker();
                                    blIsMetWaveFlag=false;
                                }
                            }
                        });
                    }
                });
                //当全部设置完成后，调用CreateOffer();
                RuningTime.getRTCClient().getInstance().CreateOffer();
            }
        });


        //挂断视频通话
        RuningTime.getWSClient().addCommand("HANGUP", new HangupCommand());
        ((HangupCommand)RuningTime.getWSClient().getCommand("HANGUP")).setListener(new ICommand.EmitterListener() {
            @Override
            public void onEmitter(JSONObject payload) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VideoCaller.this,"对方结束了和您的通话",Toast.LENGTH_SHORT).show();
                    }
                });
                VideoCaller.this.finish();
            }
        });

    }

    public class VideoListener implements RTCClient.RTCListener{

        @Override
        public void onAddStream(MediaStream ms) {
            Log.e(TAG, "VideoListener:::onAddStream:: ");
            ms.videoTracks.get(0).addRenderer(new VideoRenderer(mRemoteRender));


            VideoRendererGui.update(mRemoteRender,0,0,100,100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL);
            VideoRendererGui.update(mLocalRender,0,0,30,30, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL);
        }

        @Override
        public void onRemoveStream(MediaStream ms) {

        }

        @Override
        public void onLocalMedia(MediaStream ms) {
            Log.e(TAG, "VideoListener:::onLocalMedia:: ");
            ms.videoTracks.get(0).addRenderer(new VideoRenderer(mLocalRender));
        }

        @Override
        public void onCandidate(IceCandidate ic) {
            try{
                JSONObject msg = new JSONObject();
                msg.put("username",RuningTime.getWSClient().getUsername());
                msg.put("socket_id",RuningTime.getWSClient().getSocketID());
                msg.put("room_name",RuningTime.getWSClient().getRoomname());
                msg.put("to_username",mClientBUser.getUsername());
                msg.put("to_socket",mClientBUser.getSocketID());
                msg.put("id",ic.sdpMid);
                msg.put("lable",ic.sdpMLineIndex);
                msg.put("sdp",ic.sdp);
                new CandidateCommand().SendMessage("CANDIDATE",msg);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onLocalSDP(SessionDescription sdp) {
            try{
                JSONObject msg = new JSONObject();
                msg.put("username",WSClient.getInstance().getUsername());
                msg.put("socket_id",WSClient.getInstance().getSocketID());
                msg.put("room_name",WSClient.getInstance().getRoomname());
                msg.put("to_username",mClientBUser.getUsername());
                msg.put("to_socket",mClientBUser.getSocketID());
                msg.put("type",sdp.type.canonicalForm());
                msg.put("sdp",sdp.description);

                new OfferCommand().SendMessage("OFFER",msg);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onRemoteSDP(SessionDescription sdp) {

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        RTCClient.getInstance().startVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        RTCClient.getInstance().stopVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RTCClient.getInstance().stopVideo();
        RTCClient.getInstance().getConnection().close();
    }
}
