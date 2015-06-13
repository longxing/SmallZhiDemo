package com.iii.wifi.dao.newmanager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.manager.WifiCRUDForClient;

public abstract class AbsWifiCRUDForObject {
	public static final String OPERATION_TYPE_SET = "OPERATION_TYPE_SET";
	public static final String OPERATION_TYPE_GET = "OPERATION_TYPE_GET";
	public static final String OPERATION_TYPE_DELETE = "OPERATION_TYPE_DELETE";

	// 基本的数据操作类型
	public static final String DB_ADD = "0";
	public static final String DB_UPDATA = "1";
	public static final String DB_DELETE = "2";
	public static final String DB_SELECT = "3";
	public static final String DB_SELECT_BY_ID = "4";
	public static final String DB_SELECT_BY_ROOM_ID = "6";
	public static final String DB_SELECT_BY_DEVICE_ID = "7";
	public static final String DB_SELECT_BY_NAME = "8";

	// 音乐数据操作类型
	public static final String MUSIC_SELECT = "MUSIC_SELECT";
	public static final String MUSIC_SELECT_LOCAL = "MUSIC_SELECT_LOCAL";
	public static final String MUSIC_SELECT_CURRENT = "MUSIC_SELECT_CURRENT";
	public static final String MUSIC_DELETE_BY_ID = "MUSIC_DELETE_BY_ID";
	public static final String MUSIC_PLAY_NET_RESOURCES = "MUSIC_PLAY_NET_RESOURCES";
	public static final String PLAY_LOCAL_MUSIC_BY_ID = "PLAY_LOCAL_MUSIC_BY_ID";
	public static final String STE_LOCAL_MUSIC_FOR_REMIND = "STE_LOCAL_MUSIC_FOR_REMIND";
	

	// ==================盒子播放歌曲操作状态的status start=================
	/**
	 * 当前播放歌曲的信息
	 */
	public static final String MUSIC_CURRENT_PLAY_STATUS = "MUSIC_CURRENT_PLAY_STATUS";

	public static final String MUSIC_PLAY = "MUSIC_PLAY";
	public static final String MUSIC_GOOD_PLAY = "MUSIC_GOOD_PLAY";
	// public static final String MUSIC_CONTINUE_PLAY = "MUSIC_CONTINUE_PLAY";
	// public static final String MUSIC_STOP = "MUSIC_STOP";
	public static final String MUSIC_PLAY_OR_PAUSE = "MUSIC_PLAY_OR_PAUSE";
	public static final String MUSIC_PAUSE = "MUSIC_PAUSE";
	public static final String MUSIC_NEXT = "MUSIC_NEXT";
	public static final String MUSIC_PREVIOUS = "MUSIC_PREVIOUS";
	public static final String MUSIC_BAD = "MUSIC_BAD";
	public static final String MUSIC_GOOD = "MUSIC_GOOD";
	// ==================盒子播放歌曲操作状态的status end===================

	protected String ip;
	protected int port;

	public AbsWifiCRUDForObject(String ip, int port) {
		// TODO Auto-generated constructor stub
		this.ip = ip;
		this.port = port;
	}

	protected Socket connect() throws IOException {
		Socket socket = new Socket();
		if (!socket.isConnected()) {
			socket.connect(new InetSocketAddress(ip, port), 5000);
		}
		return socket;
	}

	protected WifiJSONObjectInfo getResult(Socket socket, String obj) throws IOException {
		OutputStream out = socket.getOutputStream();
		out.write(obj.getBytes());
		out.flush();
		WifiJSONObjectInfo object = WifiCRUDForClient.findData(socket);
		out.close();
		return object;
	}
}
