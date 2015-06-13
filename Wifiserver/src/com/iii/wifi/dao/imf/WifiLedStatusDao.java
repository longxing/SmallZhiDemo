package com.iii.wifi.dao.imf;

import android.content.Context;

import com.iii.wifi.dao.info.WifiLedStatusInfo;
import com.iii.wifi.dao.inter.IWifiLedStatusDao;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.SuperBaseContext;

public class WifiLedStatusDao implements IWifiLedStatusDao {
	private static final String LED_STATUS = KeyList.PKEY_LED_STATUS;
	private SuperBaseContext mPreferenceUtil;

	public WifiLedStatusDao(Context context) {
		mPreferenceUtil = new SuperBaseContext(context);
	}

	@Override
	public void add(final WifiLedStatusInfo info) {
		KeyList.messageQueue.post(new Runnable() {
			
			public void run() {
				KeyList.TTSUtil.setLightEnable(Boolean.valueOf(info.getLedName()));
				mPreferenceUtil.setPrefString(LED_STATUS, info.getLedName());
			}
		});
	}

	@Override
	public void delete(WifiLedStatusInfo info) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updata(final WifiLedStatusInfo info) {
		KeyList.messageQueue.post(new Runnable() {
			
			public void run() {
				mPreferenceUtil.setPrefString(LED_STATUS, info.getLedName());
				KeyList.TTSUtil.setLightEnable(Boolean.valueOf(info.getLedName()));
			}
		});
	}

	@Override
	public WifiLedStatusInfo select() {
		// TODO Auto-generated method stub
		WifiLedStatusInfo info = new WifiLedStatusInfo();
		String name = mPreferenceUtil.getPrefString(LED_STATUS, "false");
		if (name != null && !name.equals("")) {
			info.setLedName(name);
		}
		return info;
	}

}
