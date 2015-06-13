package com.iii.wifi.dao.info;

public class WifiJSONObjectForReminderInfo {
	private String type;
	private String error = "0";
	private WifiRemindInfos obj;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public WifiRemindInfos getObject() {
		return this.obj;
	}

	public void setObject(WifiRemindInfos obj) {
		this.obj = obj;
	}
}
