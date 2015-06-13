package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiRemindInfos;
import com.iii.wifi.dao.info.WifiRoomInfos;
import com.voice.common.util.Remind;

import android.content.Context;

public class WifiCRUDForRemind {

	public static final String DB_ADD = "0";
	public static final String DB_UPDATA = "1";
	public static final String DB_DELETE = "2";
	public static final String DB_SELETE = "3";
	public static final String DB_SELETE_BY_ID = "4";
	private String mIP;
	private int mPort;
	private Context mContext;

	public interface ResultForRemindListener {
		public void onResult(String type, String errorCode, WifiRemindInfos infos);
	}

	public WifiCRUDForRemind(Context context, String ip, int port) {
		mContext = context;
		mIP = ip;
		mPort = port;
	}

	public void updateRemind(final Remind r, final ResultForRemindListener resultListener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				Gson gson = new Gson();
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(mIP, mPort), 5000);
					}
					// String obj =
					// WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(DB_ADD,
					// WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT,
					// info);

					WifiJSONObjectInfo info = new WifiJSONObjectInfo();
					info.setType(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_REMIND);
					WifiRemindInfos infos = new WifiRemindInfos();
					infos.setType(DB_UPDATA);
					infos.addRemind(r);
					info.setObject(infos);
					String obj = gson.toJson(info).toString() + "\n";

					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
					// resultListener.onResult(result.getType(),
					// result.getError(),
					// ((WifiRoomInfos) result.getObject()).getWifiInfo());

				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (resultListener != null) {
						resultListener.onResult(DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					}
					return;
				}
				if (resultListener != null) {
					resultListener.onResult(result.getType(), result.getError(), gson.fromJson(gson.toJson(result.getObject()), WifiRemindInfos.class));
				}
			}
		}).start();

	}

	public void deleteRemind(final int id, final ResultForRemindListener resultListener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				Gson gson = new Gson();
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(mIP, mPort), 5000);
					}
					// String obj =
					// WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(DB_ADD,
					// WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT,
					// info);

					WifiJSONObjectInfo info = new WifiJSONObjectInfo();
					info.setType(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_REMIND);
					WifiRemindInfos infos = new WifiRemindInfos();
					infos.setType(DB_DELETE);
					Remind r = new Remind();
					r.id = id;
					infos.addRemind(r);

					info.setObject(infos);
					String obj = gson.toJson(info).toString() + "\n";

					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
					// resultListener.onResult(result.getType(),
					// result.getError(),
					// ((WifiRoomInfos) result.getObject()).getWifiInfo());

				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (resultListener != null) {
						resultListener.onResult(DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					}
					return;
				}
				if (resultListener != null) {
					resultListener.onResult(result.getType(), result.getError(), gson.fromJson(gson.toJson(result.getObject()), WifiRemindInfos.class));
				}

			}
		}).start();
	}

	public void createRemind(final Remind r, final ResultForRemindListener resultListener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				Gson gson = new Gson();
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(mIP, mPort), 5000);
					}
					// String obj =
					// WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(DB_ADD,
					// WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT,
					// info);

					WifiJSONObjectInfo info = new WifiJSONObjectInfo();
					info.setType(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_REMIND);
					WifiRemindInfos infos = new WifiRemindInfos();
					infos.setType(DB_ADD);
					infos.addRemind(r);

					info.setObject(infos);
					String obj = gson.toJson(info).toString() + "\n";

					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
					// resultListener.onResult(result.getType(),
					// result.getError(),
					// ((WifiRoomInfos) result.getObject()).getWifiInfo());

				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (resultListener != null) {
						resultListener.onResult(DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					}
					return;
				}
				if (resultListener != null) {
					resultListener.onResult(result.getType(), result.getError(), gson.fromJson(gson.toJson(result.getObject()), WifiRemindInfos.class));
				}

			}
		}).start();
	}

	public void selectRemind(final ResultForRemindListener resultListener) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				Gson gson = new Gson();
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(mIP, mPort), 5000);
					}
					// String obj =
					// WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(DB_ADD,
					// WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT,
					// info);

					WifiJSONObjectInfo info = new WifiJSONObjectInfo();
					info.setType(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_REMIND);
					WifiRemindInfos infos = new WifiRemindInfos();
					infos.setType(DB_SELETE);

					info.setObject(infos);
					String obj = gson.toJson(info).toString()+ "\n";

					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
					// resultListener.onResult(result.getType(),
					// result.getError(),
					// ((WifiRoomInfos) result.getObject()).getWifiInfo());

				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (resultListener != null) {
						resultListener.onResult(DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					}
					return;
				}
				if (resultListener != null) {
					resultListener.onResult(result.getType(), result.getError(), gson.fromJson(gson.toJson(result.getObject()), WifiRemindInfos.class));
				}

			}
		}).start();

	}

}
