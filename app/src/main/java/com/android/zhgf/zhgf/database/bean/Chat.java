package com.android.zhgf.zhgf.database.bean;
/*
/成员 类
 */
public class Chat {

	public Chat(){
	}
	
	private int id;
	private String fromId;
	private String content;
	private int holder;
	private String type;
	private long time;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getHolder() {
		return holder;
	}

	public void setHolder(int holder) {
		this.holder = holder;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isHolder() {
		boolean isholder = false;
		if(holder == 1){
			isholder = true;
		}
		return isholder;
	}
//@Override
	/*public String toString() {
		return "id:" +id + "name"+ name + "phone" + phone + "sex" + sex +"birth" + birth+"job"+ job+"ismarried:"+ismarried+"address"+address+"baptism" + baptism+" groupid: " + groupid +" groupname:" + groupname;
	}*/
	
	
	
}
