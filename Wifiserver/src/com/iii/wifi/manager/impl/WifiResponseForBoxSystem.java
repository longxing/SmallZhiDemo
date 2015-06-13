package com.iii.wifi.manager.impl;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.text.TextUtils;

import com.iii.wifi.dao.info.WifiBoxSystemInfo;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.manager.WifiCreateAndParseSockObjectManager;
import com.iii.wifi.dao.newmanager.AbsWifiCRUDForObject;
import com.iii.wifi.util.BoxSystemUtils;


/**
 * 盒子系统信息
 * @author Administrator
 *
 */
public class WifiResponseForBoxSystem extends AbsWifiResponse {

    @Override
    public String getResponse(WifiJSONObjectInfo obj, Context context) {
        // TODO Auto-generated method stub
        mResult = gson.toJson(obj);

        WifiBoxSystemInfo info = new WifiBoxSystemInfo();
        
        info.setAvailableRamSize(BoxSystemUtils.getRamAvailableSize(context));
        info.setRamTotalSize(BoxSystemUtils.getRamTotalSize(context));
        
        info.setAvailableRomSize(BoxSystemUtils.getSDAvailableSize(context));
        info.setRomTotalSize(BoxSystemUtils.getSDTotalSize(context));
        info.setBattery(BoxSystemUtils.getBattery(context));
        
        info.setMac(BoxSystemUtils.getLocalMacAddress(context));
        info.setIp(BoxSystemUtils.getWifiIp(context));
        info.setVersionCode(BoxSystemUtils.getFirmwareVersion());

        String num = BoxSystemUtils.getSerialNumber();
        if (TextUtils.isEmpty(num)) {
            num = "000000000000";
        }
        
        info.setSerial(num);
         
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        Intent result=context.registerReceiver(null, filter);
        String state="";
        switch (result.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
			case 2:
				state="正在充电";
				break;
			case 5:
				state="已充满";
				break;
			default:
				state="未充电";
				break;
		}
        info.setCharg_state(state);
        
//      info.setSerial("00000000");
        
        mResult = WifiCreateAndParseSockObjectManager.createWifiBoxSystemInfos(AbsWifiCRUDForObject.DB_SELECT,
                WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);

        return mResult;
    }

}
