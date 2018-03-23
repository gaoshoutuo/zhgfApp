package com.android.zhgf.zhgf.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.zhgf.zhgf.R;
import com.bumptech.glide.Glide;

public class BigImageActivity extends AppCompatActivity {
   void initView(String x){
       Glide.with(this).load(x).into(imageView);
   }
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
        imageView= (ImageView) findViewById(R.id.bigmap_id);
        String xy=getIntent().getStringExtra("url");
        Log.e("erty",xy);
        initView(xy);
       // initView();//其实都是伪代码 不对啊 其实在本来就是先setContentView然后再对立面进行操作
        //
    }
}
