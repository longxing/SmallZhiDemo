package com.iii.wifi.dao.info;

public class WifiJSONObjectForString {
    private String type;
    private String error = "0";
    private WifiStringInfos obj;

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

    public WifiStringInfos getObj() {
        return obj;
    }

    public void setObj(WifiStringInfos obj) {
        this.obj = obj;
    }

}
