package com.iii.wifi.dao.inter;

import com.iii.wifi.dao.info.WifiUserInfo;

public interface IWifiUserDao {
	public void add(WifiUserInfo info);

	public void delete(WifiUserInfo info);

	public void updata(WifiUserInfo info);

	public WifiUserInfo selectAll();

}
