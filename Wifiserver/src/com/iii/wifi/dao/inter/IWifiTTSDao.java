package com.iii.wifi.dao.inter;


import com.iii.wifi.dao.info.WifiTTSVocalizationTypeInfo;

public interface IWifiTTSDao {
	public void add(WifiTTSVocalizationTypeInfo info);

	public void delete(WifiTTSVocalizationTypeInfo info);

	public void updata(WifiTTSVocalizationTypeInfo info);

	public WifiTTSVocalizationTypeInfo select();
	
}
