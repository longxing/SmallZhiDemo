package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;

import android.content.Context;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiTTSVocalizationTypeInfo;
import com.iii.wifi.dao.info.WifiTTSVocalizationTypeInfos;

public class WifiCRUDForTTS {
	/**
	 * 用于TTS的查询，更新和增加 使用时传入ip,端口号,回传结果类 调用相应的方法，结果会由回传接口返回
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

	public interface ResultForTTSListener {
		public void onResult(String type, String errorCode, String ttsName);
	}

	public WifiCRUDForTTS(Context context, String ip, int port) {
		mContext = context;
		mIP = ip;
		mPort = port;
	}

	/**
	 * from
	 * json:{"error":"0","obj":{"type":"0","wifiInfos":[{"TTSType":"ttsType1"
	 * }]},"type":"1"} to
	 * json:{"error":"1","obj":{"type":"0","wifiInfos":[{"TTSType"
	 * :"ttsType1"}]},"type":"1"}
	 * 
	 * @param TTSType
	 * @param resultListener
	 */
	public void add(final String TTSType, final ResultForTTSListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiTTSVocalizationTypeInfos(DB_ADD,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, TTSType)+ "\n";
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
						((WifiTTSVocalizationTypeInfos) result.getObject()).getWifiInfo().get(0).getType());
			}

		}).start();
	}

	public void delete(WifiTTSVocalizationTypeInfo info, ResultForTTSListener resultListener) {

	}

	/**
	 * from
	 * json:{"error":"0","obj":{"type":"1","wifiInfos":[{"TTSType":"ttsType3"
	 * }]},"type":"1"} to
	 * json:{"error":"1","obj":{"type":"1","wifiInfos":[{"TTSType"
	 * :"ttsType3"}]},"type":"1"}
	 * 
	 * @param TTSType
	 * @param resultListener
	 */
	public void updata(final String TTSType, final ResultForTTSListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiTTSVocalizationTypeInfos(DB_UPDATA,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, TTSType)+ "\n";
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
						((WifiTTSVocalizationTypeInfos) result.getObject()).getWifiInfo().get(0).getType());
			}

		}).start();
	}

	/**
	 * from json:{"error":"0","obj":{"type":"3","wifiInfos":[{}]},"type":"1"} to
	 * json
	 * :{"error":"1","obj":{"type":"3","wifiInfos":[{"TTSType":"ttsType3"}]},
	 * "type":"1"}
	 * 
	 * @param resultListener
	 */
	public void select(final ResultForTTSListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiTTSVocalizationTypeInfos(DB_SELETE,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, "")+ "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(DB_SELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				resultListener.onResult(result.getType(), result.getError(),
						((WifiTTSVocalizationTypeInfos) result.getObject()).getWifiInfo().get(0).getType());
			}

		}).start();
	}
}
