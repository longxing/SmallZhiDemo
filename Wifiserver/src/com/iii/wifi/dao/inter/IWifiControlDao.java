package com.iii.wifi.dao.inter;

import java.util.List;

import com.iii.wifi.dao.info.WifiControlInfo;

public interface IWifiControlDao {
	public void add(WifiControlInfo info);

	public void delete(int id);

	public void updata(WifiControlInfo info);

	public List<WifiControlInfo> selectById(int id);

	public List<WifiControlInfo> selectByRoomId(String roomId);
	
	public List<WifiControlInfo> selectByDeviceId(String deviceid);

	public List<WifiControlInfo> selectByAll(WifiControlInfo info);
	
	public List<WifiControlInfo> selectByRoomIdAndDeviceIdAndAction(WifiControlInfo info);
	
	public List<WifiControlInfo> selectByRoomIdAndDeviceId(WifiControlInfo info);
	
	public List<WifiControlInfo> selectAll();
}
