package com.iii.wifi.util;

import com.iii.client.WifiConfig;

import android.content.Context;

/**
 * 通信端口工具类
 * 
 * @author Administrator
 * 
 */
public class PortUtil {
    private static BasePreferences mBasePreferences;

    public static BasePreferences getPreferences(Context context) {
        if (mBasePreferences == null) {
            synchronized (PortUtil.class) {
                if (mBasePreferences == null) {
                    mBasePreferences = new BasePreferences(context);
                }
            }
        }

        return mBasePreferences;
    }

    public static int getTcpPort(Context context) {
        try {
            return Integer.parseInt(getPreferences(context).getPrefString(KeyList.PKEY_TCP_PORT, WifiConfig.TCP_DEFAULT_PORT + ""));
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return WifiConfig.TCP_DEFAULT_PORT;
    }
    

    public static int getUdpPort(Context context) {
        try {
            return Integer.parseInt(getPreferences(context).getPrefString(KeyList.PKEY_UDP_PORT, WifiConfig.UDP_DEFAULT_PORT + ""));
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return WifiConfig.UDP_DEFAULT_PORT;
    }
}
