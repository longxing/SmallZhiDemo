package com.iii.wifi.dao.inter;


import com.iii.wifi.dao.info.WifiWeatherStatusInfo;

public interface IWifiWeatherStatusDao {
	public void add(WifiWeatherStatusInfo info);

	public void delete(WifiWeatherStatusInfo info);

	public void updata(WifiWeatherStatusInfo info);

	public WifiWeatherStatusInfo select();
	
}
