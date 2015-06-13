package com.iii.wifi.dao.info;

public class WifiBoxModeInfo {
    private int id;

    /**
     * 模式名称 BoxModeEnum
     */
    private String modeName;
    /**
     * 控制设备的ID，格式:ID+"||"+ID
     */
    private String controlIDs;
    
    /**
     * 动作,默认“开/关”
     */
    private String action ;

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public String getControlIDs() {
        return controlIDs;
    }

    public void setControlIDs(String controlIDs) {
        this.controlIDs = controlIDs;
    }

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

    
}
