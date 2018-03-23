package com.android.zhgf.zhgf.service;

import android.app.Application;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.zhgf.zhgf.JavaBean.NewsDetail;
import com.android.zhgf.zhgf.R;
import com.android.zhgf.zhgf.activity.MenuActivity;
import com.android.zhgf.zhgf.activity.NewsActivity;
import com.android.zhgf.zhgf.activity.NewsDetailsActivity;
import com.android.zhgf.zhgf.activity.NotiActivity;
import com.android.zhgf.zhgf.activity.UpdateActivity;
import com.android.zhgf.zhgf.app.AppApplication;
import com.android.zhgf.zhgf.bean.BadgeNumBean;
import com.android.zhgf.zhgf.bean.IntelligenceHistoryBean;
import com.android.zhgf.zhgf.bean.Lesson;
import com.android.zhgf.zhgf.database.NotificationTable;
import com.android.zhgf.zhgf.database.bean.NotificationBean;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.On;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 71568 on 2017/11/18.
 * 用于接受服务器的notification
 */

public class ServiceNotification extends Service {
    private final String TAG = "NOTIFICATION";
    private TaskNotification tn = new TaskNotification();
    private static int []struct1={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ;
   // private int []struct2=new int[4];
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return tn;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, TaskNotification.ServiceD.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tn.listen();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tn.listen();
        return super.onStartCommand(intent, flags, startId);
    }

