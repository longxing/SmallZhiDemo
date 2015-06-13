package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiMyTag;
import com.iii.wifi.dao.newmanager.AbsWifiCRUDForObject;

public class WifiCRUDForMyTag extends AbsWifiCRUDForObject {
	public WifiCRUDForMyTag(String ip, int port) {
		super(ip, port);
		// TODO Auto-generated constructor stub
	}

	public interface ResultForMyTagListener {
		public void onResult(String type, String errorCode, WifiMyTag myTag);
	}

	public void setMyTag(final String tag, final String imei, final ResultForMyTagListener resultListener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(ip, port), 5000);
					}

					WifiMyTag myTag = new WifiMyTag();
					myTag.setType(OPERATION_TYPE_SET);
					myTag.setImei(imei);
					myTag.setTag(tag);

					String obj = WifiCreateAndParseSockObjectManager.createWifiMyTagInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, myTag) + "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				WifiMyTag infos = (WifiMyTag) result.getObject();
				resultListener.onResult(result.getType(), result.getError(), infos);
			}
		}).start();
	}

	public void getMyTag(final String imei, final ResultForMyTagListener resultListener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(ip, port), 5000);
					}

					WifiMyTag myTag = new WifiMyTag();
					myTag.setType(OPERATION_TYPE_GET);
					myTag.setImei(imei);

					String obj = WifiCreateAndParseSockObjectManager.createWifiMyTagInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, myTag) + "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				WifiMyTag infos = (WifiMyTag) result.getObject();
				resultListener.onResult(result.getType(), result.getError(), infos);
			}
		}).start();
	}
}
