package com.android.zhgf.zhgf.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.zhgf.zhgf.R;

public class ResultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        // 返回图标
        setBack();
        setTitle("试卷一，政治测试");
    }
}
