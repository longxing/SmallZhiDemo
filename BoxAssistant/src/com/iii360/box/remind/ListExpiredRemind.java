package com.iii360.box.remind;

import java.util.ArrayList;
import java.util.List;

import com.iii.wifi.dao.info.WifiRemindInfos;
import com.iii.wifi.dao.manager.WifiCRUDForRemind;
import com.iii.wifi.dao.manager.WifiCRUDForRemind.ResultForRemindListener;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.WaitUtils;
import com.voice.common.util.Remind;

/**
 * 删除过期1周的备忘
 * 
 * @author hefeng
 * 
 */
public class ListExpiredRemind {
    public final static long ONE_WEEK = 7 * 24 * 60 * 60 * 1000;
    /**
     * 没有过期的
     */
    private static List<Remind> mNotExpiredList = new ArrayList<Remind>();

    /**
     * 过期的
     */
    private static List<Remind> mExpiredList = new ArrayList<Remind>();

    private List<Remind> list;
    private long mBoxTime ;

    public ListExpiredRemind(List<Remind> list,long boxTime) {
        // TODO Auto-generated constructor stub
        this.list = list;
        this.mBoxTime = boxTime;
    }

    /**
     * @param list
     * @return 没有过期的备忘列表
     */
    public List<Remind> getNotExpiredList() {
        mNotExpiredList.clear();

        if (list == null || list.isEmpty()) {
            return mNotExpiredList;
        }

        for (Remind remind : list) {
            if (isRepeatTime(remind) || remind.BaseTime >= (mBoxTime - ONE_WEEK)) {
                mNotExpiredList.add(remind);
            }
        }

        return mNotExpiredList;
    }

    /**
     * 判断时间备忘是否是周期性的
     * 
     * @param remind
     * @return
     */
    public static boolean isRepeatTime(Remind remind) {
        if (remind.avalibeFlag || remind.repeatFlag) {
            return true;
        }
        return false;
    }



    /**
     * @param list
     * @return 过期的备忘列表
     */
    public List<Remind> getExpiredList() {
        mExpiredList.clear();

        if (list == null || list.isEmpty()) {
            return mExpiredList;
        }

        for (Remind remind : list) {

            if (isRepeatTime(remind) || remind.BaseTime >= (mBoxTime - ONE_WEEK)) {

//                mNotExpiredList.add(remind);

            } else {

                mExpiredList.add(remind);

            }
        }

        return mExpiredList;
    }

    private WifiCRUDForRemind mWifiCRUDForRemind;

    /**
     * 删除过期1周的备忘
     * 
     * @param wifiCRUDForRemind
     */
    public void deleteWeekExpired(WifiCRUDForRemind wifiCRUDForRemind) {
        if (getExpiredList() == null || getExpiredList().isEmpty()) {
            return;
        }
        this.mWifiCRUDForRemind = wifiCRUDForRemind;

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            LogManager.e("删除过期的备忘 size = " + getExpiredList().size());
            for (Remind remind : getExpiredList()) {
                mWifiCRUDForRemind.deleteRemind(remind.id, new ResultForRemindListener() {
                    @Override
                    public void onResult(String type, String errorCode, WifiRemindInfos infos) {
                        // TODO Auto-generated method stub

                    }
                });
                WaitUtils.sleep(200);
            }
        }
    };
}
