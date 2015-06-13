package com.iii.wifi.dao.info;

public class WifiJSONObjectForDeviceInfo {
	private String type;
	private String error = "0";
	private WifiDeviceInfos obj;

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

	public WifiDeviceInfos getObject() {
		return this.obj;
	}

	public void setObject(WifiDeviceInfos obj) {
		this.obj = obj;
	}
}
