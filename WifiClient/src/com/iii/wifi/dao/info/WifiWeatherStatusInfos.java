package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiWeatherStatusInfos {
	private String type;
	private List<WifiWeatherStatusInfo> wifiInfos;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<WifiWeatherStatusInfo> getWifiInfo() {
		return wifiInfos;
	}

	public void setWifiInfo(List<WifiWeatherStatusInfo> wifiInfo) {
		this.wifiInfos = wifiInfo;
	}

	public void setWifiInfo(WifiWeatherStatusInfo wifiInfo) {
		if (this.wifiInfos == null) {
			this.wifiInfos = new ArrayList<WifiWeatherStatusInfo>();
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
