package com.iii.wifi.thirdpart.huanteng;

public class HuanTengInfo {
    private String id;
    private String turned_on;
    private String own_device;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTurned_on() {
        return turned_on;
    }

    public void setTurnedOn(String turned_on) {
        this.turned_on = turned_on;
    }

    public String getOwnDevice() {
        return own_device;
    }

    public void setOwnDevice(String own_device) {
        this.own_device = own_device;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "HuanTengInfo [id=" + id + ", turned_on=" + turned_on + ", own_device=" + own_device + ", name=" + name + "]";
    }

}
