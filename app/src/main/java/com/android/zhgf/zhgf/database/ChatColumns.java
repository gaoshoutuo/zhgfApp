package com.android.zhgf.zhgf.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.android.zhgf.zhgf.database.bean.Chat;
import com.android.zhgf.zhgf.database.local.DataBase;

import java.util.ArrayList;
import java.util.List;

public class ChatColumns {
    private static final int pageSize = 15;

	public static final String TABLE_NAME = "chat";

	public static final String _ID = "_id";

	public static final String FROM_ID = "from_id";

	public static final String CONTENT = "content";

	public static final String HOLDER = "holder";

	public static final String TYPE = "type";

	public static final String TIME = "time";
	
	public static final String[] COLUMN_ARRAY = {
			_ID,
			FROM_ID,
			CONTENT,
			HOLDER,
			TYPE,
			TIME
	};
    public static final String FOR_FROM_ID = ""
            + "SELECT *\r\n"
            + "FROM chat\r\n"
            + "WHERE from_id = ? AND time < ?\r\n"
            + "ORDER BY time DESC\r\n"
            + "LIMIT ? offset ?";

    public static final String HAS_MORE = "SELECT 1 FROM "+ TABLE_NAME + " WHERE " + FROM_ID + "=? AND "+ TIME + " <?";

	public static String CREAT_TABLE(String tableName){
		return new StringBuffer().
				append("CREATE TABLE IF NOT EXISTS ").append(tableName).
				append("(").
				append(_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,").
				append(FROM_ID).append(" TEXT NOT NULL,").
				append(CONTENT).append(" TEXT NOT NULL,").
				append(HOLDER).append(" INTEGER DEFAULT 0 NOT NULL,").
				append(TYPE).append(" TEXT,").
				append(TIME).append(" INTEGER NOT NULL").
				append(");").toString();
	}
	
	private static String DROP_TABLE(){
		return "DROP TABLE IF EXISTS " +  TABLE_NAME;
	}

	public static boolean insert(Chat c ){
		ContentValues cv = getValue(c);
		return DataBase.getWritableDB().insert(TABLE_NAME, null, cv)>= 0 ?true:false;
	}

    //根据用户查询，与该用户的所有对话
    public static List<Chat> byFrom(String from, int page, long time) {
        ArrayList<Chat> chats = new ArrayList<>();
        Cursor cursor = DataBase.getReadableDB().rawQuery(FOR_FROM_ID, new String[]{from, String.valueOf(time), "15", String.valueOf(page * pageSize)});
        //Cursor cursor = DataBase.getReadableDB().rawQuery("SELECT * FROM chat",null);
        while (cursor.moveToNext()) {
            Chat chat = getChat(cursor);
            chats.add(chat);
        }
        cursor.close();
        return chats;
    }

    //与改用户在改时间之前是否还有会话
    public static boolean hasMore(String from, long time){
        Cursor cursor = DataBase.getReadableDB().rawQuery(HAS_MORE, new String[]{from, String.valueOf(time)});
        boolean b = cursor.moveToNext();
        cursor.close();
        return b;
    }

	private static ContentValues getValue(Chat chat) {
		ContentValues values = new ContentValues();
		//values.put(_ID,  chat.getId());
		values.put(FROM_ID, chat.getFromId());
		values.put(CONTENT, chat.getContent());
		values.put(HOLDER, chat.getHolder());
		values.put(TYPE, chat.getType());
		values.put(TIME, chat.getTime());
		return values;
	}
	private static Chat getChat(Cursor cursor){
		Chat chat = new Chat();
		chat.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
		chat.setFromId(cursor.getString(cursor.getColumnIndex(FROM_ID)));
		chat.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));
		chat.setHolder(cursor.getInt(cursor.getColumnIndex(HOLDER)));
		chat.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
		chat.setTime(cursor.getLong(cursor.getColumnIndex(TIME)));
		//cursor.close();
		return chat;
	}
}
