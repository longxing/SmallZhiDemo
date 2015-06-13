package com.iii.wifi.dao.info;

public class WifiJSONObjectForLedTimeInfo {
	private String type;
	private String error = "0";
	private WifiLedTimeInfos obj;

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

	public WifiLedTimeInfos getObject() {
		return this.obj;
	}

	public void setObject(WifiLedTimeInfos obj) {
		this.obj = obj;
	}

}
