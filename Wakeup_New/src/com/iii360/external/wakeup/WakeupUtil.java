package com.iii360.external.wakeup;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;

public class WakeupUtil {

	/**
	 * 是否已经打开了麦克风监听
	 * 
	 * @param context
	 * @return
	 */
	static boolean isWakeupOpen(Context context) {
		BaseContext baseContext = new BaseContext(context);
		return baseContext.getPrefBoolean(KeyList.PKEY_IS_WAKE_UP_ACTIVE);

	}

	/**
	 * 供外部调用，判断调用时刻是否需要打开wakeup。逻辑是根据prefrence值加上当前的状态来判断。
	 * 
	 * @param context
	 * @return
	 */
//	static boolean isNeedOpenWakeup(Context context) {
//		int wakeupPrefrenceValue = getPrefWakeupState(context);
//		boolean retValue = false;
//		switch (wakeupPrefrenceValue) {
//		case 0:
//			retValue = false;
//			break;
//		case 2:
//			retValue = true;
//			break;
//		case 1:
//			if (isNeedWifiAndScreenOff(context)) {
//				retValue = isSpecialTime(context);
//			} else {
//				retValue = isInVoiceAssistant(context);
//			}
//			break;
//		}
//		LogManager.e(retValue + "   retvalue");
//		return true;
//		// return false;
//	}

	/**
	 * 是否打开了wifi
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isWifiOpen(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifiInfo == null ? null : wifiInfo.isAvailable();
	}

	/**
	 * 屏幕是否开启。
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isScreenOn(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		return pm.isScreenOn();
	}

	/**
	 * 保存的Wakeup首选项的值。
	 * 
	 * @param context
	 * @return 0 未开启唤醒 2 后台一直唤醒 1 条件唤醒+小智所有页面唤醒
	 */
	private static int getPrefWakeupState(Context context) {
		BaseContext mBaseContext = new BaseContext(context);
		final boolean setWakeupTrue = mBaseContext.getPrefBoolean(KeyList.PKEY_ASSISTANT_WAKE_UP, false);
		final boolean isAlwaysRunInBack = mBaseContext.getPrefBoolean(KeyList.PKEY_ASSISTANT_WAKE_UP_ALWAYS_RUN, false);
		int selectWhich = 0;
		if (setWakeupTrue) {
			if (!isAlwaysRunInBack) {
				selectWhich = 1;
			} else {
				selectWhich = 2;
			}
		}
		return selectWhich;
	}

	/**
	 * 开启条件唤醒后，判断是否达到条件唤醒的条件。
	 * 
	 * @param context
	 * @return true达到开启唤醒条件，false 未达到条件
	 */
	static boolean isSpecialTime(Context context) {
		boolean isWifiOpen = isWifiOpen(context);
		boolean isScreenOn = isScreenOn(context);
		LogManager.d("isWifiOpen is " + isWifiOpen + " isScreenOn is " + isScreenOn);
		// 判断是否达到了条件唤醒的条件
		if ((isWifiOpen && !isScreenOn) || isInVoiceAssistant(context)) {
			// 条件一 （ 黑屏+wifi网络开启 ），条件二 （在小智所有界面）
			LogManager.d("isSpecialTime达到满足条件 handSpecialTime");

			return true;

		} else {

			LogManager.d("isSpecialTime未达到满足条件 handSpecialTime");
			return false;

		}
	}

	/**
	 * 是否需要打开wifi或者和屏幕关闭的时候打开唤醒.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNeedWifiAndScreenOff(Context context) {
		BaseContext baseContext = new BaseContext(context);
		boolean isNeedOpenWifi = baseContext.getPrefBoolean(KeyList.PKEY_IS_NEED_WIFI_THEN_OPEN_WAKE_UP, false);
		boolean isNeedOpenScreenOff = baseContext.getPrefBoolean(KeyList.PKEY_IS_NEED_SCREEN_OFF_OPEN_WAKE_UP, false);
		return isNeedOpenScreenOff && isNeedOpenWifi;
	}

	/**
	 * 是否在软件里面。运行时候会自动检测是否在小智助手里面。
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isInVoiceAssistant(Context context) {
		ComponentName componentName = getTopActivity(context);
		String cls = componentName.getPackageName();
		if (context.getPackageName().equals(cls)) {

			return true;

		} else {

			return false;

		}
	}

	private static ComponentName getTopActivity(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		return rti.get(0).topActivity;
	}

	public static int getWakeupSelectWhich(Context context) {
		BaseContext baseContext = new BaseContext(context);
		final boolean setWakeupTrue = baseContext.getPrefBoolean(KeyList.PKEY_ASSISTANT_WAKE_UP, false);
		final boolean isAlwaysRunInBack = baseContext.getPrefBoolean(KeyList.PKEY_ASSISTANT_WAKE_UP_ALWAYS_RUN, false);
		int selectWhich = 0;
		if (setWakeupTrue) {
			if (!isAlwaysRunInBack) {
				selectWhich = 1;
				if (isNeedWifiAndScreenOff(context)) {
					selectWhich = 3;
				}
			} else {
				selectWhich = 2;
			}
		}
		return selectWhich;
	}

}
