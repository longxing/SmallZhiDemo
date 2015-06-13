package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiTTSVocalizationTypeInfo;
import com.iii.wifi.dao.info.WifiWeatherTimeInfos;
import com.iii.wifi.dao.newmanager.AbsWifiCRUDForObject;

public class WifiCRUDForWeatherTime {
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
	private static final String DATE_FORMAT = "yyyyMMddHHmmsssss";
	private String operateTime = "0";

	public interface ResultForWeatherTimeListener {
		public void onResult(String type, String errorCode, String result);
	}

	public interface ResultForWeatherStateAndTimeListener {
		public void onResult(String type, String errorCode, String result, boolean isOpen);
	}

	public WifiCRUDForWeatherTime(Context context, String ip, int port) {
		mContext = context;
		mIP = ip;
		mPort = port;
	}

	/**
	 * from json:{"error":"0","obj":{"type":"0","wifiInfos":[{"weatherTime":
	 * "201311"}]},"type":"9"} to
	 * json:{"error":"1","obj":{"type":"0","wifiInfos"
	 * :[{"weatherTime":"201311"}]},"type":"9"}
	 * 
	 * @param weatherTime
	 * @param resultListener
	 */
	public void add(final String weatherTime, final boolean isOpen, final ResultForWeatherTimeListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiWeatherTimeInfos(DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, weatherTime, operateTime, isOpen) + "\n";
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
				resultListener.onResult(result.getType(), result.getError(), ((WifiWeatherTimeInfos) result.getObject()).getWifiInfo().get(0).getTimeingWeatherReportTime());
			}

		}).start();
	}

	public void setWeatherCityName(final String cityName, final boolean isOpen, final ResultForWeatherTimeListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiWeatherTimeInfos(AbsWifiCRUDForObject.OPERATION_TYPE_SET, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT,
							cityName, operateTime, isOpen) + "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return;
				}
			}

		}).start();
	}

	public void delete(WifiTTSVocalizationTypeInfo info, ResultForWeatherTimeListener resultListener) {

	}

	/**
	 * from json:{"error":"0","obj":{"type":"1","wifiInfos":[{"weatherTime":
	 * "5255201311"}]},"type":"9"} to
	 * json:{"error":"1","obj":{"type":"1","wifiInfos"
	 * :[{"weatherTime":"5255201311"}]},"type":"9"}
	 * 
	 * @param weatherTime
	 * @param resultListener
	 */
	public void updata(final String weatherTime, final boolean isOpen, final ResultForWeatherTimeListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiWeatherTimeInfos(DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, weatherTime, operateTime, isOpen)
							+ "\n";
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
				resultListener.onResult(result.getType(), result.getError(), ((WifiWeatherTimeInfos) result.getObject()).getWifiInfo().get(0).getTimeingWeatherReportTime());
			}

		}).start();
	}

	/**
	 * from json：{"error":"0","obj":{"type":"3","wifiInfos":[{}]},"type":"9"} to
	 * json :{"error":"1","obj":{"type":"3","wifiInfos":[{"weatherTime":
	 * "5255201311" }]},"type":"9"} to
	 * json:{"error":"0","obj":{"type":"3","wifiInfos":[{}]},"type":"9"}//无数据
	 * 
	 * @param resultListener
	 */
	public void select(final ResultForWeatherStateAndTimeListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiWeatherTimeInfos(DB_SELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, "", operateTime, false) + "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null, false);
					return;
				}

				String aString = ((WifiWeatherTimeInfos) result.getObject()).getWifiInfo().get(0).isOpen() + "--";
				String bString = ((WifiWeatherTimeInfos) result.getObject()).getWifiInfo().get(0).getTimeingWeatherReportTime();
				resultListener.onResult(result.getType(), result.getError(), ((WifiWeatherTimeInfos) result.getObject()).getWifiInfo().get(0).getTimeingWeatherReportTime(),
						((WifiWeatherTimeInfos) result.getObject()).getWifiInfo().get(0).isOpen());
			}

		}).start();
	}
}
