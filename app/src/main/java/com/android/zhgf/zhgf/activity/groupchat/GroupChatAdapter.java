package com.android.zhgf.zhgf.activity.groupchat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.database.bean.Chat;

import java.util.ArrayList;
import java.util.List;

//import com.github.ggggxiaolong.xmpp.datasource.model.Chat;

public final class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {

    private List<Chat> mData = new ArrayList<>();

    public void setData(@NonNull List<Chat> data){
        mData.clear();
        mData.addAll(data);
    }

    public void addData(Chat chat){
        mData.add(0,chat);
        notifyItemInserted(0);
    }

    void addData(@NonNull List<Chat> data){
        mData.addAll(data);
        notifyItemInserted(mData.size() -1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = View.inflate(parent.getContext(), R.layout.item_chat_me, null);
            return new ViewHolder(view);
        } else {
            View view = View.inflate(parent.getContext(), R.layout.item_chat_other, null);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = mData.get(position);
        if (chat.isHolder())
            holder.avatar.setImageResource(R.drawable.chatpic);
        else
            holder.avatar.setImageResource(R.mipmap.ic_launcher);
        holder.content.setText(chat.getContent());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).isHolder()) {
            return 0;
        }
        return 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
