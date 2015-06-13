package com.iii.wifi.dao.info;

public class WifiJSONObjectForLedInfo {
	private String type;
	private String error = "0";
	private WifiLedStatusInfos obj;

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

	public WifiLedStatusInfos getObject() {
		return this.obj;
	}

	public void setObject(WifiLedStatusInfos obj) {
		this.obj = obj;
	}

}
