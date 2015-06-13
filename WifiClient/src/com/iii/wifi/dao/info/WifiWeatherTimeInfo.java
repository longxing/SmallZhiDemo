package com.iii.wifi.dao.info;

public class WifiWeatherTimeInfo {
	private String weatherTime;
	private String weartherCityName = null;
	private boolean isOpen = false;

	public String getTimeingWeatherReportTime() {
		return weatherTime;
	}

	public String getWeartherCityName() {
		return weartherCityName;
	}

	public void setWeartherCityName(String weartherCityName) {
		this.weartherCityName = weartherCityName;
	}

	public void setTimeingWeatherReportTime(String weatherTime) {
		this.weatherTime = weatherTime;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

}
