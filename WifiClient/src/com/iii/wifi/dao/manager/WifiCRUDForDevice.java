package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.List;

import android.content.Context;

import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiDeviceInfos;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;

public class WifiCRUDForDevice {
	/**
	 * 用于Device表的查询，更新和增加 使用时传入ip,端口号,回传结果类 调用相应的方法，结果会由回传接口返回
	 * 示例：见WifiCRUDForControl类
	 */
	public static final String DB_ADD = "0";
	public static final String DB_UPDATA = "1";
	public static final String DB_DELETE = "2";
	public static final String DB_SELECT = "3";
	public static final String DB_SELECT_BY_ROOM_ID = "6";
	public static final String DB_SELECT_BY_DEVICE_ID = "7";
	private String mIP;
	private int mPort;
	private Context mContext;

	/**
	 * 
	 * @author jsl 回传服务器执行结果
	 */
	public interface ResultListener {
		public void onResult(String type, String errorCode, List<WifiDeviceInfo> info);
	}

	public WifiCRUDForDevice(Context context, String ip, int port) {
		mContext = context;
		mIP = ip;
		mPort = port;
	}

	/**
	 * from json:{"error":"0","obj":{"type":"0","wifiInfos":[{"deviceName":
	 * "DeviceName3"
	 * ,"deviceid":"deviceid3","fitting":"fitting3","roomid":"roomid3"
	 * ,"macadd":"macadd3","id":2}]},"type":"8"} to
	 * json:{"error":"1","obj":{"type"
	 * :"0","wifiInfos":[{"deviceName":"DeviceName3"
	 * ,"deviceid":"deviceid3","fitting"
	 * :"fitting3","roomid":"roomid3","macadd":"macadd3","id":2}]},"type":"8"}
	 * 
	 * @param info
	 * @param resultListener
	 */
	public void add(final WifiDeviceInfo info, final ResultListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info) + "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (resultListener != null) {
						resultListener.onResult(DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					}
					return;
				}
				if (resultListener != null) {
					resultListener.onResult(result.getType(), result.getError(), ((WifiDeviceInfos) result.getObject()).getWifiInfo());
				}
			}

		}).start();
	}

	/**
	 * from
	 * json:{"error":"0","obj":{"type":"2","wifiInfos":[{"deviceid":"device3"
	 * ,"id":0}]},"type":"8"} to
	 * json:{"error":"1","obj":{"type":"2","wifiInfos":
	 * [{"deviceid":"device3","id":0}]},"type":"8"}
	 * 
	 * @param deviceId
	 * @param resultListener
	 */
	public void deleteByDeviceId(final String deviceId, final ResultListener resultListener) {
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
					WifiDeviceInfo info = new WifiDeviceInfo();
					info.setDeviceid(deviceId);
					String obj = WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(DB_DELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info) + "\n";
					OutputStream outputStream;

					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (resultListener != null) {
						resultListener.onResult(DB_DELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					}
					return;
				}
				if (resultListener != null) {
					resultListener.onResult(result.getType(), result.getError(), ((WifiDeviceInfos) result.getObject()).getWifiInfo());
				}
			}

		}).start();
	}

	/**
	 * from json:{"error":"0","obj":{"type":"1","wifiInfos":[{"deviceName":
	 * "DeviceName5"
	 * ,"deviceid":"deviceid5","fitting":"fitting5","roomid":"roomid5"
	 * ,"macadd":"macadd5","id":3}]},"type":"8"} to
	 * json:{"error":"1","obj":{"type"
	 * :"1","wifiInfos":[{"deviceName":"DeviceName5"
	 * ,"deviceid":"deviceid5","fitting"
	 * :"fitting5","roomid":"roomid5","macadd":"macadd5","id":3}]},"type":"8"}
	 * 
	 * @param info
	 * @param resultListener
	 */
	public void updata(final WifiDeviceInfo info, final ResultListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info) + "\n";
					OutputStream outputStream;

					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (resultListener != null) {
						resultListener.onResult(DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					}
					return;
				}
				if (resultListener != null) {
					resultListener.onResult(result.getType(), result.getError(), ((WifiDeviceInfos) result.getObject()).getWifiInfo());
				}
			}

		}).start();
	}

	/**
	 * from
	 * json:{"error":"0","obj":{"type":"3","wifiInfos":[{"id":0}]},"type":"8"}
	 * to json:{"error":"1","obj":{"type":"3","wifiInfos":[{"deviceName":
	 * "DeviceName3"
	 * ,"deviceid":"deviceid3","fitting":"fitting3","roomid":"roomid3"
	 * ,"macadd":"macadd3"
	 * ,"id":2},{"deviceName":"DeviceName4","deviceid":"deviceid4"
	 * ,"fitting":"fitting4"
	 * ,"roomid":"roomid4","macadd":"macadd4","id":3}]},"type":"8"}
	 * 
	 * @param resultListener
	 */
	public void seleteAll(final ResultListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, new WifiDeviceInfo()) + "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (resultListener != null) {
						resultListener.onResult(DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					}
					return;
				}
				if (resultListener != null) {
					resultListener.onResult(result.getType(), result.getError(), ((WifiDeviceInfos) result.getObject()).getWifiInfo());
				}
			}

		}).start();
	}

	/**
	 * from json:{"error":"0","obj":{"type":"7","wifiInfos":[{"deviceid":
	 * "deviceid3","id":0}]},"type":"8"} to
	 * json:{"error":"1","obj":{"type":"6","wifiInfos"
	 * :[{"deviceName":"DeviceName3"
	 * ,"deviceid":"deviceid3","fitting":"fitting3",
	 * "roomid":"roomid3","macadd":"macadd3","id":2}]},"type":"8"}
	 * 
	 * @param deviceId
	 * @param resultListener
	 */
	public void seleteByDeviceId(final String deviceId, final ResultListener resultListener) {
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
					WifiDeviceInfo info = new WifiDeviceInfo();
					info.setDeviceid(deviceId);
					String obj = WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(DB_SELECT_BY_DEVICE_ID, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info) + "\n";
					OutputStream outputStream;

					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (resultListener != null) {
						resultListener.onResult(DB_SELECT_BY_DEVICE_ID, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					}
					return;
				}
				if (resultListener != null) {
					resultListener.onResult(result.getType(), result.getError(), ((WifiDeviceInfos) result.getObject()).getWifiInfo());
				}
			}

		}).start();
	}

	/**
	 * from
	 * json:{"error":"0","obj":{"type":"6","wifiInfos":[{"roomid":"roomid2",
	 * "id":0}]},"type":"8"} to
	 * json:{"error":"1","obj":{"type":"6","wifiInfos":[
	 * {"deviceName":"DeviceName2"
	 * ,"deviceid":"2","fitting":"fitting2","roomid":"roomid2"
	 * ,"macadd":"macadd2","id":2}]},"type":"8"}
	 * 
	 * @param roomId
	 * @param resultListener
	 */
	public void seleteByRoomId(final String roomId, final ResultListener resultListener) {
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
					WifiDeviceInfo info = new WifiDeviceInfo();
					info.setRoomid(roomId);
					String obj = WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(DB_SELECT_BY_ROOM_ID, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info) + "\n";
					OutputStream outputStream;

					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (resultListener != null) {
						resultListener.onResult(DB_SELECT_BY_ROOM_ID, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					}
					return;
				}
				if (resultListener != null) {
					resultListener.onResult(result.getType(), result.getError(), ((WifiDeviceInfos) result.getObject()).getWifiInfo());
				}
			}

		}).start();
	}
}
