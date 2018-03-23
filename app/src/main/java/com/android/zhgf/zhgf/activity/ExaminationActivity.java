package com.android.zhgf.zhgf.activity;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.adapter.ChatAdapter;
import com.android.zhgf.zhgf.adapter.TopicAdapter;
import com.android.zhgf.zhgf.bean.OptionItem;
import com.android.zhgf.zhgf.bean.StudyInfoEntity;
import com.android.zhgf.zhgf.bean.TopicItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExaminationActivity extends BaseActivity  {
    private StudyInfoEntity studyInfoEntity;
    public Context context;
    private ListView topic_listview;
    private TopicAdapter mAdapter;
    private List<Map<String, TopicItem>> data = new ArrayList<Map<String, TopicItem>>();
    JSONObject data_json;
    String KEY = "list_topic_item";
    private Chronometer chronometer = null;
    private Button startBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);

        studyInfoEntity = (StudyInfoEntity) getIntent().getSerializableExtra("test");
        // 返回图标
        setBack();
        //
        //setTitle(studyInfoEntity.getTitleStr());
        setTitle("试卷一，政治测试");

        chronometer  = (Chronometer)findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());            //设置起始时间
        chronometer.setFormat("%s");
        startBtn = (Button)findViewById(R.id.start_btn);

        this.context = this;
        topic_listview = (ListView)findViewById(R.id.topic_quest_answer);
        mAdapter = new TopicAdapter(context, data);
        topic_listview.setAdapter(mAdapter);


        initData();
        getData();

        startBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               /* String t="";
                for (int i = 0; i < data.size(); i++) {
                    t = t+"第 "+(i+1)+" 题  ----------->您选择了【选项"
                            +data.get(i).get(KEY).getUserAnswerId().trim()+"】 \n";
                }*/

                chronometer.setBase(SystemClock.elapsedRealtime());            //设置起始时间
                chronometer.start();

                if(startBtn.getText().equals("结束考试")){
                    chronometer.stop();
                    startActivity(new Intent(ExaminationActivity.this, ResultActivity.class));
                    finish();//关闭页面
                }
                startBtn.setText("结束考试");
                //Toast.makeText(context, t, Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void initData(){
        try {
            data_json = new JSONObject();
            data_json.put("result", "success");
            JSONArray array = new JSONArray();
            for(int i=0;i<9;i++){
                JSONObject object = new JSONObject();
                switch (i){
                    case 0:
                        object.put("questionId", "一");
                        break;
                    case 1:
                        object.put("questionId", "二");
                        break;
                    case 2:
                        object.put("questionId", "三");
                        break;
                    case 3:
                        object.put("questionId", "四");
                        break;
                    case 4:
                        object.put("questionId", "五");
                        break;
                    case 5:
                        object.put("questionId", "六");
                        break;
                    case 6:
                        object.put("questionId", "七");
                        break;
                    case 7:
                        object.put("questionId", "八");
                        break;
                    case 8:
                        object.put("questionId", "九");
                        break;
                    default:
                        break;
                }

                object.put("question", "中国共产党成立于几几年？");
                object.put("answerId", "");
                object.put("userAnswerId", "3");

                JSONArray sarray = new JSONArray();
                JSONObject sobject1 = new JSONObject();
                sobject1.put("answerOption", "1");
                sobject1.put("answer", "1921");
                sarray.put(sobject1);
                JSONObject sobject2 = new JSONObject();
                sobject2.put("answerOption", "2");
                sobject2.put("answer", "1922");
                sarray.put(sobject2);
                JSONObject sobject3 = new JSONObject();
                sobject3.put("answerOption", "3");
                sobject3.put("answer", "1923");
                sarray.put(sobject3);
                JSONObject sobject4 = new JSONObject();
                sobject4.put("answerOption", "4");
                sobject4.put("answer", "1924");
                sarray.put(sobject4);

                object.put("optionList", sarray);

                array.put(object);
            }
            data_json.put("list", array);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void getData(){
        System.out.println(data_json.toString());
        data.clear();
        if(data_json.optString("result").equals("success")){

            if(data_json.optJSONArray("list")!=null){
                for(int i=0;i<data_json.optJSONArray("list").length();i++){
                    JSONObject object = data_json.optJSONArray("list").optJSONObject(i);
                    TopicItem topic = new TopicItem();
                    topic.setAnswerId(object.optString("answerId"));
                    topic.setQuestionId(object.optString("questionId"));
                    topic.setQuestion(object.optString("question"));
                    topic.setUserAnswerId(object.optString("userAnswerId"));

                    List<OptionItem> optionList = new ArrayList<OptionItem>();
                    for(int j=0;j<object.optJSONArray("optionList").length();j++){
                        JSONObject object_option = object.optJSONArray("optionList").optJSONObject(j);

                        OptionItem option = new OptionItem();
                        option.setAnswerOption(object_option.optString("answerOption"));
                        option.setAnswer(object_option.optString("answer"));

                        optionList.add(option);
                    }
                    topic.setOptionList(optionList);


                    Map<String, TopicItem> list_item = new HashMap<String, TopicItem>();
                    list_item.put(KEY, topic);
                    data.add(list_item);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }


}
