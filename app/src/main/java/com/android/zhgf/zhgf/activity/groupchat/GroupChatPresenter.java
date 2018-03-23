package com.android.zhgf.zhgf.activity.groupchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.zhgf.zhgf.activity.groupchat.GroupChatView;
import com.android.zhgf.zhgf.base.BasePresenter;
import com.android.zhgf.zhgf.database.ChatColumns;
import com.android.zhgf.zhgf.database.bean.Chat;
import com.android.zhgf.zhgf.utils.CommonField;
import com.android.zhgf.zhgf.utils.ObjectHolder;
import com.android.zhgf.zhgf.utils.ThreadUtil;
import com.android.zhgf.zhgf.utils.XMPPUtil;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static android.text.TextUtils.isEmpty;
import static com.android.zhgf.zhgf.utils.CommonField.HOLDER_ME;
import static com.android.zhgf.zhgf.utils.CommonField.HOLDER_OTHER;
import static com.android.zhgf.zhgf.utils.CommonField.INTENT_CONTENT;
import static com.android.zhgf.zhgf.utils.CommonField.INTENT_FORM;
import static com.android.zhgf.zhgf.utils.CommonField.INTENT_HOLDER;
import static com.android.zhgf.zhgf.utils.CommonField.INTENT_TIME;

//import com.github.ggggxiaolong.xmpp.datasource.model.Chat;
//import com.github.ggggxiaolong.xmpp.datasource.model.Contact;
//import com.github.ggggxiaolong.xmpp.datasource.model.ImmutableChat;

/**
 * @author mrtan
 * @version v1.0
 *          date 10/6/16
 *          增加-----
 *          判断是否还有数据
 */

final class GroupChatPresenter extends BasePresenter<GroupChatView> {
    private String fromId;
    private LocalBroadcastManager mBroadcastManager;
    private Intent mIntent;
    private long lastTime;
    private MultiUserChat multiUserChat;
    private String ownerId;

    @Override
    public void onAttach(GroupChatView view) {
        super.onAttach(view);
        mBroadcastManager = ObjectHolder.getBroadcastManager();
        mBroadcastManager.registerReceiver(mChatReceiver, new IntentFilter(CommonField.RECEIVER_CHAT));
        mIntent = new Intent(CommonField.RECEIVER_CHAT);
    }
    // 初始化监听
    public void initMultiUserChatListener() {
        multiUserChat = XMPPUtil.getMultiUserChat(fromId);
        multiUserChat.removeMessageListener(messageListener);
        multiUserChat.addMessageListener(messageListener);
        multiUserChat.addInvitationRejectionListener(new InvitationRejectionListener() {
            @Override
            public void invitationDeclined(final String invitee, final String reason) {
                Log.d("test", invitee + "拒绝" + reason + "-----------");
            }
        });
    }
    MessageListener messageListener = new MessageListener() {
        @Override
        public void processMessage(final Message message) {
            //当消息返回为空的时候，表示用户正在聊天窗口编辑信息并未发出消息
            if (!TextUtils.isEmpty(message.getBody())) {
                message.getFrom();
                String body = message.getBody();
                message.setBody(message.getBody());
                final String from = message.getFrom();
                String fromId = from.substring(0, from.indexOf('/'));
                String fromUser = from.substring(from.indexOf('/') + 1,from.length());
                if(fromUser.equals(ownerId)){
                    return;
                }
                long time = new Date().getTime();

                mIntent.putExtra(INTENT_FORM, fromId);
                mIntent.putExtra(INTENT_CONTENT, body);
                mIntent.putExtra(INTENT_TIME, time);
                mIntent.putExtra(INTENT_HOLDER, HOLDER_OTHER);
                mBroadcastManager.sendBroadcast(mIntent);

                ThreadUtil.runONWorkThread(() -> {
                /*ImmutableChat chat1 = ImmutableChat.builder().from_id(fromId).content(body).holder(false).time(time).build();
                com.android.zhgf.zhgf.datasource.model.Chat.insert(chat1);*/
                    com.android.zhgf.zhgf.database.bean.Chat chat1 = new com.android.zhgf.zhgf.database.bean.Chat();
                    chat1.setFromId(fromId);
                    chat1.setContent(body);
                    chat1.setHolder(HOLDER_OTHER);
                    chat1.setTime(time);
                    ChatColumns.insert(chat1);
                });
            }
        }
    };
    void setFromId(String from) {
        fromId = from;
        check();
    }
    void setOwnerId(String ownerId){
        this.ownerId = ownerId;
    }
    void onLoad() {
        check();
        //Contact mContact = Contact.query(fromId);

        mView.setTitle("user2");
        List<Chat> chats = ChatColumns.byFrom(fromId, 0, new Date().getTime());
        //List<Chat> chats = new ArrayList<>();
        mView.loadDate(chats);
        if (chats.size() < 1) {
            lastTime = new Date().getTime();
        } else {
            lastTime = chats.get(chats.size() - 1).getTime();
            //lastTime = new Date().getTime();
        }
    }

