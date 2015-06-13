package com.iii.wifi.dao.info;

public class WifiJSONObjectForUserInfo {
	private String type;
	private String error = "0";
	private WifiUserInfos obj;

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

	public WifiUserInfos getObject() {
		return this.obj;
	}

	public void setObject(WifiUserInfos obj) {
		this.obj = obj;
	}

}
