package com.iii360.box.entity;

public class WifiInfoMessage {
    
    /**
     * wifi ssid
     */
    private String ssid;
    /**
     * 是否加密
     */
    private boolean isEncryption;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * @return 是否加密
     */
    public boolean isEncryption() {
        return isEncryption;
    }

    public void setEncryption(boolean isEncryption) {
        this.isEncryption = isEncryption;
    }

}
