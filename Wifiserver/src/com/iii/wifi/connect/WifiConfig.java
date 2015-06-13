package com.iii.wifi.connect;

import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;

import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;
import com.voice.common.util.WifiSecurity;

public class WifiConfig extends AbsWifiManager {

    public WifiConfig(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mBaseContext = new SuperBaseContext(context);
    }

    /**
     * 查看以前是否也配置过这个网络
     * 
     * @param ssid
     * @return
     */
    public void removeExistNetwork(String ssid) {
        List<WifiConfiguration> configList = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configList) {
            if (config.SSID == null) {
                continue;
            }

            if (config.SSID.equals("\"" + ssid + "\"")) {
                mWifiManager.removeNetwork(config.networkId);
            }
        }
    }

    public WifiConfiguration createWifiConfiguration(String ssid, String password, WifiCipherType type) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";

        if (type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.hiddenSSID = false;
            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        } else if (type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.priority = 1;

        } else if (type == WifiCipherType.WIFICIPHER_WEP) {
            config.wepKeys[0] = "\"" + password + "\"";
//          config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;

        } else if (type == WifiCipherType.WIFICIPHER_EAP) {
            config.hiddenSSID = false;
            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.preSharedKey = "\"" + password + "\"";
            
        } else {
            return null;
        }

        return config;
    }
    private SuperBaseContext mBaseContext;
    public WifiCipherType getEncryptType(String ssid){
        WifiCipherType cipherType = null;
        String encrypt =  mBaseContext.getPrefString(KeyList.PKEY_WIFICIPHERTYPE, null);
        LogManager.e("传入加密方式：" + encrypt);
        
        //旧版没有传入加密方式，自己去获取
        if(TextUtils.isEmpty(encrypt)){
            //获取加密方式
            cipherType = WifiUtils.getCipherType(context, ssid);
            LogManager.e("自动获取加密方式：" + cipherType);
            
        }else{
            
            int t = Integer.parseInt(encrypt);
            
            switch (t) {

            case WifiSecurity.SECURITY_NONE:
                cipherType = WifiCipherType.WIFICIPHER_NOPASS;

                break;
            case WifiSecurity.SECURITY_WEP:
                cipherType = WifiCipherType.WIFICIPHER_WEP;

                break;
            case WifiSecurity.SECURITY_PSK:
                cipherType = WifiCipherType.WIFICIPHER_WPA;

                break;
            case WifiSecurity.SECURITY_EAP:
                cipherType = WifiCipherType.WIFICIPHER_EAP;

                break;

            default:
                cipherType = WifiCipherType.WIFICIPHER_WPA;

                break;
            }
            
        }
        
        
        return cipherType ;
    }
    
    public boolean connect(String ssid, String password) {
        WifiCipherType type = getEncryptType(ssid);
        
        LogManager.e("加密类型：" + type);
        
        removeExistNetwork(ssid);

        int id = mWifiManager.addNetwork(createWifiConfiguration(ssid, password, type));
        mWifiManager.saveConfiguration();

        boolean success = mWifiManager.enableNetwork(id, true);
        if (id == -1 || !success) {
            return false;
        }
        return true;
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
}
