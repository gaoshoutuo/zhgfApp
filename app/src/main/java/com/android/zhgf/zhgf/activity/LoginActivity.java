package com.android.zhgf.zhgf.activity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.zhgf.zhgf.JavaBean.LoginInfo;
import com.android.zhgf.zhgf.JavaBean.NewsDetail;
import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.app.Constant;
import com.android.zhgf.zhgf.core.ICommand;
import com.android.zhgf.zhgf.core.RuningTime;
import com.android.zhgf.zhgf.core.cmd.LoginCommand;
import com.android.zhgf.zhgf.service.XMPPService;
import com.android.zhgf.zhgf.tasks.LoginTask;
import com.android.zhgf.zhgf.tasks.Response.Listener;
import com.android.zhgf.zhgf.util.Base64Utils;
import com.android.zhgf.zhgf.util.DateUtil;
import com.android.zhgf.zhgf.util.SharedPreferencesUtils;
import com.android.zhgf.zhgf.utils.FabTransform;
import com.android.zhgf.zhgf.utils.MyOkhttp;
import com.android.zhgf.zhgf.utils.ThreadUtil;
import com.android.zhgf.zhgf.utils.XMPPUtil;
import com.android.zhgf.zhgf.widget.LoadingDialog;
import com.android.zhgf.zhgf.wnd.global.Configure;
import com.android.zhgf.zhgf.wnd.iface.INetwork;
import com.android.zhgf.zhgf.wnd.iface.impl.User;
import com.android.zhgf.zhgf.wnd.utils.DialogUtil;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;
import com.android.zhgf.zhgf.wnd.utils.TelephonyUtils;
import com.google.gson.Gson;
import com.ksxkq.materialpreference.SharedPreferenceStorageModule;
import com.sdsmdg.tastytoast.TastyToast;
import com.vsg.vpn.logic.VSGService;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;


public class LoginActivity extends MPermissionActivity implements  View.OnTouchListener,View.OnClickListener,Listener<Boolean>,VSGService.StateListener,VSGService.SharedLoginCallback {
    private static final String TAG = LoginActivity.class.getSimpleName();
    // 用户名输入框
    private EditText etUsername;
    // 密码输入框
    private EditText etPassword;
    // 登录按钮
    private Button btnLogin;
    // 自动登录
    private Switch swAutoLogin;
    // 密码显示标志 true 不显示 false显示
    boolean passwordVisableFlg = true;
    //显示正在加载的对话框
    private LoadingDialog mLoadingDialog;

    private LoginTask loginTask;

    private Spinner serverSpi;

    private User mUser;
    private View mProgressView;
    String HostServer="";
    String PhoneNumber="";
    String PhonePwd="";
    boolean isForgetName=false;
    boolean isForgetPwd=false;
    private ProgressDialog mProgressDialog;

    String pattern = "yyyy-MM-dd";
    private SharedPreferencesUtils helper;
    private SharedPreferenceStorageModule shareP;

