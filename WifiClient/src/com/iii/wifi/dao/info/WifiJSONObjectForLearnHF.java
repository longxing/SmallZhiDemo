package com.iii.wifi.dao.info;

import java.util.List;

public class WifiJSONObjectForLearnHF {
	private String sendInfo;
	private String HFContent;
	private String type;
	private List<WifiDeviceInfo> deviceInfos;

	public List<WifiDeviceInfo> getDeviceInfos() {
		return deviceInfos;
	}

	public void setDeviceInfos(List<WifiDeviceInfo> deviceInfos) {
		this.deviceInfos = deviceInfos;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDeviceID() {
		return sendInfo;
	}

	public void setDeviceID(String deviceID) {
		sendInfo = deviceID;
	}

	public String getHFContent() {
		return HFContent;
	}

	public void setHFContent(String hFContent) {
		HFContent = hFContent;
	}

}
