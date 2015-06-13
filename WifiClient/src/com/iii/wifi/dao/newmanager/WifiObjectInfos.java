package com.iii.wifi.dao.newmanager;

import java.util.ArrayList;
import java.util.List;

public class WifiObjectInfos {
    private String type;//操作类型，如设置，查询等
    private List<Object> wifiInfos;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<Object> getWifiInfos() {
        return wifiInfos;
    }
    public void setWifiInfos(List<Object> wifiInfos) {
        this.wifiInfos = wifiInfos;
    }
    
    public void setWifiInfo(Object wifiInfo) {
        if (this.wifiInfos == null) {
            this.wifiInfos = new ArrayList<Object>();
        }
        this.wifiInfos.add(wifiInfo);
    }
}
