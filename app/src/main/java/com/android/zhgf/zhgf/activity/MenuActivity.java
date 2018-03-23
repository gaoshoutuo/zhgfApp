package com.android.zhgf.zhgf.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.adapter.MenuAdapter;
import com.android.zhgf.zhgf.adapter.MenuIconAdapter;
import com.android.zhgf.zhgf.adapter.MyViewPagerAdapter;
import com.android.zhgf.zhgf.database.NotificationTable;
import com.android.zhgf.zhgf.database.bean.NotificationBean;
import com.android.zhgf.zhgf.service.ServiceNotification;
import com.android.zhgf.zhgf.util.CheckPermissionUtils;
import com.android.zhgf.zhgf.wnd.iface.impl.User;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.vsg.vpn.logic.VSGService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 将在 政治教育  请销假  在线学习  工作动态  新闻资讯  这些模块中加入 badgeview 用来显示未知
 * 定义 两个 数组分别表示历史未读  以及变化后的未读
 */

public class MenuActivity extends AppCompatActivity implements View.OnClickListener , EasyPermissions.PermissionCallbacks{
    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;
    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE_UPLOAD = 001;
    /**
     * 系统设置跳转Activity RequestCode
     */
    public static final int REQUEST_CODE2 = 10000;
    /**
     * 系统设置跳转Activity ResultCode
     */
    public static final int RESULT_CODE = 10001;
    /**
     * 请求CAMERA权限码
     */
    public static final int REQUEST_CAMERA_PERM = 111;

    private GridView menuCenterGv;

    private GridView menuBottomGv;
    // 现场直播
    private Button btnLivePublic;
    // 通讯录按钮
    private Button btnContact;
    // 新闻资讯按钮
    private Button btnNews;
    // 在线学习
    private Button btnStudy;
    // 考勤签到
    private Button btncodeScan;

    private Button buttonIntelligence;

    private User mUser;

    private TextView userNameTv;

    private TextView usetUtilTv;

    private TextView userTelTv;

    private MyViewPagerAdapter adapter;
    private  ArrayList<GridView>array;
    private ViewPager viewPager;
    private static final float APP_PAGE_SIZE = 9.0f;
    private static MenuIconAdapter mia1,mia2;
    //记录两个menu的badgeview 小红点数量  2 3 5 6 位置加入 如果是0 则gone 非o visibility 22位前18位用来指示两页gridview,后指示4个底部munu

    private static int []struct1={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ;
    // 第0位请销假
    //private int []struct2=new int[4];
    private static final int HANDLERINT=101;
    //静态顶多增加了生命周期  应该对整体程序不产生影响吧
    private static MenuIconAdapter mia;
    private static MenuAdapter menuBottomAdapter;
    private  ArrayList<HashMap<String,Object>> bottomlst;
    private static ArrayList<HashMap<String,Object>> lst;
    private ServiceConnection sc=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
           ServiceNotification.TaskNotification tn=(ServiceNotification.TaskNotification) iBinder;
            tn.listen();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //RoundImageView imageView = (RoundImageView) findViewById(R.id.round_imageview);
        //imageView.setImageResource(R.mipmap.android, Color.RED, 10, true);
        // view初始化
        initViews();
        //初始化权限
        initPermission();
        // 事件设置
        setupEvents();
        NotificationTable.insertSql1(new NotificationBean(1,1," "," "));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // bindService(new Intent(this,ServiceNotification.class),sc,BIND_AUTO_CREATE);
        startService(new Intent(this,ServiceNotification.class));
    }
    /**
     * 点击两次退出
     */

    private long mExitTime;
    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MenuActivity.this, "点击两次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {

            finish();
            System.exit(0);
        }
    }

