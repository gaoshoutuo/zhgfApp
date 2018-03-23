package com.android.zhgf.zhgf.wnd.view;

import cn.nodemedia.LivePublisher;



import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

public class PublishSurface extends SurfaceView {

	Context mContext;
	boolean isStarting;
	public PublishSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		LivePublisher.init(context);
		LivePublisher.setDelegate(new LivePublishDelegate());
		LivePublisher.setAudioParam(32 * 1000, LivePublisher.AAC_PROFILE_HE);
		LivePublisher.setVideoParam(640, 360, 15, 400 * 1000, LivePublisher.AVC_PROFILE_MAIN);
		LivePublisher.setDenoiseEnable(true);
		Log.e("VideoChat.Live",this.toString());
		LivePublisher.startPreview(this, ((Activity)context).getWindowManager().getDefaultDisplay().getRotation(), LivePublisher.CAMERA_FRONT);

	}

	void startPublish(String url){
		LivePublisher.startPublish(url);
	}

	void stopPublish(){
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
					Toast.makeText(mContext, "正在发布视频", Toast.LENGTH_SHORT).show();
					break;
				case 2001:
					Toast.makeText(mContext, "视频发布成功", Toast.LENGTH_SHORT).show();
					isStarting = true;
					break;
				case 2002:
					Toast.makeText(mContext, "视频发布失败", Toast.LENGTH_SHORT).show();
					break;
				case 2004:
					Toast.makeText(mContext, "视频发布结束", Toast.LENGTH_SHORT).show();
					isStarting = false;
					break;
				case 2005:
					Toast.makeText(mContext, "网络异常,发布中断", Toast.LENGTH_SHORT).show();
					break;
				case 2100:
					// mic off
					Toast.makeText(mContext, "麦克风静音", Toast.LENGTH_SHORT).show();
					break;
				case 2101:
					// mic on
					Toast.makeText(mContext, "麦克风恢复", Toast.LENGTH_SHORT).show();
					break;
				case 2102:
					// camera off

					Toast.makeText(mContext, "摄像头传输关闭", Toast.LENGTH_SHORT).show();
					break;
				case 2103:
					// camera on
					Toast.makeText(mContext, "摄像头传输打开", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
	};
}
