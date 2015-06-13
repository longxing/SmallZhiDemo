package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.iii.wifi.dao.info.HuanTengAccount;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.newmanager.AbsWifiCRUDForObject;

public class WifiCRUDForHuanteng extends AbsWifiCRUDForObject {

	public WifiCRUDForHuanteng(String ip, int port) {
		super(ip, port);
	}

	public interface ResultForHuantengListener {
		public void onResult(String type, String errorCode, HuanTengAccount userData);
	}

	public void setHuantengData(final HuanTengAccount huanTengInfo, final ResultForHuantengListener resultListener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(ip, port), 5000);
					}

					huanTengInfo.setType(OPERATION_TYPE_SET);

					String obj = WifiCreateAndParseSockObjectManager.createWifiHuanTengInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, huanTengInfo) + "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, null);
					outputStream.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				HuanTengAccount infos = (HuanTengAccount) result.getObject();
				resultListener.onResult(result.getType(), result.getError(), infos);
			}
		}).start();
	}

	public void getHuantengData(final ResultForHuantengListener resultListener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(ip, port), 5000);
					}

					HuanTengAccount huanTengInfo = new HuanTengAccount();
					huanTengInfo.setType(OPERATION_TYPE_GET);

					String obj = WifiCreateAndParseSockObjectManager.createWifiHuanTengInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, huanTengInfo)+ "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, null);
					outputStream.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				HuanTengAccount infos = (HuanTengAccount) result.getObject();
				resultListener.onResult(result.getType(), result.getError(), infos);
			}
		}).start();
	}

	public void deleteHuantengData(final ResultForHuantengListener resultListener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(ip, port), 5000);
					}

					HuanTengAccount huanTengInfo = new HuanTengAccount();
					huanTengInfo.setType(OPERATION_TYPE_DELETE);

					String obj = WifiCreateAndParseSockObjectManager.createWifiHuanTengInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, huanTengInfo)+ "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, null);
					outputStream.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				HuanTengAccount infos = (HuanTengAccount) result.getObject();
				resultListener.onResult(result.getType(), result.getError(), infos);
			}
		}).start();
	}
}
