package com.iii.wifi.dao.inter;


import com.iii.wifi.dao.info.WifiLedTimeInfo;

public interface IWifiLedTimeDao {
	public void add(WifiLedTimeInfo info);

	public void delete(WifiLedTimeInfo info);

	public void updata(WifiLedTimeInfo info);

	public WifiLedTimeInfo select();
	
}
