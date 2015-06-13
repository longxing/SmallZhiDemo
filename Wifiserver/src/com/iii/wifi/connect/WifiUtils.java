package com.iii.wifi.connect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.iii360.sup.common.utl.LogManager;

/**
 * 网络相关类
 * 
 * @author hefeng
 * 
 */
public class WifiUtils {

	static int pingTimeOut = 10*1000;
	static int pingWaitTimeOut = 15*1000;
	static Boolean mPingResult = false;
	
	private WifiUtils() {

	}

	/**
	 * 搜索附近有没有**网络
	 * 
	 * @param context
	 * @param ssid
	 * @return
	 */
	public static boolean searchWifi(Context context, String ssid) {

		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		wifiManager.startScan();

		List<ScanResult> scanResultList = wifiManager.getScanResults();

		if (scanResultList == null) {
			return false;
		}

		for (ScanResult result : scanResultList) {

			if (!TextUtils.isEmpty(result.SSID) && ssid.equals(result.SSID)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 是否连接到**网络
	 * 
	 * @param context
	 * @param ssid
	 * @return
	 */
	public static boolean isConnectWifi(Context context, String ssid) {

		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String curSsid = wifiInfo.getSSID();
		if (curSsid != null && curSsid.contains(ssid)) {
			return true;
		}
		return false;
	}

	/**
	 * 连接到**Wifi网络
	 * 
	 * @param context
	 * @param ssid
	 *            网络名称
	 * @param pwd
	 *            网络密码
	 * @return 连接是否成功
	 */
	public static boolean connectWifi(Context context, String ssid, String pwd) {
		if (TextUtils.isEmpty(pwd)) {
			return connectToWifi(context, ssid);
		}

		return connectToWifi(context, ssid, pwd);
	}

	/**
	 * 连接到没有密码的**网络
	 * 
	 * @param context
	 * @return
	 */
	private static boolean connectToWifi(Context context, String ssid) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> configList = mWifiManager.getConfiguredNetworks();
		ArrayList<Integer> netId = new ArrayList<Integer>();

		for (int i = 0; i < configList.size(); i++) {
			if (configList.get(i).SSID == null) {
				continue;
			}

			if (configList.get(i).SSID.equals("\"" + ssid + "\"")) {
				System.out.println(configList.get(i).networkId);
				netId.add(configList.get(i).networkId);
			}
		}

		for (int i = 0; i < netId.size(); i++) {
			mWifiManager.removeNetwork(netId.get(i));

		}

		WifiConfiguration config = new WifiConfiguration();
		config.SSID = "\"" + ssid + "\"";
		config.hiddenSSID = false;
		config.status = WifiConfiguration.Status.ENABLED;
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

		int wcgID = mWifiManager.addNetwork(config);

		boolean isOpen = mWifiManager.enableNetwork(wcgID, true);

		if (wcgID == -1 || !isOpen) {

			return false;

		}

		return true;
	}

	/**
	 * 连接到**Wifi网络
	 * 
	 * @param context
	 * @param ssid
	 *            网络名称
	 * @param pwd
	 *            网络密码
	 * @return 连接是否成功
	 */
	private static boolean connectToWifi(Context context, String ssid, String pwd) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiConfiguration configuration = new WifiConfiguration();

		List<WifiConfiguration> configList = wifiManager.getConfiguredNetworks();
		for (int i = 0; i < configList.size(); i++) {
			if (configList.get(i).SSID == null) {
				continue;
			}

			if (configList.get(i).SSID.equals("\"" + ssid + "\"")) {
				wifiManager.removeNetwork(configList.get(i).networkId);
			}
		}
		configuration.SSID = "\"" + ssid + "\"";
		configuration.preSharedKey = "\"" + pwd + "\"";
		configuration.hiddenSSID = true;
		configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
		configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		configuration.priority = 1;

		int id = wifiManager.addNetwork(configuration);
		wifiManager.saveConfiguration();
		boolean success = wifiManager.enableNetwork(id, true);
		if (id == -1 || !success) {
			return false;
		}
		return true;
	}

	/**
	 * 判断wifi是接入互联网
	 * Important: this api is not thread safe
	 * @return
	 */
	public static boolean pingCheck(){
		
		Thread pingThread = new Thread(new Runnable(){

			int MAX_VALUE = Integer.MAX_VALUE - 200;
			int id = new Random().nextInt(MAX_VALUE);
			String Version =  String.valueOf(id);
			String baseUrl = "http://www.baidu.com/";
			String pingUrl = baseUrl+"?version=" + Version;
			
			@Override
			public void run() {
				try {
					HttpGet httpGet = new HttpGet(pingUrl);
					HttpClient httpClient = new DefaultHttpClient();
					
					httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, pingTimeOut);
					HttpResponse httpResponse = httpClient.execute(httpGet);

					int requestCode = httpResponse.getStatusLine().getStatusCode();

					if (requestCode == HttpStatus.SC_OK) {
						mPingResult = true;
					} else {
						mPingResult = false;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LogManager.e("HttpRequest", "pingUrl url:" + pingUrl + "----exception:" + e.toString());
					mPingResult = false;
				}			
			}		
		});
			
	pingThread.start();
	try {
		pingThread.join(pingWaitTimeOut);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return mPingResult;
	}
	
	
	/**
	 * 判断wifi是可用
	 * 
	 * @return
	 */
	public static boolean pingWifi() {

		try {
			// 其中 -c 1为发送的次数，1为表示发送1次，-w 表示发送后等待响应的时间。
			Process process = Runtime.getRuntime().exec("ping -c 1 -i 0.2 -W 1 www.baidu.com");
			int status = process.waitFor();
			if (status == 0) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 判断是否打开wifi网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isOpenWifi(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.isWifiEnabled();
	}

	/**
	 * 扫描网络
	 * 
	 * @param context
	 */
	public static void startScan(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.startScan();
	}

	/**
	 * 判断是否打开网络（wifi或移动网络）
	 * 
	 * @param context
	 * @return true表示打开
	 */
	public static boolean isOpenNet(Context context) {
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			return conManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	public static boolean isConnectWifi(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		// final android.net.NetworkInfo mobile =
		// connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifi != null && wifi.isConnected() && wifi.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 自动打开wifi
	 * 
	 * @param context
	 */
	public static void autoOpenWiFi(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		if (!wifiManager.isWifiEnabled()) {

			wifiManager.setWifiEnabled(true);

		} else if (wifiManager.isWifiEnabled()) {

			wifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * 获取wifi加密类型
	 * 
	 * @param ssid
	 * @return
	 */
	public static WifiCipherType getCipherType(Context context, String ssid) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiCipherType type = WifiCipherType.WIFICIPHER_WPA;

		List<ScanResult> list = wifiManager.getScanResults();

		for (ScanResult scResult : list) {

			if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
				String capabilities = scResult.capabilities;
				LogManager.i("WifiControl", "---->BSSID=" + scResult.BSSID + "---->SSID=" + scResult.SSID + "---->capabilities=" + capabilities + "---->frequency=" + scResult.frequency
						+ "---->level=" + scResult.level + "---->timestamp=" + scResult.timestamp);

				if (!TextUtils.isEmpty(capabilities)) {

					if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
						type = WifiCipherType.WIFICIPHER_WPA;

					} else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
						type = WifiCipherType.WIFICIPHER_WEP;

					} else if (capabilities.contains("EAP") || capabilities.contains("eap")) {
						type = WifiCipherType.WIFICIPHER_EAP;

					} else {
						type = WifiCipherType.WIFICIPHER_NOPASS;
					}
				}
			}
		}
		return type;
	}
}
