package com.iii.wifi.dao.imf;

import android.content.Context;
import android.content.Intent;

import com.iii.wifi.dao.info.WifiUserInfo;
import com.iii.wifi.dao.inter.IWifiUserDao;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;

public class WifiUserDao implements IWifiUserDao {
	private static final String WIFI_USER_NAME = KeyList.PKEY_WIFINAME;
	private static final String WIFI_USER_PASSWORD = KeyList.PKEY_WIFIPASSWD;
	private SuperBaseContext mPreferenceUtil;
	private Context mContext;

	public WifiUserDao(Context context) {
		mPreferenceUtil = new SuperBaseContext(context);
		mContext = context;
	}

	@Override
	public void add(WifiUserInfo info) {
		// TODO Auto-generated method stub

//		mPreferenceUtil.setPrefString(WIFI_USER_NAME, info.getmName());
//		mPreferenceUtil.setPrefString(WIFI_USER_PASSWORD, info.getPassWord());、
		mPreferenceUtil.setPrefStringNew(WIFI_USER_NAME, info.getmName());
		mPreferenceUtil.setPrefStringNew(WIFI_USER_PASSWORD, info.getPassWord());
		
		mPreferenceUtil.setPrefString(KeyList.PKEY_WIFICIPHERTYPE, info.getEncrypt());
		
		LogManager.e("通过Ap设置盒子网络 ：　"+info.getmName() + "|" + info.getPassWord()+"|"+info.getEncrypt());
		
		
		// KeyList.WIFI_HELP.joinWifi(info.getmName(), info.getPassWord(),
		// null);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent i = new Intent(KeyList.PKEY_SEND_BROADCAST_NETCHECK);
				mContext.sendBroadcast(i);
			}
		}).start();

	}

	@Override
	public void delete(WifiUserInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updata(WifiUserInfo info) {
		// TODO Auto-generated method stub
//		mPreferenceUtil.setPrefString(WIFI_USER_NAME, info.getmName());
//		mPreferenceUtil.setPrefString(WIFI_USER_PASSWORD, info.getPassWord());
		
		mPreferenceUtil.setPrefStringNew(WIFI_USER_NAME, info.getmName());
		mPreferenceUtil.setPrefStringNew(WIFI_USER_PASSWORD, info.getPassWord());
		
		LogManager.e(info.getmName() + "        " + info.getPassWord());
		// KeyList.WIFI_HELP.joinWifi(info.getmName(), info.getPassWord(),
		// null);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent i = new Intent(KeyList.PKEY_SEND_BROADCAST_NETCHECK);
				mContext.sendBroadcast(i);
			}
		}).start();
	}

	@Override
	public WifiUserInfo selectAll() {
		// TODO Auto-generated method stub
		WifiUserInfo info = new WifiUserInfo();
//		String name = mPreferenceUtil.getPrefString(WIFI_USER_NAME, "");
//		String passWord = mPreferenceUtil.getPrefString(WIFI_USER_PASSWORD, "");
		
		String name = mPreferenceUtil.getPrefStringNew(WIFI_USER_NAME, "");
		String passWord = mPreferenceUtil.getPrefStringNew(WIFI_USER_PASSWORD, "");
		
		if (name != null && !name.equals("")) {
			info.setName(name);
		}
		if (passWord != null && !passWord.equals("")) {
			info.setPassWord(passWord);
		}
		return info;
	}
}
