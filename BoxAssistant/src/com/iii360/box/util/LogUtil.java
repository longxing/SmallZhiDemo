package com.iii360.box.util;

import android.util.Log;

import com.iii360.box.MyApplication;

public class LogUtil {
	private static final String TAG = "boxassistant";

	public static void i(String msg) {
		boolean isShow = !MyApplication.isRelease;
		if (!isShow)
			return;
		try {
			String log = bulidTag(msg);
			Log.i(TAG, log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void d(String msg) {
		boolean isShow = !MyApplication.isRelease;
		if (!isShow)
			return;
		try {
			String log = bulidTag(msg);
			Log.d(TAG, log);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void w(String msg) {
		boolean isShow = !MyApplication.isRelease;
		if (!isShow)
			return;
		try {
			String log = bulidTag(msg);
			Log.w(TAG, log);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void e(String msg) {
		boolean isShow = !MyApplication.isRelease;
		if (!isShow)
			return;
		try {
			String log = bulidTag(msg);
			Log.e(TAG, log);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public static void v(String msg) {
		boolean isShow = !MyApplication.isRelease;
		if (!isShow)
			return;
		try {
			String log = bulidTag(msg);
			Log.v(TAG, log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String bulidTag(String msg) {
		String objectName = Thread.currentThread().getStackTrace()[4].getFileName();
		String methodName = Thread.currentThread().getStackTrace()[4].getMethodName();
		return bulidTag(objectName, methodName) + msg;
	}

	private static String bulidTag(String objectName, String methodName) {
		return "[" + objectName + "|" + methodName + "]msg==";
	}
}
