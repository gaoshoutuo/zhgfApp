package com.android.zhgf.zhgf.activity;

import android.annotation.TargetApi;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.zhgf.zhgf.app.AppApplication;

public class WebRtcDemo extends MPermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission(AppApplication.PermissionLists,0x001);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void permissionSuccess(int requestCode) {
        switch (requestCode){
            case 0x001:
                initWeb();
                break;
            default:
                Log.e(TAG, "WebRtcDemo:::permissionSuccess 权限获取失败");
                break;
        }
    }

    void initWeb(){
        WebView mView = new WebView(this);

        WebSettings mSetting = mView.getSettings();
        mSetting.setJavaScriptEnabled(true);
        //mSetting.setDefaultTextEncodingName("GB18030");
        mView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        mView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    // 网页加载完成
                    Log.d("加载完成...","success");

                } else {
                    // 加载中
                    Log.d("加载中...",+newProgress+"");

                }



            }
            @Override
            public void onPermissionRequest(final PermissionRequest request) {

                WebRtcDemo.this.runOnUiThread(new Runnable(){
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        request.grant(request.getResources());
                    }// run
                });// MainActivity

            }// onPermissionRequest
        });
        setContentView(mView);
        mView.loadUrl(((AppApplication)getApplicationContext()).getConfigures().GetWebRtc());

    }
}
