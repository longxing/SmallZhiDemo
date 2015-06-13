package com.iii360.box.adpter;

import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.manager.WifiCRUDForDevice;
import com.iii.wifi.dao.manager.WifiCRUDForDevice.ResultListener;
import com.iii.wifi.dao.manager.WifiCRUDForRoom;
import com.iii360.box.R;
import com.iii360.box.config.PartsManagerActivity;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;

public class DetailRoomListHelper {

    public static void deleteDevice(final Context context, String deviceid) {
        WifiCRUDForDevice mWifiCRUDForDevice = new WifiCRUDForDevice(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
        mWifiCRUDForDevice.deleteByDeviceId(deviceid, new ResultListener() {
            @Override
            public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
                // TODO Auto-generated method stub
                LogManager.i("正在删除配件...");
                if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                    ToastUtils.show(context, R.string.ba_delete_success_toast);
                    context.startActivity(new Intent(context, PartsManagerActivity.class));

                } else {
                    ToastUtils.show(context, R.string.ba_delete_data_error_toast);
                }
            }
        });
    }

    public static void move(final Context context, String roomName,final WifiDeviceInfo deviceInfo) {
        WifiCRUDForRoom mWifiCRUDForRoom = new WifiCRUDForRoom(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
        WifiRoomInfo wInfo = new WifiRoomInfo();
        wInfo.setRoomName(roomName);
        mWifiCRUDForRoom.add(wInfo, new WifiCRUDForRoom.ResultListener() {
            @Override
            public void onResult(String type, String errorCode, List<WifiRoomInfo> info) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                    WifiCRUDForDevice mWifiCRUDForDevice = new WifiCRUDForDevice(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
                    deviceInfo.setRoomid(info.get(0).getRoomId());
                    mWifiCRUDForDevice.updata(deviceInfo, new WifiCRUDForDevice.ResultListener() {
                        @Override
                        public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
                            // TODO Auto-generated method stub
                            if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                                LogManager.i("增加新设备成功");

                                context.startActivity(new Intent(context, PartsManagerActivity.class));
                            } else {
                                LogManager.i("更新设备失败");
                            }
                        }
                    });
                }
            }
        });
    }

}
