package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiUpdateInfos {
    private String type;
    private List<WifiUpdateInfo> wifiInfos;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<WifiUpdateInfo> getWifiInfos() {
        return wifiInfos;
    }

    public void setWifiInfos(List<WifiUpdateInfo> wifiInfos) {
        this.wifiInfos = wifiInfos;
    }

    public void setWifiInfo(WifiUpdateInfo wifiInfo) {
        if (this.wifiInfos == null) {
            this.wifiInfos = new ArrayList<WifiUpdateInfo>();
        }
        this.wifiInfos.add(wifiInfo);
    }

    public WifiUpdateInfo getWifiInfoFirst() {
        if (wifiInfos != null && !wifiInfos.isEmpty()) {
            return wifiInfos.get(0);
        }
        return null;
    }

}
