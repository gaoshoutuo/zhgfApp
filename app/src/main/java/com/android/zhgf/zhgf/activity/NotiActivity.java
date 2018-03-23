package com.android.zhgf.zhgf.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.bean.Lesson;
import com.android.zhgf.zhgf.database.NotificationTable;
import com.android.zhgf.zhgf.database.bean.NotificationBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class NotiActivity extends BaseActivity {
RecyclerView recyclerView;
   List <NotificationBean>lists=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);
      editList();
        recyclerView = (RecyclerView) findViewById(R.id.noti_recycler);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        RNAdapter rnAdapter = new RNAdapter(lists);
        recyclerView.setAdapter(rnAdapter);
        setTitle("通知公告");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 统一加到shared里面
     */


void getSave(Intent intent){
    String title= intent.getStringExtra("title");
    String content=intent.getStringExtra("content");
    SharedPreferences.Editor edit= (SharedPreferences.Editor) getSharedPreferences("notification",MODE_APPEND);
    edit.putString("title",title);
    edit.putString("content",content);
    edit.putBoolean("isread",false);
    edit.apply();
    lists.add(null);
}
 void editList(){
     lists= NotificationTable.select1();
 }


    class RNAdapter extends RecyclerView.Adapter<RNAdapter.ViewHolderRN>{

        private List notiList;
            public RNAdapter(List list){
               this.notiList=list;
            }
        @Override
        public ViewHolderRN onCreateViewHolder(ViewGroup parent, int viewType) {
         View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noti,parent,false);
            ViewHolderRN viewHolderRN=new ViewHolderRN(view);
            Badge badge=new QBadgeView(NotiActivity.this).bindTarget(viewHolderRN.t2);
            badge.setBadgeGravity(Gravity.END|Gravity.TOP);

            badge.setBadgeNumber(1);
            viewHolderRN.badge=badge;
            viewHolderRN.t2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    badge.setBadgeNumber(0);
                    Toast.makeText(NotiActivity.this,"dd",Toast.LENGTH_SHORT).show();
                }
            });
            viewHolderRN.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    badge.setBadgeNumber(0);
                    Toast.makeText(NotiActivity.this,"dd",Toast.LENGTH_SHORT).show();
                }
            });
            return viewHolderRN;
        }

        @Override
        public void onBindViewHolder(ViewHolderRN holder, int position) {
            NotificationBean notificationBean= (NotificationBean) notiList.get(position);
            holder.t1.setText(notificationBean.getTitle()+" ");
            holder.t2.setText(null);

        }


        @Override
        public int getItemCount() {
            return lists.size();
        }

        class ViewHolderRN extends RecyclerView.ViewHolder{
            LinearLayout l1;
            TextView t1,t2;
            View view;
            Badge badge;

             public ViewHolderRN(View itemView) {
                 super(itemView);
                 view=itemView;
                 l1=itemView.findViewById(R.id.noti_lin);
                 t1=itemView.findViewById(R.id.content_noti);
                 t2=itemView.findViewById(R.id.title_noti);
               // badge=new QBadgeView(NotiActivity.this).bindTarget(l1);

             }
         }
    }
}







































