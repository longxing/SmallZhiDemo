package com.iii.wifi.dao.info;

public class WifiJSONObjectForWeatherInfo {
	private String type;
	private String error = "0";
	private WifiWeatherStatusInfos obj;

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

	public WifiWeatherStatusInfos getObject() {
		return this.obj;
	}

	public void setObject(WifiWeatherStatusInfos obj) {
		this.obj = obj;
	}

}
