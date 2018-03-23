package com.android.zhgf.zhgf.util;
import android.widget.Chronometer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TZXX001 on 2017/9/29.
 */

public class DateUtil {
    /**
     * 获取系统时间戳
     * @return
     */
    public long getCurTimeLong(){
        long time=System.currentTimeMillis();
        return time;
    }
    /**
     * 获取当前时间
     * @param pattern
     * @return
     */
    public static String getCurDate(String pattern){
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        return sDateFormat.format(new java.util.Date());
    }

    /**
     * 时间戳转换成字符窜
     * @param milSecond
     * @param pattern
     * @return
     */
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond* 1000L);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 将字符串转为时间戳
     * @param dateString
     * @param pattern
     * @return
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static boolean compareDate(String dateString1,String dateString2,String pattern){
        long date1 = getStringToDate(dateString1,pattern);
        long date2 = getStringToDate(dateString2,pattern);
        if(date1 > date2){
            return true;
        }
        return false;
    }
    /*
     *计算time2减去time1的差值 差值只设置 几天 几个小时 或 几分钟
     * 根据差值返回多长之间前或多长时间后
     * */
    public static String getDistanceTime(long  time1,long time2 ) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff ;
        String flag;
        if(time1<time2) {
            diff = time2 - time1;
            flag="前";
        } else {
            diff = time1 - time2;
            flag="后";
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        if(day!=0 && day <= 30){
            return day+"天"+flag;
        }else if (day > 30 ){
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日");
            String times = sdr.format(new Date(time1));
            return times;
        }
        if(hour!=0)return hour+"小时"+flag;
        if(min!=0)return min+"分钟"+flag;
        return "刚刚";
    }

    /**
     *
     * @param cmt  Chronometer控件  00:00分钟
     * @return 分钟+秒数  的所有秒数
     */
    public  static String getChronometerSeconds(Chronometer cmt) {
        int totalss = 0;
        try{
            String string = cmt.getText().toString();
            String[] split1 = string.split("分钟");
            String[] split = split1[0].split(":");
            String string3 = split[0];
            int min = Integer.parseInt(string3);
            int Mins =min*60;
            int  SS =Integer.parseInt(split[1]);

            totalss =Mins+SS;
        }catch (Exception e){
            e.printStackTrace();
        }

        return String.valueOf(totalss);
    }
}
