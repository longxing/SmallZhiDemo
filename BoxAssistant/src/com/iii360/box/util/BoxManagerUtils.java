package com.iii360.box.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.iii.client.WifiConfig;
import com.iii360.box.MyApplication;
import com.iii360.box.common.BasePreferences;

/**
 * 盒子连接帮助类
 * 
 * @author hefeng
 * 
 */
public class BoxManagerUtils {
	private static BasePreferences mBasePreferences;

	public static BasePreferences getBasePreferences(Context context) {
		if (mBasePreferences == null) {
			mBasePreferences = new BasePreferences(context);
		}
		return mBasePreferences;
	}

	/**
	 * 连接到盒子hezi_ap网络
	 * 
	 * @return 如果可以切换返回true
	 */
	public synchronized static boolean connectToBoxWifi(Context context) {
		if (WifiUtils.isConnectWifi(context, KeyList.BOX_WIFI_SSID)) {
			return true;
		}
		return WifiUtils.connectWifi(context, KeyList.BOX_WIFI_SSID, KeyList.BOX_WIFI_PASSWORD);
	}

	/**
	 * 获取盒子连接到路由器后的IP
	 * 
	 * @return 盒子当前的IP地址
	 */
	public static String getBoxIP(Context context) {
		String ip = getBasePreferences(context).getPrefString(KeyList.GKEY_BOX_IP_ADDRESS);
		if (TextUtils.isEmpty(ip)) {
			// BoxManagerUtils.createUdpService(context);
			// new UdpRunService(context, KeyList.UDP_REQUEST_PORT);
		}
		return ip;
	}

	public static int getBoxTcpPort(Context context) {
		// if (AdaptUtil.isNewProtocol270()) {
		// LogUtil.d("tcp--port=" + WifiConfig.TCP_PORT);
		// return
		// getBasePreferences(context).getPrefInteger(KeyList.PEKY_TCP_PORT,
		// WifiConfig.TCP_PORT);
		// }
		LogUtil.d("tcp--port=" + WifiConfig.TCP_DEFAULT_PORT);
		return getBasePreferences(context).getPrefInteger(KeyList.PEKY_TCP_PORT, WifiConfig.TCP_DEFAULT_PORT);
	}

	public static int getBoxUdpPort(Context context) {
		return getBasePreferences(context).getPrefInteger(KeyList.PEKY_UDP_PORT, WifiConfig.UDP_DEFAULT_PORT);
	}

	public static int getScreenWidthPx(Context context) {
		DisplayMetrics dm;
		try {
			WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			dm = new DisplayMetrics();
			manager.getDefaultDisplay().getMetrics(dm);
			return dm.widthPixels; // 屏幕宽（dip，如：320dip）
		} catch (Exception e) {
			e.printStackTrace();
			return MyApplication.screenWidthPx;
		}

	}

	public static int getScreenHeightPx(Context context) {
		try {
			WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			manager.getDefaultDisplay().getMetrics(dm);
			return dm.heightPixels;
		} catch (Exception e) {
			return MyApplication.screenHeightPx;
		}

	}

	public static float getScreenDensity(Context context) {
		try {
			WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			manager.getDefaultDisplay().getMetrics(dm);
			return dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		} catch (Exception e) {
			return MyApplication.density;
		}

	}

	public static void writeCrashLog(Context context, Throwable ex) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			pw.flush();
			PrintWriter writer = null;
			try {
				File dirFile = context.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS);
				if (!dirFile.exists()) {
					if (!dirFile.mkdirs()) {
						return;
					}
				}
				String fileName = new SimpleDateFormat("yyyy_MM_dd").format(new Date()) + "_crash.log";
				writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(dirFile, fileName), true), "utf-8"));
				writer.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss-").format(new Date()) + "crash-----" + sw.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (Exception e) {
				}
			}
		}
	}
}
