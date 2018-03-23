package com.android.zhgf.zhgf.database.local;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


import com.android.zhgf.zhgf.database.ChatColumns;
import com.android.zhgf.zhgf.database.ContactColumns;
import com.android.zhgf.zhgf.database.NotificationTable;
import com.android.zhgf.zhgf.utils.ObjectHolder;

/**
 * 本地数据库
 */

public final class DataBase extends SQLiteOpenHelper {

    private static DataBase instance;

    private DataBase() {
        super(ObjectHolder.context, "xmpp_db", null, 1);
    }

    private static DataBase getInstance() {
        if (instance == null) {
            synchronized (DataBase.class) {
                if (instance == null) {
                    instance = new DataBase();
                }
            }
        }
        return instance;
    }

    public static SQLiteDatabase getReadableDB(){
        return getInstance().getReadableDatabase();
    }

    public static SQLiteDatabase getWritableDB(){
        return getInstance().getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ChatColumns.CREAT_TABLE(ChatColumns.TABLE_NAME));
        db.execSQL(ContactColumns.CREAT_TABLE(ContactColumns.TABLE_NAME));
        db.execSQL(NotificationTable.createTable1(NotificationTable.NOTIFICATION));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }

    /*public static void beginTransaction(SQLiteDatabase db){
        db.beginTransaction();
    }

    public static void endTransaction(SQLiteDatabase db){
        db.endTransaction();
    }

    public static void setTransactionSuccessful(SQLiteDatabase db){
        db.setTransactionSuccessful();
    }

    public static SQLiteStatement getSQLiteStatement (SQLiteDatabase db, String sql){
        return db.compileStatement(sql);
    }*/
}
