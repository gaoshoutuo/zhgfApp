package com.android.zhgf.zhgf.wnd.utils;/**
 * Created by Administrator on 2016/4/20.
 */

import android.Manifest;
import android.app.Application;
import android.content.Context;

import com.android.zhgf.zhgf.wnd.global.GlobalHandler;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/*******************************************************************************************************
 * Class Name:
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title
 * @Package com.jiechu.wnd.utils
 * @date 2016/4/20 19:26
 * @Description 定时获取GPS坐标
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class GPSService implements Runnable{
    private static final String TAG = GPSService.class.getSimpleName();


    public static final int SLEEP_SEND_TIME = 1500;

    Application mApplication;

    GlobalHandler.GlobalHandlerExt mHandler;//全局Handler对象

    private Location location = null;
    private LocationManager locationManager = null;


    boolean threadDisable=false;

    public GPSService(Application app) {
        mApplication = app;
        mHandler = ((GlobalHandler) app).getHandler();
        locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);

        location = locationManager.getLastKnownLocation(getProvider());
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SLEEP_SEND_TIME,10, ll);
    }

    private String getProvider() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setCostAllowed(false);
        c.setPowerRequirement(Criteria.POWER_LOW);
        return locationManager.getBestProvider(c, true);
    }


    public void startGPS(){
        new Thread(this).start();
    }

    private LocationListener ll = new LocationListener() {
        public void onLocationChanged(Location l) {
            if (l != null) location = l;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

            Location l = locationManager.getLastKnownLocation(provider);
            if (l!=null)location=l;
        }

        @Override
        public void onProviderDisabled(String provider) {
            location=null;
        }
    };

    @Override
    public void run() {
        int Try_Number=0;
        while(!threadDisable){
            try {
                Thread.sleep(SLEEP_SEND_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Location l = getLocation();
            if (null!=null){
                ((GlobalHandler)mApplication).setXY(l.getLongitude(), l.getLatitude());

            }
            //((GlobalHandler)mApplication).getHandler().sendEmptyMessage(1000001);
        }
    }

    Location getLocation(){
        return location;
    }
}
