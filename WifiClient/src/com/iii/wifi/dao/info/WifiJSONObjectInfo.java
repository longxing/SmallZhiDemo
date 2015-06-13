package com.iii.wifi.dao.info;

public class WifiJSONObjectInfo {
	private String type;
	private String error = "0";
	private Object obj;
	private String operateTime ="0";

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getObject() {
		return this.obj;
	}

	public void setObject(Object obj) {
		this.obj = obj;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}
	
	

}
