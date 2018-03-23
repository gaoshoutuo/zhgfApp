package com.android.zhgf.zhgf.wnd.global;/**
 * Created by Administrator on 2016/4/20.
 */

import android.app.Application;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.android.zhgf.zhgf.wnd.LivePublisherActivity;

import com.android.zhgf.zhgf.wnd.iface.impl.User;
import com.android.zhgf.zhgf.wnd.service.HttpCommand;
import com.android.zhgf.zhgf.wnd.utils.DialogUtil;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;
import com.android.zhgf.zhgf.wnd.utils.TelephonyUtils;

/*******************************************************************************************************
 * Class Name:WndProject - GlobalHandler
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title GlobalHandler
 * @Package com.jiechu.wnd
 * @date 2016/4/20 14:59
 * @Description 全局的Handler对象
 * //////////////////////////////////////////////////////////////////////////////////////////////////
 *  1000001         更新GPS坐标
 * //////////////////////////////////////////////////////////////////////////////////////////////////
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class GlobalHandler extends Application {
    private static final String TAG = GlobalHandler.class.getSimpleName();

    /*TODO Service管理对象*/
    private ServiceInstanceManager mgrService;
    /*TODO 全局Handler对象*/
    private GlobalHandlerExt mHandler;
    /*TODO 全局用户对象*/
    private User mUser;
    /*TODO 全局服务器地址*//*

    private String HTTP_SERVER = "http://wnd.jx96871.cn";
    String getServer(){
        return HTTP_SERVER;
    }*/
    /**
     * TODO 当用户缓存中不存在服务器信息时，使用HTTP_SERVER做为APP配置信息的获取地址
     * */
    private String HTTP_CONFIGURE_URL = "/MobileAPP/Configure";

    //TODO 默认的服务器地址
    private String DEFAULT_SERVER = "http://wnd.jx96871.cn";
    public String[] BACKUP_SERVER = new String[]{};

    public String getHttpServerURL(){
        return DEFAULT_SERVER;
    }

    Configure m_Cfg = null;
    public Configure getConfigures(){
        return m_Cfg;
    }

    NetworkUtil m_Net;

    /*private LoopService mLooper;*/

    /*TODO 全局的的关键词列表[缓存名称]*/


    public GlobalHandler(){
        Log.e(TAG, "全局Handler["+TAG+"开始运行.......");
        mHandler = new GlobalHandlerExt();
        mgrService = new ServiceInstanceManager();
        m_Cfg = new Configure(/*getApplicationContext()*/);
        m_Net = new NetworkUtil();
        //mUser = new User(GlobalHandler.this);
        //startService(new Intent(getApplicationContext(),com.jiechu.wnd.service.HttpCommand.class));
        startService(new Intent("wnd.HttpCommand"));
    }

    private double GPS_Longitude;
    private double GPS_Latitude;
    private long GPS_Time;
    private Location m_Loc;

    public Location getLocation(){
        return m_Loc;
    }

    public void setHandler(GlobalHandlerExt pHandler){
        mHandler = pHandler;
    }

    public GlobalHandlerExt getHandler(){
        return mHandler;
    }


    public void setUser(User pUser){
        mUser = pUser;
    }
    public User getUser(){
        if (null!=mUser)return mUser;
        return null;
    }

    public void setXY(double lon,double lat){
        GPS_Longitude = lon;
        GPS_Latitude = lat;
    }
    public double getLatitude(){
        return GPS_Latitude;
    }
    public double getLongitude(){
        return GPS_Longitude;
    }

    public ServiceInstanceManager getService(){
        return mgrService;
    }



    public class GlobalHandlerExt extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "GlobalHandlerExt->handleMessage:被调用了，它的Message.what代码为：" + msg.what);
            switch (msg.what){
                case 1000001:
                    /*TODO GPS坐标通知*/
                    ((HttpCommand)mgrService.get("HttpCommand")).notify();
                    break;
                case 456:
                    try {
                        Thread.currentThread().sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DialogUtil.showGlobalAlert(getApplicationContext(),"全局对话框","在没有界面的情况下跳出来的",0,new String[]{
                            "嘿嘿嘿"
                    },null);
                    break;
                case 10086://无界面轮循消息对话框
                        DialogUtil.showGlobalAlert(getApplicationContext(), "接收命令", "您接收到一条来自指挥中心的一条指令，",0, new String[]{
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
                                        /*TODO 重新开始轮循*/

                                        break;
                                    default:
                                }
                            }
                        });
                    break;
                case 99999:
                    /*DialogUtil.showGlobalAlert(getApplicationContext(), "全局测试", "这是一条全局消息", R.drawable.ic_menu_manage, new String[]{"确定", "取消"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });*/
                    break;
                case 900://正常得到GPS坐标后的赋值操作
                    m_Loc = (Location)msg.obj;
                    if (null!=msg.obj){
                        GPS_Latitude = m_Loc.getLatitude();
                        GPS_Longitude = m_Loc.getLongitude();
                        GPS_Time = m_Loc.getTime();
                        Log.e(TAG, "handleMessage: Tiem:"+GPS_Time+"LAT:"+GPS_Latitude+"||LON:"+GPS_Longitude);
                    }
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
                case 908:
                    Log.e(TAG, "handleMessage: 当前GPS处于已打开状态");
                    break;
                case 909:
                    Log.e(TAG, "handleMessage: 当前GPS处于已关闭状态");
                    DialogUtil.showGlobalAlert(getApplicationContext(), "GPS不可用", "GPS为未打开状态，是否前去设置?", 0, new String[]{"打开GPS设置", "取消设置"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
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
                    GPS_Latitude = m_Loc.getLatitude();
                    GPS_Longitude = m_Loc.getLongitude();
                    SendGPS(m_Loc);
                    break;
                default:
                    Log.e(TAG, "handleMessage: 默认触发的。。。。。。。");
                    break;
            }
        }
    }



    Class<?> newInstance(String str_cls){
        try {
            return Class.forName(str_cls);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void finalize() throws Throwable {
        Log.e(TAG, "全局Handler["+TAG+"结束运行.......");
        mHandler=null;
        mgrService=null;
        super.finalize();
    }

    void SendGPS(final Location l){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String GPS_URL = getConfigures().GetAlternatelyURL("GPS_DATA");
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
                m_Net.HttpSendGetData(GPS_URL,GPS_PAM,null);
                /*new NetworkUtil().HttpSendGetData(GPS_URL,GPS_PAM,null);*/
                Log.e(TAG, "SendGPS: OK");
            }
        }).start();
    }

    static {
        System.loadLibrary("NodeMediaClient");
    }
}
