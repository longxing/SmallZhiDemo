package com.iii.wifi.thirdpart.yaokongbao;

import java.util.Arrays;

/**
 * 遥控宝设备信息
 * 
 * @author Peter
 * @data 2015年5月25日下午2:58:02
 */

public class YaoKongBaoDeviceInfo {

	private byte[] mDeviceId = null;   // device id (md5, 32byte)

	private String mDeviceName = null;

	private boolean isOnline = false;
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(mDeviceId);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		YaoKongBaoDeviceInfo other = (YaoKongBaoDeviceInfo) obj;
		if (!Arrays.equals(mDeviceId, other.mDeviceId))
			return false;
		return true;
	}
	
	


	public byte[] getmDeviceId() {
		return mDeviceId;
	}


	public void setmDeviceId(byte[] mDeviceId) {
		this.mDeviceId = mDeviceId;
	}


	public String getmDeviceName() {
		return mDeviceName;
	}


	public void setmDeviceName(String mDeviceName) {
		this.mDeviceName = mDeviceName;
	}


	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

}
