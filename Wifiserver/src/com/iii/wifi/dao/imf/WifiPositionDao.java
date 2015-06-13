package com.iii.wifi.dao.imf;

import android.content.Context;

import com.iii.wifi.dao.info.WifiPositionInfo;
import com.iii.wifi.dao.inter.IWifiPositionDao;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.SuperBaseContext;

public class WifiPositionDao implements IWifiPositionDao {
	private static final String LED_POSITION_TYPE = KeyList.PKEY_LOCATION;
	private SuperBaseContext mPreferenceUtil;

	public WifiPositionDao(Context context) {
		mPreferenceUtil = new SuperBaseContext(context);
	}

	@Override
	public void add(WifiPositionInfo info) {
		// TODO Auto-generated method stub
		mPreferenceUtil.setPrefString(LED_POSITION_TYPE, info.getLedName());
	}

	@Override
	public void delete(WifiPositionInfo info) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updata(WifiPositionInfo info) {
		// TODO Auto-generated method stub
		mPreferenceUtil.setPrefString(LED_POSITION_TYPE, info.getLedName());
	}

	@Override
	public WifiPositionInfo select() {
		// TODO Auto-generated method stub
		WifiPositionInfo info = new WifiPositionInfo();
		String position = mPreferenceUtil.getPrefString(LED_POSITION_TYPE, "");
		if (position != null && !position.equals("")) {
			info.setLedName(position);
		}
		return info;
	}

}
