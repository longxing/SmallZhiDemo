package com.iii.wifi.dao.info;

public class WifiJSONObjectForControlInfo {
	private String type;
	private String error = "0";
	private WifiControlInfos obj;

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

	public WifiControlInfos getObject() {
		return this.obj;
	}

	public void setObject(WifiControlInfos obj) {
		this.obj = obj;
	}

}
