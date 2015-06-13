package com.iii360.box.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.iii360.box.entity.WifiInfoMessage;
import com.voice.common.util.WifiSecurity;

/**
 * Wifi信息辅助类
 * 
 * @author hefeng
 * 
 */
public class WifiInfoUtils {
	private WifiInfoUtils() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return 手机IP地址
	 */
	public static String getLocalIpAddress() {

		try {
			Enumeration<NetworkInterface> networks;
			Enumeration<InetAddress> inets;
			NetworkInterface network;
			InetAddress inetAddress;

			for (networks = NetworkInterface.getNetworkInterfaces(); networks.hasMoreElements();) {
				network = networks.nextElement();

				for (inets = network.getInetAddresses(); inets.hasMoreElements();) {
					inetAddress = inets.nextElement();

					if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {

						return inetAddress.getHostAddress().toString();

					}
				}

			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @param context
	 * @return 手机Mac地址
	 */
	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = getWifiManager(context);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	/**
	 * @param context
	 * @return 当前手机连接wifi的ssid
	 */
	public static String getWifiSsid(Context context) {
		WifiManager wifiManager = getWifiManager(context);
		WifiInfo info = wifiManager.getConnectionInfo();
		String ssid = info.getSSID();
		if (TextUtils.isEmpty(ssid)) {
			ssid = "none";
		}
		return ssid;
	}

	/**
	 * @param context
	 * @return 当前手机连接wifi的ssid
	 */
	public static int getWifiNetWorkId(Context context) {
		WifiManager wifiManager = getWifiManager(context);
		WifiInfo info = wifiManager.getConnectionInfo();
		int id = info.getNetworkId();
		return id;
	}

	private static List<WifiInfoMessage> infos = new ArrayList<WifiInfoMessage>();

	public static List<WifiInfoMessage> getWifiList(Context context) {
		infos.clear();
		WifiInfoMessage info;
		WifiManager manager = getWifiManager(context);
		List<ScanResult> list = manager.getScanResults();
		if (list == null) {
			return infos;
		}

		for (ScanResult result : list) {
			info = new WifiInfoMessage();

			// LogManager.e("wifi=====" + result.SSID + "||" +
			// result.capabilities+"||"+WifiManager.calculateSignalLevel(result.level,
			// 5));

			if (TextUtils.isEmpty(result.SSID) || WifiManager.calculateSignalLevel(result.level, 5) <= 0) {
				continue;

			}
			if (result.SSID.contains(KeyList.BOX_WIFI_SSID)) {
				continue;
			}

			info.setSsid(result.SSID);
			info.setEncryption(isEncryption(result.capabilities));
			infos.add(info);

			// LogManager.d("wifi=====" + result.SSID + "||" +
			// result.capabilities+"||"+WifiManager.calculateSignalLevel(result.level,
			// 5));
		}
		startScan(context);
		return infos;
	}

	public static void startScan(Context context) {
		WifiManager manager = getWifiManager(context);
		manager.startScan();
	}

	/**
	 * 判断网络是否加密
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isEncryption(String type) {
		if (type.contains("WPA") || type.contains("WEP") || type.contains("EAP")) {
			return true;
		}
		return false;
	}

	private static WifiManager mWifiManager;

	public synchronized static WifiManager getWifiManager(Context context) {
		if (mWifiManager == null) {
			mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		}
		// mWifiManager.startScan();
		return mWifiManager;
	}

	/**
	 * 当前连接的wifi是否加密
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isEncryption(Context context) {
		int type = WifiSecurity.getCurrentWifiSecurity(context);
		if(type == WifiSecurity.SECURITY_NONE){
			return false;
		}
		return true;
	}
}
