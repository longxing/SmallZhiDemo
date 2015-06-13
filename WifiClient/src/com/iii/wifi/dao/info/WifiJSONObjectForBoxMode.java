package com.iii.wifi.dao.info;

public class WifiJSONObjectForBoxMode {
    /**
     * 模块类型
     */
    private String type;
    private String error = "0";
    private WifiBoxModeInfos obj;

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

    public WifiBoxModeInfos getObj() {
        return obj;
    }

    public void setObj(WifiBoxModeInfos obj) {
        this.obj = obj;
    }

}
