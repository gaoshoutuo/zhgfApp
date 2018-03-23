package com.android.zhgf.zhgf.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.activity.NewsDetailsActivity;
import com.android.zhgf.zhgf.bean.NewsCommentEntity;
import com.android.zhgf.zhgf.bean.NewsInfoEntity;
import com.android.zhgf.zhgf.util.DateUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZXX001 on 2017/10/25.
 */

public class MutiLayoutAdapter extends BaseAdapter {

    //定义两个类别标志
    private static final int TYPE_NEWSINFO = 0;
    private static final int TYPE_NEWSCOMMENT = 1;
    private static final int TYPE_STRING = 2;
    private Context mContext;
    private ArrayList<Object> mData = null;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;

    public MutiLayoutAdapter(Context mContext,ArrayList<Object> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    //多布局的核心，通过这个判断类别
    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) instanceof NewsInfoEntity) {
            return TYPE_NEWSINFO;
        } else if (mData.get(position) instanceof NewsCommentEntity) {
            return TYPE_NEWSCOMMENT;
        } else if (mData.get(position) instanceof String) {
            return TYPE_STRING;
        } else {
            return super.getItemViewType(position);
        }
    }

    //类别数目
    @Override
    public int getViewTypeCount() {
        return 3;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        ViewHolder3 holder3 = null;
        if(convertView == null){
            switch (type){
                case TYPE_NEWSINFO:
                    holder1 = new ViewHolder1();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.newslist_item, parent, false);
                    holder1.newsInfo_layout = (LinearLayout)convertView.findViewById(R.id.newsInfo_layout);
                    holder1.right_image = (ImageView)convertView.findViewById(R.id.right_image);
                    holder1.item_title = (TextView)convertView.findViewById(R.id.item_title);
                    holder1.item_source = (TextView)convertView.findViewById(R.id.item_source);
                    holder1.list_item_local = (TextView)convertView.findViewById(R.id.list_item_local);//？
                    holder1.comment_count = (TextView)convertView.findViewById(R.id.comment_count);
                    holder1.publish_time = (TextView)convertView.findViewById(R.id.publish_time);
                    holder1.item_abstract = (TextView)convertView.findViewById(R.id.item_abstract);
                    holder1.item_image_layout = (LinearLayout)convertView.findViewById(R.id.item_image_layout);
                    holder1.item_image_0 = (ImageView)convertView.findViewById(R.id.item_image_0);
                    holder1.item_image_1 = (ImageView)convertView.findViewById(R.id.item_image_1);
                    holder1.item_image_2 = (ImageView)convertView.findViewById(R.id.item_image_2);
                    holder1.hits =  (TextView)convertView.findViewById(R.id.hits_tv);
                    convertView.setTag(R.id.Tag_NewsInfo,holder1);
                    break;
                case TYPE_NEWSCOMMENT:
                    holder2 = new ViewHolder2();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.newscomment_item, parent, false);
                    holder2.nameTv = (TextView)convertView.findViewById(R.id.nameTv);
                    holder2.commentTv = (TextView)convertView.findViewById(R.id.commentTv);
                    holder2.timeTv = (TextView)convertView.findViewById(R.id.timeTv);
                    holder2.directionTv =(TextView)convertView.findViewById(R.id.directionTv);
                            convertView.setTag(R.id.Tag_NewsComment,holder2);
                    break;
                case TYPE_STRING:
                    holder3 = new ViewHolder3();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.commenttxt_item, parent, false);
                    holder3.commentTv = (TextView)convertView.findViewById(R.id.commentTv);
                    convertView.setTag(R.id.Tag_NewsCommentTxt,holder3);
                    break;
                default:
                    break;
            }
        }else{
            switch (type){
                case TYPE_NEWSINFO:
                    holder1 = (ViewHolder1) convertView.getTag(R.id.Tag_NewsInfo);
                    break;
                case TYPE_NEWSCOMMENT:
                    holder2 = (ViewHolder2) convertView.getTag(R.id.Tag_NewsComment);
                    break;
                case TYPE_STRING:
                    holder3 = (ViewHolder3) convertView.getTag(R.id.Tag_NewsCommentTxt);
                    break;
                default:
                    break;
            }
        }

        Object obj = mData.get(position);
        //设置下控件的值
        switch (type){
            case TYPE_NEWSINFO:
                NewsInfoEntity news = (NewsInfoEntity) obj;
                if(news != null){
                    holder1.item_title.setText(news.getTitle());
                    holder1.item_source.setText(news.getSource());
                    int commentNum = 0;
                    int hits = 0;
                    try {
                        commentNum = Integer.parseInt(news.getCommentNum());
                        holder1.comment_count.setText("评论 " + commentNum);
                    } catch (NumberFormatException e) {
                        holder1.comment_count.setText("评论 0");
                    }
                    try {
                        hits = Integer.parseInt(news.getHits());
                        holder1.hits.setText("" + hits);
                    } catch (NumberFormatException e) {
                        holder1.hits.setText("0");
                    }

                    //mHolder.postTimeIv.setVisibility(View.VISIBLE);

                    String timeDistance = DateUtil.getDistanceTime(Long.parseLong(news.getInputtime())*1000L, System.currentTimeMillis());
                    holder1.publish_time.setText(timeDistance);
                    List<String> imgUrlList = news.getPicList();


                    //mHolder.item_abstract.setVisibility(View.GONE);
                    String url1 = "http://gyfpjh.com/uploads/image/201612/1480921212113664-lp.jpg";
                    String url2 = "https://s9.rr.itc.cn/r/wapChange/20175_28_17/a0c54p8306958631745.jpg";
                    String url3 = "http://img1.gtimg.com/news/pics/hv1/197/152/2205/143419082.jpg";
                    //imgUrlList.clear();
        /*imgUrlList = new ArrayList<String>();
        if(position%2 == 0){
            imgUrlList.add(url1);
            imgUrlList.add(url2);
            imgUrlList.add(url3);
        }else {
            imgUrlList.add(url1);
        }*/

                    if(imgUrlList !=null && imgUrlList.size() !=0){
                        // 一张图
                        if(imgUrlList.size() == 1){
                            holder1.item_image_layout.setVisibility(View.GONE);
                            holder1.right_image.setVisibility(View.VISIBLE);
                            imageLoader.displayImage(imgUrlList.get(0), holder1.right_image, options);

                        }else{
                            holder1.item_image_layout.setVisibility(View.VISIBLE);
                            holder1.right_image.setVisibility(View.GONE);
                            imageLoader.displayImage(imgUrlList.get(0), holder1.item_image_0, options);
                            imageLoader.displayImage(imgUrlList.get(1), holder1.item_image_1, options);
                            imageLoader.displayImage(imgUrlList.get(2), holder1.item_image_2, options);
                        }
                    }else{
                        holder1.right_image.setVisibility(View.GONE);
                        holder1.item_image_layout.setVisibility(View.GONE);
                    }
                }
                break;
            case TYPE_NEWSCOMMENT:
                NewsCommentEntity newsComment = (NewsCommentEntity) obj;
                if(newsComment != null){
                    if(newsComment.getIpnameStr() != null ){
                        holder2.nameTv.setText(newsComment.getIpnameStr() + " " + newsComment.getIpuserStr());
                    }else {
                        holder2.nameTv.setText(newsComment.getIpStr());
                    }
                    holder2.commentTv.setText(newsComment.getContentStr());
                    String timeDistance = DateUtil.getDistanceTime(Long.parseLong(newsComment.getLastupdateStr())*1000L, System.currentTimeMillis());
                    holder2.timeTv.setText(timeDistance);
                    if(newsComment.getDirectionStr().equals("1")){
                        holder2.directionTv.setText("正");
                        holder2.directionTv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.like));
                    }else if(newsComment.getDirectionStr().equals("2")){
                        holder2.directionTv.setText("反");
                        holder2.directionTv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.unlike));
                    }else if(newsComment.getDirectionStr().equals("3")){
                        holder2.directionTv.setText("中");
                        holder2.directionTv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.normal));
                    }else {
                        holder2.directionTv.setText("");
                        //holder2.directionTv.setVisibility(View.GONE);
                    }
                    //holder2.directionTv.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
        return convertView;
    }


    //两个不同的ViewHolder
    private static class ViewHolder1{
        LinearLayout newsInfo_layout;
        //title
        TextView item_title;
        //图片源
        TextView item_source;
        //类似推广之类的标签
        TextView list_item_local;
        //评论数量
        TextView comment_count;
        //评论数量
        TextView hits;
        //发布时间
        TextView publish_time;
        //新闻摘要
        TextView item_abstract;
        //右上方TAG标记图片
        ImageView alt_mark;
        //右边图片
        ImageView right_image;
        //3张图片布局
        LinearLayout item_image_layout; //3张图片时候的布局
        ImageView item_image_0;
        ImageView item_image_1;
        ImageView item_image_2;
        ImageView postTimeIv;

    }

    private static class ViewHolder2{
        // name
        TextView nameTv;
        // comment
        TextView commentTv;
        // time
        TextView timeTv;

        TextView directionTv;
    }
    private static class ViewHolder3{
        // name
        TextView commentTv;
    }
}