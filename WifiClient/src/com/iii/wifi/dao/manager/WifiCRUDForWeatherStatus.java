package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;

import android.content.Context;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiTTSVocalizationTypeInfo;
import com.iii.wifi.dao.info.WifiWeatherStatusInfos;

public class WifiCRUDForWeatherStatus {
	/**
	 * 用于天气的查询，更新和增加 使用时传入ip,端口号,回传结果类 调用相应的方法，结果会由回传接口返回
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

	public interface ResultForWeatherListener {
		public void onResult(String type, String errorCode, String weatherStatus);
	}

	public WifiCRUDForWeatherStatus(Context context, String ip, int port) {
		mContext = context;
		mIP = ip;
		mPort = port;
	}

	/**
	 * from json:{"error":"0","obj":{"type":"0","wifiInfos":[{"weatherStatus":
	 * "weather1"}]},"type":"5"} to
	 * json:{"error":"1","obj":{"type":"0","wifiInfos"
	 * :[{"weatherStatus":"weather1"}]},"type":"5"} // * @param weatherStatus
	 * 
	 * @param resultListener
	 */
	public void add(final String weatherStatus, final ResultForWeatherListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiWeatherInfos(DB_ADD,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, weatherStatus)+ "\n";
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
				resultListener.onResult(result.getType(), result.getError(),
						((WifiWeatherStatusInfos) result.getObject()).getWifiInfo().get(0).getLedName());
			}

		}).start();
	}

	public void delete(WifiTTSVocalizationTypeInfo info, ResultForWeatherListener resultListener) {

	}

	/**
	 * from json:{"error":"0","obj":{"type":"1","wifiInfos":[{"weatherStatus":
	 * "weather3"}]},"type":"5"} to
	 * json:{"error":"1","obj":{"type":"1","wifiInfos"
	 * :[{"weatherStatus":"weather3"}]},"type":"5"}
	 * 
	 * @param weatherStatus
	 * @param resultListener
	 */
	public void updata(final String weatherStatus, final ResultForWeatherListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiWeatherInfos(DB_UPDATA,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, weatherStatus)+ "\n";
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
				resultListener.onResult(result.getType(), result.getError(),
						((WifiWeatherStatusInfos) result.getObject()).getWifiInfo().get(0).getLedName());
			}

		}).start();
	}

	/**
	 * from json:{"error":"0","obj":{"type":"3","wifiInfos":[{}]},"type":"5"} to
	 * json :{"error":"1","obj":{"type":"3","wifiInfos":[{"weatherStatus":
	 * "weather3" }]},"type":"5"}
	 * 
	 * @param resultListener
	 */
	public void select(final ResultForWeatherListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiWeatherInfos(DB_SELETE,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, "")+ "\n";
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
				resultListener.onResult(result.getType(), result.getError(),
						((WifiWeatherStatusInfos) result.getObject()).getWifiInfo().get(0).getLedName());
			}

		}).start();
	}
}
