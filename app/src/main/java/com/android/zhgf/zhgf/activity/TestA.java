package com.android.zhgf.zhgf.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.GridView;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.adapter.GridViewAdapter;
import com.android.zhgf.zhgf.bean.GridPicture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colorful on 2017-10-23.
 */

public class TestA extends BaseActivity {
    private List<GridPicture> gpicList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_intelligence);

        GridView gview=(GridView)findViewById(R.id.intelligence_picture);
        addToGridView();
        GridViewAdapter gAdapter=new GridViewAdapter(gpicList,TestA.this);
        gview.setAdapter(gAdapter);

    }
    void addToGridView(/*String url*/){//String depicture
        gpicList=new ArrayList<>();
        for(int i=0;i<9;i++){
           /*final */GridPicture gridPicture=new GridPicture("http://192.168.10.117:81/1.jpg");
/*
new Thread(new Runnable() {
    @Override
    public void run() {
        gridPicture.setBitmap(BitmapFactory.decodeStream(ParseUrlUtil.HandlerData(gridPicture.getResourceId())));
    }
}).start();//这种解决方案内存大 必须异步
*/
            //将异步task置静态
            //new  GridViewAdapter.ParseUrl().execute(gridPicture);

            //同时解决难题
            gpicList.add(gridPicture);
        }
    }

   /* public static synchronized OAuthConstant getInstance(){
        if(instance == null)
            instance = new OAuthConstant();
        return instance;
    }
}单例*/
}
