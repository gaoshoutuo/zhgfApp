package com.android.zhgf.zhgf.wnd.service;/**
 * Created by Administrator on 2016/5/10.
 */

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.zhgf.zhgf.wnd.global.GlobalHandler;
import com.android.zhgf.zhgf.wnd.iface.impl.User;
import com.android.zhgf.zhgf.wnd.utils.DialogUtil;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;
import com.android.zhgf.zhgf.wnd.utils.TelephonyUtils;

/*******************************************************************************************************
 * Class Name:WndProject - GPSService
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title GPSService
 * @Package com.jiechu.wnd.service
 * @date 2016/5/10 14:46
 * @Description GPS服务
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class GPSService {
    private static final String TAG = GPSService.class.getSimpleName();
    public static final int SLEEP_SEND_TIME = 1500;

    GlobalHandler m_App = null;

    Handler mHandler;

    LocationManager m_locM = null;
    Location m_loc = null;


    public GPSService(GlobalHandler app) {
        Log.e(TAG, "GPSService: GPS服务开始中............");

        (new LoopGPS()).start();
        Log.e(TAG, "GPSService: LoopGPS Start");
        Looper.prepare();
        m_App = app;
        mHandler = app.getHandler();
        Looper.loop();
        m_locM = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        m_loc = m_locM.getLastKnownLocation(getProvider());
        m_locM.requestLocationUpdates(LocationManager.GPS_PROVIDER, SLEEP_SEND_TIME, 10, new GPSListener());

    }

    private String getProvider() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setCostAllowed(false);
        c.setPowerRequirement(Criteria.POWER_LOW);
        return m_locM.getBestProvider(c, true);
    }

    class GPSListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                m_loc = location;
                mHandler.obtainMessage(900, location).sendToTarget();
            } else {
                mHandler.obtainMessage(907).sendToTarget();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    mHandler.obtainMessage(901, "AVAILABLE").sendToTarget();
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    mHandler.obtainMessage(902, "OUT_OF_SERVICE").sendToTarget();
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    mHandler.obtainMessage(903, "TEMPORARILY_UNAVAILABLE").sendToTarget();
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "当前GPS状态为可用");
            m_loc = m_locM.getLastKnownLocation(getProvider());
            mHandler.obtainMessage(908,"GPS_ENABLE").sendToTarget();
        }

        @Override
        public void onProviderDisabled(String provider) {
            mHandler.obtainMessage(909,"GPS_DISABLE").sendToTarget();
        }
    }

    class LoopGPS extends Thread{
        @Override
        public void run() {
            Log.e(TAG, "run: 开始轮循GPS");
            NetworkUtil m_Net = new NetworkUtil();
            while (true){
                try {
                    Thread.currentThread().sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (m_loc!=null && m_loc instanceof Location){
                    Log.e(TAG, "坐标: LAT:"+m_loc.getLatitude()+"|"+m_loc.getLongitude());
                    DialogUtil.Toast(m_App.getApplicationContext(),"LAT:"+m_loc.getLatitude()+"|"+m_loc.getLongitude());
                    String GPS_URL = m_App.getConfigures().GetAlternatelyURL("GPS_DATA");
                    String PhoneNumber = (String)m_App.getUser().getCache(User.CACHE_KEY_USER_PHONE);
                    TelephonyUtils mTel = new TelephonyUtils(m_App);
                    String GPS_PAM = "DeviceNumber=" +PhoneNumber+
                            "&DeviceIMEI="+mTel.getDeviceID() +
                            "&SimIMSI="+mTel.getSIM() +
                            "&Y=" + m_loc.getLatitude()+
                            "&X="+ m_loc.getLongitude()+
                            "&DeviceType=" +
                            "&DeviceIP=" +
                            "&MsgInfo=";
                    m_Net.HttpSendGetData(GPS_URL,GPS_PAM,null);
                }else{
                    Log.e(TAG, "run: 未获取到坐标信息");
                }

            }

        }
    }

    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
}
