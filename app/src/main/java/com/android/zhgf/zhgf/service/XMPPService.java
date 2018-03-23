package com.android.zhgf.zhgf.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.activity.TDMapActivity;
import com.android.zhgf.zhgf.database.ChatColumns;
import com.android.zhgf.zhgf.utils.CommonField;
import com.android.zhgf.zhgf.utils.JobUtil;
import com.android.zhgf.zhgf.utils.ObjectHolder;
import com.android.zhgf.zhgf.utils.ThreadUtil;
import com.android.zhgf.zhgf.utils.XMPPUtil;
import com.android.zhgf.zhgf.wnd.incompatible.NotifyIncompatible;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import timber.log.Timber;

import static android.text.TextUtils.isEmpty;
import static com.android.zhgf.zhgf.utils.CommonField.HOLDER_OTHER;
import static com.android.zhgf.zhgf.utils.CommonField.INTENT_CONTENT;
import static com.android.zhgf.zhgf.utils.CommonField.INTENT_FORM;
import static com.android.zhgf.zhgf.utils.CommonField.INTENT_HOLDER;
import static com.android.zhgf.zhgf.utils.CommonField.INTENT_TIME;
import static com.android.zhgf.zhgf.utils.CommonField.RECEIVER_CONTACT;

public class XMPPService extends Service {

