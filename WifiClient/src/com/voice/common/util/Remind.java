package com.voice.common.util;

import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.voice.common.tool.TimerTicker;

public class Remind implements Parcelable {
    /**
     * 这个类型纯粹是为了在两个应用间传递对象时使用的， 比较坑， <br>
     * 如果下次还有这么麻烦的传递对象的需求，可以考虑将两个应用合并成一个应用
     */

    public Long BaseTime; // 第一次备忘执行的时间
    public boolean repeatFlag = false; // 备忘是否为多次执行的
    public int repeatDistance = 1; // 执行的间隔时间
    public int repeatType = 0; // 执行的间隔类型

    public boolean avalibeFlag = false; // 备忘是否是间隔生效的，只支持 周几～周几
                                        // avalibeFrom～avalibeTo
    public int avalibeFrom = 0;
    public int avalibeTo = 0;
    public Date time;

    public long creatTime; // 备忘的创建时间
    public int id; // 备忘的ID
    public String needHand; // 备忘的内容

    public Remind() {
        // TODO Auto-generated constructor stub
    }

    public Remind(TimerTicker ticker) {
        this.BaseTime = ticker.BaseTime;
        this.repeatFlag = ticker.repeatFlag;
        this.repeatDistance = ticker.repeatDistance;
        this.repeatType = ticker.repeatType;
        this.avalibeFlag = ticker.avalibeFlag;
        this.avalibeFrom = ticker.avalibeFrom;
        this.avalibeTo = ticker.avalibeTo;
    }

    public void setInfo(long creatTime, int id, String needHand) {
        this.creatTime = creatTime;
        this.id = id;
        this.needHand = needHand;
    }

    public static final Parcelable.Creator<Remind> CREATOR = new Parcelable.Creator<Remind>() {

        public Remind createFromParcel(Parcel in) {
            return new Remind(in);
        }

        public Remind[] newArray(int size) {
            return new Remind[size];
        }

    };

    public Remind(Parcel in) {
        readFromParcel(in);
    }

    public long getRunTime() {
        // TODO Auto-generated method stub
        Calendar changeCale = Calendar.getInstance();
        changeCale.setTimeInMillis(BaseTime);
        time = changeCale.getTime();
        if (repeatFlag) {
            while ((time.getTime() - System.currentTimeMillis()) <= 2) {

                changeCale.add(repeatType, repeatDistance);
                if (avalibeFlag) {
                    int weekday = changeCale.get(Calendar.DAY_OF_WEEK);
                    weekday--;
                    if (weekday <= 0) {
                        weekday = 7;
                    }
                    if (weekday < avalibeFrom || weekday > avalibeTo) {
//						LogManager.e("continue");
                        continue;
                    }
                }
                time = changeCale.getTime();
            }
            return changeCale.getTime().getTime();
        }

        return BaseTime;
    }
    
    public long getRunTime(long boxTime) {
        // TODO Auto-generated method stub
        Calendar changeCale = Calendar.getInstance();
        changeCale.setTimeInMillis(BaseTime);
        time = changeCale.getTime();
        if (repeatFlag) {
            while ((time.getTime() - boxTime) <= 2) {

                changeCale.add(repeatType, repeatDistance);
                if (avalibeFlag) {
                    int weekday = changeCale.get(Calendar.DAY_OF_WEEK);
                    weekday--;
                    if (weekday <= 0) {
                        weekday = 7;
                    }
                    if (weekday < avalibeFrom || weekday > avalibeTo) {
                        // LogManager.e("continue");
                        continue;
                    }
                }
                time = changeCale.getTime();
            }
            return changeCale.getTime().getTime();
        }

        return BaseTime;
    }

    public boolean getReapteFlag() {
        // TODO Auto-generated method stub
        return repeatFlag;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeLong(BaseTime);
        dest.writeInt(repeatFlag ? 1 : 0);
        dest.writeInt(repeatDistance);
        dest.writeInt(repeatType);
        dest.writeInt(avalibeFlag ? 1 : 0);
        dest.writeInt(avalibeFrom);
        dest.writeInt(avalibeTo);
        dest.writeLong(creatTime);
        dest.writeInt(id);
        dest.writeString(needHand);
    }

    private void readFromParcel(Parcel in) {
        // TODO Auto-generated method stub
        BaseTime = in.readLong();
        repeatFlag = (in.readInt() == 1);
        repeatDistance = in.readInt();
        repeatType = in.readInt();
        avalibeFlag = (in.readInt() == 1);
        avalibeFrom = in.readInt();
        avalibeTo = in.readInt();
        creatTime = in.readLong();
        id = in.readInt();
        needHand = in.readString();
    }

}
