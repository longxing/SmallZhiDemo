package com.iii.wifi.dao.info;

public class WifiJSONObjectMyTagInfo {
    private String type;
    private String error = "0";
    private WifiMyTag obj;

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

    public WifiMyTag getObj() {
        return obj;
    }

    public void setObj(WifiMyTag obj) {
        this.obj = obj;
    }

}
