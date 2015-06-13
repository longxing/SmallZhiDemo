package com.iii360.box.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.text.TextUtils;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;

public class ModeData {
	private static Map<Integer, String> mDeviceGroup;// 空调操作组
	private static Map<Integer, String> mDeviceOneGroup;// 开/关空调操作组

	/**
	 * 模式的所有数据
	 */
	private static ArrayList<Map<Integer, String>> mAllModeUseList = new ArrayList<Map<Integer, String>>();

	private static List<WifiControlInfo> mNewInfos;// copy
													// mAllUseList数据来操作remove
	private static String[] mTemp;
	private static List<WifiRoomInfo> mRoomInfos;
	private static StringBuffer sBuffer;

	/**
	 * @param roomInfos
	 * @param controlInfos
	 * @return [{1=开/关客厅空调}, {2=开/关客厅电视机}] id为控制的ID
	 */
	public static ArrayList<Map<Integer, String>> getOpenModeGroupData(List<WifiRoomInfo> roomInfos, List<WifiControlInfo> controlInfos) {
		// 0 数据格式：相同设备下，操作类型归为一组Map，将所有Map存储在List中
		// 1 遍历，存储设备名称
		// 2 遍历设备，将相同设备控制，存储在Map中以ID和控制行为
		// 3 将Map存储到List上

		// 去除没有命令的数据
		Iterator<WifiControlInfo> it = controlInfos.iterator();
		while (it.hasNext()) {
			WifiControlInfo info = it.next();
			if (TextUtils.isEmpty(info.getDorder())) {
				it.remove();
			}
		}

		mAllModeUseList.clear();
		mRoomInfos = roomInfos;
		mNewInfos = new ArrayList<WifiControlInfo>(controlInfos);

		mAllModeUseList = new ArrayList<Map<Integer, String>>();
		for (WifiControlInfo info : controlInfos) {
			LogManager.e(info.getId() + "--------" + info.getAction());
			mTemp = info.getAction().split(KeyList.SEPARATOR_ACTION_SUBLIT);
			addToGroup(mTemp[1]);
		}

		return mAllModeUseList;
	}

	public static HashMap<String, ArrayList<String>> parseRoomAndControl(List<WifiRoomInfo> roomInfos, List<WifiControlInfo> controlInfos) {
		HashMap<String, ArrayList<String>> roomControls = new HashMap<String, ArrayList<String>>();
		for (int i = controlInfos.size() - 1; i >= 0; i--) {
			WifiControlInfo info = controlInfos.get(i);
			if (!infoRoomInRoomList(roomInfos, info)) {
				controlInfos.remove(i);
			}
		}
		for (int i = 0; i < controlInfos.size(); i++) {
			WifiControlInfo info = controlInfos.get(i);
			String roomid = info.getRoomId();
			ArrayList<String> controlList = roomControls.get(roomid);
			if (controlList == null) {
				controlList = new ArrayList<String>();
				controlList.add(info.getAction());
				roomControls.put(roomid, controlList);
			} else {
				controlList.add(info.getAction());
			}

		}
		return roomControls;
	}

