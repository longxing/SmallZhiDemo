package com.iii.wifi.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import com.iii.wifi.util.KeyList;
import com.iii.wifi.connect.WifiSwitch.WifiSwitchListener;
import com.iii.wifi.manager.impl.IBindBroadcastReceiver;
import com.iii.wifi.util.WaitUtil;
import com.iii360.sup.common.utl.LogManager;

/**
 * 
 * //0.判断是否wifi已经连接了 //1.打开wifi，判断是否打开 //2.扫描wifi，获取加密方式
 * //3.搜索附近是否有设置的ssid,TTS播报网络和密码错误 //4.连接wifi，判断是否连接成功
 * 
 * @author river
 * 
 */
public class WifiControl extends AbsWifiManager implements Runnable, IBindBroadcastReceiver {

	/*********************************************************************************************/
	/************************************ Member Variables ***************************************/
	/*********************************************************************************************/
	private WifiSwitch mWifiSwitch = null;
	private WifiScanner mWifiScanner = null;
	private String ssid = null;
	private String password = null;
	private WifiControlListener wifiListener = null;
	private State netState = null;
	private WifiConfig mWifiConfig = null;
	private boolean connect = true;
	private boolean next = true;

	/**
	 * 打开wifi失败 or成功 、 扫描wifi失败or成功、 附近是否存在指定wifi有or无、 连接wifi失败or成、 操作失败or成功
	 */
	public enum WifiControlState {

		STATE_OPEN_WIFI_FAIL, STATE_OPEN_WIFI_OK, STATE_SCANNER_WIFI_FAIL, STATE_SCANNER_WIFI_OK, STATE_HAVE_WIFI, STATE_NOT_HAVE_WIFI, STATE_CONNECT_WIFI_FAIL, STATE_CONNECT_WIFI_OK, STATE_OPERATION_FAIL, STATE_OPERATION_OK
	}

	public WifiControlListener getWifiListener() {
		return wifiListener;
	}

	public void setWifiListener(WifiControlListener wifiListener) {
		this.wifiListener = wifiListener;
	}

	public interface WifiControlListener {
		public void onResult(WifiControlState state);

	}

	public WifiControl(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mWifiSwitch = new WifiSwitch(context);
		mWifiScanner = new WifiScanner(context);
		mWifiConfig = new WifiConfig(context);
		registerReceiver();
	}

	@Override
	public synchronized void run() {
		connect();
	}

	public void setWifiInfo(String ssid, String password) {
		this.ssid = ssid;
		this.password = password;
	}

	/**
	 * 调用此接口前，一定先设置setWifiInfo
	 * 
	 * @return
	 */
	public boolean connect() {
		return connect(ssid, password);
	}

