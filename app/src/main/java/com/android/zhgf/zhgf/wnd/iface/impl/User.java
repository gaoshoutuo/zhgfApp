package com.android.zhgf.zhgf.wnd.iface.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.zhgf.zhgf.activity.LoginActivity;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.wnd.global.GlobalHandler;
import com.android.zhgf.zhgf.wnd.iface.INetwork;
import com.android.zhgf.zhgf.wnd.iface.IUser;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;
import com.android.zhgf.zhgf.wnd.utils.SharedUtil;
import com.android.zhgf.zhgf.wnd.utils.TelephonyUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class User implements IUser{
    private static final String TAG = User.class.getSimpleName();

    private static String SERVER_USER_LOGIN_URL="";
    private static String SERVER_USER_LOGOUT_URL="";

    private static final String CACHE_USER_KEYNAME = "WndPhone";//缓存文件的KEY,所有缓存的内容均通过此字段获取

    public static final String CACHE_KEY_HOST_SERVER = "HostServer";
    public static final String CACHE_KEY_USER_PHONE = "PhoneNumber";
    public static final String CACHE_KEY__USER_PWD = "PhonePwd";
    public static final String CACHE_KEY_USER_NAME = "UserName";
    public static final String CACHE_KEY_USER_UTIL = "UserUtil";
    public static final String CACHE_KEY_USER_IDENT = "UserIdent";
    public static final String CACHE_KEY_USER_IMG = "UserImg";
    public static final String CACHE_KEY__USER_ISFORGET_NAME = "isForgetName";
    public static final String CACHE_KEY__USER_ISFORGET_PWD = "isForgetPwd";


    public static float GPS_LAT;//用户当前GPS坐标经度
    public static float GPS_LON;//用户当前GPS坐标纬度

    private Context mContext;
    protected Context getContext(){
        return mContext;
    }


    SharedUtil mShared;

    Map<String,String> mProperties;
    public void setProperty(String name,String val){
        mProperties.put(name,val);
    }
    public String remove(String key){
        return mProperties.get(key);
    }

    public User(Context pContext) {
        mContext = pContext;
        mProperties = new HashMap<String,String>();

        /*Log.e(TAG, "User: SERVER_USER_LOGIN_URL"+SERVER_USER_LOGIN_URL);
        Log.e(TAG, "User: SERVER_USER_LOGOUT_URL"+SERVER_USER_LOGOUT_URL);*/


        try {
            mShared = new SharedUtil(mContext,CACHE_USER_KEYNAME);
        } catch (SharedUtil.InvaildSharedException e) {
            e.printStackTrace();
        }

        //TODO 权限测试暂时放在这里，在实际应用当中应该放置在用户登录成功后，由服务器向用户返回权限参数之后，把服务器分配的权限赋于用户
        m_Permission = Permission.getInstance("111111111");
        /*m_Permission = Permission.getInstance("010000010");*/
    }

    /**
     * @param gpsLat
     */
    public static void setGpsLat(float gpsLat) {
        GPS_LAT = gpsLat;
    }

    /**
     * @param gpsLon
     */
    public static void setGpsLon(float gpsLon) {
        GPS_LON = gpsLon;
    }


    private String phone_number;//用 户手机号码：当缓存中没有保存手机号码就说明这个用户还没有登录或非法进入的
    private String phone_device;//用户设备ID，用来与服务器匹配身份用的
    private String phone_mimeno;//用户移动设备的MIME码，作用同上
    private String phone_simno;//用户的SIM卡号，作用同上，并区分营运商

    private NetworkUtil.OnNetworkStatusListener mListener;


/****************************************************************************************************************************************
 * USER自身函数的实现
 *****************************************************************************************************************************************/
    /**
     * @ClassName: User
     * @Method: getCache
     * @Params
     * @Description:
     * @author <a href="mailto:5303363@qq.com?subject=hello">hnyashiro</a> 5303363@qq.com
     * @date 2016/4/20 10:50
     * ${tags}
     */
    public Object getCache(String key){
        try {
            Log.e(TAG, "getCache: key="+key);
            Object cache_val = mShared.getString(key);
            Log.e(TAG, "getCache: value="+cache_val);
            if (cache_val!=""){
                return cache_val;
            }
        } catch (SharedUtil.NotFoundDataByKeyException e) {
            /*e.printStackTrace();*/
            return null;
        }
        return null;
    }

    public boolean getBooleanCache(String key){
        try {
            Log.e(TAG, "getCache: key="+key);
            boolean cache_val = mShared.getBoolean(key);
            Log.e(TAG, "getCache: value="+cache_val);
            return cache_val;
        } catch (SharedUtil.NotFoundDataByKeyException e) {
            /*e.printStackTrace();*/
            return false;
        }
    }
    public void setCache(String key,Object val){
        if (val instanceof Boolean){
            mShared.setBoolean(key,((Boolean)val).booleanValue());
        }else if(val instanceof Integer){
            mShared.setInt(key,((Integer)val).intValue());
        }else if(val instanceof  Long){
            mShared.setLong(key, ((Long) val).longValue());
        }else if(val instanceof Float){
            mShared.setFloat(key, ((Float) val).floatValue());
        }else if(val instanceof Set<?>){
            mShared.setStringSet(key,(Set<String>)val);
        }else {
            mShared.setString(key, (String) val);
        }
    }


/****************************************************************************************************************************************
* 重构区，对IUSER定义的方法进行实现
*****************************************************************************************************************************************/

    @Override
    public void checkLogin(Intent pIntent) {

    }

    @Override
    public Object getUserCacheDataCheckAndToLogin(String pUserDataName) {
        try {
            String cache_val = mShared.getString(pUserDataName);
            if (cache_val!=""){
                return cache_val;
            }else{
                Intent mIntent = new Intent(mContext, LoginActivity.class);
                Activity a = (Activity)mContext;
                a.startActivity(mIntent);
                a.finish();
            }
        } catch (SharedUtil.NotFoundDataByKeyException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public void CheckPermission(int intPermission) {

    }

    @Override
    public void Login(final String pPhoneNumber, final String pPassword,boolean puser_forget,boolean ppass_forget, final INetwork.OnNetworkStatusListener listener) {
        SERVER_USER_LOGIN_URL = ((AppApplication)mContext.getApplicationContext()).getConfigures().GetLOGINURL("Login");
        new Thread(new Runnable() {
            @Override
            public void run() {
                TelephonyUtils mTel= new TelephonyUtils(getContext());
                NetworkUtil mNet = new NetworkUtil();
                final Map<String, Object> mPostValues = new HashMap<String,Object>();
                mPostValues.put("PhoneNumber",pPhoneNumber);
                mPostValues.put("Up",pPassword);
                mPostValues.put("DeviceIMEI",mTel.getDeviceID());
                mPostValues.put("SimIMSI",mTel.getSIM());
                mPostValues.put("DeviceIP",mNet.getLanIP(getContext()));
                Log.e("LoginUrl-----[",SERVER_USER_LOGIN_URL);
                mNet.HttpSendPostData(SERVER_USER_LOGIN_URL, mPostValues, listener);

            }
        }).start();
        /*SERVER_USER_LOGIN_URL = ((AppApplication)mContext.getApplicationContext()).getConfigures().GetLOGINURL("Login");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if ((pPhoneNumber!="")&&(!Thread.currentThread().interrupted())) {
                    String url_params="PhoneNumber="+pPhoneNumber+"&Up="+pPassword;
                    TelephonyUtils mTel= new TelephonyUtils(getContext());
                    NetworkUtil mNet = new NetworkUtil();

                    url_params+="&DeviceIP="+mNet.getLanIP(getContext())
                            +"&DeviceIMEI="+mTel.getDeviceID()
                            +"&SimIMSI="+mTel.getSIM();
                    mTel=null;

                    mNet.HttpSendGetData(SERVER_USER_LOGIN_URL,url_params,listener);
                }


            }
        }).start();*/
    }

    @Override
    public void Logout(final INetwork.OnNetworkStatusListener listener) {
        SERVER_USER_LOGOUT_URL=((AppApplication)mContext.getApplicationContext()).getConfigures().GetAlternatelyURL("Logout");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String strpn = (String)getCache("PhoneNumber");
                Log.e(TAG, "run: "+SERVER_USER_LOGOUT_URL+"Account="+strpn);
                (new NetworkUtil()).HttpSendGetData(SERVER_USER_LOGOUT_URL, "Account="+strpn, listener);
            }
        }).start();
    }

/**
 * 权限控制模块
 * 权限为9个主菜单的0:1列表，例[111111111]表过有所有权限
 * 参数p为当前菜单的位置，也就是表示当前Activity的权限值，通过这个值找到权限列表中对应位置的数，比较其为1或0判断是否有权限
 * */
    private Permission m_Permission=null;
    public void checkPermission(int p,Intent i){
        if(!m_Permission.check(p)){
            mContext.startActivity(i);
        }
    }
    /**
     * 检查权限，有true无false
     * */
    public boolean check(int p){
        return m_Permission.check(p);
    }

    /**
    * @ClassName: User
    * @Method: getFace
     * @Params
    * @Description: 获取用户头像的图像信息
    * @author hnyashiro 5303363@qq.com
    * @date 2016/4/30 22:14
    * @return
    * ${tags}
    */
    public Bitmap getFace(INetwork.OnNetworkStatusListener listener){
        String url = "http://tu.joy3g.com/20160427034449088.jpg";
        new NetworkUtil().HttpGetRemoteImage(url, listener);
        return null;
    }

    public Object getInformation(){
        return null;
    }





    @Override
    protected void finalize() throws Throwable {
        mShared=null;
        super.finalize();
    }
}
