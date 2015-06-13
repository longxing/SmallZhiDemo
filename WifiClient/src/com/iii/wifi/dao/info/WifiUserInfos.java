package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiUserInfos {
	private String type;
	private List<WifiUserInfo> wifiInfos;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<WifiUserInfo> getWifiInfo() {
		return wifiInfos;
	}

	public void setWifiInfo(List<WifiUserInfo> wifiInfo) {
		this.wifiInfos = wifiInfo;
	}

	public void setWifiInfo(WifiUserInfo wifiInfo) {
		if (this.wifiInfos == null) {
			this.wifiInfos = new ArrayList<WifiUserInfo>();
		}
		if (wifiInfo != null) {
			this.wifiInfos.add(wifiInfo);
		}
	}
	
	
	public WifiUserInfo getWifiInfoFirst() {
		if (wifiInfos != null && !wifiInfos.isEmpty()) {
			return wifiInfos.get(0);
		}
		return null;
	}
}
