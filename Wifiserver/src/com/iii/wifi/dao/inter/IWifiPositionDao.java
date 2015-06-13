package com.iii.wifi.dao.inter;


import com.iii.wifi.dao.info.WifiPositionInfo;

public interface IWifiPositionDao {
	public void add(WifiPositionInfo info);

	public void delete(WifiPositionInfo info);

	public void updata(WifiPositionInfo info);

	public WifiPositionInfo select();
	
}
