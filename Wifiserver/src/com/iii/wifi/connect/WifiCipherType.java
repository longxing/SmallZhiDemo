package com.iii.wifi.connect;

/**
 * 定义几种加密方式，一种是WEP，一种是WPA，一种是EAP，没有密码，未知加密的情况
 * 注意与WifiSecurity中的id对应
 * @author river
 * 
 */
public enum WifiCipherType {
    WIFICIPHER_NOPASS, WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_EAP
}
