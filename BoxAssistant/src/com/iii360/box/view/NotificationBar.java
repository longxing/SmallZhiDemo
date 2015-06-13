package com.iii360.box.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii360.box.R;
import com.iii360.box.SingleSelectActivity;
import com.iii360.box.config.WifiSingleAcivity;
import com.iii360.box.util.KeyList;

public class NotificationBar {
	private Context context;
	private NotificationManager mManager;
	private static final int NOTIFY_ID = 10;
	private static NotificationBar mNotificationBar;
	private static boolean isShow = false;

	public static boolean isShow() {
		return isShow;
	}

	public static void setShow(boolean isShow) {
		NotificationBar.isShow = isShow;
	}

	public static NotificationBar getInstance(Context context) {
		if (mNotificationBar == null) {
			synchronized (NotificationBar.class) {
				if (mNotificationBar == null) {
					mNotificationBar = new NotificationBar(context);
					isShow = false;
				}
			}
		}
		return mNotificationBar;
	}

	private NotificationBar(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	// public void show(String title, String content) {
	// NotificationCompat.Builder mBuilder = new
	// NotificationCompat.Builder(context);
	// mBuilder.setSmallIcon(R.drawable.ic_launcher);
	// mBuilder.setContentTitle(title);
	// mBuilder.setContentText(content);
	// //第一次提示消息的时候显示在通知栏上
	// mBuilder.setTicker("发现新消息");
	//
	// mBuilder.build().flags = Notification.FLAG_AUTO_CANCEL;
	//
	// //构建一个Intent
	// Intent resultIntent = new Intent();
	//
	// //封装一个Intent
	// PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0,
	// resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	// // 设置通知主题的意图
	// mBuilder.setContentIntent(resultPendingIntent);
	// //获取通知管理器对象
	// mManager.notify(NOTIFY_ID, mBuilder.build());
	// }

	public void showNewDevice(WifiDeviceInfo info) {
		if (isShow) {
			return;
		}

		isShow = true;
		String title = "检测到新设备,请点击进入设置";
		String content = info.getFitting();

		Notification notification = new Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent resultIntent = new Intent(); // 点击该通知后要跳转的Activity
		resultIntent.putExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY, info);
		if (info.getDeviceType() == 0) {
			resultIntent.setClass(context, WifiSingleAcivity.class);

		} else if (info.getDeviceType() == 1) {
			resultIntent.putExtra(KeyList.PKEY_SINGLE_SELECT_TYPE, SingleSelectActivity.ROOM_SELECT);
			resultIntent.setClass(context, SingleSelectActivity.class);
		}

		PendingIntent contentItent = PendingIntent.getActivity(context, 0, resultIntent, 0);
		notification.setLatestEventInfo(context, title, content, contentItent);
		// 把Notification传递给NotificationManager
		mManager.notify(NOTIFY_ID, notification);
	}

	public void dismiss() {
		isShow = false;
		mManager.cancel(NOTIFY_ID);
	}
}