    String[] vpnServer;
    String vpnServerName;
    String vpnServerIP;
    String vpnServerPort;
    int count = 0;
    private ImageButton fab;
    private Handler lgHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1) {
                if((boolean) msg.obj){
                    mProgressDialog.show();
                }
                //mProgressView.setVisibility(((boolean) msg.obj) == true ? View.VISIBLE : View.GONE);
            }else if (msg.what==2) {
                btnLogin.setEnabled(true);
                if(!(boolean) msg.obj){
                    mProgressDialog.hide();
                }
                mUser.setCache(User.CACHE_KEY__USER_ISFORGET_NAME, false);
                ((AppApplication) getApplication()).setUser(mUser);
                //mProgressView.setVisibility(((boolean) msg.obj) == true ? View.VISIBLE : View.GONE);
                showToast("登入失败");
            }else if(msg.what==6767){
                ((AppApplication)getApplication()).getConfigures().setHostServer(HostServer);
            }else if(msg.what==4){
                Log.e(TAG, "----------------------------------------登入Login----------------------------------------");
                mUser.Login(PhoneNumber, PhonePwd, isForgetName, isForgetPwd, new OnNetWorkListener());
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);
        // 设置安装时间和VPN服务器信息
        setVersionTimeAndSetServer();
        // view初始化
        initViews();
        // 事件设置
        setupEvents();
        // 数据初始化
        //initData();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    void prepare(){
        Intent intent = new Intent();
        /*在启动VPN服务之前先prepare，方法如下*/
        try{
            intent = VpnService.prepare(getApplicationContext());
        }catch (IllegalStateException ex){
            Log.e(TAG,"vpn not supported during lockdown mode");
            return;
        }
        if (intent != null){
            try{
                /*启动的activity会显示一个让用户同意授权的对话框，在用户选择“确定”或“取消”按钮结束该对话框后，会调用onActivityResult函数*/
                startActivityForResult(intent,0);
            }catch (ActivityNotFoundException ex){
                Log.e(TAG,"vpn not supported");
            }
        }else{
            /*已经prepare过了直接调用onActivityResult*/
            onActivityResult(0, RESULT_OK, null);
        }

    }
    @Override
    protected void onStart() {

        //Log.e(TAG, "mServerPresenter.Connect___________________________________________________");
        //mServerPresenter.Connect("user1", "192.168.10.197", 5222);
        super.onStart();
        //注册回调
        VSGService.registerListener(this);
    }
    @Override
    public void onStop(){
        VSGService.unregisterListener();
        super.onStop();
    }
    /**
     * 画面view初始化
     */
    private void initViews()
    {
        fab = (ImageButton) findViewById(R.id.fab);
        fab.setOnClickListener(mFabListener);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("隧道正在建立，请耐心等待");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        requestPermission(AppApplication.mPermissions,0x0001);
        // 用户名输入
        etUsername = (EditText) this.findViewById(R.id.etUserName);

        // 密码输入EditText
        etPassword = (EditText) this.findViewById(R.id.etPassword);

        // 登录按钮
        btnLogin = (Button) this.findViewById(R.id.btLogin);

        // 自动登入
        swAutoLogin = (Switch) this.findViewById(R.id.swAutoLogin);

        // 服务器选择
        serverSpi = (Spinner) findViewById(R.id.serverSpi);

        shareP = new SharedPreferenceStorageModule(LoginActivity.this);

        //TODO 创新一个用户实例，保存了用户的基本情况，验证信息及权限信息，共公设置等数据
        mUser = new User(getApplication());

        HostServer = (String)mUser.getCache(User.CACHE_KEY_HOST_SERVER);

        PhoneNumber=(String)mUser.getCache(User.CACHE_KEY_USER_PHONE);

        PhonePwd=(String)mUser.getCache(User.CACHE_KEY__USER_PWD);

        etUsername.setText(PhoneNumber);

        /*if (mUser.getCache(User.CACHE_KEY__USER_ISFORGET_NAME)!=null) {

        }*/
        //isForgetName = mUser.getBooleanCache(User.CACHE_KEY__USER_ISFORGET_NAME);
        isForgetName = helper.getBoolean("autoLogin", false);
        mProgressView = findViewById(R.id.login_progress);


        //
        if(!GetServerName()){
            vpnServer =shareP.getString("server",Constant.OPERATOR_DEFAULT).split(",");
            if(vpnServer.length > 2){

                vpnServerName = vpnServer[0];
                vpnServerIP = vpnServer[1];
                vpnServerPort = vpnServer[2];
                switch (vpnServerName){
                    case Constant.OPERATOR_CMCC: serverSpi.setSelection(0);
                        break;
                    case Constant.OPERATOR_UNICOM: serverSpi.setSelection(1);
                        break;
                    case Constant.OPERATOR_TELICOM: serverSpi.setSelection(2);
                        break;
                    case Constant.OPERATOR_TEST: serverSpi.setSelection(3);
                        break;
                    default:break;
                }
            }
            return;
        }else{
            String[] serverName = getResources().getStringArray(R.array.server_values_array);
            int count= 0;
            Configure tcfg = ((AppApplication) getApplication()).getConfigures();
            Log.e(TAG, "-----------------运营商---------"+ tcfg.GetOperatorName());
            switch (tcfg.GetOperatorName()){
                case Constant.OPERATOR_CMCC: serverSpi.setSelection(0);
                    count = 0;
                    break;
                case Constant.OPERATOR_UNICOM: serverSpi.setSelection(1);
                    count = 1;
                    break;
                case Constant.OPERATOR_TELICOM: serverSpi.setSelection(2);
                    count = 2;
                    break;
                default:break;
            }
            shareP.putString("server",serverName[count]);
            vpnServer =serverName[count].split(",");
            if(vpnServer.length > 2){

                vpnServerName = vpnServer[0];
                vpnServerIP = vpnServer[1];
                vpnServerPort = vpnServer[2];
            }
        }
        if (isForgetName && PhonePwd != null){

            swAutoLogin.setChecked(true);
            btnLogin.setEnabled(false);

            Message msg = new Message();
            msg.what=1;
            msg.obj = true;
            lgHandler.sendMessage(msg);
            prepare();
            //VpnLogin();
//-----------------------------------------以下的业务系统的用户登入动作要在VPN登入成功后执行需要修改 要注意--------------------------------
            //mUser.Login(PhoneNumber, PhonePwd, isForgetName, isForgetPwd, new OnNetWorkListener());
            //ChatLogin("user1", "123");
            //mPresenter.Login("user1", "192.168.10.197");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        XMPPUtil.getXMPPConnection(null);
                        if(XMPPUtil.login("user1","123")){
                            startService(new Intent(getApplication(), XMPPService.class));
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                   /* try {
                        if (XMPPUtil.isLogin()) {
                            loginSuccess();
                        }else {
                            XMPPUtil.connect("qq", "192.168.10.197", 5222, null);
                            XMPPUtil.addConnectionListener(mConnectionListener);
                            XMPPUtil.login("user1", "123");
                        }
                        //startService(new Intent(getApplication(), XMPPService.class));
                    } catch (Exception e) {
                        Timber.e(e);
                    }*/
                }
            }).start();

            /*// GPS服务开启
            startService(new Intent(LoginActivity.this,com.android.zhgf.zhgf.wnd.service.HttpCommand.class));

            startActivity(new Intent(LoginActivity.this, MenuActivity.class));

            LoginActivity.this.finish();*/

        }else{
            swAutoLogin.setChecked(false);
        }



    }
    /**
     * 事件设置
     */
    private void setupEvents()
    {

        // 用户名输入点击
        etUsername.setOnTouchListener(this);
        // 密码输入点击
        etPassword.setOnTouchListener(this);
        // 登录点击
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getAccount().isEmpty()){
                    showToast("你输入的账号为空！");
                    return;
                }

                if (getPassword().isEmpty()){
                    showToast("你输入的密码为空！");
                    return;
                }
