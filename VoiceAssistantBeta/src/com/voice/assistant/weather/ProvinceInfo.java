package com.voice.assistant.weather;

import java.io.Serializable;

public class ProvinceInfo implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = -4940934267112564794L;

	public String _name;
	 public int _id = -1;

	public ProvinceInfo() {

	}

	@Override
	public String toString() {
		return _name;
	}

	public ProvinceInfo(int id, String name) {
		_id = id;
		_name = name;
	}
}
