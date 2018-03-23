package com.android.zhgf.zhgf.app;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
/*import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;*/
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.android.zhgf.zhgf.BuildConfig;
import com.android.zhgf.zhgf.activity.LivePublisherActivity;
import com.android.zhgf.zhgf.activity.LoginActivity;
import com.android.zhgf.zhgf.crashhandler.CrashHandler;
import com.android.zhgf.zhgf.utils.ObjectHolder;
import com.android.zhgf.zhgf.wnd.global.Configure;
import com.android.zhgf.zhgf.wnd.global.ServiceInstanceManager;
import com.android.zhgf.zhgf.wnd.iface.impl.User;
import com.android.zhgf.zhgf.wnd.service.GPSListener;
import com.android.zhgf.zhgf.wnd.service.HttpCommand;
import com.android.zhgf.zhgf.wnd.service.LoopService;
import com.android.zhgf.zhgf.wnd.utils.DialogUtil;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;
import com.android.zhgf.zhgf.wnd.utils.TelephonyUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.android.zhgf.zhgf.R;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.File;

public class AppApplication extends Application {
    private static String TAG = AppApplication.class.getSimpleName();

	public static String[] PermissionLists = new String[]{
			Manifest.permission.INTERNET,
			Manifest.permission.CAMERA,
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.MODIFY_AUDIO_SETTINGS,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,};
	private static AppApplication mAppApplication;

	private AppApplication.GlobalHandlerExt mHandler;

	private LoopService mLooper;

	private GPSListener mGps;

	private double GPS_Longitude;
	private double GPS_Latitude;
	private long GPS_Time;
	private Location m_Loc;

	public Location getLocation(){
		return m_Loc;
	}
	Configure m_Cfg = null;



	NetworkUtil m_Net;
	/*TODO 全局用户对象*/
	private User mUser;
    /*TODO Service管理对象*/
    private ServiceInstanceManager mgrService;

	public static XMPPTCPConnection connection = null;



	public Configure getConfigures(){
		return m_Cfg;
	}
	public double getLatitude(){
		return GPS_Latitude;
	}
	public double getLongitude(){
		return GPS_Longitude;
	}

	public static String[] mPermissions = new String[]{
            android.Manifest.permission.READ_PHONE_STATE,
			android.Manifest.permission.ACCESS_FINE_LOCATION
			/*android.Manifest.permission.ACCESS_NETWORK_STATE,
			android.Manifest.permission.ACCESS_WIFI_STATE,
			Manifest.permission.BIND_VPN_SERVICE,
			Manifest.permission.CAMERA,
			Manifest.permission.RECEIVE_BOOT_COMPLETED,
			Manifest.permission.ACCESS_COARSE_LOCATION*/

	};


	public AppApplication(){

		mHandler = new GlobalHandlerExt();
        mgrService = new ServiceInstanceManager();

		//mLooper = new LoopService(getApplicationContext());
		//mGps = new GPSListener(AppApplication.this,getApplicationContext());
		m_Cfg = new Configure(/*getApplicationContext()*/);
		m_Net = new NetworkUtil();

	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initImageLoader(getApplicationContext());
		mAppApplication = this;
        mUser = new User(getApplicationContext());
		// 二维码包初始化
		ZXingLibrary.initDisplayOpinion(this);

		ObjectHolder.context = getApplicationContext();
		/**
		 * 不带重启的
		 */
//		CrashHandler.getInstance().init(this, BuildConfig.DEBUG);
		/**
		 * 带重启的
		 * 参数1:this
		 * 参数2:是否保存日志到SD卡crash目录，建议设置为BuildConfig.DEBUG，在debug时候保存，方便调试
		 * 参数3:是否crash后重启APP
		 * 参数4:多少秒后重启app，建议设为0，因为重启采用闹钟定时任务模式，app会反应3秒钟，所以最快也是3-4秒后重启
		 * 参数5：重启后打开的第一个activity，建议是splashActivity
		 */

		CrashHandler.getInstance().init(this, BuildConfig.DEBUG, true, 0, LoginActivity.class);
        /**
         * 更多的设置方法
         */

		/*//自定义Toast
		Toast toast = Toast.makeText(this, "自定义提示信息1", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		CrashHandler.setCustomToast(toast);
		//自定义提示信息
		CrashHandler.setCrashTip("自定义提示信息2");
		//自定义APP关闭动画
		CrashHandler.setCloseAnimation(android.R.anim.fade_out);*/

	}
	
	/** 获取Application */
	public static AppApplication getApp() {
		return mAppApplication;
	}

