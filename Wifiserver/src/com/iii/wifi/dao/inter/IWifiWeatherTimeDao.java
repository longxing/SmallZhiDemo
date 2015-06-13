package com.iii.wifi.dao.inter;


import com.iii.wifi.dao.info.WifiWeatherTimeInfo;

public interface IWifiWeatherTimeDao {
	public void add(WifiWeatherTimeInfo info);

	public void delete(WifiWeatherTimeInfo info);

	public void updata(WifiWeatherTimeInfo info);
	
	public WifiWeatherTimeInfo select();
	
	public void SetWeatherReportCityName(String info);
	
}