    void loadMore() {
        if (!ChatColumns.hasMore(fromId, lastTime)) {
            mView.noMoreChat();
            mView.cancelRefresh();
            return;
        }
        List<Chat> chats = ChatColumns.byFrom(fromId, 0, lastTime);
        lastTime = chats.get(chats.size() - 1).getTime();
        mView.addData(chats);
        mView.cancelRefresh();
    }

    private void check() {
        if (isEmpty(fromId)) {
            throw new RuntimeException("set the fromId first!");
        }
    }

    void send(String content) {
        check();

        if(multiUserChat == null){
            return;
        }
        try {
            //List<String> serviceNames = XMPPUtil.getMultiChatManager().getServiceNames();
            multiUserChat.sendMessage(content);
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e);
            mView.sendError();
            return;
        }
        //chat.getThreadID();

        Chat chat1 = new Chat();
        chat1.setFromId(fromId);
        chat1.setContent(content);
        chat1.setHolder(1);
        chat1.setTime(new Date().getTime());
        mView.addData(chat1);
        mView.sendSuccess();

        ThreadUtil.runONWorkThread(() -> ChatColumns.insert(chat1));
        mIntent.putExtra(INTENT_FORM, chat1.getFromId());
        mIntent.putExtra(INTENT_CONTENT, chat1.getContent());
        mIntent.putExtra(INTENT_TIME, chat1.getTime());
        mIntent.putExtra(INTENT_HOLDER, HOLDER_ME);
        mBroadcastManager.sendBroadcast(mIntent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBroadcastManager.unregisterReceiver(mChatReceiver);
    }

    public void destroy(){
        multiUserChat.removeMessageListener(messageListener);
        mBroadcastManager.unregisterReceiver(mChatReceiver);
    }

    private BroadcastReceiver mChatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(CommonField.INTENT_FORM) && intent.hasExtra(CommonField.INTENT_CONTENT)) {
                String from = intent.getStringExtra(CommonField.INTENT_FORM);
                String content = intent.getStringExtra(CommonField.INTENT_CONTENT);
                int holder = intent.getIntExtra(INTENT_HOLDER, 0);
                if (holder != HOLDER_OTHER) {
                    mView.scrollToBottom();
                    return;
                }
                long time = intent.getLongExtra(CommonField.INTENT_TIME, -1);
                if (time == -1) {
                    time = new Date().getTime();
                }
                /*ImmutableChat chat1 = ImmutableChat.builder()
                        .from_id(from)
                        .content(content)
                        .holder(false)
                        .time(time).build();*/
                Chat chat1 = new Chat();
                chat1.setFromId(from);
                chat1.setContent(content);
                chat1.setHolder(0);
                chat1.setTime(time);
                mView.addData(chat1);
            }
        }
    };
}
