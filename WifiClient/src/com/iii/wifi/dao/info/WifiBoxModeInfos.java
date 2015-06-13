package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiBoxModeInfos {
    private String type;//操作类型，如设置，查询等
    private List<WifiBoxModeInfo> wifiInfos;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<WifiBoxModeInfo> getWifiInfos() {
        return wifiInfos;
    }

    public void setWifiInfos(List<WifiBoxModeInfo> wifiInfos) {
        this.wifiInfos = wifiInfos;
    }
    
    public void setWifiInfo(WifiBoxModeInfo wifiInfo) {
        if (this.wifiInfos == null) {
            this.wifiInfos = new ArrayList<WifiBoxModeInfo>();
        }
        this.wifiInfos.add(wifiInfo);
    }
}