/*
    @Override
    public void onBackPressed() {
        isExit=!isExit;
        if (isExit==false){
            Toast.makeText(MenuActivity.this,"再点击一次退出",Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        isExit=true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            finish();
        }

        super.onBackPressed();
    }*/

    /**
     * 画面view初始化
     */

    int widthCenterInt;
    int widthBottomInt;
    private static HashMap<String,Object> map2;
    private static HashMap<String,Object> map1;
    private void initViews()
    {
        // 现场直播
        //btnLivePublic = (Button) this.findViewById(R.id.btnLivePublish);
        // 通讯录按钮
        //btnContact = (Button) this.findViewById(R.id.btnContact);
        // 新闻资讯
        //btnNews = (Button) this.findViewById(R.id.btnNews);
        // 情报报知
        //buttonIntelligence=(Button)findViewById(R.id.button3);
        // 在线学习
        //btnStudy = (Button) this.findViewById(R.id.btnStudy);
        // 考勤签到
        //btncodeScan = (Button) this.findViewById(R.id.codeScan_btn);

        userNameTv = (TextView) this.findViewById(R.id.userNameTv);

        usetUtilTv = (TextView) this.findViewById(R.id.usetUtilTv);

        userTelTv = (TextView) this.findViewById(R.id.userTelTv);
        //TODO 用户身份验证
        mUser=new User(getApplicationContext());
        //mUser.checkLogin(new Intent(this, LoginActivity.class));
        userNameTv.setText((String)mUser.getCache(User.CACHE_KEY_USER_NAME));

        usetUtilTv.setText((String)mUser.getCache(User.CACHE_KEY_USER_UTIL));

        userTelTv.setText((String)mUser.getCache(User.CACHE_KEY_USER_PHONE));

        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
         widthCenterInt = dm.widthPixels/3;
         widthBottomInt = dm.widthPixels/5;
        /*menuCenterGv = (GridView)findViewById(R.id.menuGv);

        menuCenterGv.setColumnWidth(widthCenterInt);*/

        menuBottomGv = (GridView)findViewById(R.id.menuBottomGv);

        menuBottomGv.setColumnWidth(widthBottomInt);
        resetmenuIcon1();
        /*MenuAdapter menuCenterAdapter = new MenuAdapter(MenuActivity.this,lst,menuCenterGv,3);
        menuCenterGv.setAdapter(menuCenterAdapter);
        menuCenterGv.setOnItemClickListener(new gridviewOnItemClickListener());*/

        viewPager = (ViewPager)findViewById(R.id.myviewpager);

        resetmenuIcon2();
        final int PageCount = (int)Math.ceil(lst.size()/APP_PAGE_SIZE);
        array = new ArrayList<GridView>();
        for (int i=0; i<PageCount; i++) {
            GridView menuGv = new GridView(this);
            menuGv.setColumnWidth(widthCenterInt);
            menuGv.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            mia=new MenuIconAdapter(MenuActivity.this,lst,menuGv,3,i);
          if(i==0){
              mia1=mia;
              menuGv.setAdapter(mia1);
          }else if(i==1) {
              mia2=mia;
              menuGv.setAdapter(mia2);
          }
            menuGv.setNumColumns(3);
            if( i==0 ){
                menuGv.setOnItemClickListener(new gridviewPage1OnItemClickListener());
            }else{
                menuGv.setOnItemClickListener(new gridviewPage2OnItemClickListener());
            }
            array.add(menuGv);
        }
        adapter = new MyViewPagerAdapter(this, array);
        viewPager.setAdapter(adapter);




        menuBottomAdapter = new MenuAdapter(MenuActivity.this,bottomlst,menuBottomGv,1);
        menuBottomGv.setAdapter(menuBottomAdapter);
        menuBottomGv.setOnItemClickListener(new gridviewBottomOnItemClickListener());
    }
    static String[] bottomIconName = { "请销假","定位导航", "通讯录", "扫码上传"};
    static int[] bottomIcon = {R.drawable.icons8_leave,R.drawable.menu_location,R.drawable.menu_contacts,R.drawable.icons8_upload64};
    public void resetmenuIcon2(){
        bottomlst = new ArrayList<HashMap<String,Object>>();

        for(int i=0;i<bottomIcon.length;i++){
            map2 = new HashMap<String,Object>();
            map2.put("itemImage", bottomIcon[i]);
            map2.put("itemText", bottomIconName[i]);
            if (i==0){
                map2.put("badgenumber",struct1[18]);
            }else {
                map2.put("badgenumber",0);
            }
            bottomlst.add(map2);

        }
        Log.e("bottomlst",bottomlst.toString());
    }
    static String[] iconName = { "指挥控制", "情报报知", "政治教育", "在线学习","工作动态","新闻资讯","在线查询", "现场视频","视频会议","在线考试", "北斗系统", "考勤签到",  "检查更新","通知公告","系统设置","最新推送"};
    static int[] icon = {R.drawable.icons8_control,R.drawable.menu_wireless_router,R.drawable.icons8_ed,R.drawable.menu_study,R.drawable.menu_condition,
            R.drawable.menu_news,R.drawable.icons8_search,R.drawable.menu_play,R.drawable.menu_video_conference,R.drawable.menu_test,R.drawable.menu_gps,
            R.drawable.menu_banking,R.drawable.menu_update,R.drawable.icons8_al64,R.drawable.menu_gear,R.drawable.icon8_firstmessage};

    public void resetmenuIcon1(){
        lst = new ArrayList<HashMap<String,Object>>();

        //数组顺序 改的话跟我说一下

        for(int i=0;i<icon.length;i++){
            map1 = new HashMap<String,Object>();
            map1.put("itemImage", icon[i]);
            map1.put("itemText", iconName[i]);
            map1.put("badgenumber",struct1[i]);
            lst.add(map1);
        }
    }
    class gridviewPage1OnItemClickListener implements AdapterView.OnItemClickListener
    {

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // TODO Auto-generated method stub
            switch (arg2) {
                case 0:

                    Intent intent1 = new Intent();
                    intent1.putExtra("type", 1);
                    intent1.putExtra("othertype",1);
                    intent1.setClass(MenuActivity.this,NewsActivity.class);
                    startActivity(intent1);
                    //startActivity(new Intent(MenuActivity.this, LivePublisherActivity.class));
                    break;
                case 1://情报报知添加
                    startActivity(new Intent(MenuActivity.this,IntelligenceAddActivity.class));
                    //startActivity(new Intent(MenuActivity.this, TDMapActivity.class));
                    break;
                case 2:
                    arg1.findViewById(R.id.icon_badge1).setVisibility(View.GONE);
                    Intent intent2 = new Intent();
                    intent2.putExtra("title","政治教育");
                    intent2.putExtra("type", 1);
                    intent2.putExtra("othertype",3);
                    intent2.setClass(MenuActivity.this,NewsActivity.class);
                    startActivity(intent2);
                    break;
                case 3:
                    arg1.findViewById(R.id.icon_badge1).setVisibility(View.GONE);
                    Intent studyIntent = new Intent(MenuActivity.this, NewsActivity.class);
                    studyIntent.putExtra("type", 2);
                    studyIntent.putExtra("othertype",0);
                    startActivity(studyIntent);
                    //startActivity(new Intent(MenuActivity.this,StudyLessonActivity.class));
                    //startActivity(new Intent(MenuActivity.this,LessonListActivity.class));
                    break;
                case 4:
                    arg1.findViewById(R.id.icon_badge1).setVisibility(View.GONE);
                    Intent intent3 = new Intent();
                    intent3.putExtra("title","工作动态");
                    intent3.putExtra("type", 1);
                    intent3.putExtra("othertype",2);
                    intent3.setClass(MenuActivity.this,NewsActivity.class);
                    startActivity(intent3);
                    //cameraTask();
                    break;
                case 5:
                    arg1.findViewById(R.id.icon_badge1).setVisibility(View.GONE);
                    Intent newsIntent = new Intent(MenuActivity.this, NewsActivity.class);
                    newsIntent.putExtra("type", 1);
                    newsIntent.putExtra("othertype",0);
                    startActivity(newsIntent);
                    //startActivity(new Intent(MenuActivity.this,TestActivity.class));
                    break;
                case 6://
                    Intent intent4 = new Intent();
                    intent4.putExtra("title","在线查询");
                    intent4.setClass(MenuActivity.this,TemplateActivity.class);
                    startActivity(intent4);
                    //startActivity(new Intent(MenuActivity.this,UploadFileActivity.class));
                    break;
                case 7://系统设置
                    //startActivity(new Intent(MenuActivity.this,SettingsActivity.class));
                   /* Intent intent = new Intent();
                    intent.setClass(MenuActivity.this,SettingsActivity.class);
                    startActivityForResult(intent,REQUEST_CODE2);*/
                    Intent intent = new Intent();
                    intent.setClass(MenuActivity.this,LivePublisherActivity.class);
                    intent.putExtra("isOpenVideo",false);
                    startActivity(intent);
                    //startActivity(new Intent(MenuActivity.this, LivePublisherActivity.class));
                    break;
                case 8://视频会议
                    startActivity(new Intent(MenuActivity.this,WebRtcDemo.class));
                    //startActivity(new Intent(MenuActivity.this,UpdateActivity.class));
                    break;
                default:
                    break;
            }

            /*Object obj = menuGv.getAdapter().getItem(arg2);
            HashMap<String,Object> map  = (HashMap<String,Object>)obj;
            String str = (String) map.get("itemText");*/


        }

    }
    class gridviewPage2OnItemClickListener implements AdapterView.OnItemClickListener
    {

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // TODO Auto-generated method stub
            switch (arg2) {
                case 0:
                    startActivity(new Intent(MenuActivity.this,TestActivity.class));
                    break;
                case 1://
                    Intent intent7 = new Intent();
                    intent7.putExtra("title","北斗系统");
                    intent7.setClass(MenuActivity.this,TemplateActivity.class);
                    startActivity(intent7);
                    break;
                case 2:
                    cameraTask(REQUEST_CODE);
                    break;
                case 3://更新
                    Intent intent3=new Intent(MenuActivity.this,UpdateActivity.class);
                    intent3.putExtra("json","");
                    startActivity(intent3);
                    break;
                case 5:// 系统设置
                    Intent intent = new Intent();
                    intent.setClass(MenuActivity.this,SettingsActivity.class);
                    startActivityForResult(intent,REQUEST_CODE2);
                    break;
                case 4://通知公告 别问我为什么想到10086
                   /* Intent intentNoti = new Intent(MenuActivity.this,NotiActivity.class);
                    startActivityForResult(intentNoti,10086);*/
                    Intent intent5 = new Intent();
                    intent5.putExtra("title","通知公告");
                    intent5.setClass(MenuActivity.this,TemplateActivity.class);
                    startActivity(intent5);
                    break;
                case 6://最新推送
                    break;
                default:
                    break;
            }
        }
    }

