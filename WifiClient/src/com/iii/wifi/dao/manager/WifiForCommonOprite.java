package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.logging.LogManager;

import android.util.Log;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiJSONObjectForLearnHF;
import com.iii.wifi.dao.info.WifiJSONObjectForLearnHFs;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.manager.WifiCRUDForDevice.ResultListener;
import com.iii.wifi.dao.manager.WifiCRUDForWeatherTime.ResultForWeatherTimeListener;

public class WifiForCommonOprite {

	public static final String LEARN_HF = "learnHF";
	public static final String GET_UNCONFIGED_DEVICE = "unconfigedDevice";
	public static final String PLAY_TTS = "play_tts";

	private String mIp;
	private int mPort;

	public WifiForCommonOprite(int port, String ip) {
		mIp = ip;
		mPort = port;
	}

	public void learnHF(final String deviceid, final ResultForWeatherTimeListener resultListener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(mIp, mPort), 5000);
					}
					String obj = WifiCreateAndParseSockObjectManager.createWifiCommonOpriteInfo(LEARN_HF, deviceid) + "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					Log.e("123", obj);
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				Gson gson = new Gson();
				WifiJSONObjectForLearnHFs learnHfs = gson.fromJson(result.getObject().toString(), WifiJSONObjectForLearnHFs.class);
				WifiJSONObjectForLearnHF learnHf = learnHfs.getWifiInfoFirst();
				resultListener.onResult(result.getType(), result.getError(), learnHf.getHFContent());

			}
		}).start();

	}

	public void getUnConfigedDevice(final ResultListener resultListener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(mIp, mPort), 5000);
					}
					String obj = WifiCreateAndParseSockObjectManager.createWifiCommonOpriteInfo(GET_UNCONFIGED_DEVICE, null) + "\n";
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
				Gson gson = new Gson();
				WifiJSONObjectForLearnHFs learnHfs = gson.fromJson(result.getObject().toString(), WifiJSONObjectForLearnHFs.class);
				WifiJSONObjectForLearnHF learnHf = learnHfs.getWifiInfoFirst();
				resultListener.onResult(result.getType(), result.getError(), learnHf.getDeviceInfos());
			}
		}).start();
	}

	public void playTTS(final String content, final ResultForWeatherTimeListener resultListener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Socket socket = new Socket();
				WifiJSONObjectInfo result;
				try {
					if (!socket.isConnected()) {
						socket.connect(new InetSocketAddress(mIp, mPort), 5000);
					}
					String obj = WifiCreateAndParseSockObjectManager.createWifiCommonOpriteInfo(PLAY_TTS, content)+ "\n";
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
				Gson gson = new Gson();
				WifiJSONObjectForLearnHFs learnHfs = gson.fromJson(result.getObject().toString(), WifiJSONObjectForLearnHFs.class);
				WifiJSONObjectForLearnHF learnHf = learnHfs.getWifiInfoFirst();
				resultListener.onResult(result.getType(), result.getError(), learnHf.getHFContent());
			}
		}).start();

	}

}
