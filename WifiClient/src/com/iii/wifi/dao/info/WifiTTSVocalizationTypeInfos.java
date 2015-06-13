package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiTTSVocalizationTypeInfos {
	private String type;
	private List<WifiTTSVocalizationTypeInfo> wifiInfos;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<WifiTTSVocalizationTypeInfo> getWifiInfo() {
		return wifiInfos;
	}

	public void setWifiInfo(List<WifiTTSVocalizationTypeInfo> wifiInfo) {
		this.wifiInfos = wifiInfo;
	}

	public void setWifiInfo(WifiTTSVocalizationTypeInfo wifiInfo) {
		if (this.wifiInfos == null) {
			this.wifiInfos = new ArrayList<WifiTTSVocalizationTypeInfo>();
		}
		this.wifiInfos.add(wifiInfo);
	}
	public String getWifiInfoFirst() {
		if (wifiInfos != null && !wifiInfos.isEmpty()) {
			return wifiInfos.get(0).getType();
		}
		return null;
	}
}
