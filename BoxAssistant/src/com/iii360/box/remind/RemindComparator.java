package com.iii360.box.remind;

import java.util.Comparator;

import com.voice.common.util.Remind;

/**
 * 备忘按照执行时间排序
 * 
 * @author hefeng
 */
public class RemindComparator implements Comparator {
    private long mBoxTime;

    public RemindComparator(long time) {
        this.mBoxTime = time;
    }

    @Override
    public int compare(Object lhs, Object rhs) {
        // TODO Auto-generated method stub
        Remind r1 = (Remind) lhs;
        Remind r2 = (Remind) rhs;
        Long t1 = r1.getRunTime(mBoxTime);
        Long t2 = r2.getRunTime(mBoxTime);

        if (t1 < mBoxTime && t2 > mBoxTime) {
            return 1;
        } else if (t1 > mBoxTime && t2 < mBoxTime) {
            return -1;
        }
        return t1.compareTo(t2);
    }
}
