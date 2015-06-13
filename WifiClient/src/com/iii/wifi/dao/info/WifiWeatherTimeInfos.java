package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiWeatherTimeInfos {
	private String type;
	private List<WifiWeatherTimeInfo> wifiInfos;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<WifiWeatherTimeInfo> getWifiInfo() {
		return wifiInfos;
	}

	public void setWifiInfo(List<WifiWeatherTimeInfo> wifiInfo) {
		this.wifiInfos = wifiInfo;
	}

	public void setWifiInfo(WifiWeatherTimeInfo wifiInfo) {
		if (this.wifiInfos == null) {
			this.wifiInfos = new ArrayList<WifiWeatherTimeInfo>();
		}
		this.wifiInfos.add(wifiInfo);
	}
	public String getWifiInfoFirst() {
		if (wifiInfos != null && !wifiInfos.isEmpty()) {
			return wifiInfos.get(0).getTimeingWeatherReportTime();
		}
		return null;
	}
}
