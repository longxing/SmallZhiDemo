package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiDeviceInfos {
	private String type;
	private List<WifiDeviceInfo> wifiInfos;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<WifiDeviceInfo> getWifiInfo() {
		return wifiInfos;
	}

	public void setWifiInfo(List<WifiDeviceInfo> wifiInfos) {
		this.wifiInfos = wifiInfos;
	}
	public void setWifiInfo(WifiDeviceInfo wifiInfo) {
		if (this.wifiInfos == null) {
			this.wifiInfos = new ArrayList<WifiDeviceInfo>();
		}
		this.wifiInfos.add(wifiInfo);
	}
}
