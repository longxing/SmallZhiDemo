package com.iii.wifi.dao.info;

import java.io.Serializable;

public class WifiDeviceInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int id;
    private String macadd;// mac地址
    private String deviceid;// 设置id
    private String roomid;// 房间id
    private String deviceName;// 设备名 eg : 被控制的 电视、空调....
    private String fitting;// 配件 eg： 机器狗 、wifi单品
    private String ipAdd;// 地址
    //liuwen begin
    private int deviceType;//设备类型,0是单品，1是机器狗等类似主机
    private String deviceModel;//设备型号，*必填
    //liuwen end
    public String getIpAdd() {
        return ipAdd;
    }

    public void setIpAdd(String ipAdd) {
        this.ipAdd = ipAdd;
    }

    public String getFitting() {
        return fitting;
    }

    public void setFitting(String fitting) {
        this.fitting = fitting;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMacadd() {
        return macadd;
    }

    public void setMacadd(String macadd) {
        this.macadd = macadd;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }
    //liuwen begin
    public int getDeviceType(){
        return deviceType;
    }
    public void setDeviceType(int deviceType){
        this.deviceType = deviceType;
    }
    public String getDeviceModel(){
        return deviceModel;
    }
    public void setDeviceModel(String deviceModel){
        this.deviceModel = deviceModel;
    }
    //liuwen end
}
