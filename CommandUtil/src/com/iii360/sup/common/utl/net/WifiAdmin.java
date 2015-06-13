package com.iii360.sup.common.utl.net;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.net.WTBroadcast.EventHandler;

public class WifiAdmin {
	private static WifiAdmin wiFiAdmin = null;
	private List<WifiConfiguration> mWifiConfiguration;
	private WifiInfo mWifiInfo;
	private List<ScanResult> mWifiList;
	WifiManager.WifiLock mWifiLock;
	public WifiManager mWifiManager;
	private boolean Scanresult = false;

	public WifiAdmin(Context paramContext) {
		this.mWifiManager = ((WifiManager) paramContext.getSystemService("wifi"));
		this.mWifiInfo = this.mWifiManager.getConnectionInfo();
		mWifiManager.startScan();
	}

	public static WifiAdmin getInstance(Context paramContext) {
		if (wiFiAdmin == null)
			wiFiAdmin = new WifiAdmin(paramContext);
		return wiFiAdmin;
	}

	public WifiConfiguration isExsits(String paramString) {
		if (this.mWifiManager.getConfiguredNetworks() == null) {
			return null;
		}
		Iterator localIterator = this.mWifiManager.getConfiguredNetworks().iterator();
		WifiConfiguration localWifiConfiguration;
		do {
			if (!localIterator.hasNext())
				return null;
			localWifiConfiguration = (WifiConfiguration) localIterator.next();
		} while (!localWifiConfiguration.SSID.equals("\"" + paramString + "\""));
		return localWifiConfiguration;
	}

	public void CreatWifiLock() {
		this.mWifiLock = this.mWifiManager.createWifiLock("Test");
	}

	public void OpenWifi() {
		if (!this.mWifiManager.isWifiEnabled())
			this.mWifiManager.setWifiEnabled(true);
	}

	public void ReleaseWifiLock() {
		if (this.mWifiLock.isHeld())
			this.mWifiLock.acquire();
	}

	public void addNetwork(WifiConfiguration paramWifiConfiguration) {
		int i = this.mWifiManager.addNetwork(paramWifiConfiguration);
		paramWifiConfiguration.networkId = i;
		this.mWifiManager.enableNetwork(i, false);
	}

	public void closeWifi() {
		this.mWifiManager.setWifiEnabled(false);
	}

	public void connectConfiguration(int paramInt) {
		mWifiManager.enableNetwork(paramInt, true);
	}