    private LocalBroadcastManager mBroadcastManager;
    private Intent mIntent;
    /*TODO Android通知对象管理器*/
    private NotifyIncompatible mNotify;
    private NotificationCompat.Builder mBuilder;
    public NotificationManager mNotificationManager;
    @Override
    public void onCreate() {
        mBroadcastManager = ObjectHolder.getBroadcastManager();
        mIntent = new Intent(CommonField.RECEIVER_CHAT);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Timber.i("service on create");
        if (!XMPPUtil.isConnected()) {
            Timber.e("XMPP connection was not connected!");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Inner();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("onStartCommand");
        Timber.i("connect is null: %s", !XMPPUtil.isConnected());
        if (XMPPUtil.isConnected()) {
            Timber.i("——————————————————————————————————add listener——————————————————————————————————————————————————");
            XMPPUtil.addConnectionListener(mConnectionListener);
            if (XMPPUtil.isLogin()) {
                //监听花名册的变更
                XMPPUtil.addRosterListener(mRosterListener);
                //监听消息的改变
                XMPPUtil.addChatManagerListener(mChatManagerListener);
            }
            final Set<RosterEntry> entries = XMPPUtil.getEntries();
            if (entries == null || entries.isEmpty()) {
                return START_STICKY;
            }
            ThreadUtil.runONWorkThread(new Runnable() {
                @Override
                public void run() {
                    /*Timber.i("同步花名册: %s", entries.size());
                    ArrayList<Contact> contacts = new ArrayList<>();
                    for (RosterEntry entry : entries) {
                        contacts.add(getContact(entry));
                    }
                    Contact.insertAll(contacts);*/
                }
            });
        }
        return START_STICKY;
    }

    /*private ImmutableContact getContact(RosterEntry entry) {
        String user = entry.getUser();
        ImmutableContact.Builder builder = ImmutableContact.builder()
                .user_name(entry.getName())
                .pinyin(Common.toPinYin(user))
                .avatar(null)
                .user_id(user);
        Presence presence = XMPPUtil.getPresence(user);
        if (presence != null) {
            builder.p_mode(presence.getMode())
                    .p_status(presence.getStatus())
                    .p_type(presence.getType());
        }
        return builder.build();
    }*/

    @Override
    public void onDestroy() {
        Timber.i("onDestroy");
        //监听花名册的变更
        XMPPUtil.removeRosterListener(mRosterListener);
        //监听消息的改变
        XMPPUtil.removeChatManagerListener(mChatManagerListener);
        if (XMPPUtil.isConnected()) {
            XMPPUtil.removeConnectionListener(mConnectionListener);
        }
        XMPPUtil.closeConnected();
    }

    public class Inner extends Binder {
    }

    //监听花名册的改变
    private RosterListener mRosterListener = new RosterListener() {
        @Override
        public void entriesAdded(Collection<String> addresses) {
            Timber.i("花名册增加了");
            for (String address : addresses) {
                Timber.i("address : %s", address);
                RosterEntry entry = XMPPUtil.getEntry(address);
                /*ImmutableContact contact = getContact(entry);
                Contact.insertOrUpdate(contact);*/
            }
            mBroadcastManager.sendBroadcast(new Intent(RECEIVER_CONTACT));
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            Timber.i("花名册更新了");
            for (String address : addresses) {
                RosterEntry entry = XMPPUtil.getEntry(address);
                /*ImmutableContact contact = getContact(entry);
                Contact.update(contact);*/
            }
            mBroadcastManager.sendBroadcast(new Intent(RECEIVER_CONTACT));
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            Timber.i("花名册减少了");
            for (String address : addresses) {
                //Contact.delete(address);
            }
            mBroadcastManager.sendBroadcast(new Intent(RECEIVER_CONTACT));
        }

        @Override
        public void presenceChanged(Presence presence) {
            Timber.i("状态改变了");
            //Contact.update(presence.getFrom(), presence.getStatus(), presence.getMode(), presence.getType());
        }
    };

    private ChatManagerListener mChatManagerListener = new ChatManagerListener() {
        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {
            if (createdLocally) {
                Timber.i("主动创建了一个会话, threadId = %s", chat.getThreadID());
            } else {
                Timber.i("被动创建了一个会话, threadId = %s", chat.getThreadID());
            }
            chat.addMessageListener(mChatMessageListener);
            Timber.i("会话ID : %s", chat.getThreadID());
        }
    };


    private ChatMessageListener mChatMessageListener = new ChatMessageListener() {

        @Override
        public void processMessage(Chat chat, Message message) {
            String body = message.getBody();
            if (isEmpty(body)) {
                return;
            }
            final String from = message.getFrom();
            String fromId = from.substring(0, from.indexOf('/'));
            String userId = from.substring(0, from.indexOf('@'));
            long time = new Date().getTime();
            Timber.i("会话ID：%s", chat.getThreadID());
            Timber.i("会话发起者：%s", from);
            Timber.i("会话目标：%s", message.getTo());
            Timber.i("会话主题：%s", message.getSubject());
            Timber.i("会话类型：%s", message.getType());
            Timber.i("会话内容：%s", body);
            mIntent.putExtra(INTENT_FORM, fromId);
            mIntent.putExtra(INTENT_CONTENT, body);
            mIntent.putExtra(INTENT_TIME, time);
            mIntent.putExtra(INTENT_HOLDER, HOLDER_OTHER);
            mBroadcastManager.sendBroadcast(mIntent);
             mBuilder = new NotificationCompat.Builder(getApplicationContext());
            Notification mNotification = mBuilder.build();
            mNotification.flags = Notification.FLAG_AUTO_CANCEL;

            mBuilder.setAutoCancel(true)//点击后让通知将消失
                    .setContentTitle("测试标题")
                    .setContentText("点击跳转")
                    .setTicker("点我");
            //点击的意图ACTION是跳转到Intent
            Intent resultIntent = new Intent(getApplicationContext(), TDMapActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            mNotificationManager.notify(100, mBuilder.build());

            //mNotify = new NotifyIncompatible(getApplicationContext());

            /*try {
                mNotify.HeighNotify(getResources().getString(R.string.app_name),
                        R.drawable.ic_menu_send,
                        "收到来自" + userId + "的消息",
                        body);

            } catch (Exception e) {
                e.printStackTrace();
            }*/

            ThreadUtil.runONWorkThread(() -> {
                /*ImmutableChat chat1 = ImmutableChat.builder().from_id(fromId).content(body).holder(false).time(time).build();
                com.android.zhgf.zhgf.datasource.model.Chat.insert(chat1);*/
                com.android.zhgf.zhgf.database.bean.Chat chat1 = new com.android.zhgf.zhgf.database.bean.Chat();
                chat1.setFromId(fromId);
                chat1.setContent(body);
                chat1.setHolder(0);
                chat1.setTime(time);
                ChatColumns.insert(chat1);
            });
        }
    };
    private ConnectionListener mConnectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            //连接到服务器成功
            Timber.i("server connected success");
            Log.e("login", "#########################     server connected success     #########################");
            mBroadcastManager.sendBroadcast(new Intent(CommonField.RECEIVER_CONNECTED));
            JobUtil.closeConnectJob();
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            //登陆成功
            Timber.i("server login");
            mBroadcastManager.sendBroadcast(new Intent(CommonField.RECEIVER_LOGIN));
        }

        @Override
        public void connectionClosed() {
            Timber.i("server disconnected");
            //连接断开
            XMPPUtil.closeConnected();
        }

        @Override
        public void connectionClosedOnError(Exception e) {

            XMPPUtil.closeConnected();
            Timber.e("server disconnected by error");
            Timber.i(e);
            //异常断开
            //如果系统版本大于21（5.0）执行连接任务
            //否则通过广播连接
            e.printStackTrace();
            Log.e("XMPPServicezhgf", e.getMessage());
            Log.e("XMPPServicezhgf", "&&&&&&&&&&&&&&&&&&&&&&&&&&&&server disconnected by error&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            Log.e("XMPPServicezhgf", "&&&&&&&&&&&&&&&&&&&&&&&&&&&&server disconnected by error&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            Log.e("XMPPServicezhgf", "&&&&&&&&&&&&&&&&&&&&&&&&&&&&server disconnected by error&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");


            JobUtil.startConnectJob();
        }

        @Override
        public void reconnectionSuccessful() {
            //重连成功
            Timber.i("server reconnected success");
        }

        @Override
        public void reconnectingIn(int seconds) {

        }

        @Override
        public void reconnectionFailed(Exception e) {
            XMPPUtil.closeConnected();
            //重连失败
            Timber.i("server reconnected fail");
        }
    };

}
