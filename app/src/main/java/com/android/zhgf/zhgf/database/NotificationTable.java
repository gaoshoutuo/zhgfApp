package com.android.zhgf.zhgf.database;

/**
 * Created by 71568 on 2017/11/24.
 * 通知表
 */

import android.content.ContentValues;
import android.database.Cursor;

import com.android.zhgf.zhgf.database.bean.BadgeViewBean;
import com.android.zhgf.zhgf.database.bean.NotificationBean;
import com.android.zhgf.zhgf.database.local.DataBase;
import com.android.zhgf.zhgf.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *应该专门有个数组的 用来记录 1 或者 0也就是有没有点过  setNumber(a[i])
 * 表的设计应该每个字段互相关联  而不是字段与字段独立这样
 * ID_1 政治教育文章新增数量  显示 也是倒序 未读优先
 * ID_2 在线学习
 * ID_3 新闻资讯
 * ID_4 请销假
 * ID_5 在线考试
 * 增删改查 大致只用到 增改查  删一般不删
 * 在静态方法里面用  用到的变量不得不静态
 */
public class NotificationTable {
    public static final String NOTIFICATION="notification";
    public static final String BADGEVIEW="badgeview";
    public static final String ID="id";
    public static final String NEWS_ID="news_id";
    public static final String CAT_ID="cat_id";
    public static final String MENU_NAME="menu_name";
    public static final String TITLE="title";

    public static final String ISREAD="isread";

    public static final String ID_1="id1";
    public static final String ID_1_NUMBER="id1_number";

    public static final String ID_2="id2";
    public static final String ID_2_NUMBER="id2_number";

    public static final String ID_3="id3";
    public static final String ID_3_NUMBER="id3_number";

    public static final String ID_4="id4";
    public static final String ID_4_NUMBER="id4_number";

    public static final String ID_5="id5";
    public static final String ID_5_NUMBER="id5_number";

    public static final String ID_6="id6";
    public static final String ID_6_NUMBER="id6_number";

    public static final String ID_7="id7";
    public static final String ID_7_NUMBER="id7_number";


    private static List<NotificationBean>notiList=new ArrayList<>();
    private static List<BadgeViewBean>badgeList=new ArrayList<>();
    public static String createTable1(String tableName){
        return new StringBuilder().
        append("CREATE TABLE IF NOT EXISTS ").append(tableName).
                append("(").
                append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,").
                append(NEWS_ID).append(" INTEGER ,").
                append(CAT_ID).append(" INTEGER ,").
                append(MENU_NAME).append(" TEXT ,").
                append(TITLE).append(" TEXT ").
                append(");").toString();
    }

    public static String createTable2(String tableName){
        return new StringBuilder().
                append("CREATE TABLE IF NOT EXISTS ").append(tableName).
                append("(").
                append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,").
                append(ID_1_NUMBER).append(" INTEGER ,").
                append(ID_1).append(" TEXT ,").

                append(ID_2_NUMBER).append(" INTEGER ,").
                append(ID_2).append(" TEXT ,").

                append(ID_3_NUMBER).append(" INTEGER ,").
                append(ID_3).append(" TEXT ,").

                append(ID_4_NUMBER).append(" INTEGER ,").
                append(ID_4).append(" TEXT ,").

                append(ID_5_NUMBER).append(" INTEGER ,").
                append(ID_5).append(" TEXT ,").

                append(ID_6_NUMBER).append(" INTEGER ,").
                append(ID_6).append(" TEXT ,").

                append(ID_7_NUMBER).append(" INTEGER ,").
                append(ID_7).append(" TEXT ").
                append(");").toString();
    }
    
    public static boolean insertSql1(NotificationBean nb){
        ContentValues c=new ContentValues();
        c.put(NEWS_ID,nb.getNewsId());
        c.put(CAT_ID,nb.getCatId());
        c.put(MENU_NAME,nb.getMenuName());
        c.put(TITLE,nb.getTitle());
        return DataBase.getWritableDB().insert(NOTIFICATION,null,c)>=0?true:false;

    }
    public static boolean insertSql2(BadgeViewBean bd){
        ContentValues c =new ContentValues();
        c.put(ID_1,bd.getId_1());
        c.put(ID_1_NUMBER,bd.getId_1_number());

        c.put(ID_2,bd.getId_2());
        c.put(ID_2_NUMBER,bd.getId_2_number());

        c.put(ID_3,bd.getId_3());
        c.put(ID_3_NUMBER,bd.getId_3_number());

        c.put(ID_4,bd.getId_4());
        c.put(ID_4_NUMBER,bd.getId_4_number());

        c.put(ID_5,bd.getId_5());
        c.put(ID_5_NUMBER,bd.getId_5_number());

        c.put(ID_6,bd.getId_6());
        c.put(ID_6_NUMBER,bd.getId_6_number());

        c.put(ID_7,bd.getId_7());
        c.put(ID_7_NUMBER,bd.getId_7_number());
        return DataBase.getWritableDB().insert(BADGEVIEW,null,c)>=0?true:false;
    }
    public static void drop1(){

    }
    public static void drop2(){

    }

    /**
     * 第几行发生改变就发送修改哪一行
     * @param x
     * @return
     */
    public static boolean update1(int x){
        ContentValues c=new ContentValues();
        c.put(ID,x);
            return DataBase.getWritableDB().update(NOTIFICATION,c,"isread=?",new String[]{"0"})>0?true:false;
    }
    public static boolean update2(int x,String[]st){
        ContentValues c=new ContentValues();
        c.put(ID,x);
        return DataBase.getWritableDB().update(BADGEVIEW,c,"isread=?",new String[]{"0"})>0?true:false;
    }
    public static List<NotificationBean> select1(){
        Cursor cursor=DataBase.getWritableDB().query(NOTIFICATION,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                int catId=cursor.getInt(cursor.getColumnIndex(CAT_ID));
              String title=  cursor.getString(cursor.getColumnIndex(TITLE));
              String menuName=cursor.getString(cursor.getColumnIndex(MENU_NAME));
              int newsID=cursor.getInt(cursor.getColumnIndex(NEWS_ID));
                NotificationBean nb=new NotificationBean(newsID,catId,menuName,title);
                notiList.add(nb);
            }while (cursor.moveToNext());
        }
        return notiList;
    }

    /**
     * 等沟通好json 格式再写吧
     * @return
     */
    public static List<BadgeViewBean> select2(){
        Cursor cursor=DataBase.getWritableDB().query(BADGEVIEW,null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {


            }while (cursor.moveToNext());
        }
        return badgeList;
    }
}
