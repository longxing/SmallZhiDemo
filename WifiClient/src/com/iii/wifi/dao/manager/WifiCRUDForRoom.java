package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.List;

import android.content.Context;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.info.WifiRoomInfos;

public class WifiCRUDForRoom {
	/**
	 * 用于Room的查询，更新和增加 使用时传入ip,端口号,回传结果类 调用相应的方法，结果会由回传接口返回
	 * 示例：见WifiCRUDForControl类
	 */
	public static final String DB_ADD = "0";
	public static final String DB_UPDATA = "1";
	public static final String DB_DELETE = "2";
	public static final String DB_SELETE = "3";
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
		public void onResult(String type, String errorCode, List<WifiRoomInfo> info);
	}

	public WifiCRUDForRoom(Context context, String ip, int port) {
		mContext = context;
		mIP = ip;
		mPort = port;
	}

	/**
	 * from json:{"error":"0","obj":{"type":"0","wifiInfos":[{"roomName":
	 * "roomname1","roomId":"roomid1","id":0}]},"type":"7"} to
	 * json:{"error":"1",
	 * "obj":{"type":"0","wifiInfos":[{"roomName":"roomname1","roomId"
	 * :"roomid1","id":0}]},"type":"7"}
	 * 
	 * @param info
	 * @param resultListener
	 */
	public void add(final WifiRoomInfo info, final ResultListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(DB_ADD,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info)+ "\n";
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
					resultListener.onResult(result.getType(), result.getError(),
							((WifiRoomInfos) result.getObject()).getWifiInfo());
				}
			}

		}).start();
	}

	/**
	 * from
	 * json:{"error":"0","obj":{"type":"2","wifiInfos":[{"roomId":"roomid1",
	 * "id":0}]},"type":"7"} to
	 * json:{"error":"1","obj":{"type":"2","wifiInfos":[
	 * {"roomId":"roomid1","id":0}]},"type":"7"}
	 * 
	 * @param roomId
	 * @param resultListener
	 */
	public void deleteByRoomId(final String roomId, final ResultListener resultListener) {
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
					WifiRoomInfo info = new WifiRoomInfo();
					info.setRoomId(roomId);
					String obj = WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(DB_DELETE,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info)+ "\n";
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
					resultListener.onResult(result.getType(), result.getError(),
							((WifiRoomInfos) result.getObject()).getWifiInfo());
				}
			}

		}).start();
	}

	/**
	 * from json:{"error":"0","obj":{"type":"1","wifiInfos":[{"roomName":
	 * "roomname4","roomId":"roomid4","id":2}]},"type":"7"} to
	 * json:{"error":"1",
	 * "obj":{"type":"1","wifiInfos":[{"roomName":"roomname4","roomId"
	 * :"roomid4","id":2}]},"type":"7"}
	 * 
	 * @param info
	 * @param resultListener
	 */
	public void updata(final WifiRoomInfo info, final ResultListener resultListener) {
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(DB_UPDATA,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info)+ "\n";
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
					resultListener.onResult(result.getType(), result.getError(),
							((WifiRoomInfos) result.getObject()).getWifiInfo());
				}

			}

		}).start();
	}

	/**
	 * from
	 * json:{"error":"0","obj":{"type":"6","wifiInfos":[{"roomId":"roomid1",
	 * "id":0}]},"type":"7"} to
	 * json:{"error":"1","obj":{"type":"6","wifiInfos":[
	 * {"roomName":"roomname1","roomId":"roomid1","id":2}]},"type":"7"}
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
					WifiRoomInfo info = new WifiRoomInfo();
					info.setRoomId(roomId);
					String obj = WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(DB_SELECT_BY_ROOM_ID,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info)+ "\n";
					OutputStream outputStream;

					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (resultListener != null) {
						resultListener.onResult(DB_SELECT_BY_ROOM_ID,
								WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					}
					return;
				}
				if (resultListener != null) {
					resultListener.onResult(result.getType(), result.getError(),
							((WifiRoomInfos) result.getObject()).getWifiInfo());
				}
			}

		}).start();
	}
	
	public void seleteByRoomName(final String roomId, final ResultListener resultListener){
	    
	}

	/**
	 * from
	 * json:{"error":"0","obj":{"type":"3","wifiInfos":[{"id":0}]},"type":"7"}
	 * to json:{"error":"1","obj":{"type":"3","wifiInfos":[{"roomName":
	 * "roomname1"
	 * ,"roomId":"roomid1","id":2},{"roomName":"roomname2","roomId":"roomid2"
	 * ,"id":3},{"roomName":"roomname3","roomId":"roomid3","id":4}]},"type":"7"}
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
					String obj = WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(DB_SELETE,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, new WifiRoomInfo())+ "\n";
					OutputStream outputStream;
					outputStream = socket.getOutputStream();
					outputStream.write(obj.toString().getBytes());
					outputStream.flush();
					result = WifiCRUDForClient.findData(socket, mContext);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if (resultListener != null) {
						resultListener.onResult(DB_SELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					}
					return;
				}
				if (resultListener != null) {
					resultListener.onResult(result.getType(), result.getError(),
							((WifiRoomInfos) result.getObject()).getWifiInfo());
				}
			}

		}).start();
	}
}
