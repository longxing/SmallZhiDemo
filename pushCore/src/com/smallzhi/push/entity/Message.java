package com.smallzhi.push.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.smallzhi.push.util.CommonUtil;


/**
 * 消息对象
 * @author  @author 3979434@qq.com
 *
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	private String type;//request,response,command
	private String sender;//clientImei
	private String receiver;// receiverImei
	private String action;// bind,heart,cmd_word
	private String content;// 读小说
	private String status;//状态 success,error
	private String timestamp;//时间戳
	private Map<String,String> remarkMap = new HashMap<String,String>();//备用字段

	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getSender() {
		return sender;
	}


	public void setSender(String sender) {
		this.sender = sender;
	}


	public String getReceiver() {
		return receiver;
	}


	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = String.valueOf(timestamp.getTime());
	}


	public String toString()
	{
		return CommonUtil.gson.toJson(this);
	}


	/**
	 * @return the remarkMap
	 */
	public Map<String, String> getRemarkMap() {
		return remarkMap;
	}


	/**
	 * @param remarkMap the remarkMap to set
	 */
	public void setRemarkMap(Map<String, String> remarkMap) {
		this.remarkMap = remarkMap;
	}
}
