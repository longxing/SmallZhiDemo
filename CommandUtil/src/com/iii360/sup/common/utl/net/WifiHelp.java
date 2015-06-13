package com.iii360.sup.common.utl.net;

import java.util.ArrayList;
import java.util.List;

import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.NetWorkUtil;
import com.iii360.sup.common.utl.net.WTBroadcast.EventHandler;
import com.iii360.sup.common.utl.net.WifiAdmin.WifiCipherType;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.util.Log;

public class WifiHelp {
	protected WifiAdmin mWifiAdmin;
	private JoinHostProcess mHostProcess;
	private CreateAPProcess mCreateAPProcess;

	public static final String[] WIFI_PASSWD_TYPE = { "WPA", "WEP", "NONE" };

	public WifiHelp(Context context) {
		mWifiAdmin = new WifiAdmin(context);
		mHostProcess = new JoinHostProcess();
		mCreateAPProcess = new CreateAPProcess();
	}

	/**
	 * 检查 ssid 是否在范围内
	 * 
	 * @param ssid
	 *            public 被检查到的ssid包含当前的名称，也认为是相同
	 * @return
	 */
	public boolean checkEnable(String ssid) {
		//mWifiAdmin.startScan();
		
		List<ScanResult> mConfigs = mWifiAdmin.getWifiList();

		if (mConfigs != null) {
			for (ScanResult config : mConfigs) {
				// LogManager.e("checkEnable  " + config.SSID);
				if (config.SSID.contains(ssid)) {
					LogManager.e("checkEnable true " + ssid);
					return true;
				}
			}
		}
		LogManager.e("false " + ssid);
		return false;
	}

	/**
	 * 加入wifi网络
	 * 
	 * @param apName
	 * @param apPasswd
	 * @param connectResut
	 */
	public void joinWifi(String apName, String apPasswd, ConnectResut connectResut) {
		joinWifi(apName, apPasswd, connectResut, null);
	}

	/**
	 * 加入wifi网络
	 * 
	 * @param apName
	 * @param apPasswd
	 * @param connectResut
	 */
	public void joinWifi(String apName, String apPasswd, ConnectResut connectResut, String bssid) {
		LogManager.e(apName);
		closeAP();

		mWifiAdmin.startScan();

		// 密码不足八位判断
		if (apPasswd != null && apPasswd.length() > 0 && apPasswd.length() < 8) {
			if (connectResut != null) {
				connectResut.onConnect(false);
			}
			return;
		}

		List<WifiConfiguration> configList = mWifiAdmin.getConfiguration();
		ArrayList<Integer> netId = new ArrayList<Integer>();
		if (configList != null) {
			for (int i = 0; i < configList.size(); i++) {
				if (configList.get(i).SSID.equals("\"" + apName + "\"")) {
					System.out.println(configList.get(i).networkId);
					netId.add(configList.get(i).networkId);
				}
			}
			for (int i = 0; i < netId.size(); i++) {
				mWifiAdmin.removeNetwork(netId.get(i));
			}
		}

		if (!checkEnable(apName)) {
			if (connectResut != null)
				connectResut.onConnect(false);
			return;
		}

		WifiConfiguration mConfiguration = mWifiAdmin.CreateJoinWifiConfig(apName, apPasswd,
				WifiCipherType.WIFICIPHER_WPA, bssid);
		mWifiAdmin.addNetwork(mConfiguration);

		if (mConfiguration.networkId == -1) {
			LogManager.e("error net work id");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LogManager.printStackTrace(e);
			}
			joinWifi(apName, apPasswd, connectResut, bssid);
			// connectResut.onConnect(false);
			return;
		}

		mWifiAdmin.startScan();

