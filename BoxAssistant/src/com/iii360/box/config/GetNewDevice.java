package com.iii360.box.config;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiDeviceInfos;
import com.iii.wifi.dao.manager.WifiCRUDForDevice;
import com.iii.wifi.dao.manager.WifiForCommonOprite;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.WifiCRUDUtil;

/**
 * 获取新配件
 * 
 * @author hefeng
 * 
 */
public class GetNewDevice {
    private WifiDeviceInfos mWifiDeviceInfos;
    private Context context;
    private Gson gson = new Gson();
    private BasePreferences mPreferences;

    public GetNewDevice(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.mWifiDeviceInfos = new WifiDeviceInfos();
        this.mPreferences = new BasePreferences(context);
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(KeyList.AKEY_CHECK_DEVICE_BRODCAST);
        context.registerReceiver(receiver, filter);
    }

    public void unregisterReceiver() {
        context.unregisterReceiver(receiver);
    }

    /**
     * 监听配件变更
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            getNewConfigDevice();
        }
    };

    /**
     * 获取新配件
     */
    public void getNewConfigDevice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                WifiForCommonOprite wco = new WifiForCommonOprite(BoxManagerUtils.getBoxTcpPort(context), BoxManagerUtils.getBoxIP(context));
                wco.getUnConfigedDevice(new WifiCRUDForDevice.ResultListener() {
                    @Override
                    public void onResult(String type, String errorCode, final List<WifiDeviceInfo> info) {
                        // TODO Auto-generated method stub
                        if (WifiCRUDUtil.isSuccessAll(errorCode) && info != null && !info.isEmpty()) {
                            LogManager.i("获取新配件成功 数量：" + info.size());

                            if (listener != null) {
                                listener.onData(true, info);
                            }
                            //设置当前拉取的设备列表
                            mWifiDeviceInfos.setWifiInfo(info);
                            mPreferences.setPrefString(KeyList.IKEY_NEW_DEVICE_GSON_LIST, gson.toJson(mWifiDeviceInfos));
                            //设置新设备不要在状态栏显示
                            mPreferences.setPrefBoolean(KeyList.IKEY_PUSH_NEW_DEVICE_SWTICH, true);

                        } else {
                            LogManager.i("获取新配件失败或者获取数据为空 ");

                            if (listener != null) {
                                listener.onData(false, info);
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private ArrayList<WifiDeviceInfo> mWifiDeviceInfo;

    public ArrayList<WifiDeviceInfo> getNewDeviceByUdp() {
        String unConfigDevice = mPreferences.getPrefString(KeyList.IKEY_NEW_DEVICE_GSON_LIST);
        if (TextUtils.isEmpty(unConfigDevice)) {
            return new ArrayList<WifiDeviceInfo>();
        }

        WifiDeviceInfos infos = gson.fromJson(unConfigDevice, WifiDeviceInfos.class);
        mWifiDeviceInfo = (ArrayList<WifiDeviceInfo>) infos.getWifiInfo();
        if (mWifiDeviceInfo == null) {
            mWifiDeviceInfo = new ArrayList<WifiDeviceInfo>();
        }
        //设置新设备不要在状态栏显示
        mPreferences.setPrefBoolean(KeyList.IKEY_PUSH_NEW_DEVICE_SWTICH, true);
        return mWifiDeviceInfo;
    }

    private DeviceListener listener;

    public void setListener(DeviceListener listener) {
        this.listener = listener;
    }

    public interface DeviceListener {
        public void onData(boolean isGet, List<WifiDeviceInfo> list);

    }
}
