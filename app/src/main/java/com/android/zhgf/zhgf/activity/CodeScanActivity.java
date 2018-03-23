package com.android.zhgf.zhgf.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.zhgf.zhgf.R;

public class CodeScanActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scan);

        // 返回图标
        setBack();
        setTitle("二维码扫描");
        Toast.makeText(this,"二维码扫描",Toast.LENGTH_SHORT).show();

    }
}
