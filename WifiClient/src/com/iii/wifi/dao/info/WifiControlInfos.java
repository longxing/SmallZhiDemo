package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiControlInfos {
	private String type;
	private List<WifiControlInfo> wifiInfos;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<WifiControlInfo> getWifiInfo() {
		return wifiInfos;
	}

	public void setWifiInfo(List<WifiControlInfo> wifiInfo) {
		this.wifiInfos = wifiInfo;
	}

	public void setWifiInfo(WifiControlInfo wifiInfo) {
		if (this.wifiInfos == null) {
			this.wifiInfos = new ArrayList<WifiControlInfo>();
		}
		this.wifiInfos.add(wifiInfo);
	}
	public WifiControlInfo getWifiInfoFirst() {
		if (wifiInfos != null && !wifiInfos.isEmpty()) {
			return wifiInfos.get(0);
		}
		return null;
	}
}
