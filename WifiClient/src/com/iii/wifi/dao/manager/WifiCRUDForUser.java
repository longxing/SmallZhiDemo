package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;

import android.content.Context;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiUserInfo;
import com.iii.wifi.dao.info.WifiUserInfos;

public class WifiCRUDForUser {
	/**
	 * 用于用户的查询，更新和增加 使用时传入ip,端口号,回传结果类 调用相应的方法，结果会由回传接口返回
	 * 示例：见WifiCRUDForControl类
	 */
	public static final String DB_ADD = "0";
	public static final String DB_UPDATA = "1";
	public static final String DB_DELETE = "2";
	public static final String DB_SELETE = "3";
	public static final String DB_SELETE_BY_ID = "4";
	private Context mContext;
	private String mIP;
	private int mPort;

	public interface ResultForUserListener {
		public void onResult(String type, String errorCode, String userName, String userPassWord);
	}

	public WifiCRUDForUser(Context context, String ip, int port) {
		mContext = context;
		mIP = ip;
		mPort = port;
	}

	/**
	 * from json:{"error":"0","obj":{"type":"0","wifiInfos":[{"mName":
	 * "jiangsehnglan","mPassWord":"232323232"}]},"type":"2"} to
	 * json:{"error":"1"
	 * ,"obj":{"type":"0","wifiInfos":[{"mName":"jiangsehnglan",
	 * "mPassWord":"232323232"}]},"type":"2"}
	 * 
	 * @param info
	 * @param resultListener
	 */
	public void add(final WifiUserInfo info, final ResultForUserListener resultListener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(mIP, mPort), 5000);
					}
					String obj = WifiCreateAndParseSockObjectManager.createWifiUserInfos(DB_ADD,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info)+ "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null, null);
					return;
				}
				resultListener.onResult(result.getType(), result.getError(), ((WifiUserInfos) result.getObject())
						.getWifiInfo().get(0).getmName(), ((WifiUserInfos) result.getObject()).getWifiInfo().get(0)
						.getPassWord());
			}

		}).start();

	}

	/**
	 * from json:{"error":"0","obj":{"type":"1","wifiInfos":[{"mName":
	 * "jiangsehnglan1","mPassWord":"2323232321"}]},"type":"2"} to
	 * json:{"error":
	 * "1","obj":{"type":"1","wifiInfos":[{"mName":"jiangsehnglan1"
	 * ,"mPassWord":"2323232321"}]},"type":"2"}
	 * 
	 * @param info
	 * @param resultListener
	 */
	public void updata(final WifiUserInfo info, final ResultForUserListener resultListener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(mIP, mPort), 5000);
					}
					String obj = WifiCreateAndParseSockObjectManager.createWifiUserInfos(DB_UPDATA,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info)+ "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null, null);
					return;
				}
				resultListener.onResult(result.getType(), result.getError(), ((WifiUserInfos) result.getObject())
						.getWifiInfo().get(0).getmName(), ((WifiUserInfos) result.getObject()).getWifiInfo().get(0)
						.getPassWord());
			}

		}).start();
	}

	/**
	 * from json:{"error":"0","obj":{"type":"3","wifiInfos":[{}]},"type":"2"} to
	 * json
	 * :{"error":"1","obj":{"type":"3","wifiInfos":[{"mName":"jiangsehnglan1"
	 * ,"mPassWord":"2323232321"}]},"type":"2"}
	 * 
	 * @param resultListener
	 */
	public void select(final ResultForUserListener resultListener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(mIP, mPort), 5000);
					}
					String obj = WifiCreateAndParseSockObjectManager.createWifiUserInfos(DB_SELETE,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, new WifiUserInfo())+ "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(DB_SELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null, null);
					return;
				}
				resultListener.onResult(result.getType(), result.getError(), ((WifiUserInfos) result.getObject())
						.getWifiInfo().get(0).getmName(), ((WifiUserInfos) result.getObject()).getWifiInfo().get(0)
						.getPassWord());
			}

		}).start();
	}
}
