package com.iii.wifi.dao.info;

/**
 * 关于音箱信息
 * 
 * @author Administrator
 * 
 */
public class WifiBoxSystemInfo {
    private int id;

    /**
     * 电池电量
     */
    private String battery;

    /**
     * 可用多少rom,扩展内存
     */
    private String availableRomSize;
    /**
     * rom总共大小,扩展内存
     */
    private String romTotalSize;
    /**
     * 可用多少ram,系统运行内存
     */
    private String availableRamSize;

    /**
     * 总共多少ram,系统运行内存
     */
    private String ramTotalSize;

    /**
     * 主程序版本号
     */
    private String versionCode;
    /**
     * 音箱mac地址
     */
    private String mac;
    /**
     * 音箱ip地址
     */
    private String ip;
    /**
     * 音箱序列号
     */
    private String serial;
    /**
     * 充电状态
     */
    private String charg_state;
    
	public String getCharg_state() {
		return charg_state;
	}

	public void setCharg_state(String charg_state) {
		this.charg_state = charg_state;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getRomTotalSize() {
        return romTotalSize;
    }

    public void setRomTotalSize(String romTotalSize) {
        this.romTotalSize = romTotalSize;
    }

    public String getRamTotalSize() {
        return ramTotalSize;
    }

    public void setRamTotalSize(String ramTotalSize) {
        this.ramTotalSize = ramTotalSize;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getAvailableRomSize() {
        return availableRomSize;
    }

    public void setAvailableRomSize(String availableRomSize) {
        this.availableRomSize = availableRomSize;
    }

    public String getAvailableRamSize() {
        return availableRamSize;
    }

    public void setAvailableRamSize(String availableRamSize) {
        this.availableRamSize = availableRamSize;
    }

}
