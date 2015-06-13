package com.iii.wifi.dao.info;


public class WifiBoxSystemInfos {
    private String type;//操作类型，如设置，查询等
    private WifiBoxSystemInfo info ;
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public WifiBoxSystemInfo getInfo() {
        return info;
    }
    public void setInfo(WifiBoxSystemInfo info) {
        this.info = info;
    }
}
