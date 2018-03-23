package com.android.zhgf.zhgf.core;

import android.content.Context;
import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.LinkedList;

/**
 * Created by zhuyt on 2017/11/1.
 */

public class RTCClient {
    public static String TAG = RTCClient.class.getSimpleName();

    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";
    public static final String VIDEO_TRACK_TYPE = "video";
    private static final String VIDEO_CODEC_VP8 = "VP8";
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String VIDEO_CODEC_H264 = "H264";
    private static final String VIDEO_CODEC_H264_BASELINE = "H264 Baseline";
    private static final String VIDEO_CODEC_H264_HIGH = "H264 High";
    private static final String AUDIO_CODEC_OPUS = "opus";
    private static final String AUDIO_CODEC_ISAC = "ISAC";
    private static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";
    private static final String VIDEO_FLEXFEC_FIELDTRIAL =
            "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/";
    private static final String VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/";
    private static final String VIDEO_H264_HIGH_PROFILE_FIELDTRIAL =
            "WebRTC-H264HighProfile/Enabled/";
    private static final String DISABLE_WEBRTC_AGC_FIELDTRIAL =
            "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/";
    private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
    private static final String AUDIO_LEVEL_CONTROL_CONSTRAINT = "levelControl";
    private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";
    private static final int HD_VIDEO_WIDTH = 1280;
    private static final int HD_VIDEO_HEIGHT = 720;
    private static final int BPS_IN_KBPS = 1000;



    private static RTCClient mInstance=null;
    public static RTCClient getInstance(Context pCtx){
        if(mInstance==null){
            mInstance = new RTCClient(pCtx);
        }
        return mInstance;
    }
    public static RTCClient getInstance(){
        if(mInstance==null){
            Log.e(TAG, "RTCClient:::getInstance:: 第一次调用getInstance获取实例时，请传入必要的参数");
            System.exit(0);
        }
        return mInstance;
    }
    //Peer的外部监听器
    RTCListener mListener = null;
    public void setListener(RTCListener ls){
        mListener = ls;
    }
    public interface RTCListener{
        void onAddStream(MediaStream ms);
        void onRemoveStream(MediaStream ms);
        void onLocalMedia(MediaStream ms);
        void onCandidate(IceCandidate ic);
        void onLocalSDP(SessionDescription sdp);
        void onRemoteSDP(SessionDescription sdp);
    }

    /**
     * ///////////////////////////////////////////////////////////////////////////////////////////////////
     * 创建PeerConnectionFactory工厂及相关监听对象
     * ///////////////////////////////////////////////////////////////////////////////////////////////////
     * */
    private PeerConnectionFactory mFactory;
    public PeerConnectionFactory getFactory(){
        return mFactory;
    }
    private PeerConnection mConnection;
    public PeerConnection getConnection(){
        return mConnection;
    }

    private RTCObserver mObserver=null;
    public RTCObserver getObserver(){
        return mObserver;
    }



    //TODO 构造方法
    private RTCClient(Context pCtx){
        if (PeerConnectionFactory.initializeAndroidGlobals(pCtx,true,true,true, VideoRendererGui.getEGLContext())){
            mFactory = new PeerConnectionFactory();
        }else{
            Log.e(TAG, "RTCClient:::RTCClient:: PeerConnectionFactory初始化出错");
            System.exit(0);
        }
    }

    /**
     * ///////////////////////////////////////////////////////////////////////////////////////////////////
     * 创建本地媒体流
     * ///////////////////////////////////////////////////////////////////////////////////////////////////
     * */
    private MediaStream mLocalStream=null;
    public MediaStream getLocalStream(){
        if (mLocalStream==null){
            CreateLocalStream();
        }
        return mLocalStream;
    }
    private MediaStream mRemoteStream;
    public MediaStream getRemoteStream(){
        return mRemoteStream;
    }
    private VideoCapturerAndroid mCapture;
    private VideoSource mVSource;
    public VideoSource getVideoHandler(){
        return mVSource;
    }
    private VideoTrack mVTrack;
    private MediaConstraints mVConst;
    private AudioSource mASource;
    private AudioTrack mATrack;
    private MediaConstraints mAConst;
    //创建本地媒体流
    public void CreateLocalStream(){
        mVConst = new MediaConstraints();
        mVSource = mFactory.createVideoSource(getCapture(),mVConst);
        mVTrack = mFactory.createVideoTrack("AndroidVideoStream_"+System.currentTimeMillis(),mVSource);
        mAConst = new MediaConstraints();
        mASource = mFactory.createAudioSource(mAConst);
        mATrack = mFactory.createAudioTrack("AndroidAudioStream_"+ System.currentTimeMillis(),mASource);
        mLocalStream = mFactory.createLocalMediaStream("ANDROIDSTREAM_"+System.currentTimeMillis());
        mLocalStream.addTrack(mVTrack);
        mLocalStream.addTrack(mATrack);
        if (mConnection!=null){
            mConnection.addStream(mLocalStream);
        }
        mListener.onLocalMedia(mLocalStream);
    }
    //创建一个摄像头调用的对象，默认为前置投像头
    private VideoCapturerAndroid getCapture(){
        if(mCapture==null){
            mCapture = VideoCapturerAndroid.create(VideoCapturerAndroid.getNameOfFrontFacingDevice());
        }
        return mCapture;
    }


