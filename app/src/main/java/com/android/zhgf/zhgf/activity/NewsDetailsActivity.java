package com.android.zhgf.zhgf.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.adapter.CommentAdapter;
import com.android.zhgf.zhgf.adapter.MutiLayoutAdapter;
import com.android.zhgf.zhgf.adapter.NewsInfoAdapter;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.bean.NewsCommentEntity;
import com.android.zhgf.zhgf.bean.NewsDetailEntity;
import com.android.zhgf.zhgf.bean.NewsInfoEntity;
import com.android.zhgf.zhgf.util.DateUtil;
import com.android.zhgf.zhgf.view.CommonPopupWindow;
import com.android.zhgf.zhgf.view.CommonPopupWindow.LayoutGravity;
import com.android.zhgf.zhgf.wnd.iface.INetwork;
import com.android.zhgf.zhgf.wnd.iface.impl.User;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsDetailsActivity extends BaseActivity {

    private final static String TAG = "NewsDetailsActivity";

    private NewsInfoEntity newsInfo;

    private String newsId;

    private String channelId;

    private int typeInt;

    private int othertypeInt;

    private ListView lvNews;

    private NewsInfoAdapter mAdapter;

    private CommentAdapter commentAdapter;

    private ImageView detail_loading;

    private ArrayList<NewsInfoEntity> newsList = new ArrayList<NewsInfoEntity>();

    private NewsDetailEntity newsDetailEntity = new NewsDetailEntity();

    private ArrayList<NewsCommentEntity> newsCommentList = new ArrayList<NewsCommentEntity>();
    //private ArrayList<NewsDetailEntity> newsDetailList = new ArrayList<NewsDetailEntity>();

    public final static int SET_NEWSLIST = 0;

    public final static int SET_NEWSDETAIL = 1;

    public final static int SET_NEWSCOMMENT = 2;

    public final static int GET_NEWSCOMMENT = 3;

    private TextView tvCopyFrom;

    private TextView tvUpdateTime;

    private TextView tvTitle;

    private TextView tvNewsDetails;

    private ImageView pic1;

    private ImageView pic2;

    private ImageView pic3;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private WebView wvnewsDetail;

    String pattern = "yyyy年MM月dd日";

    private Button stopBtn;

    DisplayImageOptions options;

    private Chronometer chronometer = null;

    private ImageView clockIv;

    private TextView tvmore;

    public int headHeight = 0;

    private ArrayList<Object> mData =  new ArrayList<Object>();
    private ArrayList<Object> mDataTemp =  new ArrayList<Object>();

    private MutiLayoutAdapter myAdapter = null;

    private static final int TYPE_NEWSINFO = 0;

    private ImageView likeIv;

    private ImageView unlikeIv;

    private ImageView sendCommentIv;

    private User mUser;

    private EditText commentEt;

    private boolean likeBln = false;

    private LinearLayout commentPanel;

    private RelativeLayout studyPanel;

    private int commentCount = 0;

    // popup window
    private CommonPopupWindow window;
    private LayoutGravity layoutGravity;
    private Button likeBtn;
    private Button unlikeBtn;
    private Button normalBtn;
    private Button attitudesBtn;

    private String attitudesStr = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        mUser=new User(getApplicationContext());

        lvNews = (ListView) findViewById(R.id.newsList);

        detail_loading = (ImageView)findViewById(R.id.detail_loading);

        //lvNewsComment = (ListView) findViewById(R.id.newsCommentList);

        clockIv = (ImageView)findViewById(R.id.clockIv);

        chronometer  = (Chronometer)findViewById(R.id.chronometer);

        stopBtn = (Button)findViewById(R.id.stop_btn);

        sendCommentIv = (ImageView)findViewById(R.id.sendCommentIv);

        attitudesBtn = (Button)findViewById(R.id.attitudesBtn);

        //likeIv = (ImageView)findViewById(R.id.likeIv);

        //unlikeIv = (ImageView)findViewById(R.id.unlikeIv);

        commentEt = (EditText)findViewById(R.id.commentEt);

        commentPanel = (LinearLayout)findViewById(R.id.comment_panel);

        studyPanel = (RelativeLayout)findViewById(R.id.study_panel);

        // 返回图标
        setBack();

        initData();

        if(othertypeInt == 0){
            setTitle("新闻资讯");
        }else if (othertypeInt == 1){
            setTitle("指挥控制");
        }else if (othertypeInt == 2){
            setTitle("工作动态");
        }else if (othertypeInt == 3){
            setTitle("政治教育");
        }else if (othertypeInt == 4){
            setTitle((String) getIntent().getSerializableExtra("title"));
        }

        setNewsDetails();

        setNewsList();

        //setNewsCommentDetails();

        /*window=new CommonPopupWindow(this, R.layout.popup_gravity, 200, ViewGroup.LayoutParams.WRAP_CONTENT) {
            @Override
            protected void initView() {
                View view=getContentView();
                likeBtn=(Button) view.findViewById(R.id.likeBtn);
                unlikeBtn=(Button) view.findViewById(R.id.unlikeBtn);
                normalBtn=(Button) view.findViewById(R.id.normalBtn);
                likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        attitudesBtn.setText("正");
                    }
                });
                unlikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        attitudesBtn.setText("反");
                    }
                });
                normalBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        attitudesBtn.setText("中");
                    }
                });
            }

            @Override
            protected void initEvent() {

            }
        };
        layoutGravity=new LayoutGravity(LayoutGravity.ALIGN_LEFT|LayoutGravity.TO_BOTTOM);
        layoutGravity.setHoriGravity(LayoutGravity.TO_RIGHT);
        layoutGravity.setVertGravity(LayoutGravity.TO_ABOVE);*/

        if(typeInt == 1){
            /*clockIv.setVisibility(View.GONE);
            chronometer.setVisibility(View.GONE);
            stopBtn.setVisibility(View.GONE);*/
            studyPanel.setVisibility(View.GONE);
            attitudesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(attitudesBtn.getText().equals("我的态度") || attitudesBtn.getText().equals("中")){
                        attitudesBtn.setBackgroundColor(ContextCompat.getColor(NewsDetailsActivity.this, R.color.like));
                        attitudesBtn.setText("正");
                        attitudesStr = "1";
                    }else if(attitudesBtn.getText().equals("正")){
                        attitudesBtn.setBackgroundColor(ContextCompat.getColor(NewsDetailsActivity.this, R.color.unlike));
                        attitudesBtn.setText("反");
                        attitudesStr = "2";
                    }else if(attitudesBtn.getText().equals("反")){
                        attitudesBtn.setBackgroundColor(ContextCompat.getColor(NewsDetailsActivity.this, R.color.normal));
                        attitudesBtn.setText("中");
                        attitudesStr = "3";
                    }
                    //window.showAsDropDown(attitudesBtn, 100,-392);
                }
            });
            sendCommentIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String content = commentEt.getText().toString();
                    //虑空
                    if (content.trim().length() < 1) {
                        return;
                    }
                    String HttpURL = ((AppApplication)getApplicationContext()).getConfigures().getHostServer() + "/index.php?m=app&c=index&a=add_comment&";
                    //String HttpURL = "http://192.168.10.107:89/index.php?m=app&c=index&a=add_comment&";
                    Map<String, Object> mPostValues = new HashMap<String,Object>();
                    mPostValues.put("commentid","content_" + channelId + "-" + newsId + "-1");
                    mPostValues.put("content",content.trim());
                    mPostValues.put("direction",attitudesStr);
                    mPostValues.put("id",newsId);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "submit:正在提交中");
                            (new NetworkUtil()).HttpSendPostData(HttpURL, mPostValues, new INetwork.OnNetworkStatusListener() {
                                @Override
                                public void onConnected(boolean isSuccess) {

                                }

                                @Override
                                public void onDisconnected(boolean isSuccess) {

                                }

                                @Override
                                public void onGetResult(boolean isSuccess, Object pData) {

                                }

                                @Override
                                public void onPostResult(boolean isSuccess, Object pData) {

                                    NetworkUtil.HttpStatus mStatus = (NetworkUtil.HttpStatus)pData;
                                    if (mStatus.getStatusCode()==200){
                                        Log.e(TAG, "onPostResult: 提交成功!"+mStatus.getData());
                                        handler.obtainMessage(GET_NEWSCOMMENT).sendToTarget();
                                    }else{
                                        Log.e(TAG, "onPostResult: 提交失败!" );
                                    }
                                }

                                @Override
                                public void onError(int errCode, String errMessage) {

                                }
                            });
                        }
                    }).start();



                   /* long time=System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳
                    String  timeStr=String.valueOf(time);
                    NewsCommentEntity newsComment = new NewsCommentEntity();
                    //newsComment.setUserNameStr((String)mUser.getCache(User.CACHE_KEY_USER_NAME));
                    newsComment.setUserNameStr("桐乡市国防教育委员会网友");
                    newsComment.setLastupdateStr(timeStr);
                    newsComment.setContentStr(content);
                    if(mData != null ){
                        if(commentCount == 0){
                            mData.add("评论");
                        }
                        mData.add(newsComment);
                        commentCount++;
                        if(myAdapter == null){return;}
                        myAdapter.notifyDataSetChanged();
                        commentEt.setText("");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        showToast("评论成功！");
                        lvNews.smoothScrollToPosition(mData.size());


                    }*/
                }
            });
        }else {
            commentPanel.setVisibility(View.GONE);
            chronometer.setBase(SystemClock.elapsedRealtime());            //设置起始时间
            chronometer.setFormat("%s分钟");                      //设置显示时间格式
            chronometer.start();
            stopBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    chronometer.stop();
                    sendStudyTime();
                    finish();
                }
            });
        }

    }

    private void sendStudyTime(){
        String studyTime = DateUtil.getChronometerSeconds(chronometer);
        User mUser = new User(getApplicationContext());
        String HttpURL = ((AppApplication)getApplicationContext()).getConfigures().getHostServer()
                + "/index.php";
        String params = "m=app&c=index&a=addLearned&model_id=1&cat_id="
                + channelId
                + "&data_id="
                + newsId
                + "&mobilephone="
                + (String)mUser.getCache(User.CACHE_KEY_USER_PHONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                (new NetworkUtil()).HttpSendGetData(HttpURL, params, new INetwork.OnNetworkStatusListener(){
                    @Override
                    public void onConnected(boolean isSuccess) {

                    }

                    @Override
                    public void onDisconnected(boolean isSuccess) {

                    }

                    @Override
                    public void onGetResult(boolean isSuccess, Object pData) {
                        NetworkUtil.HttpStatus mHttp = (NetworkUtil.HttpStatus) pData;
                        if (mHttp.getStatusCode() == 200) {
                            Log.w(TAG, "onGetResult: "+mHttp.getData() +"_____________________________________________________________________");
                            String strByJson = (String) mHttp.getData();

                        } else {

                        }


                    }

                    @Override
                    public void onPostResult(boolean isSuccess, Object pData) {

                    }

                    @Override
                    public void onError(int errCode, String errMessage) {
                        Log.w(TAG, "onError: 是不是出错了呀");
                    }
                });
            }
        }).start();


    }

    // 加载获取后续阅读数据
    private void setNewsList(){
        // 加载获取后续阅读数据
        if(newsList !=null && newsList.size() !=0){
            handler.obtainMessage(SET_NEWSLIST).sendToTarget();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        if(typeInt == 1){
                            // 获取后续阅读数据
                            getNewsListData();
                            // 获取新闻评论数据
                            getNewsCommentData();
                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    handler.obtainMessage(SET_NEWSLIST).sendToTarget();
                }
            }).start();
        }
    }
    // 重新加载获取评论数据
    private void setNewsCommentList(){
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    if(typeInt == 1){
                        // 获取新闻评论数据
                        getNewsCommentData();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                handler.obtainMessage(SET_NEWSCOMMENT).sendToTarget();
            }
        }).start();
    }

    // 加载数据
    private void getNewsListData(){
        String HttpURL = "";
        String params = "";

        if(typeInt == 1){
            HttpURL = ((AppApplication)getApplicationContext()).getConfigures().getHostServer() + "/index.php";
            params = "m=app&c=index&a=contents&catid=" + channelId;
        }else if (typeInt == 2){
            HttpURL =  ((AppApplication)getApplicationContext()).getConfigures().getHostServer() + "/index.php";
            params = "m=app&c=index&a=contents&catid=" + channelId;
        }
        Log.e(TAG, "run: HttpURL="+HttpURL+"?"+ params + "typeInt="+typeInt);

        //String ResultString = new NetWorkUtils().HttpSendGetData(HttpURL,params);
        (new NetworkUtil()).HttpSendGetData(HttpURL, params, onNetWorkStatusListenerForNewsList);
    }

    private INetwork.OnNetworkStatusListener onNetWorkStatusListenerForNewsList =  new INetwork.OnNetworkStatusListener(){
        @Override
        public void onConnected(boolean isSuccess) {

        }

        @Override
        public void onDisconnected(boolean isSuccess) {

        }

        @Override
        public void onGetResult(boolean isSuccess, Object pData) {
            NetworkUtil.HttpStatus mHttp = (NetworkUtil.HttpStatus) pData;
            if (mHttp.getStatusCode() == 200) {
                Log.w(TAG, "onGetResult: "+mHttp.getData());
                String strByJson = (String) mHttp.getData();
                NewsInfoEntity newsInfo = new NewsInfoEntity();
                ArrayList<NewsInfoEntity> tempList = new ArrayList<NewsInfoEntity>();
                tempList = newsInfo.getNewsList(strByJson);

                for(int i = 0;i < 2;i++){
                    int randomNumber=(int)(Math.random()*tempList.size());
                    if(randomNumber >= 0 && randomNumber < tempList.size()){
                        mData.add(tempList.get(randomNumber));
                        mDataTemp.add(tempList.get(randomNumber));
                    }
                }
            } else {
                Log.e(TAG,  TAG + "->onGetResult: 向服务器注册视频直播事件失败");
            }
        }

        @Override
        public void onPostResult(boolean isSuccess, Object pData) {

        }

        @Override
        public void onError(int errCode, String errMessage) {
            Log.w(TAG, "onError: 是不是出错了呀");
        }
    };
    private void initData() {
        //newsList = Constants.getNewsList();
        Intent intent = getIntent();

        //newsInfo = (NewsInfoEntity) getIntent().getSerializableExtra("news");

        newsId = (String)getIntent().getSerializableExtra("newsId");

        channelId = (String)getIntent().getSerializableExtra("channelId");

        typeInt = (int) getIntent().getSerializableExtra("type");

        othertypeInt = (int) getIntent().getSerializableExtra("othertype");


    }

    private void setNewsDetails(){
        // 加载新闻内容数据
        if(newsList !=null && newsList.size() !=0){
            handler.obtainMessage(SET_NEWSDETAIL).sendToTarget();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        // 获取新闻内容数据
                        getNewsDetailData();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    handler.obtainMessage(SET_NEWSDETAIL).sendToTarget();
                }
            }).start();
        }
    }

    private void setNewsCommentDetails(){
        // 加载新闻评论数据
        if(newsList !=null && newsList.size() !=0){
            handler.obtainMessage(SET_NEWSDETAIL).sendToTarget();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        // 获取新闻评论数据
                        getNewsCommentData();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    private void getNewsCommentData(){
        String HttpURL = ((AppApplication)getApplicationContext()).getConfigures().getHostServer() + "/index.php";
        String params = "";
        params = "m=app&c=index&a=comments&catid=" + channelId + "&id=" + newsId + "&modelid=1";
        Log.e(TAG, "run: HttpURL="+HttpURL+"?"+ params );

        (new NetworkUtil()).HttpSendGetData(HttpURL, params, onNetWorkStatusListenerForNewsComment);
    }


    private INetwork.OnNetworkStatusListener onNetWorkStatusListenerForNewsComment =  new INetwork.OnNetworkStatusListener(){
        @Override
        public void onConnected(boolean isSuccess) {

        }

        @Override
        public void onDisconnected(boolean isSuccess) {

        }

        @Override
        public void onGetResult(boolean isSuccess, Object pData) {
            NetworkUtil.HttpStatus mHttp = (NetworkUtil.HttpStatus) pData;
            if (mHttp.getStatusCode() == 200) {
                Log.w(TAG, "onGetResult: "+mHttp.getData());
                String strByJson = (String) mHttp.getData();
                NewsCommentEntity newsComment = new NewsCommentEntity();
                ArrayList<NewsCommentEntity> tempList = newsComment.getNewsCommentList(strByJson);
                if(tempList != null && tempList.size() > 0){
                    mData.clear();
                    mData.addAll(mDataTemp);
                    mData.add("评论");
                    commentCount = tempList.size();
                    mData.addAll(tempList);
                }
            } else {
                Log.e(TAG,  TAG + "->onGetResult: 获取新闻评论失败");
            }
        }

        @Override
        public void onPostResult(boolean isSuccess, Object pData) {

        }

        @Override
        public void onError(int errCode, String errMessage) {
            Log.w(TAG, "onError: 是不是出错了呀");
        }
    };
    // 获取新闻内容数据
    private void getNewsDetailData(){
        String HttpURL = ((AppApplication)getApplicationContext()).getConfigures().getHostServer() + "/index.php";
        String params = "";

        //HttpURL = "http://192.168.10.107:89/index.php";
        params = "m=app&c=index&a=content&catid=" + channelId + "&id=" + newsId;
        Log.e(TAG, "run: HttpURL="+HttpURL+"?"+ params );

        (new NetworkUtil()).HttpSendGetData(HttpURL, params, onNetWorkStatusListenerForNewsDetail);
    }

    private INetwork.OnNetworkStatusListener onNetWorkStatusListenerForNewsDetail =  new INetwork.OnNetworkStatusListener(){
        @Override
        public void onConnected(boolean isSuccess) {

        }

        @Override
        public void onDisconnected(boolean isSuccess) {

        }

        @Override
        public void onGetResult(boolean isSuccess, Object pData) {
            NetworkUtil.HttpStatus mHttp = (NetworkUtil.HttpStatus) pData;
            if (mHttp.getStatusCode() == 200) {
                Log.w(TAG, "onGetResult: "+mHttp.getData());
                String strByJson = (String) mHttp.getData();
                NewsDetailEntity newsDetail = new NewsDetailEntity();
                newsDetailEntity = newsDetail.getNewsDetail(strByJson);
            } else {
                Log.e(TAG,  TAG + "->onGetResult: 获取新闻内容失败");
            }
        }

        @Override
        public void onPostResult(boolean isSuccess, Object pData) {

        }

        @Override
        public void onError(int errCode, String errMessage) {
            Log.w(TAG, "onError: 是不是出错了呀");
        }
    };
    private void setNewsDetailsHead(){
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int widthInt = dm.widthPixels - 60;

        View view = LayoutInflater.from(NewsDetailsActivity.this).inflate(R.layout.newsinfodetial_item, null);

        tvmore = (TextView)view.findViewById(R.id.tvmore);

        tvCopyFrom = (TextView)view.findViewById(R.id.tvCopyFrom);

        tvUpdateTime = (TextView)view.findViewById(R.id.tvUpdateTime);

        tvTitle = (TextView)view.findViewById(R.id.tvTitle);


        //tvNewsDetails = (TextView)view.findViewById(R.id.tvNewsDetails);
        //blockLoadingNetworkImage = true;

        if(typeInt == 2){
            tvmore.setVisibility(View.GONE);
        }

        /*wvnewsDetail.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!blockLoadingNetworkImage){
                    wvnewsDetail.getSettings().setBlockNetworkImage(true);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (blockLoadingNetworkImage){
                    wvnewsDetail.getSettings().setBlockNetworkImage(false);
                }
            }
        });*/
        wvnewsDetail = (WebView) view.findViewById(R.id.wvNewsDetails);
        WebSettings ws = wvnewsDetail.getSettings();
        ws.setJavaScriptEnabled(true); // 设置支持javascript脚本
        ws.setAllowFileAccess(true); // 允许访问文件
        //ws.setBuiltInZoomControls(true); // 设置显示缩放按钮
        ws.setSupportZoom(true); // 支持缩放 <span style="color:#337fe5;"> /**
        // * 用WebView显示图片，可使用这个参数
        // * 设置网页布局类型：
        // * 1、LayoutAlgorithm.NARROW_COLUMNS ： 适应内容大小
        // * 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
        // */
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        ws.setDefaultTextEncodingName("utf-8"); // 设置文本编码
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);// 设置缓存模式</span>
        /*ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true); */
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }*/
        //添加Javascript调用java对象
        //wvnewsDetail.addJavascriptInterface(this, "java2js");
        ws.setBlockNetworkImage(false);
        ws.setJavaScriptEnabled(true);
        wvnewsDetail.setWebViewClient(new WebViewClientDemo());
        wvnewsDetail.setWebChromeClient(new WebViewChromeClientDemo());
        // 设置打开的网页
        //wvnewsDetail.loadUrl("http://www.voidcn.com/article/p-pmojsrgj-rb.html");
        // 使用WebView来显示图片
        //wvnewsDetail.loadData(newsDetailEntity.getContent(), "text/html", "utf8");

        pic1 =  (ImageView)view.findViewById(R.id.pic1);

        pic2 =  (ImageView)view.findViewById(R.id.pic2);

        pic3 =  (ImageView)view.findViewById(R.id.pic3);
        // 字体加粗
        TextPaint tp = tvTitle.getPaint();
        tp.setFakeBoldText(true);

        if(newsDetailEntity != null ){

            if(newsDetailEntity.getCopyFrom() != null){
                tvCopyFrom.setText(newsDetailEntity.getCopyFrom());
            }
            if(newsDetailEntity.getTitle() != null){
                tvTitle.setText(newsDetailEntity.getTitle());
            }

            //tvNewsDetails.setText(newsDetailEntity.getContent());

            if(null != newsDetailEntity.getInputTime()){

                tvUpdateTime.setText(DateUtil.getDateToString(newsDetailEntity.getInputTime(),pattern));
            }
            wvnewsDetail.setBackgroundColor(0);

            wvnewsDetail.loadDataWithBaseURL(null,resizeImageHtml(newsDetailEntity.getContent(),NewsDetailsActivity.this,widthInt), "text/html", "utf-8", null);
            //wvnewsDetail.loadDataWithBaseURL(null,resizeImage(newsDetailEntity.getContent(),NewsDetailsActivity.this,widthInt), "text/html", "utf-8", null);
            Log.e(TAG,  "内容: " + newsDetailEntity.getContent());
            /*if( newsDetailEntity.getPicList() !=null && newsDetailEntity.getPicList().size() != 0){

                if (newsDetailEntity.getPicList().size() == 1){
                    imageLoader.displayImage(newsDetailEntity.getPicList().get(0), pic1, options);
                    pic2.setVisibility(View.GONE);
                    pic3.setVisibility(View.GONE);
                }
                if (newsDetailEntity.getPicList().size() == 2){
                    imageLoader.displayImage(newsDetailEntity.getPicList().get(0), pic1, options);
                    imageLoader.displayImage(newsDetailEntity.getPicList().get(1), pic2, options);
                    pic3.setVisibility(View.GONE);
                }
                if (newsDetailEntity.getPicList().size() == 3){
                    imageLoader.displayImage(newsDetailEntity.getPicList().get(0), pic1, options);
                    imageLoader.displayImage(newsDetailEntity.getPicList().get(1), pic2, options);
                    imageLoader.displayImage(newsDetailEntity.getPicList().get(2), pic3, options);
                }

            }else{
                pic1.setVisibility(View.GONE);
                pic2.setVisibility(View.GONE);
                pic3.setVisibility(View.GONE);
            }*/
            pic1.setVisibility(View.GONE);
            pic2.setVisibility(View.GONE);
            pic3.setVisibility(View.GONE);
           /* String url1 = "http://gyfpjh.com/uploads/image/201612/1480921212113664-lp.jpg";
            String url2 = "https://s9.rr.itc.cn/r/wapChange/20175_28_17/a0c54p8306958631745.jpg";
            String url3 = "http://img1.gtimg.com/news/pics/hv1/197/152/2205/143419082.jpg";
            imageLoader.displayImage(url1, pic1, options);
            imageLoader.displayImage(url2, pic2, options);
            imageLoader.displayImage(url3, pic3, options);*/
        }

        lvNews.addHeaderView(view);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SET_NEWSLIST:
                    detail_loading.setVisibility(View.GONE);
                    if(myAdapter == null){
                        myAdapter = new MutiLayoutAdapter(NewsDetailsActivity.this,mData);
                    }
                    lvNews.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                    lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Intent intent = new Intent(NewsDetailsActivity.this, NewsDetailsActivity.class);
                            if (position == 0){
                                return;
                            }
                            int type = myAdapter.getItemViewType(position - 1);
                            switch (type){
                                case TYPE_NEWSINFO:
                                    NewsInfoEntity newsInfoEntity = (NewsInfoEntity)myAdapter.getItem(position - 1);
                                    if(newsInfoEntity == null){
                                        return;
                                    }
                                    //Log.e(TAG,  "position = " + position);
                                    intent.putExtra("newsId", newsInfoEntity.getNewsId());
                                    intent.putExtra("channelId", channelId);
                                    intent.putExtra("type", typeInt);
                                    intent.putExtra("othertype",othertypeInt);
                                    startActivity(intent);
                                    finish();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    break;
                case SET_NEWSDETAIL:
                    setNewsDetailsHead();
                    break;
                case SET_NEWSCOMMENT:
                    if(myAdapter == null){return;}
                    myAdapter.notifyDataSetChanged();
                    commentEt.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    showToast("评论成功！");
                    lvNews.smoothScrollToPosition(mData.size());
                    break;
                case GET_NEWSCOMMENT:
                    setNewsCommentList();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private String getHtmlData(String bodyHTML) {
        //String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>";
        String head = "<head><style>img{width:320px ;height:!important;}</style></head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    public static class WebViewChromeClientDemo extends WebChromeClient {
        // 设置网页加载的进度条
        public void onProgressChanged(WebView view, int newProgress) {
        }

        // 获取网页的标题
        public void onReceivedTitle(WebView view, String title) {
        }

        // JavaScript弹出框
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        // JavaScript输入框
        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        // JavaScript确认框
        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }
    }

    public static class WebViewClientDemo extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
            return true;
        }
        @Override
        public void onReceivedSslError(WebView view,
                                       SslErrorHandler handler, SslError error) {
            // TODO Auto-generated method stub
            // handler.cancel();// Android默认的处理方式
            //handler.proceed();// 接受所有网站的证书
            // handleMessage(Message msg);// 进行其他处理
        }
    }
    /**
     * 修改Img标签将Html文本的图片自适应屏幕
     * @param mHtml             源代码
     * @param mContex
     * @param WebviewWidth      webview控件的宽度。
     * @return
     */
    public static String resizeImageHtml(String mHtml,Context mContex,int WebviewWidth){
        StringBuilder mRstHtml=new StringBuilder();
        //解析出<img>标签
        if(mHtml == null){
            mRstHtml.append(mHtml);
            return mRstHtml.toString();
        }
        try{
            String[] mImgs=mHtml.split("<img");

            if(mImgs.length>0){
                for (int i = 0; i < mImgs.length; i++) {
                    String mImg=mImgs[i];
                    //过滤掉
                    int start=mImg.indexOf("style=");
                    int end=mImg.indexOf("/>");
                    if(start!=-1&&end!=-1&&mImg.contains("height")){//认定为Img标签
                        String mStyle=mImg.substring(mImg.indexOf("style="),mImg.indexOf("/>"));
                        String[] mMap=mStyle.split(";");
                        //LogCat.w(Arrays.toString(mMap));
                        //获取图片原始宽高
                        String mHeight="";
                        String mWidth="";
                        for (String mKey : mMap) {
                            if(mKey.contains("height")){
                                mHeight=mKey.substring(mKey.indexOf("height:")+"height:".length()).replaceAll("[^0-9]","");
                            }else if(mKey.contains("width")){
                                mWidth=mKey.substring(mKey.indexOf("width:")+"width:".length()).replaceAll("[^0-9]","");;
                            }
                        }
                        if(mWidth!="" && mHeight != ""){
                            float mImgW=Float.parseFloat(mWidth);
                            float mImgH=Float.parseFloat(mHeight);;
                            float mWebW=px2dip(mContex,WebviewWidth);
                            if(mImgW>mWebW){//需要缩放
                                float mScale=mImgW/mWebW;//获得缩放倍率
                                mImgW=mWebW;
                                mImgH=mImgH/mScale;
                                mImg=mImg.replace(mHeight,mImgH+"").replace(mWidth,mImgW+"");
                            }
                        }
                        //LogCat.w("Result=height:"+mImgH+"width:"+mImgW);
                    }else if(start!=-1&&end!=-1&&mImg.contains("<img src=")){
                        String mStyle=mImg.substring(mImg.indexOf("<img src="),mImg.indexOf("/>"));

                    }



                    if(i==mImgs.length-1){
                        mRstHtml.append(mImg);
                    }else{
                        mRstHtml.append(mImg).append("<img");
                    }
                }
            }else{
                mRstHtml.append(mHtml);
            }
        }
        catch(Exception e){
            mRstHtml =new StringBuilder();
            mRstHtml.append(mHtml);
            return mRstHtml.toString();
        }

        return mRstHtml.toString();
    }

    public static String resizeImage(String mHtml,Context mContex,int WebviewWidth){
        StringBuilder mRstHtml=new StringBuilder();
        String htmlStr = mHtml;
        int count = 0;

        float mImgW=Float.parseFloat("600");
        float mImgH=Float.parseFloat("330");;
        float mWebW=px2dip(mContex,WebviewWidth);
        if(mImgW>mWebW){//需要缩放
            float mScale=mImgW/mWebW;//获得缩放倍率
            mImgW=mWebW;
            mImgH=mImgH/mScale;
        }
        Matcher matcher = null;
        //匹配img标签的正则表达式 删除img标签
        String regxpForImgTag = "<img\\s[^>]+/>";
        Pattern pattern = Pattern.compile(regxpForImgTag);
        matcher = pattern.matcher(htmlStr);

        while (matcher != null && matcher.find()) {
            String temp = matcher.group();
            String afterTemp = "";
            //String tempUrl = temp.substring(temp.indexOf("src=") + 5);
            String style = "";
            if(temp.contains("style=")){
                style = " width: " + mImgW + "px; " + "height: " + mImgH + "px;\" />";
                afterTemp = temp.replace(" \"/>", style);
            }else{
                style = " style=\"width: " + mImgW + "px; " + "height: " + mImgH + "px;\" />";
                afterTemp = temp.replace(" />", style);
            }
            htmlStr = htmlStr.replace(temp,afterTemp);
            count++;
        }

        if(count == 0){
            //匹配img标签的正则表达式 删除img标签
            regxpForImgTag = "src[^>]+/>";
            pattern = Pattern.compile(regxpForImgTag);
            matcher = pattern.matcher(htmlStr);
            while (matcher != null && matcher.find()) {
                String temp = matcher.group();
            }
        }


        return htmlStr;
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public  static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
    public static void setListViewHeightBasedOnChildren(ListView listView,int headHeight) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        //params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + headHeight;
        //params.height = totalHeight + headHeight;
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NewsDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
