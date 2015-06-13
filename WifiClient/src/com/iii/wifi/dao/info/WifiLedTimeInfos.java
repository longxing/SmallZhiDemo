package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiLedTimeInfos {
	private String type;
	private List<WifiLedTimeInfo> wifiInfos;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<WifiLedTimeInfo> getWifiInfo() {
		return wifiInfos;
	}

	public void setWifiInfo(List<WifiLedTimeInfo> wifiInfo) {
		this.wifiInfos = wifiInfo;
	}

	public void setWifiInfo(WifiLedTimeInfo wifiInfo) {
		if (this.wifiInfos == null) {
			this.wifiInfos = new ArrayList<WifiLedTimeInfo>();
		}
		this.wifiInfos.add(wifiInfo);
	}
	public String getWifiInfoFirst() {
		if (wifiInfos != null && !wifiInfos.isEmpty()) {
			return wifiInfos.get(0).getLedName();
		}
		return null;
	}
}