	public static ArrayList<Map<Integer, String>> _getOpenModeGroupData(List<WifiRoomInfo> roomInfos, List<WifiControlInfo> controlInfos) {
		for (int i = controlInfos.size() - 1; i >= 0; i--) {
			WifiControlInfo info = controlInfos.get(i);
			if (TextUtils.isEmpty(info.getDorder())) {
				controlInfos.remove(i);
				continue;
			}
			if (!infoRoomInRoomList(roomInfos, info)) {
				controlInfos.remove(i);
			}
		}
		Map<String, ArrayList<WifiControlInfo>> map = new HashMap<String, ArrayList<WifiControlInfo>>();
		for (int i = 0; i < controlInfos.size(); i++) {
			WifiControlInfo info = controlInfos.get(i);
			String[] arr = info.getAction().split(KeyList.SEPARATOR_ACTION_SUBLIT);
			String key = info.getRoomId() + "||" + arr[1];
			// String key =
			// info.getRoomId()+"||"+info.getDeviceModel()+"||"+arr[1];
			// String key =
			// info.getRoomId()+"||"+info.getDeviceid()+"||"+arr[1];
			if (arr[0].equals(KeyList.GKEY_OPERATION_DEVICE_ARRAY[2])) {
				ArrayList<WifiControlInfo> list = new ArrayList<WifiControlInfo>();
				list.add(info);
				map.put(info.getRoomId() + "|||" + arr[1], list);
			} else {
				ArrayList<WifiControlInfo> list = map.get(key);
				if (list != null) {
					list.add(info);
				} else {
					list = new ArrayList<WifiControlInfo>();
					list.add(info);
					map.put(key, list);
				}
			}
		}

		ArrayList<Map<Integer, String>> finalList = new ArrayList<Map<Integer, String>>();
		Iterator<Entry<String, ArrayList<WifiControlInfo>>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, ArrayList<WifiControlInfo>> entry = it.next();
			ArrayList<WifiControlInfo> list = entry.getValue();
			Map<Integer, String> m = new HashMap<Integer, String>();
			for (int i = 0; i < list.size(); i++) {
				WifiControlInfo info = list.get(i);
				m.put(info.getId(), info.getAction().split(KeyList.SEPARATOR_ACTION_SUBLIT)[0] + getRoomName(info.getRoomId(), roomInfos)
						+ info.getAction().split(KeyList.SEPARATOR_ACTION_SUBLIT)[1]);
			}
			finalList.add(m);
		}

		// while(it.hasNext()){
		// String name = it.next();
		// List<WifiControlInfo> list = map.get(name);
		// for (int i = 0; i < list.size(); i++) {
		//
		// }
		// }
		return finalList;
	}

	private static String getRoomName(String roomId, List<WifiRoomInfo> roomInfos) {
		for (int i = 0; i < roomInfos.size(); i++) {
			WifiRoomInfo roominfo = roomInfos.get(i);
			if (roominfo.getRoomId().equals(roomId)) {
				return roominfo.getRoomName();
			}
		}
		return "";
	}

	private static boolean infoRoomInRoomList(List<WifiRoomInfo> roomInfos, WifiControlInfo info) {
		for (int i = 0; i < roomInfos.size(); i++) {
			WifiRoomInfo roominfo = roomInfos.get(i);
			if (roominfo.getRoomId().equals(info.getRoomId())) {
				return true;
			}
		}
		return false;
	}

	private static void addToGroup(String deviceName) {
		mDeviceGroup = new HashMap<Integer, String>();
		Iterator<WifiControlInfo> iterator = mNewInfos.iterator();

		while (iterator.hasNext()) {
			WifiControlInfo info = iterator.next();
			mTemp = info.getAction().split(KeyList.SEPARATOR_ACTION_SUBLIT);

			if (mTemp[1].equals(deviceName)) {

				sBuffer = new StringBuffer(mTemp[0]);
				sBuffer.append(getRoomName(info.getRoomId()));
				sBuffer.append(mTemp[1]);
				LogManager.w("mTemp[0]=" + mTemp[0] + "||" + KeyList.GKEY_OPERATION_DEVICE_ARRAY[2] + "==="
						+ mTemp[0].equals(KeyList.GKEY_OPERATION_DEVICE_ARRAY[2]));
				// 开/关，单独一组
				if (mTemp[0].equals(KeyList.GKEY_OPERATION_DEVICE_ARRAY[2])) {
					// LogManager.i("add 开/关=" + sBuffer.toString());
					mDeviceOneGroup = new HashMap<Integer, String>();
					mDeviceOneGroup.put(info.getId(), sBuffer.toString());
					mAllModeUseList.add(mDeviceOneGroup);

					// 打开和关闭是一组
				} else {
					mDeviceGroup.put(info.getId(), sBuffer.toString());
				}
				iterator.remove();
			}
		}

		if (!mDeviceGroup.isEmpty()) {
			// LogManager.i("add 打开、关闭=" + sBuffer.toString());
			mAllModeUseList.add(mDeviceGroup);
		}
	}

	public static String getRoomName(String roomId) {
		for (WifiRoomInfo info : mRoomInfos) {
			if (info.getRoomId().equals(roomId)) {
				return info.getRoomName();
			}
		}
		return "";
	}
}