	public void createWiFiAP(WifiConfiguration paramWifiConfiguration, boolean paramBoolean) {
		try {
			Class localClass = this.mWifiManager.getClass();
			Class[] arrayOfClass = new Class[2];
			arrayOfClass[0] = WifiConfiguration.class;
			arrayOfClass[1] = Boolean.TYPE;
			Method localMethod = localClass.getMethod("setWifiApEnabled", arrayOfClass);
			WifiManager localWifiManager = this.mWifiManager;
			Object[] arrayOfObject = new Object[2];
			arrayOfObject[0] = paramWifiConfiguration;
			arrayOfObject[1] = Boolean.valueOf(paramBoolean);
			localMethod.invoke(localWifiManager, arrayOfObject);
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public WifiConfiguration createWifiInfo(String ssid, String passwd, int paramInt, String paramString3) {
		WifiConfiguration localWifiConfiguration1 = new WifiConfiguration();
		localWifiConfiguration1.allowedAuthAlgorithms.clear();
		localWifiConfiguration1.allowedGroupCiphers.clear();
		localWifiConfiguration1.allowedKeyManagement.clear();
		localWifiConfiguration1.allowedPairwiseCiphers.clear();
		localWifiConfiguration1.allowedProtocols.clear();
		if (paramString3.equals("wt")) {
			localWifiConfiguration1.SSID = ("\"" + ssid + "\"");
			WifiConfiguration localWifiConfiguration2 = isExsits(ssid);
			if (localWifiConfiguration2 != null)
				mWifiManager.removeNetwork(localWifiConfiguration2.networkId);
			if (paramInt == 1) {
				localWifiConfiguration1.wepKeys[0] = "";
				localWifiConfiguration1.allowedKeyManagement.set(0);
				localWifiConfiguration1.wepTxKeyIndex = 0;
			} else if (paramInt == 2) {
				localWifiConfiguration1.hiddenSSID = true;
				localWifiConfiguration1.wepKeys[0] = ("\"" + passwd + "\"");
			} else {
				localWifiConfiguration1.preSharedKey = ("\"" + passwd + "\"");
				localWifiConfiguration1.hiddenSSID = true;
				localWifiConfiguration1.allowedAuthAlgorithms.set(0);
				localWifiConfiguration1.allowedGroupCiphers.set(2);
				localWifiConfiguration1.allowedKeyManagement.set(1);
				localWifiConfiguration1.allowedPairwiseCiphers.set(1);
				localWifiConfiguration1.allowedGroupCiphers.set(3);
				localWifiConfiguration1.allowedPairwiseCiphers.set(2);
			}
		} else {
			localWifiConfiguration1.SSID = ssid;
			localWifiConfiguration1.allowedAuthAlgorithms.set(0);
			localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			localWifiConfiguration1.allowedKeyManagement.set(0);
			localWifiConfiguration1.wepTxKeyIndex = 0;
			if (paramInt == 1) {
				localWifiConfiguration1.wepKeys[0] = "";
				localWifiConfiguration1.allowedKeyManagement.set(0);
				localWifiConfiguration1.wepTxKeyIndex = 0;
			} else if (paramInt == 2) {
				localWifiConfiguration1.hiddenSSID = true;
				localWifiConfiguration1.wepKeys[0] = passwd;
			} else if (paramInt == 3) {
				localWifiConfiguration1.preSharedKey = passwd;
				localWifiConfiguration1.allowedAuthAlgorithms.set(0);
				localWifiConfiguration1.allowedProtocols.set(1);
				localWifiConfiguration1.allowedProtocols.set(0);
				localWifiConfiguration1.allowedKeyManagement.set(1);
				localWifiConfiguration1.allowedPairwiseCiphers.set(2);
				localWifiConfiguration1.allowedPairwiseCiphers.set(1);
			}
		}
		/*
		 * if (paramString3.equals("wt")) { localWifiConfiguration1.SSID = ("\""
		 * + ssid + "\""); WifiConfiguration localWifiConfiguration2 =
		 * isExsits(ssid); if (localWifiConfiguration2 != null)
		 * this.mWifiManager .removeNetwork(localWifiConfiguration2.networkId);
		 * if (paramInt == 1) { localWifiConfiguration1.wepKeys[0] = "";
		 * localWifiConfiguration1.allowedKeyManagement.set(0);
		 * localWifiConfiguration1.wepTxKeyIndex = 0; } if (paramInt == 2) {
		 * localWifiConfiguration1.hiddenSSID = true; if
		 * (!paramString3.equals("wt")) break label367;
		 * localWifiConfiguration1.wepKeys[0] = ("\"" + paramString2 + "\""); }
		 * } while (true) {
		 * localWifiConfiguration1.allowedAuthAlgorithms.set(1);
		 * localWifiConfiguration1
		 * .allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		 * localWifiConfiguration1
		 * .allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		 * localWifiConfiguration1
		 * .allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		 * localWifiConfiguration1
		 * .allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
		 * localWifiConfiguration1.allowedKeyManagement.set(0);
		 * localWifiConfiguration1.wepTxKeyIndex = 0; if (paramInt == 3) { if
		 * (!paramString3.equals("wt")) break label378;
		 * localWifiConfiguration1.preSharedKey = ("\"" + paramString2 + "\"");
		 * localWifiConfiguration1.hiddenSSID = true;
		 * localWifiConfiguration1.allowedAuthAlgorithms.set(0);
		 * localWifiConfiguration1.allowedGroupCiphers.set(2);
		 * localWifiConfiguration1.allowedKeyManagement.set(1);
		 * localWifiConfiguration1.allowedPairwiseCiphers.set(1);
		 * localWifiConfiguration1.allowedGroupCiphers.set(3);
		 * localWifiConfiguration1.allowedPairwiseCiphers.set(2);
		 * localWifiConfiguration1.status = 2; } return localWifiConfiguration1;
		 * localWifiConfiguration1.SSID = ssid; break; label367:
		 * localWifiConfiguration1.wepKeys[0] = paramString2; } label378:
		 * localWifiConfiguration1.preSharedKey = paramString2;
		 * localWifiConfiguration1.allowedAuthAlgorithms.set(0);
		 * localWifiConfiguration1.allowedProtocols.set(1);
		 * localWifiConfiguration1.allowedProtocols.set(0);
		 * localWifiConfiguration1.allowedKeyManagement.set(1);
		 * localWifiConfiguration1.allowedPairwiseCiphers.set(2);
		 * localWifiConfiguration1.allowedPairwiseCiphers.set(1);
		 */
		return localWifiConfiguration1;
	}

	public WifiConfiguration createAPWifiInfo(String ssid, String passwd, int paramInt) {
		WifiConfiguration localWifiConfiguration1 = new WifiConfiguration();
		localWifiConfiguration1.allowedAuthAlgorithms.clear();
		localWifiConfiguration1.allowedGroupCiphers.clear();
		localWifiConfiguration1.allowedKeyManagement.clear();
		localWifiConfiguration1.allowedPairwiseCiphers.clear();
		localWifiConfiguration1.allowedProtocols.clear();
		localWifiConfiguration1.SSID = ssid;
		localWifiConfiguration1.allowedAuthAlgorithms.set(1);
		localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		localWifiConfiguration1.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
		localWifiConfiguration1.allowedKeyManagement.set(0);
		localWifiConfiguration1.wepTxKeyIndex = 0;
		if (paramInt == 1) {
			localWifiConfiguration1.wepKeys[0] = "";
			localWifiConfiguration1.allowedKeyManagement.set(0);
			localWifiConfiguration1.wepTxKeyIndex = 0;
		} else if (paramInt == 2) {
			localWifiConfiguration1.hiddenSSID = true;
			localWifiConfiguration1.wepKeys[0] = passwd;
		} else if (paramInt == 3) {
			localWifiConfiguration1.preSharedKey = passwd;
			localWifiConfiguration1.allowedAuthAlgorithms.set(0);
			localWifiConfiguration1.allowedProtocols.set(1);
			localWifiConfiguration1.allowedProtocols.set(0);
			localWifiConfiguration1.allowedKeyManagement.set(1);
			localWifiConfiguration1.allowedPairwiseCiphers.set(2);
			localWifiConfiguration1.allowedPairwiseCiphers.set(1);
		}
		return localWifiConfiguration1;
	}

	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}

	public WifiConfiguration CreateJoinWifiConfig(String SSID, String Password, WifiCipherType Type, String bssid) {
		WifiConfiguration localWifiConfiguration2 = isExsits(SSID);
		if (localWifiConfiguration2 != null)
			mWifiManager.removeNetwork(localWifiConfiguration2.networkId);
		WifiConfiguration config = new WifiConfiguration();
		if (bssid != null && bssid.length() > 0) {
			config.BSSID = bssid;
		}
		if (Password == null || Password.length() == 0) {
			Type = WifiCipherType.WIFICIPHER_NOPASS;
			config = new WifiConfiguration();
			config.SSID = "\"" + SSID + "\"";

			config.hiddenSSID = false;
			config.status = WifiConfiguration.Status.ENABLED;
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			return config;
		}

		config.allowedAuthAlgorithms.clear();

		config.allowedGroupCiphers.clear();

		config.allowedKeyManagement.clear();

		config.allowedPairwiseCiphers.clear();

		config.allowedProtocols.clear();

		config.SSID = "\"" + SSID + "\"";

		// WEP

		if (Type == WifiCipherType.WIFICIPHER_WEP) {

			config.preSharedKey = "\"" + Password + "\"";

			config.hiddenSSID = true;

			config.allowedAuthAlgorithms

			.set(WifiConfiguration.AuthAlgorithm.SHARED);

			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

			config.allowedGroupCiphers

			.set(WifiConfiguration.GroupCipher.WEP104);

			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

			config.wepTxKeyIndex = 0;

		}

		// WPA

		if (Type == WifiCipherType.WIFICIPHER_WPA) {

			config.preSharedKey = "\"" + Password + "\"";

			config.hiddenSSID = true;
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

			config.status = WifiConfiguration.Status.ENABLED;

		}

		return config;

	}

	public void disconnectWifi(int paramInt) {
		this.mWifiManager.disableNetwork(paramInt);
	}
	
	public void disconnect() {
		this.mWifiManager.disconnect();
	}

	public String getApSSID() {
		try {
			Method localMethod = this.mWifiManager.getClass().getDeclaredMethod("getWifiApConfiguration", new Class[0]);
			if (localMethod == null)
				return null;
			Object localObject1 = localMethod.invoke(this.mWifiManager, new Object[0]);
			if (localObject1 == null)
				return null;
			WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
			if (localWifiConfiguration.SSID != null)
				return localWifiConfiguration.SSID;
			Field localField1 = WifiConfiguration.class.getDeclaredField("mWifiApProfile");
			if (localField1 == null)
				return null;
			localField1.setAccessible(true);
			Object localObject2 = localField1.get(localWifiConfiguration);
			localField1.setAccessible(false);
			if (localObject2 == null)
				return null;
			Field localField2 = localObject2.getClass().getDeclaredField("SSID");
			localField2.setAccessible(true);
			Object localObject3 = localField2.get(localObject2);
			if (localObject3 == null)
				return null;
			localField2.setAccessible(false);
			String str = (String) localObject3;
			return str;
		} catch (Exception localException) {
		}
		return null;
	}

	public WifiConfiguration getById(int id) {
		List<WifiConfiguration> Configuration = getConfiguration();
		if (Configuration != null)
			for (WifiConfiguration wiCon : Configuration) {
				if (wiCon.networkId == id) {
					return wiCon;
				}
			}
		return null;
	}

	public String getBSSID() {
		if (this.mWifiInfo == null)
			return "NULL";
		return this.mWifiInfo.getBSSID();
	}

	public List<WifiConfiguration> getConfiguration() {
		return this.mWifiConfiguration;
	}

	public int getIPAddress() {
		if (this.mWifiInfo == null)
			return 0;
		return this.mWifiInfo.getIpAddress();
	}

	public String getMacAddress() {
		if (this.mWifiInfo == null)
			return "NULL";
		return this.mWifiInfo.getMacAddress();
	}

	public int getNetworkId() {
		if (this.mWifiInfo == null)
			return 0;
		return this.mWifiInfo.getNetworkId();
	}

	public int getWifiApState() {
		try {
			int i = ((Integer) this.mWifiManager.getClass().getMethod("getWifiApState", new Class[0])
					.invoke(this.mWifiManager, new Object[0])).intValue();
			return i;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return 4;
	}

	public WifiInfo getWifiInfo() {
		return this.mWifiManager.getConnectionInfo();
	}

	public List<ScanResult> getWifiList() {
		return this.mWifiList;
	}

	public StringBuilder lookUpScan() {
		StringBuilder localStringBuilder = new StringBuilder();
		for (int i = 0;; i++) {
			if (i >= 2)
				return localStringBuilder;
			localStringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
			localStringBuilder.append(((ScanResult) this.mWifiList.get(i)).toString());
			localStringBuilder.append("/n");
		}
	}

	public void startScan() {
		mWifiManager.startScan();
		mWifiList = mWifiManager.getScanResults();
		Scanresult = true;
		if (mWifiList == null || mWifiList.size() == 0) {
			Scanresult = false;
		}
		if (!Scanresult) {
			LogManager.e("scan ing ");
			long startTime = System.currentTimeMillis();
			WTBroadcast.ehList.add(new EventHandler() {

				@Override
				public void wifiStatusNotification() {
					// TODO Auto-generated method stub

				}

				@Override
				public void scanResultsAvailable() {
					// TODO Auto-generated method stub
					Scanresult = true;
				}

				@Override
				public void handleConnectChange(State status) {
					// TODO Auto-generated method stub

				}
			});

			while (!Scanresult && (System.currentTimeMillis() - startTime) < 15000) {
				try {
					LogManager.e("wait ing for scan result ");
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		mWifiList = mWifiManager.getScanResults();
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
	}

	public void removeNetwork(int id) {
		mWifiManager.removeNetwork(id);
	}
	
	public int getWifiState() {
		return this.mWifiManager.getWifiState();
	}
}