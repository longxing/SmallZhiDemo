package com.iii.wifi.dao.inter;

import java.util.List;

import com.iii.wifi.dao.info.WifiRoomInfo;

public interface IWifiRoomDao {
	public void add(WifiRoomInfo info);

	public void deleteByRoomId(String roomId);

	public void updata(WifiRoomInfo info);

	public List<WifiRoomInfo> selectByAll(WifiRoomInfo info);

	public List<WifiRoomInfo> selectByAll();

	public List<WifiRoomInfo> selectByRoomId(String roomId);

	public List<WifiRoomInfo> selectByRoomName(String roomName);

}
