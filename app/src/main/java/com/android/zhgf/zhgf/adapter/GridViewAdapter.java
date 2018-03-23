package com.android.zhgf.zhgf.adapter;

import android.content.Context;

import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.android.zhgf.zhgf.R;

import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.bean.GridPicture;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by colorful on 2017-10-09.
 */

public class GridViewAdapter extends BaseAdapter {
List<GridPicture>image;//大概是需要URL的json吧
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    private LayoutInflater layoutInflater;
    public GridViewAdapter(List<GridPicture> imagePath, Context context) {
        layoutInflater= LayoutInflater.from(context);
        this.image = imagePath;
    }

    @Override
    public int getCount() {
        return image.size();
    }

    @Override
    public Object getItem(int position) {
        return image.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

   int lastPosition=-1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridPicture gp= (GridPicture) getItem(position);
        ViewHolder viewHolder;
        View view;
        if(convertView==null){
            view= layoutInflater.inflate(R.layout.item_picture_gridview,null,false);
            viewHolder=new ViewHolder();
            viewHolder.imageView=(ImageView)view.findViewById(R.id.image_gridview);
            view.setTag(viewHolder);
        }else {
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }
        ViewGroup.LayoutParams pa=new ViewGroup.LayoutParams((int)(width/3.1),height/5);
        view.setLayoutParams(pa);
       /* if(gp.getBitmap()!=null){
            viewHolder.imageView.setAdjustViewBounds(true);//只有设置成true才可以用
            viewHolder.imageView.setImageBitmap(gp.getBitmap());
            Glide.with(AppApplication.getApp()).load(gp.getUrl()).into(viewHolder.imageView);
        }else {
            viewHolder.imageView.setImageResource(R.drawable.adt);
        }*/
            Log.e("position",position+"");
           Glide.with(AppApplication.getApp()).load(gp.getUrl()).into(viewHolder.imageView);
         if(position==0){
                       Glide.with(AppApplication.getApp()).load(gp.getUrl())
        .placeholder(R.drawable.ic_android_black_24dp)
                .error(R.drawable.ic_android_black_24dp)
                .diskCacheStrategy(DiskCacheStrategy.ALL).
                   into(viewHolder.imageView);
          }
        //点击看大图还是需要流量的  没有做缓存本地的操作
        return view;
    }

       WindowManager manager = (WindowManager) AppApplication.getApp().getSystemService(Context.WINDOW_SERVICE);
      final int height = manager.getDefaultDisplay().getHeight();
      final int width = manager.getDefaultDisplay().getWidth();
    //其实这段可以拆解
    public void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 3;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加

        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);//c初始化 非置零
            // 获取item的高度和
            totalHeight += height/5;
            Log.e("高度",totalHeight+"");//706 1006 1306
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置参数
        listView.setLayoutParams(params);
    }


    static class ViewHolder{
        ImageView imageView;
    }
}
