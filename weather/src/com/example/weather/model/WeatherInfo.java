package com.example.weather.model;

import java.io.Serializable;

public class WeatherInfo implements Serializable {
	private final static String DEFAULT_VALUE = "N/A";

	public static class ItemInfo implements Serializable {
		public String _weatherName = DEFAULT_VALUE;
		public String _temp = DEFAULT_VALUE;
		public String _wind = DEFAULT_VALUE;
		public String _imgType = DEFAULT_VALUE;
	}

	public String _city = DEFAULT_VALUE;
	public String _date = DEFAULT_VALUE;
	public String _week = DEFAULT_VALUE;
	public String _info = DEFAULT_VALUE;
	public String _temp = DEFAULT_VALUE;

	public ItemInfo[] ItemInfoList;
}
