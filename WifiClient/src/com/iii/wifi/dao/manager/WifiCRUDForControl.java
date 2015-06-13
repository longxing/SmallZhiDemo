package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiControlInfos;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;

public class WifiCRUDForControl {
	/**
	 * 用于控制的查询，更新和增加 使用时传入ip,端口号,回传结果类 调用相应的方法，结果会由回传接口返回 示例：WifiControlInfo
	 * info = new WifiControlInfo(); info.setInfoId("123456");
	 * info.setCorder("corder"); info.setDeviceid("deviceid");
	 * info.setDorder("dorder"); info.setFrequency("3");
	 * info.setMacadd("12:51:21:54"); info.setRoomname("roomname");
	 * WifiCRUDForControl control =
	 * WifiCRUDForControl(MainActivity.this,"192.168.20.173","5578");
	 * control.add(info,new
	 * com.iii.wifi.dao.manager.WifiCRUDForControl.ResultListener() {
	 * 
	 * @Override public void onResult(String type, String errorCode,
	 *           List<WifiControlInfo> info) { // TODO Auto-generated method
	 *           stub Log.i("jiangshenglan", "type = " + type + ";errorCode = "
	 *           + errorCode); if (errorCode.equals("1")) {
	 *           Log.i("jiangshenglan", info.get(0).toString()); } }
	 * 
	 *           });
	 */
	public static final String DB_ADD = "0";
	public static final String DB_UPDATA = "1";
	public static final String DB_DELETE = "2";
	public static final String DB_SELETE = "3";
	public static final String DB_SELETE_BY_ID = "4";
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
		public void onResult(String type, String errorCode, List<WifiControlInfo> info);
	}

	public WifiCRUDForControl(Context context, String ip, int port) {
		mContext = context;
		mIP = ip;
		mPort = port;
	}

	/**
	 * from json:{"error":"0","obj":{"type":"0","wifiInfos":[{"action":
	 * "jiangshenglan1"
	 * ,"corder":"corder1","deviceid":"deviceid1","dorder":"dorder1"
	 * ,"roomId":"roomname1","id":0,"frequency":10}]},"type":"0"} to
	 * json:{"error"
	 * :"1","obj":{"type":"0","wifiInfos":[{"action":"jiangshenglan1"
	 * ,"corder":"corder1"
	 * ,"deviceid":"deviceid1","dorder":"dorder1","roomId":"roomname1"
	 * ,"id":0,"frequency":10}]},"type":"0"}
	 * 
	 * @param info
	 * @param resultListener
	 */
	public void add(final WifiControlInfo info, final ResultListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiControlInfos(DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info) + "\n";
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
				resultListener.onResult(result.getType(), result.getError(), ((WifiControlInfos) result.getObject()).getWifiInfo());
			}

		}).start();
	}

	/**
	 * from
	 * json:{"error":"0","obj":{"type":"2","wifiInfos":[{"id":0,"frequency":
	 * 0}]},"type":"0"} to
	 * json:{"error":"1","obj":{"type":"2","wifiInfos":[{"id"
	 * :0,"frequency":0}]},"type":"0"}
	 * 
	 * @param id
	 * @param resultListener
	 */
	public void delete(final int id, final ResultListener resultListener) {
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
					WifiControlInfo info = new WifiControlInfo();
					info.setId(id);
					String obj = WifiCreateAndParseSockObjectManager.createWifiControlInfos(DB_DELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info) + "\n";
					OutputStream outputStream;

					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(DB_DELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				resultListener.onResult(result.getType(), result.getError(), ((WifiControlInfos) result.getObject()).getWifiInfo());
			}

		}).start();
	}

	/**
	 * from json:{"error":"0","obj":{"type":"1","wifiInfos":[{"action":
	 * "jiangshenglan5"
	 * ,"corder":"corder6","deviceid":"deviceid6","dorder":"dorder6"
	 * ,"roomId":"roomname6","id":2,"frequency":100}]},"type":"0"} to
	 * json:{"error"
	 * :"1","obj":{"type":"1","wifiInfos":[{"action":"jiangshenglan5"
	 * ,"corder":"corder6"
	 * ,"deviceid":"deviceid6","dorder":"dorder6","roomId":"roomname6"
	 * ,"id":2,"frequency":100}]},"type":"0"}
	 * 
	 * @param info
	 * @param resultListener
	 */
	public void updata(final WifiControlInfo info, final ResultListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiControlInfos(DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info) + "\n";
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
				resultListener.onResult(result.getType(), result.getError(), ((WifiControlInfos) result.getObject()).getWifiInfo());
			}

		}).start();
	}

	/**
	 * from
	 * json:{"error":"0","obj":{"type":"4","wifiInfos":[{"id":2,"frequency":
	 * 0}]},"type":"0"} to
	 * json:{"error":"1","obj":{"type":"4","wifiInfos":[{"action"
	 * :"jiangshenglan5"
	 * ,"corder":"corder6","deviceid":"deviceid6","dorder":"dorder6"
	 * ,"roomId":"roomname6","id":2,"frequency":100}]},"type":"0"} to
	 * json:{"error":"1","obj":{"type":"6","wifiInfos":[]},"type":"0"} // 无数据
	 * 
	 * @param id
	 * @param resultListener
	 */
	public void seleteById(final int id, final ResultListener resultListener) {
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
					WifiControlInfo info = new WifiControlInfo();
					info.setId(id);
					String obj = WifiCreateAndParseSockObjectManager.createWifiControlInfos(DB_SELETE_BY_ID, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info) + "\n";
					OutputStream outputStream;

					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(DB_SELETE_BY_ID, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				resultListener.onResult(result.getType(), result.getError(), ((WifiControlInfos) result.getObject()).getWifiInfo());
			}

		}).start();
	}

	/**
	 * from
	 * json:{"error":"0","obj":{"type":"6","wifiInfos":[{"roomId":"roomname1"
	 * ,"id":0,"frequency":0}]},"type":"0"} to
	 * json:{"error":"1","obj":{"type":"6"
	 * ,"wifiInfos":[{"action":"jiangshenglan1"
	 * ,"corder":"corder1","deviceid":"deviceid1"
	 * ,"dorder":"dorder1","roomId":"roomname1"
	 * ,"id":1,"frequency":10}]},"type":"0"}
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
						socket.connect(new InetSocketAddress(mIP, mPort), 15000);
					}
					WifiControlInfo info = new WifiControlInfo();
					info.setRoomId(roomId);
					String obj = WifiCreateAndParseSockObjectManager.createWifiControlInfos(DB_SELECT_BY_ROOM_ID, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info) + "\n";
					OutputStream outputStream;

					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					resultListener.onResult(DB_SELECT_BY_ROOM_ID, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				resultListener.onResult(result.getType(), result.getError(), ((WifiControlInfos) result.getObject()).getWifiInfo());
			}

		}).start();
	}

	/**
	 * from
	 * json:{"error":"0","obj":{"type":"3","wifiInfos":[{"id":0,"frequency":
	 * 0}]},"type":"0"} to
	 * json:{"error":"1","obj":{"type":"3","wifiInfos":[{"action"
	 * :"jiangshenglan3"
	 * ,"corder":"corder2","deviceid":"deviceid3","dorder":"dorder3"
	 * ,"roomId":"roomname3"
	 * ,"id":2,"frequency":20},{"action":"jiangshenglan4","corder"
	 * :"corder3","deviceid"
	 * :"deviceid4","dorder":"dorder4","roomId":"roomname4",
	 * "id":3,"frequency":20
	 * },{"action":"jiangshenglan1","corder":"corder1","deviceid"
	 * :"deviceid1","dorder"
	 * :"dorder1","roomId":"roomname1","id":1,"frequency":10}]},"type":"0"}
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiControlInfos(DB_SELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, new WifiControlInfo()) + "\n";
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
				resultListener.onResult(result.getType(), result.getError(), ((WifiControlInfos) result.getObject()).getWifiInfo());
			}

		}).start();
	}

	/**
	 * from json:{"error":"0","obj":{"type":"7","wifiInfos":[{"deviceid":
	 * "deviceid1","id":0,"frequency":0}]},"type":"0"} to
	 * json:{"error":"1","obj"
	 * :{"type":"7","wifiInfos":[{"action":"jiangshenglan1"
	 * ,"corder":"corder1","deviceid"
	 * :"deviceid1","dorder":"dorder1","roomId":"roomname1"
	 * ,"id":1,"frequency":10}]},"type":"0"}
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
					WifiControlInfo info = new WifiControlInfo();
					info.setDeviceid(deviceId);
					String obj = WifiCreateAndParseSockObjectManager.createWifiControlInfos(DB_SELECT_BY_DEVICE_ID, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info) + "\n";
					OutputStream outputStream;

					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(DB_SELECT_BY_DEVICE_ID, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				resultListener.onResult(result.getType(), result.getError(), ((WifiControlInfos) result.getObject()).getWifiInfo());
			}

		}).start();
	}
}
