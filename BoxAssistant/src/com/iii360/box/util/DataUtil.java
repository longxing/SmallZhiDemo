package com.iii360.box.util;

import android.text.TextUtils;

public class DataUtil {
    /**
     * @param device
     * @return 提示语音语句,如“打开客厅空调”
     */
    public static String[] tipsVoice(String roomName ,String device) {
        String[] devices = device.split(KeyList.SEPARATOR);
        String[] tips = new String[devices.length * 2];
        int j = 0;
        for (int i = 0; i < devices.length; i++) {
			tips[j] = "小智,打开" + roomName + devices[i];
			j++;
			tips[j] = "小智,关闭" + roomName + devices[i];
			j++;
        }
        return tips;
    }

    /**
     * 分割action字段“打开||空调”
     * 
     * @param action
     * @return 空调
     */
    public static String getDeviceName(String action) {
        if (TextUtils.isEmpty(action)) {
            return "";
        }
        String[] datas = action.split(KeyList.SEPARATOR_ACTION_SUBLIT);
        if (datas.length >= 1) {
            return datas[1];
        }
        return action;
    }

    /**
     * 分割action字段“打开||空调”
     * 
     * @param action
     * @return 打开
     */
    public static String getAction(String action) {
        if (TextUtils.isEmpty(action)) {
            return "";
        }
        String[] datas = action.split(KeyList.SEPARATOR_ACTION_SUBLIT);
        if (datas.length >= 1) {
            return datas[0];
        }
        return action;
    }

    /**
     * 组装action字段“打开||空调”"
     * 
     * @param action
     * @param deviceName
     * @return “打开||空调”"
     */
    public static String formatAction(String action, String deviceName) {
        return action + KeyList.SEPARATOR_ACTION + deviceName;
    }

    /**
     * 判断deviceName字段中是否包含addDevice设备
     * @param deviceNames
     * @param addDevice
     * @return
     */
    public static boolean isExistDevice(String deviceNames, String addDevice) {
        if (TextUtils.isEmpty(deviceNames)) {
            return false;
        }
        String[] d = deviceNames.split(KeyList.SEPARATOR);

        for (int i = 0; i < d.length; i++) {
            if (d[i].equals(addDevice)) {
                return true;
            }
        }
        return false;
    }
}
