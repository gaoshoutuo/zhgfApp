package com.android.zhgf.zhgf.wnd.incompatible;/**
 * Created by Administrator on 2016/4/20.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;


//import io.dcloud.HBuilder.Hello.R;

/*******************************************************************************************************
 * Class Name:WndProject - NotifyIncompatible
 *
 * @author hnyashiro 5303363@qq.com
 * @project WndProject
 * @Title NotifyIncompatible
 * @Package com.jiechu.wnd.incompatible
 * @date 2016/4/20 16:21
 * @Description 任务栏通知栏，不同版本的兼容解决
 * @copyright 2016 嘉兴杰出信息科技有限公司 All Right Reserved.
 *******************************************************************************************************/
public class NotifyIncompatible {
    private static final String TAG = NotifyIncompatible.class.getSimpleName();
    private static final int NOTIFY_NUMBER = 10000001;
    private static final int VERSION_NUM_SP = 16;
    Context mContext = null;
    NotificationManager manager = null;
    Notification.Builder builder = null;
    Notification notify;
    RemoteViews views = null;


    public NotifyIncompatible(Context pContext) {
        mContext = pContext;
        manager = (NotificationManager) pContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Deprecated
    void LowNotify(int icon_id, int title_id, long when) {
        /*notify = new Notification(
                icon_id,                                            //通知栏小图标
                mContext.getResources().getString(title_id),        //通知栏标题
                when                                                //when，是一个数字，我到现在都不知道干嘛用的
        );

        Intent mIntent = new Intent(mContext,MainActivity.class);
        //mIntent.setClass(this,MainActivity.class);
        PendingIntent pintent = PendingIntent.getActivities(mContext,100, new Intent[]{mIntent},PendingIntent.FLAG_UPDATE_CURRENT);
        notify.contentIntent=pintent;
        manager.notify(100000001,notify);*/
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void HeighNotify(String title, int icon_id, String content_title, String content_text) throws Exception {
        Intent mIntent = new Intent(mContext,Activity.class);
        PendingIntent pintent = PendingIntent.getActivities(mContext,0, new Intent[]{mIntent},PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new Notification.Builder(mContext);              //初始化通知对象
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder .setContentIntent(pintent)                        //设置已定义好的Intebt对象
                    .setTicker(title)                                 //设置任务栏标题
                    .setSmallIcon(icon_id)                            //设置小图标
                    .setLargeIcon(BitmapFactory                       //设置大图标
                            .decodeResource(mContext.getResources(), 0))
                    .setOngoing(true)                                 //设置为正在进行中的通知，不可被撤销

                    .setWhen(System.currentTimeMillis())              //设置when参数，具体干嘛用不知道
                    .setContentTitle(content_title)                   //设计通知栏标题
                    .setContentText(content_text);
            manager.notify(100000001,builder.build());
            return;
        }
            if (mIntent!=null)mIntent=null;
            if (pintent!=null)pintent=null;
            if (builder!=null)builder=null;
            throw new Exception("本设备不支持高版本消息通知，请更换设备后重试");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void updateHeighNotify(String title,String content){
        builder.setContentTitle(title).setContentText(content);
        manager.notify(NOTIFY_NUMBER, builder.build());
    }
}