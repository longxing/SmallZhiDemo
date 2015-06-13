package com.iii.wifi.dao.info;

public class WifiJSONObjectForWeatherTimeInfo {
	private String type;
	private String error = "0";
	private WifiWeatherTimeInfos obj;

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

	public WifiWeatherTimeInfos getObject() {
		return this.obj;
	}

	public void setObject(WifiWeatherTimeInfos obj) {
		this.obj = obj;
	}

}
