package com.android.zhgf.zhgf.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.adapter.GridLessonAdapter;
import com.android.zhgf.zhgf.bean.Lesson;
import com.android.zhgf.zhgf.utils.MyOkhttp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LessonListActivity extends BaseActivity {

    private static RecyclerView recyclerview;
    private CoordinatorLayout coordinatorLayout;
    private GridLessonAdapter mAdapter;
    private List<Lesson> lessonList;
    private GridLayoutManager mLayoutManager;
    private int lastVisibleItem;
    private int page=1;
    private ItemTouchHelper itemTouchHelper;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_list);
        // 返回图标
        setBack();

        setTitle("在线学习");
        initView();
        setListener();

        new GetData().execute("http://112.11.127.22:88/index.php?m=app&c=index&a=comments&catid=19&id=7354&modelid=1");
    }

    private void initView(){
        lessonList =  new ArrayList();
        recyclerview=(RecyclerView)findViewById(R.id.grid_recycler);
        mLayoutManager=new GridLayoutManager(LessonListActivity.this,2,GridLayoutManager.VERTICAL,false);
        recyclerview.setLayoutManager(mLayoutManager);

        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.grid_swipe_refresh) ;
        //调整SwipeRefreshLayout的位置
        swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));


    }

    private void setListener(){

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page=1;
                new GetData().execute("http://112.11.127.22:88/index.php?m=app&c=index&a=comments&catid=19&id=7354&modelid=1");
            }
        });



        //recyclerview滚动监听
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //0：当前屏幕停止滚动；1时：屏幕在滚动 且 用户仍在触碰或手指还在屏幕上；2时：随用户的操作，屏幕上产生的惯性滑动；
                // 滑动状态停止并且剩余少于两个item时，自动加载下一页
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem +2>=mLayoutManager.getItemCount()) {
                    new GetData().execute("http://112.11.127.22:88/index.php?m=app&c=index&a=comments&catid=19&id=7354&modelid=1");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                获取加载的最后一个可见视图在适配器的位置。
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

            }
        });
    }


    private class GetData extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //设置swipeRefreshLayout为刷新状态
            swipeRefreshLayout.setRefreshing(true);
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
                    jsonObject = new JSONObject(result);
                    jsonData = jsonObject.getString("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int i = 0;i< 10;i++){
                    lessonList.add(new Lesson("http://112.11.127.22:88/uploadfile/2017/1113/20171113044716439.jpg","军事理论"));
                    lessonList.add(new Lesson("http://112.11.127.22:88/uploadfile/2017/1108/20171108045107439.jpg","军事理论"));
                    lessonList.add(new Lesson("http://112.11.127.22:88/uploadfile/2017/1108/20171108044315258.jpg","军事理论"));
                }
                if(mAdapter==null){
                    recyclerview.setAdapter(mAdapter = new GridLessonAdapter(LessonListActivity.this,lessonList));

                    mAdapter.setOnItemClickListener(new GridLessonAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view) {
                            int position=recyclerview.getChildAdapterPosition(view);
                            startActivity(new Intent(LessonListActivity.this,StudyLessonActivity.class));
                        }

                        @Override
                        public void onItemLongClick(View view) {
                            //itemTouchHelper.startDrag(recyclerview.getChildViewHolder(view));
                        }
                    });

                    //itemTouchHelper.attachToRecyclerView(recyclerview);
                }else{
                    mAdapter.notifyDataSetChanged();
                }
            }
            //停止swipeRefreshLayout加载动画
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    /*private void initView(){

        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int widthCenterInt = dm.widthPixels/3;
        int widthBottomInt = dm.widthPixels/5;

        lessonGv = (GridView)findViewById(R.id.lessonGv);
        lessonGv.setColumnWidth(widthBottomInt);

        ArrayList<HashMap<String,Object>> lessonlst = new ArrayList<HashMap<String,Object>>();
        String[] lessonIconName = { "军事理论","国家安全", "军事理论", "国家安全","军事理论","国家安全", "军事理论", "国家安全","军事理论","国家安全", "军事理论", "国家安全"};
        int[] lessonIcon = {R.mipmap.lesson1,R.mipmap.lesson1,R.mipmap.lesson1,R.mipmap.lesson1,R.mipmap.lesson1,R.mipmap.lesson1,R.mipmap.lesson1,R.mipmap.lesson1,R.mipmap.lesson1,R.mipmap.lesson1,R.mipmap.lesson1,R.mipmap.lesson1};
        for(int i=0;i<lessonIcon.length;i++){
            HashMap<String,Object> map = new HashMap<String,Object>();
            map = new HashMap<String,Object>();
            map.put("itemImage", lessonIcon[i]);
            map.put("itemText", lessonIconName[i]);
            lessonlst.add(map);
        }

        MenuAdapter lesssonAdapter = new MenuAdapter(LessonListActivity.this,lessonlst,lessonGv,5);
        lessonGv.setAdapter(lesssonAdapter);

    }*/
}
