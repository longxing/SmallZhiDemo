package com.iii.wifi.dao.info;

public class WifiJSONObjectForPositionInfo {
	private String type;
	private String error = "0";
	private WifiPositionInfos obj;

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

	public WifiPositionInfos getObject() {
		return this.obj;
	}

	public void setObject(WifiPositionInfos obj) {
		this.obj = obj;
	}
}
