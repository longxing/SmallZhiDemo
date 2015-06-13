package com.iii.wifi.thirdpart.broadlink;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.com.broadlink.blnetwork.BLNetwork;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.iii.wifi.dao.imf.BroadlinkDeviceDao;
import com.iii.wifi.util.HardwareUtils;
import com.iii360.sup.common.utl.LogManager;

/**
 * BLNetwork.jar/gson-2.2.4.jar/libBLNetwork.so
 * 
 * @author Administrator
 *
 */
public class BroadlinkDevice {
	private BLNetwork mBlNetwork;
	private String api_id = "api_id";
	private String command = "command";
	private DeviceInfo selectedDevice;
	private ArrayList<DeviceInfo> deviceArrayList;

	private String CODE = "code";
	private String STATUS = "status";
	private String MSG = "msg";

	private BroadlinkDeviceDao mBroadlinkDeviceDao;

	public BroadlinkDevice(Context context) {
		mBlNetwork = BLNetwork.getInstanceBLNetwork(context);
		mBroadlinkDeviceDao = new BroadlinkDeviceDao(context);
	}

	public List<DeviceInfo> probeList() {
		List<DeviceInfo> deviceList = new ArrayList<DeviceInfo>();
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		JsonArray listJsonArray = new JsonArray();
		String probeOut;
		in.addProperty(api_id, 11);
		in.addProperty(command, "probe_list");
		String string = in.toString();
		probeOut = mBlNetwork.requestDispatch(string);
		out = new JsonParser().parse(probeOut).getAsJsonObject();
		int code = out.get(CODE).getAsInt();
		String msg = out.get(MSG).getAsString();
		listJsonArray = out.get("list").getAsJsonArray();
		Gson gson = new Gson();
		Type listType = new TypeToken<ArrayList<DeviceInfo>>() {
		}.getType();
		LogManager.i("BroadlinkDevice --------call back device infos befpre");
		if (listJsonArray != null && listJsonArray.size() > 0) {
			LogManager.i("BroadlinkDevice --------call back device infos:" + listJsonArray.toString());
		}
		deviceArrayList = (ArrayList<DeviceInfo>) gson.fromJson(listJsonArray, listType);
		String[] deviceNamesAndMac = new String[deviceArrayList.size()];
		LogManager.i("BroadlinkDevice --------add device before");
		if (deviceArrayList.size() > 0) {
			for (int i = 0; i < deviceArrayList.size(); i++) {
				deviceList.add(deviceArrayList.get(i));
				LogManager.i("BroadlinkDevice --------add device name:" + deviceArrayList.get(i).getName());
				selectedDevice = deviceArrayList.get(i);
				List<DeviceInfo> list = mBroadlinkDeviceDao.selectByMac(selectedDevice.getMac());
				if (list == null || list.isEmpty()) {
					// 增加至博联数据库
					mBroadlinkDeviceDao.add(selectedDevice);
				}
			}
		}
		// 可用设备mac列表
		List<DeviceInfo> list = mBroadlinkDeviceDao.selectMac();
	
		// 刷新至博联底层
		addDevice(list);

		// 可用设备信息
		ArrayList<String> macList = deviceStatus(list);
		deviceList = mBroadlinkDeviceDao.selectAll(macList);
		LogManager.i("BroadlinkDevice --------search db  device infos befpre");
		if (deviceList != null && deviceList.size() > 0) {
			LogManager.i("BroadlinkDevice --------search db device infos:" + deviceList.get(0).getMac());
		}

		return deviceList;
	}

