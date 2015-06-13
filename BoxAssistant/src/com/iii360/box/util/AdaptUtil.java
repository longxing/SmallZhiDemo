package com.iii360.box.util;

import android.text.TextUtils;

import com.iii360.box.MyApplication;
import com.iii360.box.common.BasePreferences;

public class AdaptUtil {
	public static boolean isNewProtocol252() {
		if (!MyApplication.isNeedJudgeVersion)
			return true;
		BasePreferences basePreferences = new BasePreferences(MyApplication.instance);
		String currVersion = basePreferences.getPrefString(KeyList.PKEY_REQUEST_HARDVERSION, "");
		LogUtil.d("2.5.2音箱版本新功能比较:" + currVersion);
		if (TextUtils.isEmpty(currVersion) || "null".equals(currVersion))
			return false;
		if (currVersion.compareTo(KeyList.KEY_HARDVERSION252) <= 0) {
			return false;
		}
		return true;
	}
	public static boolean isNewProtocol260() {
		if (!MyApplication.isNeedJudgeVersion)
			return true;
		BasePreferences basePreferences = new BasePreferences(MyApplication.instance);
		String currVersion = basePreferences.getPrefString(KeyList.PKEY_REQUEST_HARDVERSION, "");
		LogUtil.d("2.6.0音箱版本新功能比较:" + currVersion);
		if (TextUtils.isEmpty(currVersion) || "null".equals(currVersion))
			return false;
		if (currVersion.compareTo(KeyList.KEY_HARDVERSION260) <= 0) {
			return false;
		}
		return true;
	}
//	public static boolean isNewProtocol270() {
//		if (!MyApplication.isNeedJudgeVersion)
//			return true;
//		BasePreferences basePreferences = new BasePreferences(MyApplication.instance);
//		String currVersion = basePreferences.getPrefString(KeyList.PKEY_REQUEST_HARDVERSION, "");
//		LogUtil.d("2.7.0音箱版本新功能比较:" + currVersion);
//		if (TextUtils.isEmpty(currVersion) || "null".equals(currVersion))
//			return false;
//		if (currVersion.compareTo(KeyList.KEY_HARDVERSION270) <= 0) {
//			return false;
//		}
//		return true;
//	}
}
