package com.iii.wifi.dao.info;

import java.io.Serializable;

public class HuanTengAccount implements Serializable {
	private String type;
	private String username;
	private String password;
	private long AddTime;
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getAddTime() {
		return AddTime;
	}
	public void setAddTime(long addTime) {
		AddTime = addTime;
	}
}
