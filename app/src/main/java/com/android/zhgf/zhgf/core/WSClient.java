package com.android.zhgf.zhgf.core;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;

public class WSClient{
    public static String TAG = WSClient.class.getSimpleName();
    public static String WS_URL = "http://20.150.180.188:8088";
    private static WSClient mInstance=null;
    public static WSClient getInstance(){
        if(mInstance==null){
            mInstance = new WSClient();
        }
        return mInstance;
    }

    private String Username;
    public void setUsername(String val){
        Username = val;
    }
    public String getUsername(){
        return Username;
    }

    private String SocketID;
    public void setSocketID(String val){
        SocketID = val;
    }
    public String getSocketID(){
        return SocketID;
    }

    private String Roomname;
    public void setRoomname(String val){
        Roomname = val;
    }
    public String getRoomname(){
        return Roomname;
    }

    private Socket mSocket;
    public void setSocket(Socket val){
        mSocket = val;
    }
    public Socket getSocket(){
        return mSocket;
    }

    private HashMap<String,ICommand> mCommands = new HashMap<>();
    public void setmCmdLists(HashMap<String,ICommand> val){
        mCommands = val;
    }
    public HashMap<String,ICommand> getmCmdLists(){
        return mCommands;
    }

    public void addCommand(String key,ICommand cmd){
        mCommands.put(key,cmd);
        mSocket.on(key, new StatusEmitter(cmd));
    }
    public void popCommand(String key){
        mCommands.remove(key);
        mSocket.off(key);
    }
    public ICommand getCommand(String key){
        return mCommands.get(key);
    }






    public WSClient(){
        try {
            mSocket = IO.socket(WS_URL);
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "WSClient:::call:: 已登录服务器"+WS_URL);
                }
            });
            mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e(TAG, "WSClient:::call:: 已退出服务器"+WS_URL);
                }
            });
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void SendMessage(String type, JSONObject payload){
        Log.e(TAG, "WSClient:::SendMessage:: 正在向服务器发送【"+type+"】类型的消息");
        while (!mSocket.connected()){
            //Log.e(TAG, "WSClient:::SendMessage:: 等待连接完成!状态:::"+mSocket.connected());
        }
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mSocket.emit(type,payload);
    }


    private LinkedList<OnlineUser> mOnlineUsers;
    public void setOnlineUsers(LinkedList<OnlineUser> val){
        mOnlineUsers = val;
    }
    public LinkedList<OnlineUser> getOnlineUsers(){
        return mOnlineUsers;
    }
    public OnlineUser getUser(String socketID){
        int len=mOnlineUsers.size();
        for(int i=0;i<=len;i++){
            if (mOnlineUsers.get(i).getSocketID().equals(socketID)){
                return mOnlineUsers.get(i);
            }
        }
        return null;
    }



    public static class OnlineUser implements Serializable{
        private String Username;
        public void setUsername(String val){
            Username = val;
        }
        public String getUsername(){
            return Username;
        }
        private String SocketID;
        public void setSocketID(String val){
            SocketID = val;
        }
        public String getSocketID(){
            return SocketID;
        }

        private String Roomname;
        public void setRoomname(String val){
            Roomname = val;
        }
        public String getRoomname(){
            return Roomname;
        }

        private boolean IsBusy;
        public void setIsBusy(boolean val){
            IsBusy = val;
        }
        public boolean getIsBusy(){
            return IsBusy;
        }
    }


}