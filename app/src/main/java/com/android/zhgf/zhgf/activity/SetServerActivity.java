package com.android.zhgf.zhgf.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.zhgf.zhgf.R;
//import com.github.ggggxiaolong.xmpp.utils.CommonField;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.app.Constant;
import com.android.zhgf.zhgf.util.SharedPreferencesUtils;
import com.android.zhgf.zhgf.utils.FabTransform;
import com.android.zhgf.zhgf.utils.PreferencesUtils;
//import com.github.ggggxiaolong.xmpp.utils.ThreadUtil;
import com.android.zhgf.zhgf.wnd.global.Configure;
import com.ksxkq.materialpreference.SharedPreferenceStorageModule;
import com.sdsmdg.tastytoast.TastyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.text.TextUtils.isEmpty;

public class SetServerActivity extends Activity  {

    @BindView(R.id.container)
    ViewGroup mContainer;
    @BindView(R.id.pwd)
    TextInputEditText mpwd;
    @BindView(R.id.address)
    TextInputEditText mAddress;
    @BindView(R.id.port)
    TextInputEditText mPort;
    @BindView(R.id.confirm)
    Button mConfirm;
    @BindView(R.id.serverSpi)
    Spinner serverSpi;
    private SharedPreferencesUtils helper;
    private SharedPreferenceStorageModule shareP;
    String[] vpnServer;
    String vpnServerName;
    String vpnServerIP;
    String vpnServerPort;

    int beforePos = 0;
    String beforeServerName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_server);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            FabTransform.setup(this, mContainer);
        }



        //set watcher action
        initListener();
        //set the latest data
        initData();
    }

    private void initData() {
        shareP = new SharedPreferenceStorageModule(SetServerActivity.this);
        serverSpi.setSelection(0);
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        helper = new SharedPreferencesUtils(this, "setting");
        /*vpnServer =helper.getString(Constant.OPERATOR_CMCC).split(",");
        if(vpnServer != null &&vpnServer.length > 2) {
            mAddress.setText(vpnServer[1]);
            mPort.setText(vpnServer[2]);

        }*/
        serverSpi.setSelection(0);
        /*String name = PreferencesUtils.getString(CommonField.SERVER_NAME);
        String address = PreferencesUtils.getString(CommonField.SERVER_ADDRESS);
        int port = PreferencesUtils.getInt(CommonField.SERVER_PORT);
        Timber.i("name = %s, address = %s, port = %s", name, address, port);
        if (!isEmpty(address) && !isEmpty(name) && port != 0) {
            mName.setText(name);
            mAddress.setText(address);
            mPort.setText(String.valueOf(port));
        }*/
    }

    private void initListener() {

        mAddress.addTextChangedListener(mTextWatcher);
        mAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mPort.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mPort.addTextChangedListener(mTextWatcher);
        mPort.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mpwd.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mpwd.addTextChangedListener(mTextWatcher);
        mpwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT && isSettingValid()) {
                    mConfirm.performClick();
                    return true;
                }
                return false;
            }
        });

        serverSpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if(beforeServerName != ""){
                    String valueStr = beforeServerName + "," + mAddress.getText().toString() + "," + mPort.getText().toString();
                    helper.putValues(new SharedPreferencesUtils.ContentValue(beforeServerName,valueStr));
                }
                beforePos = pos;
                beforeServerName = serverSpi.getSelectedItem().toString();
                switch (pos){
                    case 0: vpnServer =helper.getString(Constant.OPERATOR_CMCC).split(",");
                        break;
                    case 1: vpnServer =helper.getString(Constant.OPERATOR_UNICOM).split(",");
                        break;
                    case 2: vpnServer =helper.getString(Constant.OPERATOR_TELICOM).split(",");
                        break;
                    case 3: vpnServer =helper.getString(Constant.OPERATOR_TEST).split(",");
                        break;
                    default:break;
                }
                if(vpnServer != null &&vpnServer.length > 2) {
                    mAddress.setText(vpnServer[1]);
                    mPort.setText(vpnServer[2]);

                }

                /*String[] serverName = getResources().getStringArray(R.array.server_values_array);
                shareP.putString("server",serverName[pos]);
                vpnServer =serverName[pos].split(",");
                if(vpnServer.length > 2){

                    vpnServerName = vpnServer[0];
                    vpnServerIP = vpnServer[1];
                    vpnServerPort = vpnServer[2];
                    Configure tcfg = ((AppApplication) getApplication()).getConfigures();
                    tcfg.setOperatorName(vpnServerName);
                }
                //showToast( "你点击的是:"+serverName[pos]);*/
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    @Override
    public void onBackPressed()  {
        dismiss(null);
    }

    @OnClick(R.id.cancel)
    public void dismiss(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    @OnClick(R.id.confirm)
    void setServer() {
        Timber.i("sure clicked");
        /*ThreadUtil.runONWorkThread(new Runnable() {
            @Override
            public void run() {
                String address = mAddress.getText().toString();
                String name = mName.getText().toString();
                int port = Integer.parseInt(mPort.getText().toString());
                mPresenter.Connect(name, address, port);
            }
        });*/
    }

    private boolean isSettingValid() {
        return mpwd.length() > 0 && mAddress.length() > 0 && mPort.length() > 0;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mConfirm.setEnabled(isSettingValid());
        }
    };
}
