package com.iii.wifi.dao.info;

public enum BoxModeEnum {
    MODE_NORMAL("无") ,          //没有设置模式
    MODE_GO_HOME("回家模式"),      //回家模式
    MODE_LEAVE_HOME("离家模式"),   //离家模式
    MODE_GET_UP("起床模式"),       //起床模式
    MODE_SLEEP("睡眠模式"),        //睡眠模式
    MODE_BREAKFAST("早餐模式"),    //早餐模式
    MODE_LUNCH("午餐模式"),        //午餐模式
    MODE_DINNER("晚餐模式");       //晚餐模式
    

    private final String value;
    /**
     * @param text
     */
    private BoxModeEnum(final String value) {
        this.value = value;
    }
    
    public String getValue(){
        return value ;
    }
}