    //绑定服务的向上转型
    public class TaskNotification extends Binder {
        public void listen() {
            try {
                final Socket socket = IO.socket("http://192.168.10.185:3222");
               // final Socket socket = IO.socket("http://20.150.180.187:3222");
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.e("Socket.IO", "Server is connected!");
                        socket.emit("foo","你好 已经连接");

                    }
                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.e("Socket.IO", "Server is connected!");

                    }
                }).on("notification", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.e("args", args[0].toString());
                    }
                }).on("news", new Emitter.Listener() {
                    //此处接受政工网消息  没有单一消息
                    @Override
                    public void call(Object... args) {
                        Log.e("args123", args[0].toString());
                      NotificationBean nb = parseJSONObeject(args[0].toString());
                        //此处解析 args 解析json obj 然后依次填入
                        Intent intent = new Intent(AppApplication.getApp(), NewsDetailsActivity.class);
                        intent.putExtra("title", nb.getMenuName());
                        intent.putExtra("newsId", nb.getNewsId()+"");
                        intent.putExtra("type",1);
                        intent.putExtra("channelId", nb.getCatId()+"");
                        intent.putExtra("othertype", 4);

                        PendingIntent pi = PendingIntent.getActivity(AppApplication.getApp(), 0, intent, 0);
                        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notification = new NotificationCompat.Builder(AppApplication.getApp())
                                .setContentTitle(nb.getTitle())
                                .setContentText(null)
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                                .setContentIntent(pi)
                                .setAutoCancel(true)
                                .setPriority(android.support.v7.app.NotificationCompat.PRIORITY_MAX)
                                .setSound(/*Uri.parse("android:resource://com.android.zhgf.zhgf/"+R.raw.sound1)*/
                                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                                )
                                .setVibrate(new long[]{ 0, 1000, 1000, 1000 })
                                .setColor(Color.GREEN)
                                .build();
                      /*  notification.ledOffMS=1000;
                        notification.ledOnMS=1000;
                        notification.flags = 1;*/
                        nm.notify(nb.getCatId(),notification);

                        NotificationTable.insertSql1(nb);
                    }
                }).on("fivemsg", new Emitter.Listener() {
                    //小红点
                    @Override
                    public void call(Object... args) {
                    //获取五类数据的后台表唯一id  并且存到数据库未读里并且让badgeview显示
                        //本地id 对于远程
                         parseJsonObject2(args[0].toString());
                      /*  Intent intent2=new Intent();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("list", (Serializable) badgeList);
                        intent2.putExtras(bundle);*/
                        for (int i=0;i<badgeList.size();i++) {
                            BadgeNumBean bnb=  badgeList.get(i);
                            Log.e("小红点","+++++++++++++++++++++++");
                            switch (bnb.getCatId()){
                                case 1:
                                    struct1[2] =bnb.getNotReadCount();
                                    break;
                                case 2:
                                    struct1[3] =bnb.getNotReadCount();
                                    break;
                                case 3:
                                    struct1[4] =bnb.getNotReadCount();
                                    break;
                                case 4:
                                    struct1[5] =bnb.getNotReadCount();
                                    break;
                                case 5:
                                    struct1[18] =bnb.getNotReadCount();
                                    break;
                                default:break;
                            }
                        }
                        Message message=new Message();
                        message.what=101;
                        message.obj=struct1;
                       MenuActivity.handler.sendMessage(message);
                    }
                })
                .on("updatenotify", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.e("updatenotify",args[0].toString());
                        Intent intent=new Intent(AppApplication.getApp(), UpdateActivity.class);
                        intent.putExtra("json",args[0].toString());
                        setNotify("更新通知",123,intent);
                    }
                }) .on("openvideoroom", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        //就一个房间号 roomId
                       // JSONObject jb=(JSONObject) args[0]; 这种解析完全是错误的

                        try {
                            JSONObject jb=new JSONObject(args[0].toString());
                            String createTime= jb.getString("createTime");
                            String updateUrl= jb.getString("updateUrl");

                           Log.e("jsonObject",createTime+updateUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent=new Intent();
                    }
                });
                socket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        private void setNotify(String title,int notifyId,Intent intent){
            PendingIntent pi = PendingIntent.getActivity(AppApplication.getApp(), 0, intent, 0);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(AppApplication.getApp())
                    .setContentTitle(title)
                    .setContentText(null)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setPriority(android.support.v7.app.NotificationCompat.PRIORITY_MAX)
                    .setSound(/*Uri.parse("android:resource://com.android.zhgf.zhgf/"+R.raw.sound1)*/
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    )
                    .setVibrate(new long[]{ 0, 1000, 1000, 1000 })
                    .setColor(Color.GREEN)
                    .build();
                      /*  notification.ledOffMS=1000;
                        notification.ledOnMS=1000;
                        notification.flags = 1;*/
            nm.notify(notifyId,notification);
        }

        //程序前读取数据库   程序后写入数据库
        private NotificationBean parseJSONObeject(String jsonData) {
            NotificationBean nb=null;
            try {
                JSONArray ja = new JSONArray(jsonData);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jb = ja.getJSONObject(i);
                    int catId = jb.getInt("cat_id");
                    int newsId=jb.getInt("news_id");
                    String menu_name=jb.getString("menu_name");
                    String title=jb.getString("title");
                  nb=  new NotificationBean(newsId,catId,menu_name,title);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return nb;
        }
        private List<BadgeNumBean> badgeList=new ArrayList<>();

        private List<BadgeNumBean>parseJsonObject2(String jsonData){
            try {
                JSONArray ja = new JSONArray(jsonData);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jb = ja.getJSONObject(i);
                    int catId = jb.getInt("cat_id");
                    int notReadCount=jb.getInt("not_read_count");
                    BadgeNumBean badgeNumBean=new BadgeNumBean();
                    badgeNumBean.setCatId(catId);
                    badgeNumBean.setNotReadCount(notReadCount);
                    badgeList.add(badgeNumBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
             Log.e("list",badgeList.toString());
            return badgeList;
        }


        public class ServiceD extends IntentService {
            public ServiceD() {
                super(null);
            }

            public ServiceD(String name) {
                super(name);
            }

            @Override
            protected void onHandleIntent(Intent intent) {
                startService(new Intent(this, ServiceNotification.class));
                Log.d("09876", "onhandle");
            }

            @Override
            public int onStartCommand(Intent intent, int flags, int startId) {
                //  startService(new Intent(this,ServiceNotification.class));
                return super.onStartCommand(intent, flags, startId);
            }

            @Override
            public void onDestroy() {
                Log.d("09876", "结束");
                super.onDestroy();

            }
        }

    }
}