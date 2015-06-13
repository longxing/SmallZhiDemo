package com.iii.wifi.dao.info;

public class WifiStringInfo {
    private int id;
    /**
     * 消息
     */
    private String message;
    /**
     * 操作类型,区分操作消息分别取执行
     */
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
