package com.iii.wifi.dao.info;

public class WifiJSONObjectForBoxSystem {
    private String type;
    private String error = "0";
    private WifiBoxSystemInfos obj;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public WifiBoxSystemInfos getObj() {
        return obj;
    }

    public void setObj(WifiBoxSystemInfos obj) {
        this.obj = obj;
    }

}