	public int operation(String modeName, String mac, String dorde) {
		if (HardwareUtils.DEVICE_MODEL_BL_SP2.equals(modeName) || HardwareUtils.DEVICE_MODEL_BL_SPMini.equals(modeName)) {
			JsonObject in = new JsonObject();
			JsonObject out = new JsonObject();
			String sp2outString;
			in.addProperty(api_id, 72);
			in.addProperty(command, "sp2_control");
			in.addProperty("mac", mac.replaceAll(HardwareUtils.MAC_ADRESS_SEPERATER, ":"));
			in.addProperty("status", Integer.parseInt(dorde));
			String string = in.toString();
			sp2outString = mBlNetwork.requestDispatch(string);
			out = new JsonParser().parse(sp2outString).getAsJsonObject();
			return out.get(CODE).getAsInt();
		} else if (HardwareUtils.DEVICE_MODEL_BL_RM2.equals(modeName)) {
			JsonObject in = new JsonObject();
			JsonObject out = new JsonObject();
			String outString;
			in.addProperty(api_id, 134);
			in.addProperty(command, "rm2_send");
			in.addProperty("mac", mac.replaceAll(HardwareUtils.MAC_ADRESS_SEPERATER, ":"));
			in.addProperty("data", dorde);
			String inString = in.toString();
			outString = mBlNetwork.requestDispatch(inString);
			out = new JsonParser().parse(outString).getAsJsonObject();
			return out.get(CODE).getAsInt();
		}
		return -1;
	}

	public void addDevice(List<DeviceInfo> deviceInfo) {
		for (int i = 0; i < deviceInfo.size(); i++) {
			DeviceInfo device = deviceInfo.get(i);
			JsonObject in = new JsonObject();
			JsonObject out = new JsonObject();
			in.addProperty(api_id, 12);
			in.addProperty(command, "device_add");
			in.addProperty("mac", device.getMac());
			in.addProperty("type", device.getType());
			in.addProperty("name", device.getName());
			in.addProperty("lock", device.getLock());
			in.addProperty("password", device.getPassword());
			in.addProperty("id", device.getId());
			in.addProperty("subdevice", device.getSubdevice());
			in.addProperty("key", device.getKey());
			String string = in.toString();
			String outString;
			outString = mBlNetwork.requestDispatch(string);
			out = new JsonParser().parse(outString).getAsJsonObject();
		}
	}

	public ArrayList<String> deviceStatus(List<DeviceInfo> macList) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < macList.size(); i++) {
			DeviceInfo device = macList.get(i);
			JsonObject in = new JsonObject();
			JsonObject out = new JsonObject();
			in.addProperty(api_id, 16);
			in.addProperty(command, "device_state");
			in.addProperty("mac", device.getMac());
			String string = in.toString();
			String outString;
			outString = mBlNetwork.requestDispatch(string);
			out = new JsonParser().parse(outString).getAsJsonObject();
			if (out.get(CODE).getAsInt() == 0) {
				if ("LOCAL".equals(out.get(STATUS).getAsString())) {
					// if ("REMOTE".equals(out.get(STATUS).getAsString()) ||
					// "LOCAL".equals(out.get(STATUS).getAsString())) {
					list.add(device.getMac());
				}
			}
		}
		return list;
	}

	/**
	 * 采集指令
	 * 
	 * @param mac
	 * @param callBack
	 */
	public boolean sendLearnHF(String mac) {
		mac = mac.replaceAll(HardwareUtils.MAC_ADRESS_SEPERATER, ":");
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		String outString;
		in.addProperty(api_id, 132);
		in.addProperty(command, "rm2_study");
		in.addProperty("mac", mac);
		String inString = in.toString();
		outString = mBlNetwork.requestDispatch(inString);
		out = new JsonParser().parse(outString).getAsJsonObject();
		int code = out.get(CODE).getAsInt();
		return code == 0 ? true : false;
	}

	public String getHF(String mac) {
		mac = mac.replaceAll(HardwareUtils.MAC_ADRESS_SEPERATER, ":");
		String data = null;
		JsonObject in = new JsonObject();
		JsonObject out = new JsonObject();
		String outString;
		in.addProperty(api_id, 133);
		in.addProperty(command, "rm2_code");
		in.addProperty("mac", mac);
		String inString = in.toString();
		outString = mBlNetwork.requestDispatch(inString);
		out = new JsonParser().parse(outString).getAsJsonObject();
		int code = out.get(CODE).getAsInt();
		if (0 == code) {
			data = out.get("data").getAsString();
		} else {
			data = null;
		}
		return data;
	}

}
