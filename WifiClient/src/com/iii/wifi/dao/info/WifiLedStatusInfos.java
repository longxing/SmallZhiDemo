package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiLedStatusInfos {
	private String type;
	private List<WifiLedStatusInfo> wifiInfos;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<WifiLedStatusInfo> getWifiInfo() {
		return wifiInfos;
	}

	public void setWifiInfo(List<WifiLedStatusInfo> wifiInfo) {
		this.wifiInfos = wifiInfo;
	}

	public void setWifiInfo(WifiLedStatusInfo wifiInfo) {
		if (this.wifiInfos == null) {
			this.wifiInfos = new ArrayList<WifiLedStatusInfo>();
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
