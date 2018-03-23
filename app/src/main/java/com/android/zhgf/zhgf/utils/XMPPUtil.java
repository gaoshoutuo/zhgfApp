package com.android.zhgf.zhgf.utils;

import android.util.Log;

import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.app.Constant;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

import static android.text.TextUtils.isEmpty;

/**
 * Created by mrtan on 10/2/16.
 */

public final class XMPPUtil {

    /**
     * 服务器连接实体
     */
    private static XMPPTCPConnection connection;
    private static Roster mRoster;
    private static ChatManager mChatManager;
    private static MultiUserChatManager multiUserChatManager;
    private static final HashMap<String, String> CHAT_MAP = new HashMap<>();
    private static final MChatManagerListener mChatManagerListener = new MChatManagerListener();
    /**
     * 建立连接
     */
    public static void getXMPPConnection(ConnectionListener listener) throws IOException, XMPPException, SmackException{
        if (connection == null || !connection.isConnected()) {
            XMPPTCPConnectionConfiguration builder = XMPPTCPConnectionConfiguration.builder()
                    .setHost(Constant.IM_HOST)//ip
                    .setPort(Constant.IM_PORT)//端口
                    .setServiceName(Constant.IM_SERVER)//此处填写openfire服务器名称
                    .setCompressionEnabled(false)//是否允许使用压缩
                    .setSendPresence(true)//是否发送Presece信息
                    .setDebuggerEnabled(true)//是否开启调试
                    .setResource("android")//设置登陆设备标识
                    .setConnectTimeout(15 * 1000)//连接超时时间
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)//设置TLS安全模式时使用的连接
                    .build();
            // 是否使用SASL
            SASLAuthentication.blacklistSASLMechanism(SASLMechanism.DIGESTMD5);
            //AppApplication.connection = new XMPPTCPConnection(builder);

            //AppApplication.connection.connect();
            connection = new XMPPTCPConnection(builder);
            connection.connect();

            if (listener != null) {
                //AppApplication.connection.addConnectionListener(listener);
                connection.addConnectionListener(listener);
            }
            //connection = AppApplication.connection;
        }
    }
    /**
     * 根据服务器名，地址，端口登陆
     *
     * @throws IOException
     * @throws XMPPException
     * @throws SmackException
     */
    public static void connect(String name, String address, int port, ConnectionListener listener) throws IOException, XMPPException, SmackException {
        Timber.i("connect to service");
        if (isConnected()) {
            Timber.i("connect not null");
            connection.removeConnectionListener(listener);
            closeConnected();
        }
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setServiceName(name);
        builder.setHost(address);
        builder.setPort(port);
        builder.setResource("android");//设置登陆设备标识
        builder.setConnectTimeout(5000);//连接超时时间
        builder.setCompressionEnabled(false);
        //todo 添加证书
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        final XMPPTCPConnection conn = new XMPPTCPConnection(builder.build());
        conn.connect();
        if (listener != null) {
            conn.addConnectionListener(listener);
        }
        connection = conn;
    }

    /**
     * 初始化花名册，会话管理器实体
     */
    private static void init() {
        mRoster = Roster.getInstanceFor(XMPPUtil.connection);
        mChatManager = ChatManager.getInstanceFor(XMPPUtil.connection);
        multiUserChatManager = MultiUserChatManager.getInstanceFor(XMPPUtil.connection);

    }

    /**
     * 添加花名册监听
     *
     * @param listener 监听
     * @return 结果
     */
    public static boolean addRosterListener(RosterListener listener) {
        if (mRoster != null) {
            mRoster.addRosterListener(listener);
            return true;
        }
        return false;
    }

    /**
     * 取消花名册监听
     *
     * @param listener 监听
     * @return 结果
     */
    public static boolean removeRosterListener(RosterListener listener) {
        if (mRoster != null) {
            mRoster.removeRosterListener(listener);
            return true;
        }
        return false;
    }

    /**
     * 添加会话监听
     *
     * @param listener 监听
     * @return 结果
     */
    public static boolean addChatManagerListener(ChatManagerListener listener) {
        if (mChatManager != null) {
            mChatManager.addChatListener(listener);
            return true;
        }
        return false;
    }

    /**
     * 取消会话监听
     *
     * @param listener 监听
     * @return 结果
     */
    public static boolean removeChatManagerListener(ChatManagerListener listener) {
        if (mChatManager != null) {
            mChatManager.removeChatListener(listener);
            return true;
        }
        return false;
    }

    /**
     * 通过地址查询entry
     *
     * @param address 地址
     * @return entry
     */
    public static RosterEntry getEntry(String address) {
        if (mRoster == null) {
            return null;
        }
        return mRoster.getEntry(address);
    }

    /**
     * 获取所有的entry
     *
     * @return 列表
     */
    public static Set<RosterEntry> getEntries() {
        if (mRoster == null) {
            return null;
        }
        return mRoster.getEntries();
    }

    /**
     * 获取entry 的 状态
     *
     * @param userId id
     * @return 状态
     */
    public static Presence getPresence(String userId) {
        if (mRoster == null) {
            return null;
        }
        return mRoster.getPresence(userId);
    }

    /**
     * 获取会话
     *
     * @param userId toId
     * @return chat
     */
    public static Chat getChat(String userId) {
        Chat chat = null;
        if(mChatManager == null){
            return null;
        }
        String threadId = CHAT_MAP.get(userId);
        if (threadId != null) {
            chat = mChatManager.getThreadChat(threadId);
        }
        if (chat == null) {
            chat = mChatManager.createChat(userId);
        }
        CHAT_MAP.put(userId, chat.getThreadID());
        return chat;
    }

    /**
     * 获取会话
     *
     * @param userId toId
     * @return chat
     */
    public static MultiUserChatManager getMultiChatManager() {

        return multiUserChatManager;
    }

    // 获取聊天室
    public static MultiUserChat getMultiUserChat (String roomName){
        MultiUserChat multiUserChat = null;
        if(multiUserChatManager == null){
            return null;
        }
        multiUserChat = multiUserChatManager.getMultiUserChat(roomName);
        return multiUserChat;

    }

    /**
     * 登陆
     *
     * @param userName 用户名
     * @param password 密码
     * @throws IOException
     * @throws XMPPException
     * @throws SmackException
     */
   /* public static void login(String userName, String password) throws IOException, XMPPException, SmackException {
        if (!isConnected()) {
            throw new RuntimeException("call the connect first!");
        }
        Timber.i("login-- username: %s, password: %s", userName, password);
        //SASLAuthentication.registerSASLMechanism(new SASLPlainMechanism());
        connection.login(userName, password, "android");
        PreferencesUtils.getEditor()
                .putString(CommonField.USER_NAME, userName)
                .putString(CommonField.USER_PASSWORD, password)
                .apply();
        ObjectHolder.XMPP_ID = userName + "@" + PreferencesUtils.getString(CommonField.SERVER_NAME) + "/android";
        init();
        Timber.i("login success");
    }*/
    public static boolean login(String username, String password) throws IOException, XMPPException, SmackException{
        checkConnect();
        if (connection.isAuthenticated()) {//已经登录
            PreferencesUtils.getEditor()
                    .putString(CommonField.USER_NAME, username)
                    .putString(CommonField.USER_PASSWORD, password)
                    .apply();
            //ObjectHolder.XMPP_ID = username + "@" + PreferencesUtils.getString(CommonField.SERVER_NAME) + "/android";
            ObjectHolder.XMPP_ID = username + "@" + Constant.IM_HOST;
            init();
            return true;
        } else {

            connection.login(username, password, "android");//登录
            PreferencesUtils.getEditor()
                    .putString(CommonField.USER_NAME, username)
                    .putString(CommonField.USER_PASSWORD, password)
                    .apply();
            //ObjectHolder.XMPP_ID = username + "@" + PreferencesUtils.getString(CommonField.SERVER_NAME) + "/android";
            ObjectHolder.XMPP_ID = username + "@" + Constant.IM_HOST;
            init();
            return connection.isAuthenticated();
        }
    }

    /**
     * 检查连接
     */
    private static void checkConnect() {
        if (connection == null) {//null
            try {
                getXMPPConnection(null);
            } catch (SmackException | IOException | XMPPException e) {
                e.printStackTrace();
            }
        }
        if (!connection.isConnected()) {//没有连接到服务器
            try {
                connection.connect();
            } catch (SmackException | IOException | XMPPException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 根据缓存的账号密码登陆
     *
     * @return 结果
     */
    public static boolean login() {
        String username = PreferencesUtils.getString(CommonField.USER_NAME);
        String password = PreferencesUtils.getString(CommonField.USER_PASSWORD);
        if (!isEmpty(username) && !isEmpty(password)) {
            try {
                Log.e("login", "&&&&&&&&&&&&&&&&&&&&&&&&&&&&     login     &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                login(username, password);
                return true;
            } catch (Exception e) {
                Timber.e("login fail");
                Timber.e(e);
                return false;
            }
        }
        Timber.e("login fail");
        return false;
    }

    /**
     * 根据保存的服务器信息登陆
     *
     * @param listener 监听
     * @return 结果
     * @throws IOException
     * @throws XMPPException
     * @throws SmackException
     */
    public static boolean connect(ConnectionListener listener) throws IOException, XMPPException, SmackException {
        String name = PreferencesUtils.getString(CommonField.SERVER_NAME);
        String address = PreferencesUtils.getString(CommonField.SERVER_ADDRESS);
        int port = PreferencesUtils.getInt(CommonField.SERVER_PORT);
        if (isEmpty(address) || isEmpty(name) || port == 0) {
            return false;
        } else {
            connect(name, address, port, listener);
        }
        return true;
    }

    /**
     * 添加连接监听
     *
     * @param listener 监听
     */
    public static void addConnectionListener(ConnectionListener listener) {
        if (connection != null) {
            connection.addConnectionListener(listener);
        }
    }

    /**
     * 取消连接监听
     *
     * @param listener 监听
     */
    public static void removeConnectionListener(ConnectionListener listener) {
        if (connection != null) {
            connection.removeConnectionListener(listener);
        }
    }

    /**
     * 是否与服务器建立连接
     *
     * @return 结果
     */
    public static boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    /**
     * 是否登陆
     *
     * @return 结果
     */
    public static boolean isLogin() {
        return connection != null && connection.isConnected() && connection.isAuthenticated();
    }

    /**
     * 主动关闭连接
     */
    public static void closeConnected() {
        if (isConnected()) {
            mRoster = null;
            mChatManager = null;
            multiUserChatManager = null;
            CHAT_MAP.clear();
            connection.disconnect();
            connection = null;
        }
    }

    /**
     * 包装类
     */
    public static class ConnectionListenerWrapper implements ConnectionListener {
        //连接到服务器成功
        @Override
        public void connected(XMPPConnection connection) {
            Timber.i("connected");
        }

        //登陆成功
        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            Timber.i("authenticated");
        }

        //连接断开
        @Override
        public void connectionClosed() {
            Timber.i("connectionClosed");
        }

        //异常断开
        @Override
        public void connectionClosedOnError(Exception e) {
            Timber.i("connectionClosedOnError");
        }

        //重连成功
        @Override
        public void reconnectionSuccessful() {
            Timber.i("reconnectionSuccessful");
        }

        @Override
        public void reconnectingIn(int seconds) {
            Timber.i("reconnectingIn");

        }

        //重连失败
        @Override
        public void reconnectionFailed(Exception e) {
            Timber.i("reconnectionFailed");
        }
    }

    public static class MChatManagerListener implements ChatManagerListener {

        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {
            if (createdLocally) {
                Timber.i("主动创建了一个会话, threadId = %s", chat.getThreadID());
            } else {
                Timber.i("被动创建了一个会话, threadId = %s", chat.getThreadID());
            }
        }
    }
    public static String getServiceName(){
        String serviceName = null;
        if (isConnected()) {
            serviceName = connection.getServiceName();
            return serviceName;
        }
        return serviceName;
    }
}
