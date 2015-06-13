package com.iii360.box.entity;

import java.io.Serializable;

public class DetailDeviceInfo implements Serializable {
    private String roomId;
    private String deviceId;
    private String fittingName;
    private String deviceName;
    private String deviceIds[];
    private String deviceNames[];
    private boolean study;


    public boolean isStudy() {
        return study;
    }

    public void setStudy(boolean study) {
        this.study = study;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFittingName() {
        return fittingName;
    }

    public void setFittingName(String fittingName) {
        this.fittingName = fittingName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String[] getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(String[] deviceIds) {
        this.deviceIds = deviceIds;
    }

    public String[] getDeviceNames() {
        return deviceNames;
    }

    public void setDeviceNames(String[] deviceNames) {
        this.deviceNames = deviceNames;
    }
}
