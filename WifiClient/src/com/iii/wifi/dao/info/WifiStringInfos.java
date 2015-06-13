package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

public class WifiStringInfos {
    private String type;//操作类型，如设置，查询等
    private List<WifiStringInfo> infos;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<WifiStringInfo> getInfos() {
        return infos;
    }

    public void setInfos(List<WifiStringInfo> infos) {
        this.infos = infos;
    }

    public void setInfo(WifiStringInfo wifiInfo) {
        if (this.infos == null) {
            this.infos = new ArrayList<WifiStringInfo>();
        }
        this.infos.add(wifiInfo);
    }
}
