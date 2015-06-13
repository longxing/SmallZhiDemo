package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiJSONObjectForLearnHFs {
	private String type;
	private List<WifiJSONObjectForLearnHF> wifiInfos;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<WifiJSONObjectForLearnHF> getWifiInfo() {
		return wifiInfos;
	}

	public void setWifiInfo(List<WifiJSONObjectForLearnHF> wifiInfo) {
		this.wifiInfos = wifiInfo;
	}

	public void setWifiInfo(WifiJSONObjectForLearnHF wifiInfo) {
		if (this.wifiInfos == null) {
			this.wifiInfos = new ArrayList<WifiJSONObjectForLearnHF>();
		}
		this.wifiInfos.add(wifiInfo);
	}
	public WifiJSONObjectForLearnHF getWifiInfoFirst() {
		if (wifiInfos != null && !wifiInfos.isEmpty()) {
			return wifiInfos.get(0);
		}
		return null;
	}
}
