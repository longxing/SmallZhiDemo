package com.iii.wifi.dao.info;

public class WifiJSONObjectForHuanTeng {
    private String type;
    private String error = "0";
    private HuanTengAccount obj;


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

    public HuanTengAccount getObj() {
        return obj;
    }

    public void setObj(HuanTengAccount obj) {
        this.obj = obj;
    }

}
