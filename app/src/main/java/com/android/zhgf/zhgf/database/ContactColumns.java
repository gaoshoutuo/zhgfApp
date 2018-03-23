package com.android.zhgf.zhgf.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.android.zhgf.zhgf.database.bean.Chat;
import com.android.zhgf.zhgf.database.bean.Contact;
import com.android.zhgf.zhgf.database.local.DataBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZXX001 on 2017/11/21.
 */

public class ContactColumns {

    public static final String TABLE_NAME = "contact";

    private static final String NODE_ID = "node_id";

    private static final String NAME = "name";

    private static final String TELEPHONENO = "telephoneNo";

    private static final String UTIL = "util";

    private static final String PARENT_ID = "parent_id";

    private static final String PERSONFLG = "personFlg";

    private static final String INSERTSQL = "insert into contact(node_id,name,telephoneNo,util,parent_id,personFlg) values(?,?,?,?,?,?)";

    public static final String SELECT_ALLPEOPLE = ""
            + "SELECT *\r\n"
            + "FROM contact\r\n"
            + "WHERE personFlg = 1\r\n"
            + "ORDER BY name";

    public static final String SELECT_PEOPLE = ""
            + "SELECT *\r\n"
            + "FROM contact\r\n"
            + "WHERE personFlg = 1\r\n"
            + "AND name like ?" + "\r\n"
            //+ "AND （name like ?" + "% "
            //+ " OR TELEPHONENO like ?" + "% )\n"
            + "ORDER BY name";

    public static String CREAT_TABLE(String tableName){
        return new StringBuffer().
                append("CREATE TABLE IF NOT EXISTS ").append(tableName).
                append("(").
                append(NODE_ID).append(" TEXT NOT NULL,").
                append(NAME).append(" TEXT NOT NULL,").
                append(TELEPHONENO).append(" TEXT,").
                append(UTIL).append(" TEXT,").
                append(PARENT_ID).append(" TEXT NOT NULL,").
                append(PERSONFLG).append(" INTEGER DEFAULT 0 NOT NULL").
                append(");").toString();
    }

    private static String DROP_TABLE(){
        return "DROP TABLE IF EXISTS " +  TABLE_NAME;
    }

    private static String DELETE_TABLE(){
        return "DELETE FROM " +  TABLE_NAME;
    }

    public static void deleteAll(){
        DataBase.getWritableDB().execSQL(DELETE_TABLE());
    }
    public static boolean insert(Contact c ){
        ContentValues cv = getValue(c);
        return DataBase.getWritableDB().insert(TABLE_NAME, null, cv)>= 0 ?true:false;
    }

    public static void insert(List<Contact> contactList){
        SQLiteDatabase db = DataBase.getWritableDB();
        SQLiteStatement stat = db.compileStatement(INSERTSQL);
        db.beginTransaction();
        for (Contact line : contactList) {
            stat.bindString(1, line.getNode_id());
            stat.bindString(2, line.getName());
            stat.bindString(3, line.getTelephoneNo());
            stat.bindString(4, line.getUtil());
            stat.bindString(5, line.getParent_id());
            stat.bindLong(6, line.getPersonFlg());
            stat.executeInsert();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public static List<Contact> selectAll(){
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor cursor = DataBase.getReadableDB().rawQuery(SELECT_ALLPEOPLE,null);
        while (cursor.moveToNext()) {
            Contact contact = getContact(cursor);
            contacts.add(contact);
        }
        cursor.close();
        return contacts;
    }

    public static List<Contact> selectPeople(String name){
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor cursor = DataBase.getReadableDB().rawQuery(SELECT_PEOPLE,new String[]{name + "%"});
        while (cursor.moveToNext()) {
            Contact contact = getContact(cursor);
            contacts.add(contact);
        }
        cursor.close();
        return contacts;
    }

    private static ContentValues getValue(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(NODE_ID, contact.getNode_id());
        values.put(NAME, contact.getName());
        values.put(TELEPHONENO, contact.getTelephoneNo());
        values.put(UTIL, contact.getUtil());
        values.put(PARENT_ID, contact.getParent_id());
        values.put(PERSONFLG, contact.getPersonFlg());
        return values;
    }

    private static Contact getContact(Cursor cursor){
        Contact contact = new Contact();
        contact.setNode_id(cursor.getString(cursor.getColumnIndex(NODE_ID)));
        contact.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        contact.setTelephoneNo(cursor.getString(cursor.getColumnIndex(TELEPHONENO)));
        contact.setUtil(cursor.getString(cursor.getColumnIndex(UTIL)));
        contact.setParent_id(cursor.getString(cursor.getColumnIndex(PARENT_ID)));
        contact.setPersonFlg(cursor.getInt(cursor.getColumnIndex(PERSONFLG)));
        contact.setOnlineBln(false);
        return contact;
    }
}
