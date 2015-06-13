package com.iii.wifi.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.iii.wifi.manager.impl.IBindBroadcastReceiver;
import com.iii360.sup.common.utl.LogManager;

public class WifiSwitch extends AbsWifiManager implements IBindBroadcastReceiver {
    public interface WifiSwitchListener {
        public void onConnectResult(int state);
    }

    private WifiSwitchListener switchListener;

    public void setSwitchListener(WifiSwitchListener switchListener) {
        this.switchListener = switchListener;
    }

    public WifiSwitch(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        registerReceiver();
    }

    public void openWifi(WifiSwitchListener switchListener) {
        if (switchListener != null) {
            this.switchListener = switchListener;
            openWifi();
        }
    }

    public void closeWifi(WifiSwitchListener switchListener) {
        this.switchListener = switchListener;
        openWifi();
    }

    /**
     * 打开wifi功能
     * 
     * @return
     */
    public boolean openWifi() {
        boolean bRet = true;
//        if (!mWifiManager.isWifiEnabled()) {
        bRet = mWifiManager.setWifiEnabled(true);
//        }
        return bRet;
    }

    /**
     * 关闭wifi功能
     * 
     * @return
     */
    public boolean closeWifi() {
        boolean bRet = true;
        if (mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(false);
        }
        return bRet;
    }

    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    public boolean checkOpenWifi() {
        long time = System.currentTimeMillis();

        //开启wifi功能需要一段时间(在手机上测试一般需要1-3秒左右)，所以要等到wifi
        //状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            if (System.currentTimeMillis() - time > 5000) {
                LogManager.i(WIFI_HEAD + "open wifi timeout (5s)");
                return false;
            }

            try {
                LogManager.i(WIFI_HEAD + "open wifi ,waiting ... " + mWifiManager.getWifiState());
                //让它睡个200毫秒在检测……
                Thread.currentThread();
                Thread.sleep(200);
            } catch (InterruptedException ie) {
            }
        }

        return true;
    }

    public void checkWifi() {
        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
            LogManager.i(WIFI_HEAD + "网卡正在关闭");
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            LogManager.i(WIFI_HEAD + "网卡已经关闭");
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            LogManager.i(WIFI_HEAD + "网卡正在打开");
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            LogManager.i(WIFI_HEAD + "网卡已经打开");
        } else {
            LogManager.i(WIFI_HEAD + "---_---晕......没有获取到状态---_---");
        }
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
        unregisterReceiver();
    }

    @Override
    public void registerReceiver() {
        // TODO Auto-generated method stub
        context.registerReceiver(receiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    @Override
    public void unregisterReceiver() {
        // TODO Auto-generated method stub
        try {
            context.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();

            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                // 这个监听wifi的打开与关闭，与wifi的连接无关

                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

                switch (wifiState) {

                case WifiManager.WIFI_STATE_DISABLED: //常量1，表示不可用
                    LogManager.i("wifi 不可用");

                    if (switchListener != null) {
                        switchListener.onConnectResult(WifiManager.WIFI_STATE_DISABLED);
                    }

                    break;
                case WifiManager.WIFI_STATE_DISABLING://常量0，表示停用中
                    LogManager.i("wifi 停用中");

                    break;
                case WifiManager.WIFI_STATE_ENABLING://常量2，表示启动中
                    LogManager.i("wifi 启动中");

                    break;
                case WifiManager.WIFI_STATE_ENABLED://常量3，表示准备就绪
                    LogManager.i("wifi 准备就绪");
                    if (switchListener != null) {
                        switchListener.onConnectResult(WifiManager.WIFI_STATE_ENABLED);
                    }

                    break;

                case WifiManager.WIFI_STATE_UNKNOWN://常量4，表示未知状态
                    LogManager.i("wifi 未知状态");
//                    if (switchListener != null) {
//                        switchListener.onConnectResult(WifiManager.WIFI_STATE_DISABLED);
//                    }
//                    break;
                }

            }

        }
    };
}
