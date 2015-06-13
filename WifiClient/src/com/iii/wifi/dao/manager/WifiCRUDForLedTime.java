package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiLedTimeInfo;
import com.iii.wifi.dao.info.WifiLedTimeInfos;

public class WifiCRUDForLedTime {
	/**
	 * 用于ledTime的查询，更新和增加 使用时传入ip,端口号,回传结果类 调用相应的方法，结果会由回传接口返回
	 * 示例：见WifiCRUDForControl类
	 */
	public static final String DB_ADD = "0";
	public static final String DB_UPDATA = "1";
	public static final String DB_DELETE = "2";
	public static final String DB_SELETE = "3";
	public static final String DB_SELETE_BY_ID = "4";
	private String mIP;
	private int mPort;
	private Context mContext;
	private static final String DATE_FORMAT = "yyyyMMddHHmmsssss";
	private String operateTime = "0";

	public interface ResultForLedTimeListener {
		public void onResult(String type, String errorCode, String ledName);
	}

	public interface ResultForLedStateAndTimeListener {
		public void onResult(String type, String errorCode, String ledName, boolean isOpen);
	}

	public WifiCRUDForLedTime(Context context, String ip, int port) {
		mContext = context;
		mIP = ip;
		mPort = port;
	}

	/**
	 * from json:{"error":"0","obj":{"type":"0","wifiInfos":[{"ledTimeName":
	 * "ledTime"}]},"type":"4"} to
	 * json:{"error":"1","obj":{"type":"0","wifiInfos"
	 * :[{"ledTimeName":"ledTime"}]},"type":"4"}
	 * 
	 * @param ledTimeName
	 * @param resultListener
	 */
	public void add(final String ledTimeName, final boolean isOpen, final ResultForLedTimeListener resultListener) {
		operateTime = new SimpleDateFormat(DATE_FORMAT).format(new Date(System.currentTimeMillis()));
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiLedTimeInfos(DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, ledTimeName, operateTime, isOpen) + "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				resultListener.onResult(result.getType(), result.getError(), ((WifiLedTimeInfos) result.getObject()).getWifiInfo().get(0).getLedName());
			}

		}).start();
	}

	public void delete(WifiLedTimeInfo info, ResultForLedTimeListener resultListener) {

	}

	/**
	 * from json:{"error":"0","obj":{"type":"1","wifiInfos":[{"ledTimeName":
	 * "ledTime1"}]},"type":"4"} to
	 * json:{"error":"1","obj":{"type":"1","wifiInfos"
	 * :[{"ledTimeName":"ledTime1"}]},"type":"4"}
	 * 
	 * @param ledTimeName
	 * @param resultListener
	 */
	public void updata(final String ledTimeName, final boolean isOpen, final ResultForLedTimeListener resultListener) {
		operateTime = new SimpleDateFormat(DATE_FORMAT).format(new Date(System.currentTimeMillis()));
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiLedTimeInfos(DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, ledTimeName, operateTime, isOpen) + "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				resultListener.onResult(result.getType(), result.getError(), ((WifiLedTimeInfos) result.getObject()).getWifiInfo().get(0).getLedName());
			}

		}).start();

	}

	/**
	 * from json:{"error":"0","obj":{"type":"3","wifiInfos":[{}]},"type":"4"} to
	 * json
	 * :{"error":"1","obj":{"type":"3","wifiInfos":[{"ledTimeName":"ledTime1"
	 * }]},"type":"4"}
	 * 
	 * @param resultListener
	 */
	public void selete(final ResultForLedStateAndTimeListener resultListener) {
		operateTime = new SimpleDateFormat(DATE_FORMAT).format(new Date(System.currentTimeMillis()));
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiLedTimeInfos(DB_SELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, "", operateTime, false) + "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(DB_SELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null, false);
					return;
				}
				String aString = result.toString();
				String A = ((WifiLedTimeInfos) result.getObject()).getWifiInfo().get(0).getLedName();
				resultListener.onResult(result.getType(), result.getError(), ((WifiLedTimeInfos) result.getObject()).getWifiInfo().get(0).getLedName(), ((WifiLedTimeInfos) result.getObject())
						.getWifiInfo().get(0).isOpen());
			}

		}).start();
	}
}
