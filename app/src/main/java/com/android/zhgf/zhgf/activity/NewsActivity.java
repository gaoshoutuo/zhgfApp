package com.android.zhgf.zhgf.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.zhgf.zhgf.JavaBean.CategoryList;
import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.adapter.NewsFragmentPagerAdapter;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.bean.ChannelItem;
import com.android.zhgf.zhgf.fragment.NewsFragment;
import com.android.zhgf.zhgf.tool.BaseTools;
import com.android.zhgf.zhgf.view.ColumnHorizontalScrollView;
import com.android.zhgf.zhgf.wnd.iface.INetwork;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends BaseActivity {

    private static final String TAG = NewsActivity.class.getSimpleName();
    /** 自定义HorizontalScrollView */
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
    LinearLayout mRadioGroup_content;
    LinearLayout ll_more_columns;
    LinearLayout titlell;
    RelativeLayout rl_column;
    private ViewPager mViewPager;
    private ImageView button_more_columns;
    /** 用户选择的新闻分类列表*/
    private ArrayList<ChannelItem> userChannelList=new ArrayList<ChannelItem>();
    /** 当前选中的栏目*/
    private int columnSelectIndex = 0;
    /** 左阴影部分*/
    public ImageView shade_left;
    /** 右阴影部分 */
    public ImageView shade_right;
    /** 屏幕宽度 */
    private int mScreenWidth = 0;
    /** Item宽度 */
    private int mItemWidth = 0;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    // 标题栏
    List<String> lstTitleName = new ArrayList<>();

    //protected SlidingMenu side_drawer;

    /** head 头部 的中间的loading*/
    private ProgressBar top_progress;
    /** head 头部 中间的刷新按钮*/
    private ImageView top_refresh;
    /** head 头部 的左侧菜单 按钮*/
    private ImageView top_head;
    /** head 头部 的右侧菜单 按钮*/
    private ImageView top_more;
    /** 请求CODE */
    public final static int CHANNELREQUEST = 1;
    /** 调整返回的RESULTCODE */
    public final static int CHANNELRESULT = 10;
    // 画面类型 1:新闻资讯 2:在线学习
    private int typeInt = 0;
    private int othertypeInt = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        typeInt = (int) getIntent().getSerializableExtra("type");
        othertypeInt = (int) getIntent().getSerializableExtra("othertype");
        // 返回图标
        setBack();
        if(typeInt == 1){
            setTitle("新闻资讯");
        }else if (typeInt == 2){
            setTitle("在线学习");
        }
        if (othertypeInt == 1){
            setTitle("指挥控制");
        }else if (othertypeInt == 2){
            setTitle("工作动态");
        }else if (othertypeInt == 3){
            setTitle("政治教育");
        }else if(othertypeInt==4){
            setTitle("通知公告");
        }

        mScreenWidth = BaseTools.getWindowsWidth(this);

        mItemWidth = mScreenWidth / 3 + mScreenWidth / 20;// 一个Item宽度为屏幕的1/7
        initView();
    }

    /** 初始化layout控件*/
    private void initView() {
        mColumnHorizontalScrollView = findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = findViewById(R.id.mRadioGroup_content);
        rl_column = findViewById(R.id.rl_column);
        mViewPager = findViewById(R.id.mViewPager);
        shade_left = findViewById(R.id.shade_left);
        shade_right = findViewById(R.id.shade_right);
        titlell = (LinearLayout) findViewById(R.id.title_ll);
        if(othertypeInt != 0){
            titlell.setVisibility(View.GONE);
        }

        setView();
        setChangelView();//****************************************************测试用
    }
    /**
     *  当栏目项发生变化时候调用
     * */
    private void setChangelView() {
        initColumnData();
        initTabColumn();
        initFragment();

    }
    /** 获取Column栏目 数据*/
    private void initColumnData() {
        //userChannelList = ((ArrayList<ChannelItem>)ChannelManage.getManage(AppApplication.getApp().getSQLHelper()).getUserChannel());
        /*userChannelList.add(new ChannelItem(0,"军事新闻",1,1));
        userChannelList.add(new ChannelItem(1,"国外军事新闻",2,0));
        userChannelList.add(new ChannelItem(2,"本地新闻",3,0));*/
       /* for(int i = 0;i < lstTitleName.size();i++){
            if(i == 0){
                userChannelList.add(new ChannelItem(i,lstTitleName.get(i),i,1));
            }else{
                userChannelList.add(new ChannelItem(i,lstTitleName.get(i),i,0));
            }

        }*/
       /*if(typeInt == 2){
            userChannelList.add(new ChannelItem("0","国防教育学习",1,1));
            userChannelList.add(new ChannelItem("1","政治教育学习",2,0));
            userChannelList.add(new ChannelItem("2","其他教育学习",3,0));
       }*/
    }
    /**
     *  初始化Column栏目项
     * */
    private void initTabColumn() {
//			TextView localTextView = (TextView) mIn
            mRadioGroup_content.removeAllViews();
            int count =  userChannelList.size();
            mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, shade_left, shade_right, ll_more_columns, rl_column);
            for(int i = 0; i< count; i++){
                if(userChannelList.get(i).getName().length() > 4){
                    mItemWidth = mScreenWidth / 3 + mScreenWidth / 20;
                }else{
                    mItemWidth = mScreenWidth / 4;
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth , ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 5;
                params.rightMargin = 5;
            TextView columnTextView = new TextView(this);
            columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
//			localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
            columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
            columnTextView.setGravity(Gravity.CENTER);
            columnTextView.setPadding(5, 5, 5, 5);
            columnTextView.setId(i);
            columnTextView.setText(userChannelList.get(i).getName());
            columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_text_color_day));
                //columnTextView.setLines(1);
            if(columnSelectIndex == i){
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for(int i = 0;i < mRadioGroup_content.getChildCount();i++){
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v)
                            localView.setSelected(false);
                        else{
                            localView.setSelected(true);
                            mViewPager.setCurrentItem(i);
                        }
                    }
                    //Toast.makeText(getApplicationContext(), userChannelList.get(v.getId()).getName(), Toast.LENGTH_SHORT).show();
                }
            });
            mRadioGroup_content.addView(columnTextView, i ,params);
        }
    }
    /**
     *  选择的Column里面的Tab
     * */
    private void selectTab(int tab_postion) {
        columnSelectIndex = tab_postion;
        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
            View checkView = mRadioGroup_content.getChildAt(tab_postion);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l + k / 2 - mScreenWidth / 2;
            // rg_nav_content.getParent()).smoothScrollTo(i2, 0);
            mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
            // mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
            // mItemWidth , 0);
        }
        //判断是否选中
        for (int j = 0; j <  mRadioGroup_content.getChildCount(); j++) {
            View checkView = mRadioGroup_content.getChildAt(j);
            boolean ischeck;
            ischeck = j == tab_postion;
            checkView.setSelected(ischeck);
        }
    }
    /**
     *  初始化Fragment
     * */
    private void initFragment() {
        fragments.clear();//清空
        int count =  userChannelList.size();
        for(int i = 0; i< count;i++){
            Bundle data = new Bundle();
            data.putString("text", userChannelList.get(i).getName());
            data.putString("id", userChannelList.get(i).getCatId());
            data.putInt("type", typeInt);
            data.putInt("othertype",othertypeInt);

            NewsFragment newsfragment = new NewsFragment();
            newsfragment.setHttpURL(((AppApplication)getApplicationContext()).getConfigures().getHostServer());

            newsfragment.setArguments(data);
            fragments.add(newsfragment);
        }
        NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments);
