package com.iii360.box.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import com.iii360.box.common.BasePreferences;
import com.iii360.box.connect.UnConnectWifiActivity;
import com.iii360.box.util.AppUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.WifiInfoUtils;
import com.iii360.box.util.WifiUtils;

/**
 * 监听wifi网络
 * 
 * @author hefeng
 * 
 */
public class WifiReceiver extends BroadcastReceiver {
	private Context context;
	private BasePreferences preferences;

	public interface WifiStateListener {
		/**
		 * @param isConnect
		 *            连接是否成功
		 * @param ssid
		 *            wifi名称
		 */
		public void onConnect(boolean isConnect, String ssid);
	}

	private static WifiStateListener wifiListener;

	public static void setWifiListener(WifiStateListener wifiListener) {
		WifiReceiver.wifiListener = wifiListener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent == null) {
			return;
		}
		this.preferences = new BasePreferences(context);
		this.context = context;
		String action = intent.getAction();

		Parcelable parcelable = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action) && (null != parcelable)) {

			NetworkInfo networkInfo = (NetworkInfo) parcelable;
			State state = networkInfo.getState();

			switch (state) {

			case CONNECTED:// wifi连接成功
				if(WifiUtils.isConnectWifi(context)){
					setWifiState(true, WifiInfoUtils.getWifiSsid(context));
					LogManager.e("wifi connect success : " + WifiInfoUtils.getWifiSsid(context));
				}

				break;

			case DISCONNECTED:// wifi连接失败
			case UNKNOWN:// 未知错误

				// setWifiState(false, WifiInfoUtils.getWifiSsid(context));
				LogManager.e("wifi connect error : " + WifiInfoUtils.getWifiSsid(context));
				String conn = preferences.getPrefString(KeyList.KEY_CONNECTING_AP);
				if (conn != null && conn.equals("yes")) {
					return;
				}
				if (AppUtils.isAppToBackground(context))
					return;
				Intent i = new Intent(context, UnConnectWifiActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
				break;

			default:
				break;
			}
		}

	}

	/**
	 * @param isConnect
	 *            wifi连接状态
	 */
	private void setWifiState(boolean isConnect, String ssid) {
		if (wifiListener != null) {
			wifiListener.onConnect(isConnect, ssid);
		}

		// Intent intent = new Intent(KeyList.AKEY_WIFI_CHNAGE) ;
		// intent.putExtra(KeyList.IKEY_WIFI_SSID, ssid);
		// context.sendBroadcast(new Intent(KeyList.AKEY_WIFI_CHNAGE)) ;
	}
}
