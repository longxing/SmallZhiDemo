package com.iii.wifi.dao.inter;

import java.util.List;

import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;

public interface IWifiDeviceDao {
	public void add(WifiDeviceInfo info);

	public void deleteByDeviceId(String deviceId);

	public void updata(WifiDeviceInfo info);

	public List<WifiDeviceInfo> selectByAll(WifiDeviceInfo info);

	public List<WifiDeviceInfo> selectByAll();

	public List<WifiDeviceInfo> selectByDeviceId(String deviceId);
	
	public List<WifiDeviceInfo> selectByMacAdd(String macAdd);

	public List<WifiDeviceInfo> selectByRoomId(String roomId);

	public List<WifiDeviceInfo> selectByDeviceName(String macAdd,String roomId);
}