    @Override
    protected void finalize() throws Throwable {

        super.finalize();
    }
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
        Log.e(TAG, "全局Handler["+TAG+"结束运行.......");
        //stopService(new Intent(getApplicationContext(),));
        mgrService.get(HttpCommand.class.getSimpleName()).stopSelf();
        mHandler=null;
        mgrService=null;
		super.onTerminate();
		//整体摧毁的时候调用这个方法
	}
	/** 初始化ImageLoader */
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "ZHGFAPP/Cache");//获取到缓存的目录地址
		Log.d("cacheDir", cacheDir.getPath());
		//创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(context)
				//.memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
				//.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)设置缓存的详细信息，最好不要设置这个
				.threadPoolSize(3)//线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				//.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation你可以通过自己的内存缓存实现
				//.memoryCacheSize(2 * 1024 * 1024)  
				///.discCacheSize(50 * 1024 * 1024)  
				.discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
				//.discCacheFileNameGenerator(new HashCodeFileNameGenerator())//将保存的时候的URI名称用HASHCODE加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				//.discCacheFileCount(100) //缓存的File数量
				.discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
				//.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				//.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);//全局初始化此配置
	}
    public ServiceInstanceManager getService(){
        return mgrService;
    }

	public GlobalHandlerExt getHandler(){
		return mHandler;
	}

	class GlobalHandlerExt extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
                case 10086://无界面轮循消息对话框
                    /*DialogUtil.showGlobalAlert(getApplicationContext(), "接收命令", "您接收到一条来自指挥中心的一条指令，", R.drawable.ic_menu_manage, new String[]{
                            "打开现场视频", "取消视频"
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    Intent tpInt = new Intent(getApplicationContext(), LivePublisherActivity.class);
                                    tpInt.putExtra("isOpenVideo",true);
                                    tpInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    getApplicationContext().startActivity(tpInt);
                                    break;
                                case DialogInterface.BUTTON_NEUTRAL:
                                        *//*TODO 重新开始轮循*//*

                                    break;
                                default:
                            }
                        }
                    });*/
                    Intent tpInt = new Intent(getApplicationContext(), LivePublisherActivity.class);
                    tpInt.putExtra("isOpenVideo",true);
                    tpInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    getApplicationContext().startActivity(tpInt);
                    break;
                case 901:
                    Log.e(TAG, "handleMessage: GPS当前状态可见");

                    break;
                case 902:
                    Log.e(TAG, "handleMessage: 当前GPS状态为服务区外状态");

                    break;
                case 903:
                    Log.e(TAG, "handleMessage: 当前GPS状态为暂停服务状态");
                    break;
                case 909:
                    Log.e(TAG, "handleMessage: 当前GPS处于已关闭状态");
                    DialogUtil.showGlobalAlert(getApplicationContext(), "GPS不可用", "GPS为未打开状态，是否前去设置?", 0, new String[]{"打开GPS设置", "取消设置"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    ((HttpCommand)getService().get("HttpCommand")).openActivity(intent);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    DialogUtil.Toast(getApplicationContext(),"使用本系统必须打开GPS功能!");
                                    System.exit(0);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    break;
				case 666:
					m_Loc = (Location)msg.obj;
					if (null!=msg.obj){
						GPS_Latitude = m_Loc.getLatitude();
						GPS_Longitude = m_Loc.getLongitude();
						GPS_Time = m_Loc.getTime();
						Log.e(TAG, "handleMessage: Tiem:"+GPS_Time+"LAT:"+GPS_Latitude+"||LON:"+GPS_Longitude);
					}
					SendGPS(m_Loc);
					Log.e(TAG, "handleMessage:GPS坐标取到了 "+m_Loc.getLatitude()+"|"+m_Loc.getLongitude());
					break;
				default:
					break;
			}
		}
	}
	void SendGPS(final Location l){
		new Thread(new Runnable() {
			@Override
			public void run() {
				String GPS_URL = getConfigures().GetGPSURL("GPS_DATA");
				String PhoneNumber = (String)getUser().getCache(User.CACHE_KEY_USER_PHONE);
				TelephonyUtils mTel = new TelephonyUtils(getApplicationContext());

				String GPS_PAM = "DeviceNumber=" +PhoneNumber+
						"&DeviceIMEI="+mTel.getDeviceID() +
						"&SimIMSI="+mTel.getSIM() +
						"&Y=" + l.getLatitude()+
						"&X="+ l.getLongitude()+
						"&DeviceType=" +
						"&DeviceIP=" +
						"&MsgInfo=";
                Log.e(TAG, "GPS_PAM: " + GPS_PAM);
				m_Net.HttpSendGetData(GPS_URL,GPS_PAM,null);
                /*new NetworkUtil().HttpSendGetData(GPS_URL,GPS_PAM,null);*/
				Log.e(TAG, "SendGPS: OK");
			}
		}).start();
	}

	public void setUser(User pUser){
		mUser = pUser;
	}
	public User getUser(){
		if (null!=mUser)return mUser;
		return null;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		//MultiDex.install(this);
	}

}
