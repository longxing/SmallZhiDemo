package com.iii.wifi.dao.info;

public class WifiJSONObjectForUserDataInfo {
    private String type;
    private String error = "0";
    private WifiUserData obj;


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

    public WifiUserData getObj() {
        return obj;
    }

    public void setObj(WifiUserData obj) {
        this.obj = obj;
    }

}
