package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiRoomInfos {
	private String type;
	private List<WifiRoomInfo> wifiInfos;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<WifiRoomInfo> getWifiInfo() {
		return wifiInfos;
	}

	public void setWifiInfo(List<WifiRoomInfo> wifiInfo) {
		this.wifiInfos = wifiInfo;
	}

	public void setWifiInfo(WifiRoomInfo wifiInfo) {
		if (this.wifiInfos == null) {
			this.wifiInfos = new ArrayList<WifiRoomInfo>();
		}
		this.wifiInfos.add(wifiInfo);
	}
}
