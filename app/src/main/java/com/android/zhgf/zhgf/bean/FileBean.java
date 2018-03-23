package com.android.zhgf.zhgf.bean;

import com.android.zhgf.zhgf.utils.annotation.TreeNodeId;
import com.android.zhgf.zhgf.utils.annotation.TreeNodeLabel;
import com.android.zhgf.zhgf.utils.annotation.TreeNodePid;

public class FileBean {

	/**
	 * 当前id
	 */
	@TreeNodeId/*(type = integer.class)*/
	private int id;
	/**
	 * 父节点id
	 */
	@TreeNodePid
	private int pId;
	/**
	 * 标记名称
	 */
	@TreeNodeLabel
	private String lable;

	
	
	public FileBean(int id, int pId, String lable) {
		this.id = id;
		this.pId = pId;
		this.lable = lable;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getpId() {
		return pId;
	}

	public void setpId(int pId) {
		this.pId = pId;
	}

	public String getLable() {
		return lable;
	}

	public void setLable(String lable) {
		this.lable = lable;
	}

}
