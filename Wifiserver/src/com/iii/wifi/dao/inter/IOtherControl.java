package com.iii.wifi.dao.inter;

import java.util.List;

import com.iii.wifi.dao.info.WifiDeviceInfo;

public interface IOtherControl {

	public String startLearnHF(String deviceId);

	public List<WifiDeviceInfo> getUnConfigedDevice();

}
