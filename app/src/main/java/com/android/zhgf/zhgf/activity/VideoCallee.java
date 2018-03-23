package com.android.zhgf.zhgf.activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.core.ICommand;
import com.android.zhgf.zhgf.core.RTCClient;
import com.android.zhgf.zhgf.core.RuningTime;
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

import java.util.LinkedList;

/**
 * Created by zhuyt on 2017/11/1.
 * 视频通话被呼叫端
 */

public class VideoCallee extends MPermissionActivity{
    public static String TAG = VideoCaller.class.getSimpleName();

    public static VideoCallee mInstance;
    WSClient.OnlineUser ClientB=null;

    GLSurfaceView glDisplay;
    VideoRenderer.Callbacks mLocalRender;
    VideoRenderer.Callbacks mRemoteRender;

    VideoRendererGui.ScalingType SCALE_ASPECT_FILL = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_callee);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ClientB = (WSClient.OnlineUser) getIntent().getSerializableExtra("ClientB");

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
        RuningTime.getRTCClient(VideoCallee.this).setIceServers(mIceServers);
        RuningTime.getRTCClient(VideoCallee.this).setListener(new VideoListener());
        RuningTime.getRTCClient(VideoCallee.this).CreateLocalStream();
        RuningTime.getRTCClient(VideoCallee.this).CreateConnection();



        //挂断视频通话
        RuningTime.getWSClient().addCommand("HANGUP", new HangupCommand());
        ((HangupCommand)RuningTime.getWSClient().getCommand("HANGUP")).setListener(new ICommand.EmitterListener() {
            @Override
            public void onEmitter(JSONObject payload) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VideoCallee.this,"对方结束了和您的通话",Toast.LENGTH_SHORT).show();
                    }
                });
                VideoCallee.this.finish();
            }
        });


        //Cancle取消视频通话
        RuningTime.getWSClient().addCommand("CANCLE",new CancleCommand());
        ((CancleCommand)RuningTime.getWSClient().getCommand("CANCLE")).setListener(new ICommand.EmitterListener() {
            @Override
            public void onEmitter(JSONObject payload) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VideoCallee.this,"对方取消了本次通话",Toast.LENGTH_SHORT).show();
                    }
                });
                VideoCallee.this.finish();
            }
        });


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

        InitEvents();


        RuningTime.isBusy_Callee = true;
        mInstance = VideoCallee.this;

    }

    ImageView btnAccept;
    LinearLayout AnswerFace;
    LinearLayout ChattingFace;
    ImageView btnReject;
    ImageView btnHangup;
    ImageView btnSwitchCamera;
    void InitEvents(){
        btnAccept = (ImageView) findViewById(R.id.btnAccept);
        AnswerFace = (LinearLayout) findViewById(R.id.AnswerFace);
        ChattingFace = (LinearLayout) findViewById(R.id.ChattingFace);

        AnswerFace.setVisibility(View.VISIBLE);
        ChattingFace.setVisibility(View.GONE);


        btnSwitchCamera = (ImageView) findViewById(R.id.btnSwitchCamera);
        btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RTCClient.getInstance().switchCamera();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Log.e(TAG, "VideoCallee:::onClick:: 向ClinetB发送同意信息");
                    JSONObject msg = new JSONObject();
                    msg.put("username",RuningTime.getWSClient().getUsername());
                    msg.put("socket_id",RuningTime.getWSClient().getSocketID());
                    msg.put("room_name",RuningTime.getWSClient().getRoomname());
                    msg.put("to_username",ClientB.getUsername());
                    msg.put("to_socket",ClientB.getSocketID());
                    msg.put("to_room",ClientB.getRoomname());
                    (new AcceptCommand()).SendMessage("ACCEPTVIDEO",msg);
                    SwitchButtonFace();

                    WSClient.getInstance().addCommand("OFFER",new OfferCommand());
                    ((OfferCommand)WSClient.getInstance().getCommand("OFFER")).setListener(new ICommand.EmitterListener() {
                        @Override
                        public void onEmitter(JSONObject payload) {
                            try {
                                Log.e(TAG, "VideoCallee:::onEmitter:: 接收到客户端A发送过来的OFFER");
                                RTCClient.getInstance().setRemoteDescription(new SessionDescription(
                                        SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                                        payload.getString("sdp")
                                ));
                                RTCClient.getInstance().CreateAnswer();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch(JSONException e){
                    e.printStackTrace();
                }
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
                    msg.put("to_username",ClientB.getUsername());
                    msg.put("to_socket",ClientB.getSocketID());
                    msg.put("to_room",ClientB.getRoomname());
                    new HangupCommand().SendMessage("HANGUP",msg);
                    VideoCallee.this.finish();

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }


    void SwitchButtonFace(){
        AnswerFace.setVisibility(View.GONE);
        ChattingFace.setVisibility(View.VISIBLE);
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
            Log.e(TAG, "VideoListener:::onCandidate:: ");
            try{
                JSONObject msg = new JSONObject();
                msg.put("username",RuningTime.getWSClient().getUsername());
                msg.put("socket_id",RuningTime.getWSClient().getSocketID());
                msg.put("room_name",RuningTime.getWSClient().getRoomname());
                msg.put("to_username",ClientB.getUsername());
                msg.put("to_socket",ClientB.getSocketID());
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
                msg.put("to_username",ClientB.getUsername());
                msg.put("to_socket",ClientB.getSocketID());
                msg.put("type",sdp.type.canonicalForm());
                msg.put("sdp",sdp.description);

                new AnswerCommand().SendMessage("ANSWER",msg);

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
