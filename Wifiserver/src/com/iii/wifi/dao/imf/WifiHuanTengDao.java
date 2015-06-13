package com.iii.wifi.dao.imf;

import android.content.Context;
import android.content.Intent;

import com.iii.wifi.dao.info.HuanTengAccount;
import com.iii.wifi.dao.inter.IWifiHuanTengDao;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.SuperBaseContext;

public class WifiHuanTengDao implements IWifiHuanTengDao {
	private SuperBaseContext mPreferenceUtil;
	private Context context;

	public WifiHuanTengDao(Context context) {
		mPreferenceUtil = new SuperBaseContext(context);
		this.context = context;
	}

	@Override
	public void set(HuanTengAccount info) {
		mPreferenceUtil.setPrefString(KeyList.PKEY_HUANTENG_USERNAME, info.getUsername());
		mPreferenceUtil.setPrefString(KeyList.PKEY_HUANTENG_PASSWORD, info.getPassword());
		mPreferenceUtil.setPrefLong(KeyList.PKEY_HUANTENG_ADD_TIME, info.getAddTime());
		// TODO 通知huanteng账号发生张变
		Intent intent = new Intent(KeyList.PKEY_HUANTENG_ACCOUNT_CHANGE_ACTION);
		context.sendBroadcast(intent);
	}

	/***
	 * 删除时，1.如果已经被删除过了（值为空串）就直接返回成功(1),,3.不为空还不一样则不能删除
	 */
	@Override
	public void delete() {
		String username = mPreferenceUtil.getPrefString(KeyList.PKEY_HUANTENG_USERNAME, "");
		String password = mPreferenceUtil.getPrefString(KeyList.PKEY_HUANTENG_PASSWORD, "");
		long addTime = mPreferenceUtil.getPrefLong(KeyList.PKEY_HUANTENG_ADD_TIME, 0);
		if (username.equals("") || password.equals("") || addTime == 0)
			return;
		mPreferenceUtil.setPrefString(KeyList.PKEY_HUANTENG_USERNAME, "");
		mPreferenceUtil.setPrefString(KeyList.PKEY_HUANTENG_PASSWORD, "");
		mPreferenceUtil.setPrefLong(KeyList.PKEY_HUANTENG_ADD_TIME, 0);
		// TODO 通知huanteng账号发生张变
		Intent intent = new Intent(KeyList.PKEY_HUANTENG_ACCOUNT_CHANGE_ACTION);
		context.sendBroadcast(intent);

	}

	@Override
	public HuanTengAccount get() {
		String username = mPreferenceUtil.getPrefString(KeyList.PKEY_HUANTENG_USERNAME, "");
		String password = mPreferenceUtil.getPrefString(KeyList.PKEY_HUANTENG_PASSWORD, "");
		long addTime = mPreferenceUtil.getPrefLong(KeyList.PKEY_HUANTENG_ADD_TIME, 0);
		HuanTengAccount info = new HuanTengAccount();
		info.setPassword(password);
		info.setUsername(username);
		info.setAddTime(addTime);
		return info;
	}

}
