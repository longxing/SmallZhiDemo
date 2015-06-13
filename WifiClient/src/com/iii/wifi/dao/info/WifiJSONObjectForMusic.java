package com.iii.wifi.dao.info;

public class WifiJSONObjectForMusic {
    /**
     * 模块类型
     */
    private String type;
    private String error = "0";
    private WifiMusicInfos obj;

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

    public WifiMusicInfos getObj() {
        return obj;
    }

    public void setObj(WifiMusicInfos obj) {
        this.obj = obj;
    }

}
