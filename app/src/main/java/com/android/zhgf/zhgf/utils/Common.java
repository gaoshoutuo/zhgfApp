package com.android.zhgf.zhgf.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;

import com.android.zhgf.zhgf.R;
import com.github.promeg.pinyinhelper.Pinyin;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static String toPinYin(String str) {
        char[] chars = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            sb.append(Pinyin.toPinyin(c));
        }
        return sb.toString();
    }

    /**
     * check EditText is empty or not
     *
     * @param edText pass EditText for check is empty or not
     * @return true or false
     */
    public static boolean isEmptyEditText(EditText edText) {
        if (edText.getText().toString().trim().length() > 0)
            return false;
        else
            return true;
    }

    /**
     * check availability of Internet
     *
     * @param context
     * @return true or false
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /**
     * Get today's date in any format.
     *
     * @param dateFormat pass format for get like: "yyyy-MM-dd hh:mm:ss"
     * @return current date in string format
     */
    public static String getCurrentDate(String dateFormat) {
        Date d = new Date();
        String currentDate = new SimpleDateFormat(dateFormat).format(d.getTime());
        return currentDate;
    }

    /**
     * use for make local notification from application
     *
     * @param mContext
     * @param title    for the Notification
     * @param message  for the notification
     * @param mIntent  for open activity to open on touch of notification
     */
    @SuppressLint("NewApi")
    @SuppressWarnings({"static-access"})
    public static void sendLocatNotification(Context mContext, String title,
                                             String message, Intent mIntent) {
        System.out.println("called: " + title + " : " + message);
        int appIconResId = 0;
        PendingIntent pIntent = null;
        if (mIntent != null)
            pIntent = PendingIntent.getActivity(mContext, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final PackageManager pm = mContext.getPackageManager();
        String packageName = mContext.getPackageName();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = pm.getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA);
            appIconResId = applicationInfo.icon;
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }

        Notification notification;
        if (mIntent == null) {
            notification = new Notification.Builder(mContext)
                    .setSmallIcon(appIconResId).setWhen(System.currentTimeMillis())
                    .setContentTitle(message)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    .setAutoCancel(true)
                    .setContentText(message)
                    .setContentIntent(PendingIntent.getActivity(mContext, 0, new Intent(), 0))
                    .getNotification();

        } else {
            notification = new Notification.Builder(mContext)
                    .setSmallIcon(appIconResId).setWhen(System.currentTimeMillis())
                    .setContentTitle(message)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    .setContentIntent(pIntent).getNotification();
        }
        // Remove the notification on click
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        NotificationManager manager = (NotificationManager) mContext
                .getSystemService(mContext.NOTIFICATION_SERVICE);
        // manager.notify(0, notification);
        manager.notify(R.string.app_name, notification);
    }

    /**
     * calculate difference form two dates Note: both dates are in same format.
     *
     * @param mDate1 date 1
     * @param mDate2 date 2
     * @return date difference in long
     */
    public static long calculateDays(Date mDate1, Date mDate2) {
        return Math.abs((mDate1.getTime() - mDate2.getTime()) / (24 * 60 * 60 * 1000) + 1);
    }

    /**
     * 大于今天0点
     */
    public static boolean isToDay(Date date) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        String format = dateFormat.format(new Date());
        try {
            Date today = dateFormat.parse(format);
            return today.before(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 小于今天0点，大于昨天0点
     */
    public static boolean isTomorrow(Date date) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        String format = dateFormat.format(new Date());
        try {
            Date today = dateFormat.parse(format);
            if (today.after(date)) {
                today.setTime(today.getTime() - 24 * 60 * 60 * 1000);
                return today.before(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getTime(Date date){
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        return dateFormat.format(date);
    }

    public static String getDate(Date date){
        DateFormat dateFormat = DateFormat.getDateInstance();
        return dateFormat.format(date);
    }

}