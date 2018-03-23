package com.android.zhgf.zhgf.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.wnd.utils.DialogHelper;
import com.android.zhgf.zhgf.wnd.utils.DialogUtil;
import com.android.zhgf.zhgf.wnd.utils.TelephonyUtils;
import com.android.zhgf.zhgf.wnd.utils.UpdateManager;

public class UpdateActivity extends Activity {
    public static final String T=UpdateActivity.class.getSimpleName();
    private UpdateManager updateMan;
    private ProgressDialog updateProgressDialog;
    private TextView update_info;
    private Button update_btn;
    //private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        update_info =  (TextView) this.findViewById(R.id.update_info);
        update_btn = (Button) this.findViewById(R.id.update_btn);

        /*mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在检查更新");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);*/

        TelephonyUtils mTel = new TelephonyUtils(getApplicationContext());
        //网络可用
        if(mTel.isNetworkAvailable()){
            // wifi连接 && wifi可用
            if(mTel.isWifi()&& mTel.isWifiAvailable()){
                 //检查是否有更新
                //如果有更新提示下载
                //mProgressDialog.show();
                updateMan = new UpdateManager(UpdateActivity.this, appUpdateCb);
                updateMan.checkUpdate();

            }else{
                AlertDialog ad = new AlertDialog.Builder(UpdateActivity.this).create();
                ad.setTitle("温馨提示");
                ad.setMessage("当前未在WiFi环境下，是否继续检查更新。");
                ad.setButton("确定",new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //检查是否有更新
                        //如果有更新提示下载
                        //mProgressDialog.show();
                        updateMan = new UpdateManager(UpdateActivity.this, appUpdateCb);
                        updateMan.checkUpdate();

                    }
                });
                ad.setButton2("退出",new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int which) {
                        finish();
                        return;

                    }
                });
                ad.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                        return;
                    }
                });
                ad.setCanceledOnTouchOutside(false);
                ad.show();

            }

        }else{
            //网络不可用跳对话框，请连接网络
            AlertDialog ad = new AlertDialog.Builder(UpdateActivity.this).create();
            ad.setTitle("温馨提示");
            ad.setMessage("网络未连接，请查看网络设置。");
            ad.setButton("确定",new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog,
                                    int which) {
                    finish();
                    return;

                }
            });

            ad.setCanceledOnTouchOutside(false);
            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                    return;
                }
            });
            ad.show();
            /*DialogHelper.Confirm(UpdateActivity.this,
                    "温馨提示",
                    "网络未连接，请查看网络设置。",
                    "确定",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                            return;

                        }
                    },null,null);*/

        }



    }
    // 自动更新回调函数
    UpdateManager.UpdateCallback appUpdateCb = new UpdateManager.UpdateCallback()
    {

        public void downloadProgressChanged(int progress) {
            if (updateProgressDialog != null
                    && updateProgressDialog.isShowing()) {
                updateProgressDialog.setProgress(progress);
            }

        }

        public void downloadCompleted(Boolean sucess, CharSequence errorMsg) {
            if (updateProgressDialog != null
                    && updateProgressDialog.isShowing()) {
                updateProgressDialog.dismiss();
            }
            if (sucess) {
                updateMan.update();
            } else {
                try{
                    AlertDialog ad = new AlertDialog.Builder(UpdateActivity.this).create();
                    ad.setTitle("错误");
                    ad.setMessage("下载更新失败，是否重试？");
                    ad.setButton("重新下载",new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int which) {
                            updateProgressDialog = new ProgressDialog(
                                    UpdateActivity.this);
                            updateProgressDialog
                                    .setMessage(getText(R.string.dialog_downloading_msg));
                            updateProgressDialog.setIndeterminate(false);
                            updateProgressDialog
                                    .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            updateProgressDialog.setMax(100);
                            updateProgressDialog.setProgress(0);
                            updateProgressDialog.setCanceledOnTouchOutside(false);
                            updateProgressDialog.show();

                            updateMan.downloadPackage();

                        }
                    });
                    ad.setButton2("下次再说",new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                            return;

                        }
                    });
                    ad.setCanceledOnTouchOutside(false);
                    ad.show();
                }catch(Exception e){
                    e.printStackTrace();
                }




                /*DialogHelper.Confirm(UpdateActivity.this,
                        R.string.dialog_error_title,
                        R.string.dialog_downfailed_msg,
                        R.string.dialog_downfailed_btndown,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                updateMan.downloadPackage();

                            }
                        }, R.string.dialog_downfailed_btnnext, null);*/
            }
        }

        public void downloadCanceled()
        {
            // TODO Auto-generated method stub

        }

        public void checkUpdateCompleted(Boolean hasUpdate,
                                         CharSequence updateInfo) {
            //mProgressDialog.hide();
            if (hasUpdate) {

                DialogHelper.Confirm(UpdateActivity.this,
                        getText(R.string.dialog_update_title),
                        getText(R.string.dialog_update_msg).toString()
                                +
                                getText(R.string.dialog_update_msg2).toString(),
                        getText(R.string.dialog_update_btnupdate),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                updateProgressDialog = new ProgressDialog(
                                        UpdateActivity.this);
                                updateProgressDialog
                                        .setMessage(getText(R.string.dialog_downloading_msg));
                                updateProgressDialog.setIndeterminate(false);
                                updateProgressDialog
                                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                updateProgressDialog.setMax(100);
                                updateProgressDialog.setProgress(0);
                                updateProgressDialog.setCanceledOnTouchOutside(false);
                                updateProgressDialog.show();
                                updateMan.downloadPackage();
                            }
                        },getText( R.string.dialog_update_btnnext), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {

                            }
                        });
                update_btn.setVisibility(View.VISIBLE);
                update_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateProgressDialog = new ProgressDialog(
                                UpdateActivity.this);
                        updateProgressDialog
                                .setMessage(getText(R.string.dialog_downloading_msg));
                        updateProgressDialog.setIndeterminate(false);
                        updateProgressDialog
                                .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        updateProgressDialog.setMax(100);
                        updateProgressDialog.setProgress(0);
                        updateProgressDialog.setCanceledOnTouchOutside(false);
                        updateProgressDialog.show();
                        updateMan.downloadPackage();
                    }
                });
                update_info.setText("有新的版本可以下载。");

            }else{
                update_info.setText("您已经是最新版本了，无需更新");
                update_btn.setVisibility(View.GONE);
            }

        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE){

        }else{

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }*/
        if(updateProgressDialog != null){
            updateProgressDialog.dismiss();
        }
    }
}
