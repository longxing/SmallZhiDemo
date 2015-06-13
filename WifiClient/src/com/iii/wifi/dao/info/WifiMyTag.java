package com.iii.wifi.dao.info;

public class WifiMyTag {
    /**
     * 格式：***_***
     */
    private String tag ;
    private String type;
    private String imei;
    
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
