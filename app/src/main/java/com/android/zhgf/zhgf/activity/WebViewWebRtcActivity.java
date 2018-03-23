package com.android.zhgf.zhgf.activity;

import android.annotation.TargetApi;
import android.media.audiofx.BassBoost;
import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.app.AppApplication;

import static io.dcloud.common.util.ReflectUtils.getApplicationContext;

public class WebViewWebRtcActivity extends BaseActivity {
    private WebView webView;
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_web_rtc);
        webView = (WebView) findViewById(R.id.web_view);
        editText = (EditText) findViewById(R.id.et_url);
        button = (Button) findViewById(R.id.btn_search);
        // editText.setText("https://www.baidu.com");
        editText.setText(((AppApplication)getApplicationContext()).getConfigures().GetWebRtc());

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        //如果不调用这个函数，将会打开一个默认的浏览器

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editText.getText().toString().toLowerCase();
                if (TextUtils.isEmpty(url)) {
                    Toast.makeText(WebViewWebRtcActivity.this, "url不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    webView.loadUrl(url);
                }
            }
        });
        //判断页面加载的过程
        webView.setWebChromeClient(new WebChromeClient() {
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

                WebViewWebRtcActivity.this.runOnUiThread(new Runnable(){
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        request.grant(request.getResources());
                    }// run
                });// MainActivity

            }// onPermissionRequest

        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(webView.canGoBack())
            {
                webView.goBack();//返回上一页面
                return true;
            }
            else
            {
                System.exit(0);//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
