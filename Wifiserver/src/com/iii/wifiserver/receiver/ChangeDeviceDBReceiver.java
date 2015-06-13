package com.iii.wifiserver.receiver;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.iii.wifi.dao.imf.WifiControlDao;
import com.iii.wifi.dao.imf.WifiModeDao;
import com.iii.wifi.dao.info.WifiBoxModeInfo;
import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;

public class ChangeDeviceDBReceiver extends BroadcastReceiver {
    private WifiModeDao mWifiModeDao;
    private WifiControlDao mWifiControlDao;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();

        if (action.equals(KeyList.IKEY_DELETE_DEVICE) || action.equals(KeyList.IKEY_DELETE_CONTROL)) {
            LogManager.i("KeyList.IKEY_DELETE_DEVICE or IKEY_DELETE_CONTROL");

            mWifiModeDao = new WifiModeDao(context);
            mWifiControlDao = new WifiControlDao(context);

            updateModeDB(context, mWifiModeDao.selectOpenModeData());
            updateModeDB(context, mWifiModeDao.selectCloseModeData());

            context.sendBroadcast(new Intent(KeyList.AKEY_COMMAND_CHANGE));

        }
    }

    private void updateModeDB(Context context, List<WifiBoxModeInfo> modes) {
        if (modes == null || modes.isEmpty()) {
            return;
        }

        StringBuffer controlBuffer;

//        List<WifiBoxModeInfo> modes = mWifiModeDao.selectAll();
//        List<WifiBoxModeInfo> modes = mWifiModeDao.selectOpenModeData();

        //获取所有模式控制的ID
        for (WifiBoxModeInfo mode : modes) {
            String controlId = mode.getControlIDs();
            LogManager.w("getControlIDs : " + controlId);

            controlBuffer = new StringBuffer();

            if (!TextUtils.isEmpty(controlId)) {

                String id[] = controlId.split(KeyList.SEPARATOR_ACTION_SUBLIT);

                for (int i = 0; i < id.length; i++) {

                    List<WifiControlInfo> controls = mWifiControlDao.selectById(Integer.parseInt(id[i]));

                    if (controls != null && !controls.isEmpty()) {
                        controlBuffer.append(id[i]);
                        controlBuffer.append(KeyList.SEPARATOR_ACTION);
                    }
                }

                String news = "";
                if (controlBuffer.toString().contains(KeyList.SEPARATOR_ACTION)) {
                    news = controlBuffer.substring(0, controlBuffer.length() - 2);
                }

                LogManager.d("set ids : " + news);
                mode.setControlIDs(news);
                mWifiModeDao.update(mode);
            }

        }
    }
}
