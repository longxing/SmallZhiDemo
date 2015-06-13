package com.iii.wifi.dao.info;

import java.io.Serializable;

public class WifiControlInfo implements Serializable{
    private int id; //id
    private String corder;//中文命令
    private String dorder;//命令数据
    @Deprecated
    /**
     * 弃用，和deviceID中的roomid重复
     */
    private String roomId;//房间id
    private String deviceid;//设备id
    private String action;//动作
    private int frequency;//命令使用频率
    //liuwen begin
    private String model;//设备型号
    //liuwen end
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getCorder() {
        return corder;
    }
    public void setCorder(String corder) {
        this.corder = corder;
    }
    public String getDorder() {
        return dorder;
    }
    public void setDorder(String dorder) {
        this.dorder = dorder;
    }
    public String getRoomId() {
        return roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    public String getDeviceid() {
        return deviceid;
    }
    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }
    public int getFrequency() {
        return frequency;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    //liuwen begin
    public String getDeviceModel(){
        return model;
    }
    public void setDeviceModel(String model){
        this.model = model;
    }
    //liuwen end
    
}