public  MenuActivity getInStance(){
    return MenuActivity.this;

}
    //static 静态内部类的使用是有说法的  静态就方便全局拿
    public static Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作 本来是用观察者模式比较好
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == HANDLERINT) {
            struct1= (int[]) msg.obj;
                Log.e("msg",struct1[2]+"");
                menuBottomAdapter.menuList.clear();
                   for(int i=0;i<bottomIconName.length;i++){
                      map2= new HashMap<String,Object>();
                       map2.put("itemImage", bottomIcon[i]);
                       map2.put("itemText", bottomIconName[i]);
                       if (i==0){
                           map2.put("badgenumber",struct1[18]);
                       }else {
                           map2.put("badgenumber",0);
                       }
                       menuBottomAdapter.menuList.add(map2);
               }
              mia1.menuList.clear();
                for(int i=0;i<9;i++){
                    map1 = new HashMap<String,Object>();
                    map1.put("itemImage", icon[i]);
                    map1.put("itemText", iconName[i]);
                    map1.put("badgenumber",struct1[i]);
                    mia1.menuList.add(map1);
                }
                menuBottomAdapter.notifyDataSetChanged();
                mia1.notifyDataSetChanged();
              //  mia2.notifyDataSetChanged();
            }
        }
    };


    class gridviewBottomOnItemClickListener implements AdapterView.OnItemClickListener
    {

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // TODO Auto-generated method stub
            switch (arg2) {
                case 0:
                    arg1.findViewById(R.id.icon_badge2).setVisibility(View.GONE);
                    Intent intent5 = new Intent();
                    intent5.putExtra("title","请销假");
                    intent5.setClass(MenuActivity.this,TemplateActivity.class);
                    startActivity(intent5);
                    /*Intent newsIntent = new Intent(MenuActivity.this, NewsActivity.class);
                    newsIntent.putExtra("type", 1);
                    startActivity(newsIntent);*/
                    break;
                case 1:
                    startActivity(new Intent(MenuActivity.this, TDMapActivity.class));
                    /*Intent studyIntent = new Intent(MenuActivity.this, NewsActivity.class);
                    studyIntent.putExtra("type", 2);
                    startActivity(studyIntent);*/
                    break;
                case 2:
                    //startActivity(new Intent(MenuActivity.this, ContactActivity.class));
                    startActivity(new Intent(MenuActivity.this, ContactTreeActivity.class));
                    break;
                case 3:
                    cameraTask(REQUEST_CODE_UPLOAD);
                    break;
            }


        }

    }
    /**
     * 初始化权限事件
     */
    private void initPermission() {
        //检查权限
        String[] permissions = CheckPermissionUtils.checkPermission(this);
        if (permissions.length == 0) {
            //权限都申请了
            //是否登录
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }
    /**
     * 事件设置
     */
    private void setupEvents()
    {
        // 现场直播
        //btnLivePublic.setOnClickListener(this);
        // 通讯录监听
        //btnContact.setOnClickListener(this);
        // 新闻资讯监听
        //btnNews.setOnClickListener(this);
        // 在线学习
        //btnStudy.setOnClickListener(this);
        // 情报报知
        //buttonIntelligence.setOnClickListener(this);
        // 考勤签到
        //btncodeScan.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       /* switch (view.getId()) {
            case R.id.btnContact:
                startActivity(new Intent(MenuActivity.this, ContactActivity.class));
                break;
            case R.id.btnNews:
                Intent newsIntent = new Intent(MenuActivity.this, NewsActivity.class);
                newsIntent.putExtra("type", 1);
                startActivity(newsIntent);
                break;
            case R.id.btnStudy:
                Intent studyIntent = new Intent(MenuActivity.this, NewsActivity.class);
                studyIntent.putExtra("type", 2);
                startActivity(studyIntent);
                break;//
        }*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE || requestCode == REQUEST_CODE_UPLOAD) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    if(requestCode == REQUEST_CODE_UPLOAD){
                        Intent intent = new Intent();
                        intent.putExtra("title","扫码上传");
                        intent.putExtra("url",result);
                        intent.setClass(MenuActivity.this,UploadFileActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MenuActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }else if (requestCode == REQUEST_CODE2 && resultCode == RESULT_CODE){
            finish();
        }else if(requestCode == 10086 && resultCode == RESULT_CODE){
            //此处撰写通知公告逻辑
           // data.putExtra();
        }

        /*else if(requestCode == REQUEST_CODE_UPLOAD){
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Intent intent = new Intent();
                    intent.putExtra("title","文件上传");
                    intent.putExtra("url",result);
                    intent.setClass(MenuActivity.this,UploadFileActivity.class);
                    startActivity(intent);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MenuActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }*/


    }
    /**
     * EsayPermissions接管权限处理逻辑
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @AfterPermissionGranted(REQUEST_CAMERA_PERM)
    public void cameraTask(int codeInt) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!

            Intent intent = new Intent(getApplication(), CaptureActivity.class);
            startActivityForResult(intent, codeInt);
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "需要请求camera权限",
                    REQUEST_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Toast.makeText(this, "执行onPermissionsGranted()...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //Toast.makeText(this, "执行onPermissionsDenied()...", Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, "当前App需要申请camera权限,需要打开设置页面么?")
                    .setTitle("权限申请")
                    .setPositiveButton("确认")
                    .setNegativeButton("取消", null /* click listener */)
                    .setRequestCode(REQUEST_CAMERA_PERM)
                    .build()
                    .show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        VSGService.logout(MenuActivity.this, null);
    }


}
