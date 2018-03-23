package com.android.zhgf.zhgf.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.adapter.NewsInfoAdapter;
import com.android.zhgf.zhgf.adapter.StudyAdapter;
import com.android.zhgf.zhgf.bean.NewsInfoEntity;
import com.android.zhgf.zhgf.bean.StudyInfoEntity;
import com.android.zhgf.zhgf.tool.Constants;
import com.android.zhgf.zhgf.wnd.iface.INetwork;
import com.android.zhgf.zhgf.wnd.utils.NetworkUtil;

import java.util.ArrayList;

public class TestActivity extends BaseActivity implements  StudyAdapter.InnerItemOnclickListener{

    private static final String TAG = TestActivity.class.getSimpleName();

    private ArrayList<StudyInfoEntity> testList = new ArrayList<StudyInfoEntity>();

    private ListView testLv;

    private StudyAdapter studyAdapter;

    public final static int SET_TESTLIST = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        // 返回图标
        setBack();
        setTitle("在线考试");

        testLv = (ListView) this.findViewById(R.id.testLv);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    // 加载数据
                    getTestData();
                    testList = Constants.getTestList();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                handler.obtainMessage(SET_TESTLIST).sendToTarget();
            }
        }).start();
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SET_TESTLIST:
                    if(studyAdapter == null){
                        studyAdapter = new StudyAdapter(TestActivity.this,testList);
                    }
                    studyAdapter.setOnInnerItemOnclickListener(new StudyAdapter.InnerItemOnclickListener() {

                        @Override
                        public void click(View v) {
                            int position;
                            //position = (Integer) v.getTag();
                            Intent intent = new Intent(TestActivity.this, ExaminationActivity.class);
                            //intent.putExtra("test", studyAdapter.getItem(position));
                            startActivity(intent);
                        }
                    });
                    testLv.setAdapter(studyAdapter);
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    // 加载数据
    private void getTestData(){
        String HttpURL = "";
        String params = "";

        /*HttpURL = "http://192.168.10.107:89/index.php";
        params = "m=app&c=index&a=contents&catid=" + channel_id;
        Log.e(TAG, "run: HttpURL="+HttpURL+"?"+ params + "typeInt="+typeInt);*/


        (new NetworkUtil()).HttpSendGetData(HttpURL, params, onNetWorkStatusListener);
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
                Log.w(TAG, "onGetResult: "+mHttp.getData());
                String strByJson = (String) mHttp.getData();
                /*NewsInfoEntity newsInfo = new NewsInfoEntity();
                newsList = newsInfo.getNewsList(strByJson);*/
            } else {
                Log.e(TAG,  TAG + "->onGetResult: 获取考试列表失败");
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

    @Override
    public void click(View v) {
;
    }
}
