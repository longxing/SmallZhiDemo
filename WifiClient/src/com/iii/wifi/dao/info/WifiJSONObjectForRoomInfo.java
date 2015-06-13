package com.iii.wifi.dao.info;

public class WifiJSONObjectForRoomInfo {
	private String type;
	private String error = "0";
	private WifiRoomInfos obj;

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

	public WifiRoomInfos getObject() {
		return this.obj;
	}

	public void setObject(WifiRoomInfos obj) {
		this.obj = obj;
	}

}
