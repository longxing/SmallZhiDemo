package com.iii.wifi.dao.info;

public class WifiJSONObjectVolumeInfo {
    private String type;
    private String error = "0";
    private WifiVolume obj;

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

    public WifiVolume getObj() {
        return obj;
    }

    public void setObj(WifiVolume obj) {
        this.obj = obj;
    }

}
