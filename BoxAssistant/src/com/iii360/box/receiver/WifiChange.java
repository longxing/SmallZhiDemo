package com.iii360.box.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;

public class WifiChange {
    public interface WifiStateListener {
        /**
         * @param isConnect
         *            连接是否成功
         * @param ssid
         *            wifi名称
         */
        public void onConnect(boolean isConnect, String ssid);
    }

    private WifiStateListener wifiListener;
    private Context context;

    public void setWifiListener(WifiStateListener wifiListener) {
        this.wifiListener = wifiListener;
    }

    public WifiChange(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(KeyList.AKEY_WIFI_CHNAGE);
        context.registerReceiver(receiver, filter);

    }

    public void unregisterReceiver() {
        context.unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(KeyList.AKEY_WIFI_CHNAGE)) {
                String ssid = intent.getStringExtra(KeyList.IKEY_WIFI_SSID);
                LogManager.d("WifiChange ssid = " + ssid);

                if (wifiListener != null) {
                    wifiListener.onConnect(true, ssid);
                }

            }
        }
    };

}
