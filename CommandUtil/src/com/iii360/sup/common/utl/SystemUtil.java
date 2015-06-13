package com.iii360.sup.common.utl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * 
 * @author Jerome.系统相关的工具类
 * 
 */
public class SystemUtil {
	/**
	 * 
	 * @param context
	 *            Context
	 * @return 版本对应的信息
	 */
	public static String getVersionInfo(Context context) {
		String version = null;
		PackageInfo packInfo = null;
		if (version == null || "".equals(version)) {
			try {
				final PackageManager packageManager = context.getPackageManager();
				packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
				version = packInfo.versionName;
			} catch (NameNotFoundException e) {
				LogManager.printStackTrace(e, "BaseContext", "getVersionInfoA");
			}
		}
		LogManager.d("MyEngine", "getVersionInfo", version);
		return version;
	}

	public static String getIMEI() {

//		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		String deviceID = telephonyManager.getDeviceId();
//		if (deviceID == null || deviceID.length() == 0 || deviceID.equals("null")) {
//			try {
//				deviceID = SystemUtil.getLocalMacAddress(context).replaceAll(":", "");
//				long id = Long.valueOf(deviceID, 16);
//				deviceID = id + "";
//			} catch (Exception e) {
//				LogManager.printStackTrace(e);
//			}
//
//		}
//		return deviceID;
		return ShellUtils.readSerialNumber();
	}

	public static String getDeviceId() {
		return ShellUtils.readSerialNumber();
	}
	
	public static String getProductName() {
		try {
			return SystemProperties.get("ro.product.device");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info != null && info.getMacAddress()!=null) {
			return info.getMacAddress().replace(":", "");
		}
		return "";
	}

	public static int getVersionCode(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			// 当前应用的版本名称
			// String versionName = info.versionName;

			// 当前版本的版本号
			int versionCode = info.versionCode;
			return versionCode;
			// 当前版本的包名
			// String packageNames = info.packageName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public static String getBrandModel() {
		return getBrand() + " " + getModel();
	}

	public static String getBrand() {
		return android.os.Build.BRAND;
	}

	public static String getModel() {
		return android.os.Build.MODEL;
	}

	public static String getNativePhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String phoneNumber = telephonyManager.getLine1Number();
		return phoneNumber;
	}

	public static boolean isActivityTop(Context context, String activityName) {
		String topActivity = getTopActivity(context).getClassName();
		if (topActivity.contains(activityName)) {
			return true;
		}
		return false;
	}

	public static DisplayMetrics getScreenSize(Context context) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics;
	}

	public static ComponentName getTopActivity(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		return rti.get(0).topActivity;
	}

	private static List<String> getHomes(Context context) {
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
		}

		return names;
	}

	public static boolean isInstallPackage(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		List<String> pName = new ArrayList<String>();
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);

	}


}
