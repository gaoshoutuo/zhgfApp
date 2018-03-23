package com.android.zhgf.zhgf.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.interfaceL.ViewHolderInterface;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TZXX001 on 2017/10/9.
 */

public class MenuAdapter extends BaseAdapter {

    public ArrayList<HashMap<String,Object>> menuList;
    private Activity activity;
    private LayoutInflater inflater = null;
    private int height;
    private GridView mGv;
    private int intRowNum;

    public MenuAdapter(Activity activity, ArrayList<HashMap<String,Object>> menuList,GridView gv,int intRowNum){
        this.activity = activity;
        this.menuList = menuList;
        this.mGv = gv;
        this.intRowNum = intRowNum;
        inflater = LayoutInflater.from(activity);
    }
    @Override
    public int getCount() {
        return this.menuList == null ? 0 : this.menuList.size();
    }

    @Override
    public Object getItem(int position) {
        if (menuList != null && menuList.size() != 0) {
            return menuList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        MenuAdapter.ViewHolder mHolder;
        View view = convertView;
        if (view == null) {
            if(intRowNum == 3){
                view = inflater.inflate(R.layout.menu_item, null);
            }else {
                view = inflater.inflate(R.layout.menubottom_item, null);
            }

            mHolder = new MenuAdapter.ViewHolder();
            mHolder.menuImageIv = (ImageView)view.findViewById(R.id.menuImageIv);
            mHolder.menuNameTv = (TextView)view.findViewById(R.id.menuNameTv);
            mHolder.badgeTv = (TextView)view.findViewById(R.id.icon_badge2);


            view.setTag(mHolder);
        }else{
            mHolder = (MenuAdapter.ViewHolder) view.getTag();
        }
        HashMap<String,Object> map = (HashMap)getItem(position);
        mHolder.menuImageIv.setImageResource((int)map.get("itemImage"));
        mHolder.menuNameTv.setText((String)map.get("itemText"));


        switch (position){
            case 0:
                int leave=(int)map.get("badgenumber");
                if(leave!=0){
                    Log.e("leave",leave+"");
                    mHolder.badgeTv.setText(leave+"");
                    mHolder.badgeTv.setVisibility(View.VISIBLE);
                }
                break;
            default:break;
        }
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                mGv.getHeight()/intRowNum);
        view.setLayoutParams(param);

        //ViewGroup.LayoutParams itemparams = view.getLayoutParams();
        //itemparams.height = height;
        //view.setLayoutParams(itemparams);
        return view;
    }



    static class ViewHolder {
         ImageView menuImageIv;
         TextView menuNameTv;
        public TextView badgeTv;

    }

    public String transetCatId(int catId){
        String name=null;
        switch (catId){
            case 1:
                name="政治教育";
                break;
            case 2:
                name="在线学习";
                break;
            case 3:
                name="工作动态";
                break;
            case 4:
                name="新闻资讯";
                break;
            case 5:
                name="请销假";
                break;
            default:break;

        }
        return name;
    }
}
