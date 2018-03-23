package com.android.zhgf.zhgf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.zhgf.zhgf.app.AppApplication;
import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.ItemizedOverlay;
import com.tianditu.android.maps.MapController;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.MyLocationOverlay;
import com.tianditu.android.maps.Overlay;
import com.tianditu.android.maps.OverlayItem;
import com.android.zhgf.zhgf.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyt on 2016/7/18.
 */
public class TDMapActivity extends Activity {
    private static String TAG = TDMapActivity.class.getSimpleName();

    private MapView mMap = null;
    private MapController mCrl = null;
    List<Overlay> mOverlays = null;




    double x = 30.6311;
    double y = 120.5475;
    GeoPoint GeoMovePoint=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdmap);
        MapView mMap  = (MapView)findViewById(R.id.mTDMapVIew);
        mMap.setBuiltInZoomControls(true);

        mOverlays = mMap.getOverlays();

        MyLocationOverlay mo = new MyLocationOverlay(this,mMap);
        mo.enableCompass();
        mo.enableMyLocation();
        mOverlays.add(mo);






        mCrl = mMap.getController();

        GeoPoint tpPoint = new GeoPoint((int)(x*1E6), (int)(y*1E6));
        mCrl.setCenter(tpPoint);
/*        OverlayItem oi = new OverlayItem(tpPoint,"","");*/



        mCrl.setZoom(10);

        Drawable d = getResources().getDrawable(R.drawable.ic_action_name);

        mMap.getOverlays().add(new HumansOverlay(this,d));
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    class HumansOverlay extends ItemizedOverlay<OverlayItem> {
        Context mCtx;
        private List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();


        private double mLat1 = 30.63122;
        private double mLon1 = 120.547528;
        private double mLat2 = 30.60122;
        private double mLon2 =120.527528;
        private double mLat3 = 30.61122;
        private double mLon3 = 120.527528;

        public HumansOverlay(Context pCtx,Drawable maker){
            super(boundCenter(maker));
            mCtx = pCtx;

            GeoPoint p1 = new GeoPoint((int)(mLat1*1E6),(int)(mLon1*1E6));
            GeoPoint p2 = new GeoPoint((int)(mLat2*1E6),(int)(mLon2*1E6));
            GeoPoint p3 = new GeoPoint((int)(mLat3*1E6),(int)(mLon3*1E6));
            /*mGeoList.add(new OverlayItem(p1,"T1","Point1"));
            mGeoList.add(new OverlayItem(p2,"T2","Point2"));
            mGeoList.add(new OverlayItem(p3,"T3","Point3"));*/
            populate();
            //GeoPoint me = new GeoPoint((int)(((AppApplication)getApplication()).getLatitude()),(int)(((AppApplication)getApplication()).getLongitude()));
            OverlayItem oi = new OverlayItem(p2,"T4","aaaaaaaaaaaaaa");
            //mGeoList.add(oi);
        }

        @Override
        protected boolean onTap(int i) {
            Toast.makeText(mCtx,mGeoList.get(i).getSnippet(),Toast.LENGTH_SHORT).show();
            return super.onTap(i);
        }

        @Override
        protected OverlayItem createItem(int i) {
            return mGeoList.get(i);
        }

        @Override
        public int size() {
            return mGeoList.size();
        }
    }
}
