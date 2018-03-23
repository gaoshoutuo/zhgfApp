package com.android.zhgf.zhgf.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.adapter.ChatAdapter;
import com.android.zhgf.zhgf.bean.ItemModel;
import com.android.zhgf.zhgf.bean.msgModel;
import com.android.zhgf.zhgf.constant.Constant;

import org.greenrobot.eventbus.ThreadMode;

public class ChatActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;

    private String txtContent;
    private String form, to;

    private EditText et_message;//文本输入框
    private TextView tv_send;//发送标签
    private ImageView chat_more;//用于切换键盘与功能面板
    private TextView send_loc;//发送位置标签

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 返回图标
        setBack();
        // 右图标
        //setRight(R.drawable.icons8_people48);
        setTitle("聊天");
        findView();
        init();
        initEditText();

    }

    /**
     * 订阅接收消息
     * Subscribe，其含义为订阅者。
     * 在其内传入了threadMode，我们定义为ThreadMode.MAIN，其含义是该方法在UI线程完成。
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recMsgEventBus(msgModel msg) {
        if (msg.getFromUser().equals(to)) {
            if (msg.getType() == Constant.MSG_TYPE_TEXT) {
                //在最后插入一条item，包括布局，聊天信息
                adapter.insertLastItem(new ItemModel(ItemModel.LEFT_TEXT, msg));
            }
            //滑动到最后
            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }

    /**
     * 发送文本消息
     */
    private void sendTextMessage() {
        if (txtContent.equals("")) {
            return;
        }

       /* final String message = form + Constant.SPLIT + to + Constant.SPLIT
                + Constant.MSG_TYPE_TEXT + Constant.SPLIT
                + txtContent;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SmackUtils.getInstance().sendMessage(message, to);
            }
        }).start();*/

        //在聊天列表插入一条文本消息
        msgModel msgModel = new msgModel();
        msgModel.setToUser(to);
        msgModel.setType(Constant.MSG_TYPE_TEXT);
        msgModel.setContent(txtContent);
        adapter.insertLastItem(new ItemModel(ItemModel.RIGHT_TEXT, msgModel));
        //insertSession(msgModel);
        adapter.insertLastItem(new ItemModel(ItemModel.LEFT_TEXT, msgModel));
        //滑动到最后,清空输入框
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        et_message.setText("");
        /*InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_message.getWindowToken(), 0);*/

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_message:
                break;
            case R.id.tv_send:
                sendTextMessage();//发送文本消息
                break;
            case R.id.chat_more:
                break;
            default:
                break;
        }
    }
    private void findView() {
        recyclerView = (RecyclerView) findViewById(R.id.recylerView);
        et_message = (EditText) findViewById(R.id.et_message);
        tv_send = (TextView) findViewById(R.id.tv_send);
        chat_more = (ImageView) findViewById(R.id.chat_more);

    }

    private void init() {
        //注册EventBus
        EventBus.getDefault().register(this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter = new ChatAdapter(mContext));
        et_message.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        chat_more.setOnClickListener(this);
        txtContent = "";
        /*form = PreferencesUtils.getInstance().getString("username");
        //接收数据
        Bundle bundle = this.getIntent().getExtras();
        to = bundle.getString("to_user");*/
    }

    private void initEditText() {
        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //去掉空格的消息
                txtContent = s.toString().trim();
            }
        });
    }
}