//                DialogUtil.Toast(getApplication(),mHServer.getText().toString()+"|"+HostServer);
                Pattern m_ptn = Pattern.compile("^((1[0-9][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

                PhoneNumber = etUsername.getText().toString();
                Matcher mcr = m_ptn.matcher(PhoneNumber);
                if (!mcr.find()) {
                    DialogUtil.Toast(LoginActivity.this, "手机号码格式不正确，请输入正确的手机号码");
                    return;
                }
                btnLogin.setEnabled(false);
                PhonePwd = etPassword.getText().toString();

                if(swAutoLogin.isChecked()){
                    isForgetName = true;
                }else{
                    isForgetName = false;
                }



                Message msg = new Message();
                msg.what=1;
                msg.obj = true;
                lgHandler.sendMessage(msg);
                prepare();
                //((AppApplication)getApplication()).getConfigures().setHostServer(HostServer);

                //VpnLogin();
                //-----------------------------------------以下的业务系统的用户登入动作要在VPN登入成功后执行需要修改 要注意--------------------------------
                //mUser.Login(PhoneNumber, PhonePwd, isForgetName, isForgetPwd, new OnNetWorkListener());
                //ChatLogin("user1", "123");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            XMPPUtil.getXMPPConnection(null);
                            if(XMPPUtil.login("user1","123")){
                                startService(new Intent(getApplication(), XMPPService.class));
                            }
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                        /*try {
                            XMPPUtil.connect("user1", "192.168.10.197", 5222, null);
                            //XMPPUtil.addConnectionListener(mConnectionListener);
                            XMPPUtil.login("user1", "123");
                            startService(new Intent(getApplication(), XMPPService.class));
                        } catch (Exception e) {
                            Timber.e(e);
                        }*/
                    }
                }).start();
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            XMPPUtil.connect("user1", "192.168.10.197", 5222, null);
                            //XMPPUtil.addConnectionListener(mConnectionListener);
                            XMPPUtil.login("user1", "123");
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                }).start();
                startService(new Intent(getApplication(), XMPPService.class));*/
                /*startActivity(new Intent(LoginActivity.this,MenuActivity.class));
                // GPS服务开启
                startService(new Intent(LoginActivity.this,com.android.zhgf.zhgf.wnd.service.HttpCommand.class));
                LoginActivity.this.finish();*/


            }
        });


        serverSpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                String[] serverName = getResources().getStringArray(R.array.server_values_array);
                shareP.putString("server",serverName[pos]);
                vpnServer =serverName[pos].split(",");
                if(vpnServer.length > 2){

                    vpnServerName = vpnServer[0];
                    vpnServerIP = vpnServer[1];
                    vpnServerPort = vpnServer[2];
                    Configure tcfg = ((AppApplication) getApplication()).getConfigures();
                    tcfg.setOperatorName(vpnServerName);
                    Log.e(TAG, "-----------------serverSpi 设置运营商---------"+ tcfg.GetOperatorName());
                }
                //showToast( "你点击的是:"+serverName[pos]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


    }
    // 获取运营商名设置相应服务器
    Boolean GetServerName(){
        TelephonyUtils mTel = new TelephonyUtils(getApplicationContext());
        //网络可用
        if(mTel.isNetworkAvailable()){
            // wifi连接 && wifi可用
            if(mTel.isWifi()&& mTel.isWifiAvailable()){
                //判断wifi运营商
                //GetIP();
                //return false;
            }else{
                //数据连接 判断运营商
                mTel.getProvidersName();
            }
        }else{
            //网络不可用跳对话框，请连接网络
            DialogUtil.Toast(LoginActivity.this, "网络未连接，请查看网络设置");
            return false;
        }
        return true;
    }
    private void GetIP(){
        //new GetData().execute("http://ip.cn/?hgjcfy=pfgb1");
        new GetData().execute("http://whois.pconline.com.cn/ipJson.jsp");
    }

    private class GetData extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            return MyOkhttp.get(params[0]);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!TextUtils.isEmpty(result)){

                JSONObject jsonObject;
                Gson gson=new Gson();
                String jsonData=null;

                try {
                    String ip = result.split(":")[1].replaceAll("\"","").split(",")[0];
                    String serverName = result.split(":")[8].replaceAll("\"","").split(" ")[1].split(",")[0];
                    /*jsonObject = new JSONObject(temp);
                    jsonData = jsonObject.getString("IPCallBack");
                } catch (JSONException e) {
                    e.printStackTrace();*/
                } catch (Exception e){

                }

            }
        }
    }
    void VpnLogin(){


        Bundle bundle= new Bundle();
        bundle.putString(VSGService.Key.KEY_ACCESSMODE, VSGService.AccessMode.MODECS);
        bundle.putString(VSGService.Key.KEY_AUTHTYPE,  VSGService.AuthenticateType.USERNAME_PASSWORD);
        bundle.putString(VSGService.Key.KEY_USERNAME, PhoneNumber);

        if(PhoneNumber.equals("13819047139")){
            bundle.putString(VSGService.Key.KEY_PASSWORD, "15033X");
        }else{
            bundle.putString(VSGService.Key.KEY_PASSWORD, PhonePwd);
        }
        //bundle.putString(VSGService.Key.KEY_PASSWORD, PhonePwd);
        //bundle.putString(VSGService.Key.KEY_PASSWORD, "15033X");
        bundle.putString(VSGService.Key.KEY_GATEWAY, vpnServerIP);
        bundle.putString(VSGService.Key.KEY_PORT, vpnServerPort);
        VSGService.authStart(LoginActivity.this,bundle);
    }
    void ChatLogin(final String username, final String password) {
        ThreadUtil.runONWorkThread(() -> {
            if (XMPPUtil.isLogin()) {
                loginSuccess();
            }
            //如果未登陆服务器，首先登陆服务器
            if (!XMPPUtil.isConnected()) {
                try {
                    XMPPUtil.connect("user1", "192.168.10.197",5222, null);
                    //saveConfig(name, address, port);
                    //ThreadUtil.runOnUIThread(() -> connectSuccess());
                } catch (Exception e) {
                    Timber.e(e);
                    // ThreadUtil.runOnUIThread(() -> { connectFail();
                    //});
                }
            }
            try {
                Timber.i("start login");
                XMPPUtil.addConnectionListener(mConnectionListener);
                XMPPUtil.login(username, password);
            } catch (Exception e) {
                Timber.e(e);
                ThreadUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        loginFail();
                    }
                });
            }




         }

        );
    }

    public void loginSuccess() {
        ThreadUtil.runOnUIThread(() -> {
            TastyToast.makeText(getApplicationContext(), "Login Success!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            startService(new Intent(getApplication(), XMPPService.class));
        });

    }

    public void loginFail() {
        TastyToast.makeText(getApplicationContext(), "Login Fail!", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
    }

    public void connect() {
        try {
            XMPPUtil.connect("user1", "192.168.10.197",5222, null);
            //saveConfig(name, address, port);
            ThreadUtil.runOnUIThread(() -> connectSuccess());
        } catch (Exception e) {
            Timber.e(e);
            ThreadUtil.runOnUIThread(() -> { connectFail();
            });

        }
    }

    public void connectFail() {
        TastyToast.makeText(getApplicationContext(), "Set Server First!", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
    }

    public void connectSuccess() {
        ChatLogin("user1", "123");
    }

    private ConnectionListener mConnectionListener = new XMPPUtil.ConnectionListenerWrapper() {
        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            loginSuccess();
        }
    };

    @Override
    public void sharedLoginCallback(int ret) {
        if(ret == VSGService.SharedCallbackValue.CALLBACK_SHARED_SUCCESS)
        {
            Toast.makeText(getApplicationContext(), "隧道共享登录成功", Toast.LENGTH_LONG).show();

        }
        else
        {
            Toast.makeText(getApplicationContext(), "没有共享认证信息", Toast.LENGTH_LONG).show();

            //没有共享到数据，需用户自己实现通过登录信息正常登录，即使用	vsgauth.authStart（）函数登录！！！
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case 0:
                //VSGService.logout(LoginActivity.this, null);
                if (resultCode == RESULT_OK){
                    Bundle bundle = new Bundle();
                    bundle.putString(VSGService.Key.KEY_ACCESSMODE,VSGService.AccessMode.MODENC); //设置 nc 模式
                    bundle.putString(VSGService.Key.KEY_AUTHTYPE,VSGService.AuthenticateType.USERNAME_PASSWORD);
                    bundle.putString(VSGService.Key.KEY_USERNAME, PhoneNumber);
                    if(PhoneNumber.equals("13819047139")){
                        bundle.putString(VSGService.Key.KEY_PASSWORD, "15033X");
                    }else{
                        bundle.putString(VSGService.Key.KEY_PASSWORD, PhonePwd);
                    }
                    bundle.putString(VSGService.Key.KEY_GATEWAY, vpnServerIP);
                    bundle.putString(VSGService.Key.KEY_PORT, vpnServerPort);
                    bundle.putInt(VSGService.Key.KEY_ICON, R.mipmap.ic_launcher);
                    VSGService.authStart(LoginActivity.this,bundle);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

            /**
                           _ooOoo_
                          o8888888o
                          88" . "88
                          (| -_- |)
                          O\  =  /O
                       ____/`---'\____
                     .'  \\|     |//  `.
                    /  \\|||  :  |||//  \
                   /  _||||| -:- |||||-  \
                   |   | \\\  -  /// |   |
                   | \_|  ''\---/''  |   |
                   \  .-\__  `-`  ___/-. /
                 ___`. .'  /--.--\  `. . __
              ."" '<  `.___\_<|>_/___.'  >'"".
             | | :  `- \`.;`\ _ /`;.`/ - ` : | |
             \  \ `-.   \_ __\ /__ _/   .-` /  /
        ======`-.____`-.___\_____/___.-`____.-'======
                           `=---='
        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                 佛祖保佑       永无BUG
             不要怀疑为什么要反复handler发消息处理http请求  因为杀内存异常退出导致的网络阻塞
             不得不这样写vpn的代码           在此警告没有金刚钻不要瞎改代码。2017.10.30  00:30
        */

    @Override
    public void stateChanged(int state) {
        Message msg = new Message();
        switch (state){
            case VSGService.AuthState.STATE_DISABLED:
                Toast.makeText(getApplicationContext(), "隧道未登录", Toast.LENGTH_LONG).show();
                Log.e(TAG, "隧道未登录");
                break;
            case VSGService.AuthState.STATE_CONNECTING:
                Toast.makeText(getApplicationContext(), "隧道登录中", Toast.LENGTH_LONG).show();
                Log.e(TAG, "隧道登录中");
                break;
            case VSGService.AuthState.STATE_DISCONNECTING:
                Toast.makeText(getApplicationContext(), "隧道用户退出中", Toast.LENGTH_LONG).show();
                Log.e(TAG, "隧道用户退出中");
                break;
            case VSGService.AuthState.STATE_CONNECTED:
                Toast.makeText(getApplicationContext(), "NC隧道登录成功", Toast.LENGTH_LONG).show();
                Log.e(TAG, "NC隧道登录成功");
                msg.what = 4;
                lgHandler.sendMessage(msg);
                break;
            case VSGService.AuthState.AUTH_SUCCESS:
                Toast.makeText(getApplicationContext(), "隧道登录成功", Toast.LENGTH_LONG).show();
                Log.e(TAG, "隧道登录成功");
                break;
            case VSGService.AuthState.AUTH_GET_RESOURCE_ERROR:
                Toast.makeText(getApplicationContext(), "隧道获取资源失败", Toast.LENGTH_LONG).show();
                Log.e(TAG, "隧道获取资源失败");
                msg.what = 2;
                msg.obj = false;
                lgHandler.sendMessage(msg);
                break;
            case VSGService.AuthState.AUTH_RESOURCE_NOTEXIST_ERROR:
                //Toast.makeText(getApplicationContext(), "没有资源", Toast.LENGTH_LONG).show();
                break;
            case VSGService.AuthState.AUTH_GATEWAY_UNREACHABLE_ERROR:
                Toast.makeText(getApplicationContext(), "隧道网关不可达", Toast.LENGTH_LONG).show();
                Log.e(TAG, "隧道网关不可达");
                msg.what = 2;
                msg.obj = false;
                lgHandler.sendMessage(msg);
                break;
            case VSGService.AuthState.AUTH_USERNAME_LOCKED:
                Toast.makeText(getApplicationContext(), "隧道用户已锁定", Toast.LENGTH_LONG).show();
                Log.e(TAG, "隧道用户已锁定");
                msg.what = 2;
                msg.obj = false;
                lgHandler.sendMessage(msg);
                break;
            case VSGService.AuthState.AUTH_USER_OUTOF_VALID_PERIOD:
                Toast.makeText(getApplicationContext(), "该隧道用户当前不在登录有效期内", Toast.LENGTH_LONG).show();
                Log.e(TAG, "该隧道用户当前不在登录有效期内");
                msg.what = 2;
                msg.obj = false;
                lgHandler.sendMessage(msg);
                break;
            case VSGService.AuthState.AUTH_FRIST_LOGIN_MODIFY_PSW:
                Toast.makeText(getApplicationContext(), "不支持首次登录隧道修改密码", Toast.LENGTH_LONG).show();
                Log.e(TAG, "不支持首次登录隧道修改密码");
                msg.what = 2;
                msg.obj = false;
                lgHandler.sendMessage(msg);
                break;
            case  VSGService.AuthState.AUTH_USER_NO_ACL:
                Toast.makeText(getApplicationContext(), "该隧道用户没有任何访问权限", Toast.LENGTH_LONG).show();
                Log.e(TAG, "该隧道用户没有任何访问权限");
                msg.what = 2;
                msg.obj = false;
                lgHandler.sendMessage(msg);
                break;
            case  VSGService.AuthState.AUTH_MULTIFACTOR_NEED_SMS:
            case  VSGService.AuthState.AUTH_MULTIFACTOR_NEED_HARDWARE:
            case  VSGService.AuthState.AUTH_MULTIFACTOR_NEED_BIND_IP:
            case  VSGService.AuthState.AUTH_MULTIFACTOR_NEED_MAC:
                Toast.makeText(getApplicationContext(), "辅助认证不支持", Toast.LENGTH_LONG).show();
                Log.e(TAG, "辅助认证不支持");
                msg.what = 2;
                msg.obj = false;
                lgHandler.sendMessage(msg);
                break;
            case  VSGService.AuthState.AUTH_ONLINE_OVER_LICENSE:
                Toast.makeText(getApplicationContext(), "系统在线隧道用户数已达最大,请稍后再试", Toast.LENGTH_LONG).show();
                Log.e(TAG, "系统在线隧道用户数已达最大,请稍后再试");
                msg.what = 2;
                msg.obj = false;
                lgHandler.sendMessage(msg);
                break;
            case  VSGService.AuthState.AUTH_USER_PASSWORD_ERROR:
            case  VSGService.AuthState.AUTH_USER_AND_GROUP_NOT_FOUND:
                Toast.makeText(getApplicationContext(), "隧道用户名或密码错误", Toast.LENGTH_LONG).show();
                Log.e(TAG, "隧道用户名或密码错误");
                msg.what = 2;
                msg.obj = false;
                lgHandler.sendMessage(msg);
                break;
            default:
                //Toast.makeText(getApplicationContext(),"未定义的错误,错误码："+Integer.toHexString(state), Toast.LENGTH_LONG).show();
                Log.e(TAG, "未定义的错误,错误码");
                msg.what = 4;
                //msg.obj = false;
                lgHandler.sendMessage(msg);
                break;
        }
    }


    class OnNetWorkListener implements INetwork.OnNetworkStatusListener {
        @Override
        public void onConnected(boolean isSuccess) {
            if (isSuccess) Log.e(TAG, "onConnected: 连接成功");
        }

        @Override
        public void onDisconnected(boolean isSuccess) {
            if (isSuccess) Log.e(TAG, "onDisconnected: 断开连接成功");
        }

        @Override
        public void onGetResult(boolean isSuccess, Object pData) {
            NetworkUtil.HttpStatus mStatus = (NetworkUtil.HttpStatus) pData;
            /*TODO Log.e("LoginActivity", (String) mStatus.getData());*/
            Message msg = new Message();
            msg.what = 1;
            msg.obj = false;

            if (mStatus.getStatusCode() == 200) {
                String html = (String) mStatus.getData();
                Log.e(TAG, "onGetResult: Success");
                Log.e(TAG, "onGetResult: DATA[" + mStatus.getData());
                Log.e(TAG, "onGetResult: html.substring(0,1)=" + html.substring(0, 1));
                if (Integer.parseInt(html.substring(0, 1)) == 1) {
                    Log.e(TAG, "onGetResult: Login Success");

                    //mUser.setCache(User.CACHE_KEY_HOST_SERVER, mHServer.getText().toString());
                    /*lgHandler.obtainMessage(6767).sendToTarget();*/

                    //Log.e(TAG, "onGetResult: setup hostserver:"+ HostServer);
                    //Log.e(TAG, "onGetResult: 设置Cache->PhoneNumber为"+PhoneNumber);
                    mUser.setCache(User.CACHE_KEY_USER_PHONE, PhoneNumber);
                    //Log.e(TAG, "onGetResult: 设置Cache->PhonePwd");
                    mUser.setCache(User.CACHE_KEY__USER_PWD, PhonePwd);
                    //Log.e(TAG, "onGetResult: 设置Cache->isForgetName" + isForgetName);
                    mUser.setCache(User.CACHE_KEY__USER_ISFORGET_NAME, isForgetName);
                    //Log.e(TAG, "onGetResult: 设置Cache->isForgetPwd" + isForgetPwd);
                    mUser.setCache(User.CACHE_KEY__USER_ISFORGET_PWD, isForgetPwd);
                    ((AppApplication) getApplication()).setUser(mUser);
                    helper.putValues(new SharedPreferencesUtils.ContentValue("autoLogin", isForgetName));
                    if (html.indexOf("#") > -1) {
                        Log.e(TAG, "onGetResult: HTML:" + html);
                        String[] tmp = html.split("\\#");
                        Configure tcfg = ((AppApplication) getApplication()).getConfigures();
                        try {
                            if (tmp[1] != "") {
                                /*if (tmp[1].indexOf("rtmp://")>-1){
                                    tcfg.setRtmpServer(tmp[1]);
                                }else {

                                }*/
                                //tcfg.setRtmpServer("rtmp://" + tmp[1] + "/live/");
                                tcfg.setFTPServer(tmp[1]);

                                mUser.setCache(User.CACHE_KEY_USER_NAME, tmp[2]);
                                mUser.setCache(User.CACHE_KEY_USER_UTIL, tmp[3]);
                                mUser.setCache(User.CACHE_KEY_USER_IDENT, tmp[4]);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //用户登录的操作
                    RuningTime.getWSClient().addCommand("LOGIN",new LoginCommand());
                    ((LoginCommand)RuningTime.getWSClient().getCommand("LOGIN")).setListener(new ICommand.EmitterListener() {
                        @Override
                        public void onEmitter(JSONObject payload) {
                            /*Intent mit = new Intent(MainActivity.this,VideoCaller.class);
                            startActivity(mit);*/
                        }
                    });

                    /*try {
                        JSONObject message = new JSONObject();
                        message.put("username",(String)mUser.getCache(User.CACHE_KEY_USER_NAME));
                        message.put("userpwd","1");
                        message.put("room_name",(String)mUser.getCache(User.CACHE_KEY_USER_PHONE));
                        ((LoginCommand) RuningTime.getWSClient().getCommand("LOGIN")).SendMessage("LOGIN",message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    // GPS服务开启
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startService(new Intent(LoginActivity.this,com.android.zhgf.zhgf.wnd.service.HttpCommand.class));
                        }
                    }).start();

                    startActivity(new Intent(LoginActivity.this, MenuActivity.class));

                    LoginActivity.this.finish();
                } else {
                    Log.e(TAG, "onGetResult: Login Fail");
                    msg.what = 2;
                    msg.obj = false;
                }
            } else {
                Log.e(TAG, "onGetResult: Fail");
                Log.e(TAG, "onGetResult: DATA[" + mStatus.getData());
                msg.what = 2;
                msg.obj = false;

            }
            lgHandler.sendMessage(msg);
            //show_infoBox.setText((String) mStatus.getData());

        }

        @Override
        public void onPostResult(boolean isSuccess, Object pData) {
            NetworkUtil.HttpStatus mStatus = (NetworkUtil.HttpStatus)pData;
            Message msg = new Message();
            msg.what = 1;
            msg.obj = false;
            if (mStatus.getStatusCode() == 200) {
                Log.e(TAG, "onGetResult: Success");
                Log.e(TAG, "onGetResult: DATA[" + mStatus.getData());
                String html = (String) mStatus.getData();
                Gson gson = new Gson();
                LoginInfo loginInfo = gson.fromJson(html,LoginInfo.class);
                if(loginInfo.getLoginStats()== 1){
                    Log.e(TAG, "onGetResult: Login Success");
                    mUser.setCache(User.CACHE_KEY_USER_PHONE, PhoneNumber);
                    mUser.setCache(User.CACHE_KEY__USER_PWD, PhonePwd);
                    ((AppApplication) getApplication()).setUser(mUser);
                    helper.putValues(new SharedPreferencesUtils.ContentValue("autoLogin", isForgetName));
                    Configure tcfg = ((AppApplication) getApplication()).getConfigures();
                    mUser.setCache(User.CACHE_KEY_USER_NAME, loginInfo.getPepoleName());
                    mUser.setCache(User.CACHE_KEY_USER_UTIL, loginInfo.getPepoleProperty());
                    String[] tmp = loginInfo.getPeopleHeadImg().split("~");
                    if(tmp.length > 1){
                        String imgUrl = "http://" + loginInfo.getZhgfServicesAddress() + tmp[1];
                        mUser.setCache(User.CACHE_KEY_USER_IMG,imgUrl);
                    }
                    // 政工网接口接址
                    tcfg.setHostServer(loginInfo.getZgwServicesAddress());
                    // 业务系统接口地址
                    tcfg.setLoginGPSServer(loginInfo.getZhgfServicesAddress());
                    // FTP
                    tcfg.setFTPServer(loginInfo.getFtpServicesAddress());
                    // FTP用户名
                    tcfg.setFTPServicesUser(loginInfo.getFtpServicesUser());
                    // FTP密码
                    tcfg.setFTPServicesPass(loginInfo.getFtpServicesPass());
                    // RTMP视频查看服务器地址
                    tcfg.setLiveServer(loginInfo.getRtmpServerAddress());
                    // 视频会议系统地址
                    tcfg.setWebRtc(loginInfo.getVideoMeetingServicesAddress());

                    // GPS服务开启
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startService(new Intent(LoginActivity.this,com.android.zhgf.zhgf.wnd.service.HttpCommand.class));
                        }
                    }).start();

                    startActivity(new Intent(LoginActivity.this, MenuActivity.class));

                    LoginActivity.this.finish();
                } else {
                    Log.e(TAG, "onGetResult: Login Fail");
                    msg.what = 2;
                    msg.obj = false;
                }
            } else {
                Log.e(TAG, "onGetResult: Fail");
                Log.e(TAG, "onGetResult: DATA[" + mStatus.getData());
                msg.what = 2;
                msg.obj = false;

            }
            lgHandler.sendMessage(msg);

        }

        @Override
        public void onError(int errCode, String errMessage) {
            Log.e("Login", "ErrorCode:" + errCode + "|" + errMessage);
            /*Message msg = new Message();
            msg.what = 2;
            msg.obj = false;
            lgHandler.sendMessage(msg);*/
            count++;
            if(count > 4){
                Message msg = new Message();
                msg.what = 2;
                msg.obj = false;
                lgHandler.sendMessage(msg);
            }else{
                Message msg = new Message();
                msg.what = 4;
                msg.obj = false;
                lgHandler.sendMessage(msg);
            }

            //showToast("登入失败");

        }
    }
    private void initData() {


        //判断用户第一次登陆
        if (firstLogin()) {
            swAutoLogin.setChecked(false);//取消自动登录的复选框
        }

        //判断是否自动登录
        if (autoLogin()) {
            swAutoLogin.setChecked(true);//勾选自动登录
            setTextNameAndPassword();//把密码和账号输入到输入框中
            //login();//去登录就可以
        }
    }

    private void setVersionTimeAndSetServer(){
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        helper = new SharedPreferencesUtils(this, "setting");
        boolean firstLogin = helper.getBoolean("firstLogin", true);
        if(firstLogin){
            long time=System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳
            String  timeStr=DateUtil.getDateToString(time,pattern);
            String[] serverName = getResources().getStringArray(R.array.server_values_array);

            helper.putValues(new SharedPreferencesUtils.ContentValue("firstLogin", false),
                    new SharedPreferencesUtils.ContentValue("creatTime", timeStr),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false));

            if(serverName.length > 3){
                helper.putValues(new SharedPreferencesUtils.ContentValue(Constant.OPERATOR_CMCC,serverName[0]),
                                new SharedPreferencesUtils.ContentValue(Constant.OPERATOR_UNICOM,serverName[1]),
                                new SharedPreferencesUtils.ContentValue(Constant.OPERATOR_TELICOM,serverName[2]),
                                new SharedPreferencesUtils.ContentValue(Constant.OPERATOR_TEST,serverName[3]));
            }

        }
    }

    /**
     * 判断是否是第一次登陆
     */
    private boolean firstLogin() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        boolean first = helper.getBoolean("first", true);
        if (first) {
            long time=System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳
            String  timeStr=DateUtil.getDateToString(time,pattern);
            //创建一个ContentVa对象（自定义的）设置不是第一次登录，,并自动登录是默认不选，创建账号和密码为空
            helper.putValues(new SharedPreferencesUtils.ContentValue("first", false),
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
                    new SharedPreferencesUtils.ContentValue("name", ""),
                    new SharedPreferencesUtils.ContentValue("password", ""),
                    new SharedPreferencesUtils.ContentValue("creatTime", timeStr));
            return true;
        }
        return false;
    }

    /**
     * 判断是否自动登录
     */
    private boolean autoLogin() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        boolean autoLogin = helper.getBoolean("autoLogin", false);
        return autoLogin;
    }

    /**
     * 把本地保存的数据设置数据到输入框中
     */
    public void setTextNameAndPassword() {
        etUsername.setText("" + getLocalName());
        etPassword.setText("" + getLocalPassword());
    }


    /**
     * 获得保存在本地的用户名
     */
    public String getLocalName() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        String name = helper.getString("name");
        return name;
    }


    /**
     * 获得保存在本地的密码
     */
    public String getLocalPassword() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        String password = helper.getString("password");
        return Base64Utils.decryptBASE64(password);   //解码一下

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        // et.getCompoundDrawables()得到一个长度为4的数组，该方法返回包含控件左,上,右,下四个位置的Drawable的数组
        Drawable [] drawables=etUsername.getCompoundDrawables();
        // 如果取得失败，不再处理
        if(drawables.length <4)
            return false;
        Drawable drawable = drawables[2];
        // 如果右边没有图片，不再处理
        if (drawable == null)
            return false;
        // 如果不是按下事件，不再处理
        if (motionEvent.getAction() != MotionEvent.ACTION_UP)
            return false;
        // 判断点击位置如果是右面的图标
        if (motionEvent.getX() > etPassword.getWidth()
                - etPassword.getPaddingRight()
                - drawable.getIntrinsicWidth()){

            switch (view.getId()) {
                case R.id.etUserName:
                    // 清空
                    etUsername.setText("");
                    break;
                case R.id.etPassword:
                    // 判断密码显示标志
                    if (passwordVisableFlg)
                    {
                        passwordVisableFlg = false;
                        etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                    }else{
                        passwordVisableFlg = true;
                        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    }
                    break;

            }

            // 光标定位最后
            etPassword.setSelection(etPassword.length());
        }



        return false;
    }



    @Override
    public void onClick(View view) {

        //无论如何保存一下用户名
        loadUserName();
        // 登录
        login();
    }
    /**
     * 保存用户账号
     */
    public void loadUserName() {
        if (!getAccount().equals("") || !getAccount().equals("请输入登录账号")) {
            SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
            helper.putValues(new SharedPreferencesUtils.ContentValue("name", getAccount()));
        }

    }
    /**
     * 获取账号去掉空格
     */
    public String getAccount() {
        return etUsername.getText().toString().trim();//去掉空格
    }

    /**
     * 获取密码去掉空格
     */
    public String getPassword() {
        return etPassword.getText().toString().trim();//去掉空格
    }
    /**
     * 登录
     *
     */
    private void login() {

        //先做一些基本的判断，比如输入的用户命为空，密码为空，网络不可用多大情况，都不需要去链接服务器了，而是直接返回提示错误
        if (getAccount().isEmpty()){
            showToast("你输入的账号为空！");
            return;
        }

        if (getPassword().isEmpty()){
            showToast("你输入的密码为空！");
            return;
        }
        loginTask = new LoginTask(this, this, getAccount(), etPassword.getText().toString());
        loginTask.execute();
        /*//登录一般都是请求服务器来判断密码是否正确，要请求网络，要子线程
        showLoading();//显示加载框
        Thread loginRunnable = new Thread() {

            @Override
            public void run() {
                super.run();
                btnLogin.setClickable(false);//点击登录后，设置登录按钮不可点击状态


                //睡眠1秒
                LoginResult loginResult = null;
                try {

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //判断账号和密码
                //if (getAccount().equals("1") && getPassword().equals("1")) {

                if (loginResult.isSuccess()) {
                    showToast("登录成功");
                    loadSwitchState();//记录下当前自动登录的状态;

                    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                    finish();//关闭页面
                } else {
                    showToast("输入的登录账号或密码不正确");
                }

                btnLogin.setClickable(true);  //这里解放登录按钮，设置为可以点击
                hideLoading();//隐藏加载框
            }
        };
        loginRunnable.start();*/

    }

    /**
     * 记录下当前自动登录的状态
     */
    public void loadSwitchState() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        // 如果设置自动登录
        if(swAutoLogin.isChecked()){
            //保存密码数据,自动登录状态
            helper.putValues(
                    new SharedPreferencesUtils.ContentValue("autoLogin", true),
                    new SharedPreferencesUtils.ContentValue("password", Base64Utils.encryptBASE64(getPassword())));
        }else{
            //保存密码数据,自动登录状态
            helper.putValues(
                    new SharedPreferencesUtils.ContentValue("autoLogin", false),
                    new SharedPreferencesUtils.ContentValue("password", ""));
        }
    }
    /**
     * 显示加载的进度款
     */
    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, getString(R.string.loading), false);
        }
        mLoadingDialog.show();
    }


    /**
     * 隐藏加载的进度框
     */
    public void hideLoading() {
        if (mLoadingDialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.hide();
                }
            });

        }
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResponse(Boolean result) {
        if (result) {
            showToast("登录成功");
            loadSwitchState();//记录下当前自动登录的状态;

            //TODO 创建一个与Http服务器通信[集合了GPS坐标的发送，提交数据，服务端命令的接收及提示等功能]的Service，不放在这里，放在登录成功之后
        /*startService(new Intent("com.jiechu.wnd.HttpCommand"));*/
            startService(new Intent(LoginActivity.this,com.android.zhgf.zhgf.wnd.service.HttpCommand.class));

            startActivity(new Intent(LoginActivity.this, MenuActivity.class));


            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onErrorResponse(Exception exception) {

        Toast.makeText(this,exception.getMessage(), Toast.LENGTH_SHORT).show();

    }

   /* @Override
    public void onResponse(Boolean response) {
        if (response) {
            showToast("登录成功");
            loadSwitchState();//记录下当前自动登录的状态;

            startActivity(new Intent(MainActivity.this, MenuActivity.class));


            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onErrorResponse(Exception exception) {
        *//*if (((SmackInvocationException)exception).isCausedBySASLError()) {
            Toast.makeText(this, R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
        }*//*
        Toast.makeText(this,"输入的登录账号或密码不正确", Toast.LENGTH_SHORT).show();
    }
*/

    private View.OnClickListener mFabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplication(), SetServerActivity.class);
            Timber.i("start Activity in %d", Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                FabTransform.addExtras(intent, ContextCompat.getColor(getApplicationContext(), R.color.colorGreen), R.drawable.icons8_setting48);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, getString(R.string.transition_server_setting));
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        XMPPUtil.removeConnectionListener(mConnectionListener);
        mConnectionListener = null;
        if (loginTask != null) {
            loginTask.dismissDialogAndCancel();
        }
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

}