	public boolean connect(final String ssid, final String password) {

		LogManager.d(WIFI_HEAD, "==========>>connect start" + ssid + "||" + password);

		connect = true;
		next = true;

		// 当前是否连接了
		if (WifiUtils.isConnectWifi(context, ssid)) {
			if (wifiListener != null) {
				wifiListener.onResult(WifiControlState.STATE_CONNECT_WIFI_OK);
			}
			return true;
		}

		LogManager.d(WIFI_HEAD, "==========>>wifi switch : " + mWifiSwitch.isWifiEnabled());

		/***
		 * wifi开关没有打开，打开wifi开关
		 */
		if (!mWifiSwitch.isWifiEnabled()) {
			next = false;
			LogManager.d(WIFI_HEAD + "==========>>start open wifi");
			mWifiSwitch.openWifi(new WifiSwitchListener() {

				@Override
				public void onConnectResult(int state) {
					// TODO Auto-generated method stub
					mWifiSwitch.openWifi(null);
					mWifiSwitch.destroy();
					switch (state) {
					case WifiManager.WIFI_STATE_DISABLED:
						if (wifiListener != null) {
							wifiListener.onResult(WifiControlState.STATE_OPEN_WIFI_FAIL);
						}
						connect = false;
						LogManager.d(WIFI_HEAD + "==========>>open wifi back result fail");
						break;

					case WifiManager.WIFI_STATE_ENABLED:
						LogManager.d(WIFI_HEAD + "==========>>open wifi back result success!");
						if (wifiListener != null) {
							wifiListener.onResult(WifiControlState.STATE_OPEN_WIFI_OK);
						}
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								// 扫描wifi
								LogManager.i(WIFI_HEAD, "==========>>start scanner wifi");
								mWifiScanner.scanner();
								boolean scanner = mWifiScanner.checkScannerWifiResult();
								LogManager.i(WIFI_HEAD, "==========>>scanner wifi end and  result=" + scanner);
								if (wifiListener != null) {
									if (scanner) {
										wifiListener.onResult(WifiControlState.STATE_SCANNER_WIFI_OK);
									} else {
										wifiListener.onResult(WifiControlState.STATE_SCANNER_WIFI_FAIL);
									}
								}

								next = false;
								connect = mWifiConfig.connect(ssid, password);

								if (wifiListener != null) {
									if (connect) {
										wifiListener.onResult(WifiControlState.STATE_OPERATION_OK);
									} else {
										wifiListener.onResult(WifiControlState.STATE_OPERATION_FAIL);
									}
								}

								LogManager.i(WIFI_HEAD, "==========>>connect end " + connect);
								checkConnect();
							}
						}).start();
						break;

					default:
						break;
					}
				}
			});

		} else {
			LogManager.d(WIFI_HEAD, "==========>>wifi switch : true");
		}

		if (!connect) {
			return connect;
		}

		if (next) {
			
			//TODO fix scan 
			boolean available = mWifiScanner.checkBSAvailable(ssid);
			if(false == available){
				if (KeyList.TTSUtil.isWorking())
					KeyList.TTSUtil.playContent("连接失败，未发现该网络，请检查路由器是否开启");
				if (wifiListener != null) {
					wifiListener.onResult(WifiControlState.STATE_NOT_HAVE_WIFI);
				}
				
				return connect;
			}
	
			connect = mWifiConfig.connect(ssid, password);

			if (wifiListener != null) {
				if (connect) {
					wifiListener.onResult(WifiControlState.STATE_OPERATION_OK);
				} else {
					wifiListener.onResult(WifiControlState.STATE_OPERATION_FAIL);
				}
			}

			LogManager.i(WIFI_HEAD, "----------connect end---------" + connect);
			checkConnect();
		}
		return connect;
	}

	/**
	 * 每隔一秒检查网络的连接状态，连接成功立马返回。超过35s认为连接超时，连接失败
	 */
	public void checkConnect() {
		long t = System.currentTimeMillis();
		netState = null;
		while (netState == null || netState != State.CONNECTED) {

			LogManager.e(WIFI_HEAD + "wifi connecting...." + netState);

			if (System.currentTimeMillis() - t > 35000) {
				if (wifiListener != null) {
					wifiListener.onResult(WifiControlState.STATE_CONNECT_WIFI_FAIL);
				}
				return;
			}
			WaitUtil.sleep(1000);
		}

		if (wifiListener != null) {
			wifiListener.onResult(WifiControlState.STATE_CONNECT_WIFI_OK);
		}
	}

	/**
	 * 得到连接的ID
	 */
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	/**
	 * 断开当前连接的网络
	 */
	public void disConnect() {
		int netId = getNetworkId();
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiInfo = null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		mWifiSwitch.destroy();
		mWifiScanner.destroy();
		unregisterReceiver();
	}

	/**
	 * 注册网络状态改变监听
	 */

	@Override
	public void registerReceiver() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		context.registerReceiver(receiver, filter);
	}

	/**
	 * 注销网络状态改变监听
	 */
	@Override
	public void unregisterReceiver() {
		// TODO Auto-generated method stub
		try {
			context.unregisterReceiver(receiver);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 网络状态改变接收器，更新wifi连接的状态
	 */
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
				// 这个监听wifi的连接状态即是否连上了一个有效无线路由
				Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (null != parcelableExtra) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					netState = networkInfo.getState();
					LogManager.d(WIFI_HEAD, "========>>isConnected = " + State.CONNECTED + "||netState=" + netState);

				}

			}
		}
	};
}
