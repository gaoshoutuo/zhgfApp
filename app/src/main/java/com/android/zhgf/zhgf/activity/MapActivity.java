package com.android.zhgf.zhgf.activity;/**
 * Created by Administrator on 2016/5/2.
 */

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import com.android.zhgf.zhgf.R;

import com.android.zhgf.zhgf.app.AppApplication;
import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.MyLocationOverlay;

import java.util.Timer;
import java.util.TimerTask;

/*******************************************************************************************************
 * Class Name:WndProject - MapActivity
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title MapActivity
 * @Package com.jiechu.wnd
 * @date 2016/5/2 21:45
 * @Description 地图
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/


public class MapActivity extends Activity {
    private static final String TAG = MapActivity.class.getSimpleName();

    /**
     * int 10000000 default 坐标偏移参数;
     */
    private static int LIMIT_TRANS_LOCATION = 10000000;
    private MapView mMapView = null;
    protected View mViewLayout = null;
    protected Context mCon = null;
    int mCount = 0;

    double LatPos=0;
    double LonPos=0;
    Handler handl;
    AppApplication mGHnd=null;
    MyLocationOverlay mMyLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(this);
        //以上两行功能一样
        mViewLayout = inflater.inflate(R.layout.activity_map, null);
        setContentView(mViewLayout);
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.displayZoomControls(true);

        mMyLocation = new MyOverlay(this,mMapView);

        if (mGHnd==null) {
            mGHnd = (AppApplication) getApplication();
        }
        LatPos = mGHnd.getLatitude();
        LonPos = mGHnd.getLongitude();


        handl = new Handler() {
            public void handleMessage(Message msg) {
                int transLat = (int)(mGHnd.getLatitude()*LIMIT_TRANS_LOCATION);
                int transLon = (int)(mGHnd.getLongitude()*LIMIT_TRANS_LOCATION);
                Log.e(TAG, "MapActivity->handleMessage: 坐标Lat:" + transLat + "|Lon:" + transLon);
                if (transLat>0 && transLon>0) {
                    GeoPoint tpGeo = new GeoPoint(transLat, transLon);
                    mMapView.getController().animateTo(tpGeo);
                    //Log.e(TAG, "MapActivity->handleMessage: Lat:" + transLat + "|Lon:" + transLat);
                }else{
                    Log.e(TAG, "handleMessage: 暂时未获取到坐标");
                }

            }
        };
        TimerTask MoveTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = handl.obtainMessage(2);
                handl.sendMessage(msg);
            }
        };
        Timer mMoveTimer = new Timer();
        mMoveTimer.schedule(MoveTask, 1000, 2000);

        /*ImageButton location = (ImageButton)findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                GeoPoint point = mMyLocation.getMyLocation();
                if(point != null)
                    mMapView.getController().animateTo(point);
            }

        });*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        mMapView.getController().stopAnimation(false);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK)
            finish();
        return super.onKeyUp(keyCode, event);
    }

    class MyOverlay extends MyLocationOverlay
    {
        public MyOverlay(Context context, MapView mapView) {
            super(context, mapView);
            // TODO Auto-generated constructor stub
        }

        // 处理在"我的位置"上的点击事件
        protected boolean dispatchTap()
        {
            Toast.makeText(mCon, "您点击了我的位置", Toast.LENGTH_SHORT).show();
            return true;
        }
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            super.onLocationChanged(location);
            if(location != null){
                String strLog = String.format("您当前的位置:\r\n" +
                                "纬度:%f\r\n" +
                                "经度:%f",
                        location.getLongitude(), location.getLatitude());
                Toast.makeText(mCon, strLog, Toast.LENGTH_SHORT).show();
            }
            GeoPoint point = mMyLocation.getMyLocation();
            if(point != null)
                mMapView.getController().animateTo(point);
        }
    }




}
