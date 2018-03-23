package com.android.zhgf.zhgf.wnd.service;/**
 * Created by Administrator on 2016/6/7.
 */

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.zhgf.zhgf.app.AppApplication;
import com.ksxkq.materialpreference.SharedPreferenceStorageModule;

import static android.content.ContentValues.TAG;

/*******************************************************************************************************
 * Class Name:WndProject - GPSListener
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title GPSListener
 * @Package com.jiechu.wnd.service
 * @date 2016/6/7 22:48
 * @Description
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class GPSListener {
    LocationManager lm;
    Handler mGd;
    Location m_loc;
    private float minDistance = 8; //默认最小距离
    private Long minTime = 50000L;  //设置每5秒 获取一次GPS定位信息
    private SharedPreferenceStorageModule shareP;
    public GPSListener(AppApplication ctx) {
        Log.e("GPSListener", "GPSListener: Begin");
        mGd = ctx.getHandler();
        Log.e("GPSListener", "Get Context");
        shareP = new SharedPreferenceStorageModule(ctx);
        int timeInt = Integer.parseInt(shareP.getString("gpstime","50"));
        minTime = new Long((long)timeInt)* 1000L;
        Log.e("GPSListener", "Get Location_Service");
        /*if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }*/
        lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        //从GPS获取最近的定位信息
        try{
            m_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.e("GPSListener", "Get Last Know Location");

            //设置每minTime秒 获取一次GPS定位信息
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, new LocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // TODO Auto-generated method stub
                    switch (status) {
                        //GPS状态为可见时
                        case LocationProvider.AVAILABLE:
                            Log.i(TAG, "当前GPS状态为可见状态");
                            mGd.obtainMessage(901, "AVAILABLE").sendToTarget();
                            break;
                        //GPS状态为服务区外时
                        case LocationProvider.OUT_OF_SERVICE:
                            Log.i(TAG, "当前GPS状态为服务区外状态");
                            mGd.obtainMessage(902, "OUT_OF_SERVICE").sendToTarget();
                            break;
                        //GPS状态为暂停服务时
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                            Log.i(TAG, "当前GPS状态为暂停服务状态");
                            mGd.obtainMessage(903, "TEMPORARILY_UNAVAILABLE").sendToTarget();
                            break;
                    }
                }

                @Override
                public void onProviderEnabled(String provider) {

                    // 当GPS LocationProvider可用时，更新定位
                    try{
                        updateView(lm.getLastKnownLocation(provider));
                    }catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    mGd.obtainMessage(908,"GPS_ENABLE").sendToTarget();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    // TODO Auto-generated method stub
                    updateView(null);
                    mGd.obtainMessage(909,"GPS_DISABLE").sendToTarget();
                }

                @Override
                public void onLocationChanged(Location location) {
                    // 当GPS定位信息发生改变时，更新定位
                    updateView(location);
                }
            });
        }catch (SecurityException e) {
            e.printStackTrace();
        }





        //更新显示定位信息
        (new LoopGPS()).start();
        Log.e("GPSListener", "start Location Server");
    }

    void updateView(Location l){
        m_loc = l;
    }



    class LoopGPS extends Thread{
        @Override
        public void run() {
            int i=10000000;

            while (i>0){
                try {
                    int timeInt = Integer.parseInt(shareP.getString("gpstime","50"));
                    minTime = new Long((long)timeInt)* 1000L;
                    sleep(minTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (m_loc!=null && m_loc instanceof Location) {
                    mGd.obtainMessage(666,m_loc).sendToTarget();
                    Log.e("GPS", "坐标: LAT:" + m_loc.getLatitude() + "|" + m_loc.getLongitude());
                }else{
                    Log.e("GPS", "没有坐标");
                }
            }
        }
    }
}
