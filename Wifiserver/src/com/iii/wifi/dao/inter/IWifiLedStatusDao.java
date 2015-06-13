package com.iii.wifi.dao.inter;


import com.iii.wifi.dao.info.WifiLedStatusInfo;

public interface IWifiLedStatusDao {
	public void add(WifiLedStatusInfo info);

	public void delete(WifiLedStatusInfo info);

	public void updata(WifiLedStatusInfo info);

	public WifiLedStatusInfo select();
	
}
