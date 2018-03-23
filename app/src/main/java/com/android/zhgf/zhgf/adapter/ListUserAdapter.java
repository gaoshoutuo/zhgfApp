package com.android.zhgf.zhgf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.core.WSClient;

import java.util.LinkedList;


/**
 * Created by zhuyt on 2017/10/24.
 */

public class ListUserAdapter extends BaseAdapter {

    private LinkedList<WSClient.OnlineUser> lsRemoteUsers;
    public void setData(LinkedList<WSClient.OnlineUser> p){
        lsRemoteUsers.clear();
        lsRemoteUsers = p;
    }
    private Context mContext;
    private LayoutInflater mInflater;
    public ListUserAdapter(Context pCtx, LinkedList<WSClient.OnlineUser> pRemoteUsers){
        mContext = pCtx;
        lsRemoteUsers = pRemoteUsers;
        mInflater = LayoutInflater.from(pCtx);
    }



    @Override
    public int getCount() {
        return lsRemoteUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        displayUser mDu=null;
        if (view==null){
            mDu = new displayUser();
            view = mInflater.inflate(R.layout.list_user_item,null);
            mDu.username = view.findViewById(R.id.lsiUsername);
            mDu.socketid = view.findViewById(R.id.lsiSocketid);
            mDu.roomname = view.findViewById(R.id.lsRoom);
            view.setTag(mDu);
        }else{
            mDu = (displayUser)view.getTag();
        }
        if (i<=lsRemoteUsers.size()-1) {
            WSClient.OnlineUser ttUser = lsRemoteUsers.get(i);
            mDu.username.setText(lsRemoteUsers.get(i).getUsername());
            mDu.socketid.setText(lsRemoteUsers.get(i).getSocketID());
            mDu.roomname.setText(lsRemoteUsers.get(i).getRoomname());
        }
        mDu.socketid.setVisibility(View.GONE);
        mDu.roomname.setVisibility(View.GONE);
        return view;
    }


    public class displayUser{
        public TextView username;
        public TextView socketid;
        public TextView roomname;
    }
}
