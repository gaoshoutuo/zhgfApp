package com.android.zhgf.zhgf.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import com.android.zhgf.zhgf.JavaBean.IntelligenceInfo;
import com.android.zhgf.zhgf.R;

import com.android.zhgf.zhgf.adapter.IntelligenceAdapter;
import com.android.zhgf.zhgf.bean.IntelligenceHistoryBean;


import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

public class Intelligence extends BaseActivity {

    RecyclerView recyclerView;
    private ArrayList<IntelligenceHistoryBean>rList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rList=(ArrayList<IntelligenceHistoryBean>) getIntent().getSerializableExtra("jsonData");
       // addToRecy();
        IntelligenceAdapter rAdapter=new IntelligenceAdapter(rList,Intelligence.this);
        /*setContentView(R.layout.item_intelligence);*/
        setTitle("情报历史");
        setContentView(R.layout.activity_intelligence);
        findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView=(RecyclerView)findViewById(R.id.intell_layout);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rAdapter);
        rAdapter.setOnItemClickListener(new IntelligenceAdapter.OnItemClickListener(){
            //一个有某种方法的动态对象
            @Override
            public void onItemClick(View view , int position){
                Toast.makeText(Intelligence.this, position+"", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /* public static synchronized OAuthConstant getInstance(){
         if(instance == null)
             instance = new OAuthConstant();
         return instance;
     }
 }单例
 */


    void addToRecy(/*String url*/){

        rList=new ArrayList<>();
        for(int i=0;i<3;i++){
            //这边i<几不是最终的  最终肯定要根据文件夹数目来看
            //IntelligenceInfo intelligenceInfo=new IntelligenceInfo("http://192.168.10.103:81/7156.txt","http://192.168.10.103:81/7156.amr","http://192.168.10.103:81/7156.mp4");

        }
    }

}
