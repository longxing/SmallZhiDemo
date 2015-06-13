package com.iii360.box.entity;

import com.iii360.box.util.TimeUtil;
import com.voice.common.util.Remind;

public class RemindAdapter {
    public int id;
    private String createTime;
//    private String excuteTime;
    private String event;
    public boolean repeatFlag = false; // 备忘是否为多次执行的
    public int repeatDistance = 1; // 执行的间隔时间
    public int repeatType = 0; // 执行的间隔类型,Calendar

    private Remind remind;

    public RemindAdapter(Remind remind) {
        // TODO Auto-generated constructor stub
        this.remind = remind;
    }

    public int getId() {
        return this.remind.id;
    }

    public String getCreateTime() {
        StringBuffer buff = new StringBuffer();
        buff.append(TimeUtil.getYear(this.remind.creatTime));
        buff.append("/");
        buff.append(TimeUtil.getMonth(this.remind.creatTime));
        return buff.toString();
    }

//    public String getExcuteTime() {
//        return excuteTime;
//    }

    public String getEvent() {
        return this.remind.needHand;
    }

    public boolean isRepeatFlag() {
        return this.remind.repeatFlag;
    }

    public int getRepeatDistance() {
        return this.remind.repeatDistance;
    }

    public int getRepeatType() {
        return this.remind.repeatType;
    }
}
