package com.android.zhgf.zhgf.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.tool.FastBlur;

public class VideoChatActivity extends Activity {

    private ImageView imgUserHead;

    private ImageView imgBg;

    private int scaleRatio = 5;

    private int blurRadius = 10;

    private LinearLayout layoutMenu1;

    private LinearLayout layoutMenu;

    private LinearLayout layoutJieTing;

    private TextView timeTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        imgUserHead = (ImageView) findViewById(R.id.imgUserHead);
        imgBg = (ImageView) findViewById(R.id.imgBg);

        layoutMenu1 =  (LinearLayout) findViewById(R.id.layoutMenu1);

        layoutMenu =  (LinearLayout) findViewById(R.id.layoutMenu);

        layoutJieTing = (LinearLayout) findViewById(R.id.layoutJieTing);

        timeTv = (TextView) findViewById(R.id.textTime);

        layoutMenu.setVisibility(View.GONE);
        timeTv.setVisibility(View.GONE);
        LinearLayout layoutGuaDuan = (LinearLayout) findViewById(R.id.layoutGuaDuan);
        LinearLayout layoutMianTi = (LinearLayout) findViewById(R.id.layoutMianTi);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        layoutJieTing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutMenu1.setVisibility(View.GONE);
                layoutMenu.setVisibility(View.VISIBLE);
                timeTv.setVisibility(View.VISIBLE);
            }
        });

        layoutGuaDuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgUserHead.setImageResource(R.drawable.header1);

                Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.header1);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap,
                        mBitmap.getWidth() / scaleRatio,
                        mBitmap.getHeight() / scaleRatio,
                        false);
                Bitmap blurBitmap = FastBlur.doBlur(scaledBitmap, blurRadius, true);

                imgBg.setImageBitmap(blurBitmap);

            }
        });

        layoutMianTi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgUserHead.setImageResource(R.drawable.header2);

                Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.header2);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap,
                        mBitmap.getWidth() / scaleRatio,
                        mBitmap.getHeight() / scaleRatio,
                        false);
                Bitmap blurBitmap = FastBlur.doBlur(scaledBitmap, blurRadius, true);

                imgBg.setImageBitmap(blurBitmap);
            }
        });
    }
}
