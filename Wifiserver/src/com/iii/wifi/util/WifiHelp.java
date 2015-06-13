package com.iii.wifi.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;
import com.iii360.sup.common.utl.net.UdpClient;
import com.iii360.sup.common.utl.net.UdpClient.udpOngetData;

public class WifiHelp extends com.iii360.sup.common.utl.net.WifiHelp {
	// private WifiAdmin mWifiAdmin;
	private Handler mHandler;

	// private String mConnectName;
	private Context context;
	
	private int mUdpPort;

	// public WifiHelp(Context context, Handler handler) {
	// mWifiAdmin = new WifiAdmin(context);
	// this.mHandler = handler;
	// mConnectName = mWifiAdmin.getWifiInfo().getSSID();
	// this.context = context;
	// }

	public WifiHelp(Context context, Handler handler) {
		super(context);
		mHandler = handler;
		this.context = context;
		mUdpPort = PortUtil.getUdpPort(context);
	}

	/**
	 * 检查客户端当前的wifi状态，<br>
	 * 决定是否进行查找主机，重连，等操作
	 */
	public void CheckHost() {

		mWifiAdmin.OpenWifi();
		mWifiAdmin.startScan();
		// String currentWifiName = mWifiAdmin.getWifiInfo().getSSID();
		LogManager.e("currentWifiName" + getCurrentSSID());
		// if (currentWifiName == null) {
		// mHandler.sendEmptyMessage(KeyList.NEED_FIND_SERVER);
		// return;
		// }
		// if (currentWifiName.equals(KeyList.PKEY_APNAME)) {
		// mHandler.sendEmptyMessage(KeyList.HOST_CONNECT_SUCESS);
		//
		// } else
		// if (currentWifiName != null && currentWifiName.length() > 0) {

		if (getCurrentSSID() != null && getCurrentSSID().equals(KeyList.PKEY_APNAME)) {
			mHandler.removeMessages(KeyList.NEED_FIND_SERVER);
			Message msg = new Message();
			msg.obj = "192.168.43.1";
			msg.what = KeyList.GET_UDP_INFO;
			mHandler.sendMessage(msg);
			return;
		}

		if (KeyList.UDP_CLIENT == null) {
			KeyList.UDP_CLIENT = UdpClient.getInstance(new SuperBaseContext(context),true);
		}

		KeyList.UDP_CLIENT.setonGetData(new udpOngetData() {

			@Override
			public void ongetdata(DatagramPacket receivePacket) {
				// TODO Auto-generated method stub
				String reciver = new String(receivePacket.getData());
				LogManager.e(reciver);
				if (reciver.startsWith(KeyList.PKEY_APNAME)) {
					mHandler.removeMessages(KeyList.NEED_FIND_SERVER);
					Message msg = new Message();
					msg.obj = receivePacket.getAddress().getHostAddress();
					msg.what = KeyList.GET_UDP_INFO;
					mHandler.sendMessage(msg);
					// TODO
				}
			}
		});

		mHandler.sendEmptyMessageDelayed(KeyList.NEED_FIND_SERVER, 5000);
	}



	/**
	 * 释放网络连接
	 */
	public void release() {
		if (KeyList.TCP_CLIENT != null) {
			KeyList.TCP_CLIENT.disConnect();
			KeyList.TCP_CLIENT = null;
		}
		if (KeyList.UDP_CLIENT != null) {
			KeyList.UDP_CLIENT.disConnect();
			KeyList.UDP_CLIENT = null;
		}
	}

	// public void reJoin() {
	// if (mConnectName != null && !mConnectName.equals(KeyList.PKEY_APNAME)) {
	// WifiConfiguration config = mWifiAdmin.isExsits(mConnectName);
	// if (config != null) {
	// mWifiAdmin.connectConfiguration(config.networkId);
	// } else {
	// mWifiAdmin.startScan();
	// List<WifiConfiguration> mWifiConfigs = mWifiAdmin.getConfiguration();
	// }
	// }
	//
	// }
	
    /**
     * @param context
     * @return 当前手机连接wifi的ssid
     */
    public static String getWifiSsid(Context context) {
    	WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = mWifiManager.getConnectionInfo();
        String ssid = info.getSSID() ;
        if(TextUtils.isEmpty(ssid)){
            ssid = "none" ;
        }
        return ssid;
    }
    
    public static InetAddress getBroadcastAddress(Context context) throws IOException {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		// handle null somehow
        if (dhcp == null) {
            return InetAddress.getLocalHost();
        }
		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

    
    public static boolean isConnectWifi(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        
        if (wifi.isConnected()) {
            return true;
        } else {
        	LogManager.e("wifyState:"+wifi.getDetailedState()+","+wifi.getExtraInfo()+","+wifi.getReason());
            return false;
        }
    }

}
