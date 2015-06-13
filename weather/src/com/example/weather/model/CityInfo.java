package com.example.weather.model;

import java.io.Serializable;

public class CityInfo implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = 2278535655456608022L;
	public String _cityCode;
	public int _provinceId;
	public String _name;
	 public int _id = -1;

	public CityInfo() {
		_cityCode = "101010100";
		_provinceId = 0;
		_name = "上海";
	}

	@Override
	public String toString() {
		return _name;
	}

	public CityInfo(String code, int provinceId, String name) {
		_cityCode = code;
		_provinceId = provinceId;
		_name = name;
	}
}
