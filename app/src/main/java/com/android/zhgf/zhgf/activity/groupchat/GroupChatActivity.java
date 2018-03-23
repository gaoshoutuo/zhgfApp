package com.android.zhgf.zhgf.activity.groupchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.activity.groupchat.GroupChatAdapter;
import com.android.zhgf.zhgf.activity.groupchat.GroupChatPresenter;
import com.android.zhgf.zhgf.activity.groupchat.GroupChatView;
import com.android.zhgf.zhgf.database.bean.Chat;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.github.ggggxiaolong.xmpp.datasource.model.Chat;

public class GroupChatActivity extends AppCompatActivity implements GroupChatView {

/*    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;*/
    @BindView(R.id.list)
    RecyclerView mList;
    @BindView(R.id.content)
    EditText mContent;
    @BindView(R.id.send)
    TextView mSend;
    @BindView(R.id.action_panel)
    LinearLayout mActionPanel;
    @BindView(R.id.root)
    CoordinatorLayout mRoot;
    @BindView(R.id.swipeReFreshLayout)
    SwipeRefreshLayout mSwipeReFresh;

    public static final String FROM_ID = "GroupActivity.fromId";
    private GroupChatAdapter mAdapter;
    private GroupChatPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        ButterKnife.bind(this);
        init();
        mPresenter.setOwnerId("user3");
        mPresenter.setFromId(getIntent().getStringExtra(FROM_ID));
        mPresenter.initMultiUserChatListener();
        mPresenter.onLoad();
    }

    /**
     * 更新数据
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mPresenter.setFromId(intent.getStringExtra(FROM_ID));
        mPresenter.onLoad();
    }

    private void init() {
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new GroupChatAdapter();
        mList.setAdapter(mAdapter);
        LinearLayoutManager layout = new LinearLayoutManager(getApplicationContext());
        layout.setReverseLayout(true);
        layout.setStackFromEnd(false);
        mList.setLayoutManager(layout);

        mSwipeReFresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mPresenter = new GroupChatPresenter();
        mPresenter.onAttach(this);

        mSwipeReFresh.setOnRefreshListener(() -> mPresenter.loadMore());

    }

    @OnClick(R.id.send)
    public void send() {
        String content = mContent.getText().toString();
        //虑空
        if (content.trim().length() < 1) {
            return;
        }
        mPresenter.send(content);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadDate(List<Chat> chats) {
        mAdapter.setData(chats);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void scrollToBottom() {
        mList.scrollToPosition(0);
    }

    public void scrollTo(int size) {
        mList.scrollToPosition(mAdapter.getItemCount() - size);
    }

    @Override
    public void addData(Chat chat) {
        mAdapter.addData(chat);
        scrollToBottom();
    }

    @Override
    public void addData(List<Chat> chats) {
        mAdapter.addData(chats);
        if (chats.size() < 5) {
            scrollTo(1);
        } else {
            scrollTo(chats.size() - 3);
        }
    }

    @Override
    public void sendError() {
        Toast.makeText(getApplicationContext(), "发送错误", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void sendSuccess() {
        mContent.setText("");
        scrollToBottom();
    }

    @Override
    public void cancelRefresh() {
        mSwipeReFresh.setRefreshing(false);
    }

    @Override
    public void noMoreChat() {
        TastyToast.makeText(getApplicationContext(), "没有更多", TastyToast.LENGTH_SHORT, TastyToast.DEFAULT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