		mWifiAdmin.connectConfiguration(mConfiguration.networkId);
		int id = mConfiguration.networkId;
		mHostProcess.start(id, connectResut);
	}

	public interface ConnectResut {
		public void onConnect(boolean result);
	}

	/**
	 * 对加入wifi结果做判断
	 * 
	 * @author jushang
	 * 
	 */
	class JoinHostProcess implements Runnable {
		public boolean running = false;
		private long startTime = 0L;
		private Thread thread = null;
		private int id = 0;
		private ConnectResut mConnectResut;
		private boolean reTry;
		private boolean isRuning = true;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//
			while (isRuning) {
				mWifiAdmin.startScan();
				if (!this.running)
					return;

				if (mWifiAdmin.getById(id) != null && (mWifiAdmin.getById(id).status == 0)) {
					LogManager.e("wifi join ok!!!!!!");
					stop();
					isRuning = false;
					if (mConnectResut != null) {
						mConnectResut.onConnect(true);
					}
				} else if (System.currentTimeMillis() - this.startTime >= 45000L && !reTry) {
					LogManager.e("wifi reTry");
					reTry = true;
					mWifiAdmin.connectConfiguration(id);
				} else if (System.currentTimeMillis() - this.startTime >= 100000L) {
					LogManager.e("wifi join fail!!");
					stop();
					isRuning = false;
					if (mConnectResut != null) {
						mConnectResut.onConnect(false);
					}
				}

				try {
					Thread.sleep(5000L);
				} catch (Exception localException) {
					localException.printStackTrace();
				}
			}
		}

		public void start(int id, ConnectResut connectResut) {
			thread = new Thread(this);
			running = true;
			startTime = System.currentTimeMillis();
			this.id = id;
			reTry = false;
			thread.start();
			mConnectResut = connectResut;
		}

		public void stop() {
			this.running = false;
			this.thread = null;
			this.startTime = 0L;
		}

	}

	/**
	 * 获得可用wifi列表
	 * 
	 * @return
	 */
	public ArrayList<CustomScanResult> getScanResult() {

		mWifiAdmin.startScan();

		List<ScanResult> scanResults = mWifiAdmin.getWifiList();
		// Log.e("123", "scanResults " + scanResults.size());

		List<WifiConfiguration> configList = mWifiAdmin.getConfiguration();

		ArrayList<CustomScanResult> mCustomScanResults = new ArrayList<WifiHelp.CustomScanResult>();

		starttag: for (ScanResult result : scanResults) {
			String ssid = result.SSID;
			CustomScanResult cResult = new CustomScanResult();
			cResult.scanResult = result;
			// Log.e("123", result.capabilities);
			LogManager.i(result.capabilities);
			for (int i = 0; i < WIFI_PASSWD_TYPE.length; i++) {
				if (result.capabilities.contains(WIFI_PASSWD_TYPE[i])) {
					cResult.passWdType = i;
					break;
				}
			}
			mCustomScanResults.add(cResult);
			for (WifiConfiguration config : configList) {
				if (config.SSID.replace("\"", "").equals(ssid)) {
					cResult.status = config.status;

					continue starttag;
				}
			}
			cResult.status = 3;
		}
		return mCustomScanResults;
	}

	public WifiAdmin getWifiAdmin() {
		return mWifiAdmin;
	}

	/**
	 * 当前所在网络的名称
	 * 
	 * @return
	 */
	public String getCurrentSSID() {
		if (mWifiAdmin.getWifiInfo() != null && mWifiAdmin.getWifiInfo().getSSID() != null) {
			return mWifiAdmin.getWifiInfo().getSSID().replaceAll("\"", "");
		}
		return null;
	}

	public class CustomScanResult {
		public ScanResult scanResult;
		public int status;
		public int passWdType;
	}

	/**
	 * 创建热点
	 * 
	 * @param connectResut
	 */
	public void creatAP(ConnectResut connectResut, String apName, String apPasswd) {
		mWifiAdmin.closeWifi();
		mWifiAdmin.createWiFiAP(mWifiAdmin.createWifiInfo(apName, apPasswd, 3, "ap"), true);
		mCreateAPProcess.start(connectResut);
	}

	/**
	 * 关闭wifi热点
	 */
	public void closeAP() {
		mWifiAdmin.createWiFiAP(mWifiAdmin.createWifiInfo(mWifiAdmin.getApSSID(), "81028066", 3, "ap"), false);
		mWifiAdmin.OpenWifi();
	}

	class CreateAPProcess implements Runnable {
		public boolean running = false;
		private long startTime = 0L;
		private Thread thread = null;
		private ConnectResut mConnectResut;

		public void run() {

			while (true) {
				LogManager.e("ap_ip:"+mWifiAdmin.getWifiApState()+","+NetWorkUtil.getLocalIpAddress());
				if (!this.running)
					return;
				if (((mWifiAdmin.getWifiApState() == 3) || (mWifiAdmin.getWifiApState() == 13)) && NetWorkUtil.getLocalIpAddress()!=null) {
					LogManager.e("wifi ap creat ok");
					stop();
					if (mConnectResut != null) {
						mConnectResut.onConnect(true);
					}
				} else if (System.currentTimeMillis() - this.startTime >= 30000L) {
					LogManager.e("wifi ap creat fail!!");
					stop();
					if (mConnectResut != null) {
						mConnectResut.onConnect(false);
					}
				}
				try {
					Thread.sleep(50L);
				} catch (Exception localException) {
					localException.printStackTrace();
				}
			}
		}

		public void start(ConnectResut connectResut) {
			thread = new Thread(this);
			running = true;
			startTime = System.currentTimeMillis();
			mConnectResut = connectResut;
			thread.start();

		}

		public void stop() {
			this.running = false;
			this.thread = null;
			this.startTime = 0L;
		}
	}

	public CustomScanResult getCustomResult(String ssid) {

		ArrayList<CustomScanResult> mCustomScanResults = getScanResult();
		for (CustomScanResult result : mCustomScanResults) {
			if (result.scanResult.SSID.equals(ssid)) {
				return result;
			}
		}
		return null;

	}
}