    /**
     * ///////////////////////////////////////////////////////////////////////////////////////////////////
     * 创建PeerConnection
     * ///////////////////////////////////////////////////////////////////////////////////////////////////
     * */
    MediaConstraints pcMediaConstraints=null;
    LinkedList<PeerConnection.IceServer> mIceServers=null;
    public void CreateConnection(){
        if (mFactory==null){
            Log.e(TAG, "RTCClient:::CreateConnection:: PeerConnectionFactory未被实例化");
            System.exit(0);
        }

        //设置PeerConnection连接的约束
        pcMediaConstraints = new MediaConstraints();
        pcMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio","true"));
        pcMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo","true"));
        pcMediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement","true"));

        //设置ICE服务器列表
        if (mIceServers==null){
            Log.e(TAG, "RTCClient:::CreateConnection:: ICE服务器列表未被加载");
            System.exit(0);
        }
        if (mObserver==null){
            mObserver = new RTCObserver();
        }

        mConnection = mFactory.createPeerConnection(mIceServers,pcMediaConstraints,mObserver);
        if (mLocalStream!=null){
            mConnection.addStream(mLocalStream);
        }
    }
    /**
     * ///////////////////////////////////////////////////////////////////////////////////////////////////
     * 与客户端A交互所需的方法
     * ///////////////////////////////////////////////////////////////////////////////////////////////////
     * */
    //设置信令服务器发送过来的客户端A的SDP信息
    public void setRemoteDescription(SessionDescription sdp){
        mConnection.setRemoteDescription(mObserver,sdp);
    }

    //设置信令服务器发送过来的客户端A的IceCandidate信息
    public void setCandidate(IceCandidate ice){
        mConnection.addIceCandidate(ice);
    }

    //设置ICE服务器列表
    public void setIceServers(LinkedList<PeerConnection.IceServer> ls){
        mIceServers = ls;
    }

    public void CreateOffer(){
        mConnection.createOffer(mObserver,new MediaConstraints());
    }
    public void CreateAnswer(){
        mConnection.createAnswer(mObserver,new MediaConstraints());
    }





    /**
     * ///////////////////////////////////////////////////////////////////////////////////////////////////
     * PeerConnection的监听
     * ///////////////////////////////////////////////////////////////////////////////////////////////////
     * */
    public class RTCObserver implements SdpObserver,PeerConnection.Observer{

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {

        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            mListener.onCandidate(iceCandidate);
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            mListener.onAddStream(mediaStream);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            mListener.onRemoveStream(mediaStream);
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

        }

        @Override
        public void onRenegotiationNeeded() {

        }

        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            mConnection.setLocalDescription(mObserver,sessionDescription);
            mListener.onLocalSDP(sessionDescription);
        }

        @Override
        public void onSetSuccess() {

        }

        @Override
        public void onCreateFailure(String s) {

        }

        @Override
        public void onSetFailure(String s) {

        }
    }

    public void stopVideo(){
        getVideoHandler().stop();
    }
    public void startVideo(){
        getVideoHandler().restart();
    }
    //设置静音
    public void setWaveMet(boolean isFlag){
        if (isFlag){
            mLocalStream.addTrack(mATrack);
        }else{
            mLocalStream.removeTrack(mATrack);
        }

        //mATrack.setEnabled(mATrack.enabled()?false:true);
    }
    //前后摄像头转换
    public boolean switchCamera(){
        if(mCapture!=null && mCapture instanceof VideoCapturerAndroid){
            return mCapture.switchCamera();
        }
        return false;
    }
}
