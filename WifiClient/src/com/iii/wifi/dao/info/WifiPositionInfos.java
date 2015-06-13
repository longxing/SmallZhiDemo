package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiPositionInfos {
	private String type;
	private List<WifiPositionInfo> wifiInfos;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<WifiPositionInfo> getWifiInfo() {
		return wifiInfos;
	}

	public void setWifiInfo(List<WifiPositionInfo> wifiInfo) {
		this.wifiInfos = wifiInfo;
	}

	public void setWifiInfo(WifiPositionInfo wifiInfo) {
		if (this.wifiInfos == null) {
			this.wifiInfos = new ArrayList<WifiPositionInfo>();
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
