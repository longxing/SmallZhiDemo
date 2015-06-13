package com.iii.wifi.dao.info;

public class WifiUpdateInfo {
    private int id;
    /**
     * 当前设置的房间名称，如客厅
     */
    private String roomName;
    /**
     * 当前设置的设备名称，如电视，空调
     */
    private String deviceName;
    /**
     * 已经设置的房间名称，如客厅
     */
    private String oldRoomName ;
    /**
     * 已经设置的设备名称，如电视，空调
     */
    private String oldDeviceName ;
    
    private WifiDeviceInfo wifiDeviceInfo;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getRoomName() {
        return roomName;
    }
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public String getOldRoomName() {
        return oldRoomName;
    }
    public void setOldRoomName(String oldRoomName) {
        this.oldRoomName = oldRoomName;
    }
    public String getOldDeviceName() {
        return oldDeviceName;
    }
    public void setOldDeviceName(String oldDeviceName) {
        this.oldDeviceName = oldDeviceName;
    }
    public WifiDeviceInfo getWifiDeviceInfo() {
        return wifiDeviceInfo;
    }
    public void setWifiDeviceInfo(WifiDeviceInfo wifiDeviceInfo) {
        this.wifiDeviceInfo = wifiDeviceInfo;
    }
    
    
}
