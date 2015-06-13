package com.iii.wifi.dao.info;

public class WifiJSONObjectForTTSInfo {
	private String type;
	private String error = "0";
	private WifiTTSVocalizationTypeInfos obj;

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

	public WifiTTSVocalizationTypeInfos getObject() {
		return this.obj;
	}

	public void setObject(WifiTTSVocalizationTypeInfos obj) {
		this.obj = obj;
	}

}
