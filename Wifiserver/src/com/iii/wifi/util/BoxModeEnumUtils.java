package com.iii.wifi.util;

import com.iii.wifi.dao.info.BoxModeEnum;

public class BoxModeEnumUtils {
//    MODE_GO_HOME("回家模式"),      //回家模式 100000
//    MODE_LEAVE_HOME("离家模式"),   //离家模式 100001
//    MODE_GET_UP("起床模式"),       //起床模式 100002
//    MODE_SLEEP("睡眠模式"),        //睡眠模式 100003
//    MODE_BREAKFAST("早餐模式"),    //早餐模式 100004
//    MODE_LUNCH("午餐模式"),        //午餐模式 100005
//    MODE_DINNER("晚餐模式");       //晚餐模式 100006
//关闭回家模式 100007
//关闭离家模式 100008
//关闭起床模式 100009
//关闭睡眠模式 100010
//关闭早餐模式 100011
//关闭午餐模式 100012
//关闭晚餐模式 100013
    
    /**
     * 打开模式数据IDs
     */
    public static final int[] modeArrayId = { 100000, 100001, 100002, 100003, 100004, 100005, 100006 };
    /**
     * 删除回家模式IDs
     */
    public static final int[] deleteModeArrayId = { 100007, 100008, 100009, 100010, 100011, 100012, 100013 };
    
    /**
     * @param modeName
     * @return 通过模式名称获取id
     */
    public static final int getModeId(String modeName) {
        BoxModeEnum[] modeArray = { BoxModeEnum.MODE_GO_HOME, BoxModeEnum.MODE_LEAVE_HOME, BoxModeEnum.MODE_GET_UP, BoxModeEnum.MODE_SLEEP,
                BoxModeEnum.MODE_BREAKFAST, BoxModeEnum.MODE_LUNCH, BoxModeEnum.MODE_DINNER };

        for (int i = 0; i < modeArray.length; i++) {
            if (modeName.contains(modeArray[i].getValue())) {
                return modeArrayId[i];
            }
        }

        return modeArrayId[0];
    }
    
    /**
     * @param modeName
     * @return 通过模式名称获取id
     */
    public static final int getDeleteModeId(String modeName) {
        BoxModeEnum[] modeArray = { BoxModeEnum.MODE_GO_HOME, BoxModeEnum.MODE_LEAVE_HOME, BoxModeEnum.MODE_GET_UP, BoxModeEnum.MODE_SLEEP,
                BoxModeEnum.MODE_BREAKFAST, BoxModeEnum.MODE_LUNCH, BoxModeEnum.MODE_DINNER };

        for (int i = 0; i < modeArray.length; i++) {
            if (modeName.contains(modeArray[i].getValue())) {
                return deleteModeArrayId[i];
            }
        }

        return deleteModeArrayId[0];
    }
}