//		mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mAdapetr);
        mViewPager.addOnPageChangeListener(pageListener);
    }
    private INetwork.OnNetworkStatusListener onNetWorkStatusListener =  new INetwork.OnNetworkStatusListener(){
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
                Log.e(TAG, TAG + "->onGetResult: 新闻资讯向服务器请求数据成功");
                Log.w(TAG, "onGetResult: "+mHttp.getData());
                String strByJson = (String) mHttp.getData();
                try {
                    //拿到数组
                    JsonObject jsonObject = new JsonParser().parse(strByJson).getAsJsonObject();
                    JsonArray jsonArray = jsonObject.getAsJsonArray("msg");
                    int i = 0;
                    for (JsonElement msg : jsonArray) {
                        CategoryList msgbean = new Gson().fromJson(msg, new TypeToken<CategoryList>() {
                        }.getType());
                        //lstTitleName.add(msgbean.getCatname());

                        if(othertypeInt == 0 && i == 0 ){
                            userChannelList.add(new ChannelItem(msgbean.getCatid(),msgbean.getCatname(),i,1));
                        }else{
                            if(othertypeInt == 0){
                                if(!msgbean.getCatname().equals("全文检索")){
                                    userChannelList.add(new ChannelItem(msgbean.getCatid(),msgbean.getCatname(),i,1));
                                }
                            }else if(othertypeInt == 1){
                                if(msgbean.getCatname().equals("通知公告")){
                                    userChannelList.add(new ChannelItem(msgbean.getCatid(),msgbean.getCatname(),i,1));
                                }
                            }else if(othertypeInt == 2){
                                if(msgbean.getCatname().equals("工作动态")){
                                    userChannelList.add(new ChannelItem(msgbean.getCatid(),msgbean.getCatname(),i,1));
                                }
                            }else if(othertypeInt == 3){
                                userChannelList.add(new ChannelItem("14","政治教育",i,1));
                                break;
                            }
                        }
                        i++;
                    }

                } catch (Exception ex) {
                    // 异常处理代码
                    Log.e(TAG,  TAG + "->onGetResult: json解析数据失败");
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
    private void setView() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                String HttpURL = "";
                String params = "";
                if(typeInt == 1){
                     //HttpURL = "http://192.168.10.107:89/index.php";
                     HttpURL = ((AppApplication)NewsActivity.this.getApplicationContext()).getConfigures().getHostServer() + "/index.php";
                     params = "m=app&c=index&a=category&catid=9";
                }else if(typeInt == 2){
                    HttpURL = ((AppApplication)NewsActivity.this.getApplicationContext()).getConfigures().getHostServer() + "/index.php";
                    params = "m=app&c=index&a=category&catid=9";
                }

                Log.e(TAG, "run: HttpURL="+HttpURL+"?"+ params);

                //String ResultString = new NetWorkUtils().HttpSendGetData(HttpURL,params);
                (new NetworkUtil()).HttpSendGetData(HttpURL, params, onNetWorkStatusListener);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        setChangelView();
                    }
                });
            }
        }).start();


    }


    /**
     *  ViewPager切换监听方法
     * */
    public ViewPager.OnPageChangeListener pageListener= new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            mViewPager.setCurrentItem(position);
            selectTab(position);
        }
    };


}
