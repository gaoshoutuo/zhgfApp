package com.android.zhgf.zhgf.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.bean.NewsCommentEntity;
import com.android.zhgf.zhgf.util.DateUtil;

import java.util.ArrayList;

/**
 * Created by TZXX001 on 2017/10/24.
 */

public class CommentAdapter extends BaseAdapter {
    private ArrayList<NewsCommentEntity> newsCommentList;
    private Activity activity;
    private LayoutInflater inflater = null;
    private int height = 0;
    public CommentAdapter(Activity activity,ArrayList<NewsCommentEntity> list){
        this.activity = activity;
        this.newsCommentList = list;
        this.inflater = LayoutInflater.from(activity);

    }
    @Override
    public int getCount() {
        return newsCommentList == null ? 0 : newsCommentList.size();
    }

    @Override
    public NewsCommentEntity getItem(int position) {
        if (newsCommentList != null && newsCommentList.size() != 0) {
            return newsCommentList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        CommentAdapter.ViewHolder mHolder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.comment_item, null);
            mHolder = new CommentAdapter.ViewHolder();
            mHolder.nameTv = (TextView)view.findViewById(R.id.nameTv);
            mHolder.commentTv = (TextView)view.findViewById(R.id.commentTv);
            mHolder.timeTv = (TextView)view.findViewById(R.id.timeTv);

            view.setTag(mHolder);
        } else {
            mHolder = (CommentAdapter.ViewHolder) view.getTag();
        }
        mHolder.nameTv.setText(newsCommentList.get(position).getUserNameStr());
        mHolder.commentTv.setText(newsCommentList.get(position).getContentStr());
        String timeDistance = DateUtil.getDistanceTime(Long.parseLong(newsCommentList.get(position).getLastupdateStr())*1000L, System.currentTimeMillis());
        mHolder.timeTv.setText(timeDistance);
        view.measure(0, 0);
        height += view.getMeasuredHeight();

        return view;
    }
    public int getHeight(){
        return height;
    }
    static class ViewHolder {
        LinearLayout studyLLayout;
        // name
        TextView nameTv;
        // comment
        TextView commentTv;
        // time
        TextView timeTv;

    }
}
