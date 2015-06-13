package com.iii360.box.entity;

public class WifiCRUDInfo {
    private String roomId;// 房间ID
    private String roomName;// 房间名
    private String deviceId;// 设置id
    private String deviceName;//设备名
    private String fitting;//配件
    private String dorder;//命令数据
    private String action;//动作

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getFitting() {
        return fitting;
    }

    public void setFitting(String fitting) {
        this.fitting = fitting;
    }

    public String getDorder() {
        return dorder;
    }

    public void setDorder(String dorder) {
        this.dorder = dorder;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
