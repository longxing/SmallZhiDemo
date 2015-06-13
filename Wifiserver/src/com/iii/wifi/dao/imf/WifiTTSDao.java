package com.iii.wifi.dao.imf;

import android.content.Context;

import com.iii.wifi.dao.info.WifiTTSVocalizationTypeInfo;
import com.iii.wifi.dao.inter.IWifiTTSDao;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;

public class WifiTTSDao implements IWifiTTSDao {
	private static final String TTS_TYPE = KeyList.PKEY_TTS_TYPE;
	private SuperBaseContext mPreferenceUtil;

	public WifiTTSDao(Context context) {
		mPreferenceUtil = new SuperBaseContext(context);
	}

	@Override
	public void add(WifiTTSVocalizationTypeInfo info) {
		// TODO Auto-generated method stub
		mPreferenceUtil.setPrefString(TTS_TYPE, info.getType());
		KeyList.TTSUtil.setType(Integer.valueOf(info.getType()));
	}

	@Override
	public void delete(WifiTTSVocalizationTypeInfo info) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updata(WifiTTSVocalizationTypeInfo info) {
		// TODO Auto-generated method stub
		mPreferenceUtil.setPrefString(TTS_TYPE, info.getType());
		KeyList.TTSUtil.setType(Integer.valueOf(info.getType()));
	}

	@Override
	public WifiTTSVocalizationTypeInfo select() {
		// TODO Auto-generated method stub
		WifiTTSVocalizationTypeInfo info = new WifiTTSVocalizationTypeInfo();
		String type = mPreferenceUtil.getPrefString(TTS_TYPE, "0");
		LogManager.e("set tts type = "+type);
		if (type != null && !type.equals("")) {
			info.setType(type);
		}
		
		int t = 0;
        try {
            t = Integer.valueOf(type);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mPreferenceUtil.setPrefString(TTS_TYPE, "0");
        }
		KeyList.TTSUtil.setType(t);
		return info;
	}

}
