package com.voice.common.util;

import java.util.List;

import com.iii360.sup.common.utl.LogManager;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiSecurity {
    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;

    public static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    /**
     * 获取当前网络加密方式
     * @param context
     * @return
     */
    public static int getCurrentWifiSecurity(Context context) {
        
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = mWifiManager.getConnectionInfo();

        // 得到配置好的网络连接
        List<WifiConfiguration> wifiConfigList = mWifiManager.getConfiguredNetworks();
        for (int i = 0; i < wifiConfigList.size(); i++) {
        	//12-08 16:24:52.237: E/info(11005): Tenda============"Tenda"
        	String ssid = wifiConfigList.get(i).SSID;
        	ssid = ssid.replaceAll("\"", "");
        	LogManager.e( info.getSSID()+"============"+ssid);
            if (info.getSSID().replaceAll("\"", "").equals(ssid)&&info.getNetworkId()== wifiConfigList.get(i).networkId) {
             	LogManager.e( info.getSSID()+" come ....");
                return getSecurity(wifiConfigList.get(i));
            }
        }
        return SECURITY_PSK;
    }

    /**
     * 获取指定网络加密方式
     * @param context
     * @return
     */
    public static int getWifiSecurity(Context context,String ssId,int networkId) {
        
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        // 得到配置好的网络连接
        List<WifiConfiguration> wifiConfigList = mWifiManager.getConfiguredNetworks();
        for (int i = 0; i < wifiConfigList.size(); i++) {
        	//12-08 16:24:52.237: E/info(11005): Tenda============"Tenda"
        	String ssid = wifiConfigList.get(i).SSID;
        	ssid = ssid.replaceAll("\"", "");
        	LogManager.e( ssId+"============"+ssid);
            if (ssId.equals(ssid)&&networkId== wifiConfigList.get(i).networkId) {
             	LogManager.e( ssid+" come ....");
                return getSecurity(wifiConfigList.get(i));
            }
        }

        return SECURITY_PSK;
    }
}
