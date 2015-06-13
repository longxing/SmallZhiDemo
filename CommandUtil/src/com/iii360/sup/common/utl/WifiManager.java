package com.iii360.sup.common.utl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WifiManager {
    
    private android.net.wifi.WifiManager mWifiManager;
    public WifiManager(Context context) {
        mWifiManager = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    public static boolean isWifiConnected( Context context ) {
        final ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo  = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo.isConnected() ;
    }
    public static boolean isWifiEnabled(Context context) {
        final android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) context.
                getSystemService(
                Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }
    public static boolean isAnyWifiAvaiable( Context context ) {
        final ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo  = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo.isAvailable();
    }
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }
}
