package com.iii360.box.receiver;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiDeviceInfos;
import com.iii360.box.MyApplication;
import com.iii360.box.MyService;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.config.WifiConfigActivity;
import com.iii360.box.connect.OnLineBoxListActivity;
import com.iii360.box.util.AppUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.LogUtil;
import com.iii360.box.view.NotificationBar;

public class GlobalReceiver extends BroadcastReceiver {
	private BasePreferences mPreferences;
	private Gson gson = new Gson();
	private NotificationBar mNotificationBar;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		mPreferences = new BasePreferences(context);

		// 获取新配件列表
		if (action.equals(KeyList.AKEY_NEW_DEVICE_LIST_ACTION)) {
			synchronized (context) {
				String ip = intent.getStringExtra(KeyList.KEY_DEVICE_IP);
				String gsonEntity = intent.getStringExtra(KeyList.IKEY_NEW_DEVICE_LIST);
				LogManager.e("ip=" + ip + "||" + mPreferences.getPrefString(KeyList.GKEY_BOX_IP_ADDRESS));
				if (!ip.equals(mPreferences.getPrefString(KeyList.GKEY_BOX_IP_ADDRESS))) {
					return;
				}
				LogManager.e("" + gsonEntity);
				String lastGsonEntity = mPreferences.getPrefString(KeyList.IKEY_NEW_DEVICE_GSON_LIST);
				mPreferences.setPrefString(KeyList.IKEY_NEW_DEVICE_GSON_LIST, gsonEntity);
				mNotificationBar = NotificationBar.getInstance(context);

				if (TextUtils.isEmpty(gsonEntity)) {
					if (mNotificationBar != null) {
						mNotificationBar.dismiss();
					}
					return;
				}

				// LogManager.d("==================================");
				// LogManager.d("lastGsonEntity=" + lastGsonEntity);
				// LogManager.d("gsonEntity=" + gsonEntity);
				// LogManager.d("same =" + gsonEntity.equals(lastGsonEntity));

				if (gsonEntity.equals(lastGsonEntity)) {
					return;
				}

				WifiDeviceInfos infos = gson.fromJson(gsonEntity, WifiDeviceInfos.class);
				WifiDeviceInfo info = infos.getWifiInfo().get(0);

				// 没有显示过，显示一次
				if (TextUtils.isEmpty(lastGsonEntity)) {
					// LogManager.d("没有显示过，显示一次");
					mNotificationBar.showNewDevice(info);
					mPreferences.setPrefString(KeyList.IKEY_SHOW_NEW_DEVICE_MAC, info.getMacadd());

					return;
				}

				compareDevice(lastGsonEntity, gsonEntity);

				// LogManager.i("GlobalReceiver Notification show :" +
				// !mPreferences.getPrefBoolean(KeyList.IKEY_PUSH_NEW_DEVICE_SWTICH,
				// false));

				// 已经显示了，就不在显示
				if (!mPreferences.getPrefBoolean(KeyList.IKEY_PUSH_NEW_DEVICE_SWTICH, false)) {
					// LogManager.d("已经显示了，就不在显示 ");
					mNotificationBar.showNewDevice(info);
					mPreferences.setPrefString(KeyList.IKEY_SHOW_NEW_DEVICE_MAC, info.getMacadd());
				}

				LogManager.d("GlobalReceiver mDeleteList size=" + mDeleteList.size());
				LogManager.d("GlobalReceiver mAddList size=" + mAddList.size());

				// 如果移除列表有数据，则移除状态栏上显示的
				if (!mDeleteList.isEmpty()) {
					String mac = mPreferences.getPrefString(KeyList.IKEY_SHOW_NEW_DEVICE_MAC);
					if (!TextUtils.isEmpty(mac)) {
						for (int i = 0; i < mDeleteList.size(); i++) {
							LogManager.i("mDeleteList mac :getMacadd()  " + mac + "===" + mDeleteList.get(i).getMacadd());
							if (mDeleteList.get(i).getMacadd().equals(mac)) {
								LogManager.e("Notification dismiss");
								mNotificationBar.dismiss();
							}
						}
					}
					mDeleteList.clear();
				}

				// 如果添加列表有数据，则在状态栏上显示的
				if (!mAddList.isEmpty()) {
					// LogManager.d("添加列表有数据，则在状态栏上显示的");
					mNotificationBar.showNewDevice(mAddList.get(0));
					mPreferences.setPrefString(KeyList.IKEY_SHOW_NEW_DEVICE_MAC, mAddList.get(0).getMacadd());
					mAddList.clear();
				}

			}
		} else if (action.equals(KeyList.AKEY_CANCEL_NEW_DEVICE_NOTIFICATION)) {
			// 关闭状态栏新配件提醒广播
			LogManager.i("关闭状态栏新配件提醒广播");
			mNotificationBar = NotificationBar.getInstance(context);
			mNotificationBar.dismiss();

		} else if (action.equals(KeyList.ACTION_MUSIC_STATUS_CHANGE)) {
			// LogManager.e("音乐状态发生改变，接收广播");
			// if (musicStateListener != null) {
			// musicStateListener.onChange();
			// }
		} else if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_PACKAGE_RESTARTED) || action.equals(Intent.ACTION_TIME_TICK)) {
			// 开机 //settings中force stop
			// 应用//系统每隔1分钟发一次(Intent.ACTION_TIME_TICK必须动态注册)

			LogUtil.i("Start MyService action=" + action);

			if (!AppUtils.isServiceRunning(context, "com.iii360.box.MyService")) {
				context.startService(new Intent(context, MyService.class));
			} else {
				LogUtil.i("MyService Running ... ");
			}

		} else if (action.equals(KeyList.ACTION_BOX_OUT_OF_LINE)) {
			String conn = mPreferences.getPrefString(KeyList.KEY_CONNECTING_AP);
			if (conn != null && conn.equals("yes")) {
				List<Activity> acts = MyApplication.getInstance().getActivityList();
				for (Activity ac : acts) {
					if (ac instanceof WifiConfigActivity) {
						continue;
					}
					ac.finish();
				}
				return;
			}
			if (AppUtils.isAppToBackground(context)) {
				MyApplication.getInstance().exit();
				mPreferences.setPrefString(KeyList.GKEY_BOX_IP_ADDRESS, "");
				return;
			}

			// ToastUtils.show(context, "手机与音箱网络断开,正在重新连接");
			Intent _intent = new Intent(context, OnLineBoxListActivity.class);
			_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(_intent);
			MyApplication.getInstance().exit();
			mPreferences.setPrefString(KeyList.GKEY_BOX_IP_ADDRESS, "");
		}
	}

	/**
	 * 新增设备列表
	 */
	private List<WifiDeviceInfo> mAddList = new ArrayList<WifiDeviceInfo>();
	/**
	 * 去除设备列表
	 */
	private List<WifiDeviceInfo> mDeleteList = new ArrayList<WifiDeviceInfo>();

	/**
	 * 比较没有配置设备列表
	 * 
	 * @param lastData
	 *            上一次显示的列表数据
	 * @param currentData
	 *            当前获取到的列表数据
	 */
	private void compareDevice(String lastData, String currentData) {
		if (lastData.equals(currentData)) {
			mPreferences.setPrefBoolean(KeyList.IKEY_PUSH_NEW_DEVICE_SWTICH, true);

		} else {

			WifiDeviceInfos lastInfo = gson.fromJson(lastData, WifiDeviceInfos.class);
			WifiDeviceInfos currentInfo = gson.fromJson(currentData, WifiDeviceInfos.class);

			List<WifiDeviceInfo> list1 = lastInfo.getWifiInfo();
			List<WifiDeviceInfo> list2 = currentInfo.getWifiInfo();

			mAddList.clear();
			mDeleteList.clear();

			// 旧数据和新数据比较，如果新数据中有，旧数据没有，则说明有新的设备添加了
			for (int i = 0; i < list2.size(); i++) {
				// 判断列表中是否存在
				if (!isExist(list2.get(i), list1)) {
					mAddList.add(list2.get(i));
				}
			}
			// 旧数据和新数据比较，如果旧数据有，新数据中没有，则说明有设备不在线了
			for (int i = 0; i < list1.size(); i++) {
				if (!isExist(list1.get(i), list2)) {
					mDeleteList.add(list1.get(i));
				}
			}

			if (mAddList.isEmpty() && mDeleteList.isEmpty()) {
				mPreferences.setPrefBoolean(KeyList.IKEY_PUSH_NEW_DEVICE_SWTICH, true);
			}
		}

	}

	private boolean isExist(WifiDeviceInfo info, List<WifiDeviceInfo> list) {
		String mac = info.getMacadd();

		for (int i = 0; i < list.size(); i++) {

			if (mac.equals(list.get(i).getMacadd())) {

				return true;
			}
		}
		return false;
	}

}
