package com.iii.wifi.dao.inter;

import com.iii.wifi.dao.info.HuanTengAccount;

public interface IWifiHuanTengDao {
	int DELETE_SUCCESS = 1;
	int DELETE_FAIL = -1;

	public void set(HuanTengAccount info);


	public HuanTengAccount get();

	void delete();

}
