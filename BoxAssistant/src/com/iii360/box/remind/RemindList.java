package com.iii360.box.remind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.iii360.box.util.TimeUtil;
import com.voice.common.util.Remind;

public class RemindList {
    private Map<String, List<Remind>> mGroupMap;
    private Map<String, List<Remind>> mNewMap;
    private List<Remind> mRemindList;
    private int i = 0;

    public RemindList(List<Remind> list) {
        // TODO Auto-generated constructor stub
        this.groupList(list);
    }

    /**
     * 分组
     * 
     * @param list
     * @return
     */
    public Map<String, List<Remind>> groupList(List<Remind> list) {
        mGroupMap = new HashMap<String, List<Remind>>();

        for (Remind remind : list) {
            String createTime = timeKey(remind.creatTime);
            if (mGroupMap.containsKey(createTime)) {

                mRemindList = mGroupMap.get(createTime);

            } else {
                mRemindList = new ArrayList<Remind>();
            }
            mRemindList.add(remind);
            mGroupMap.put(createTime, mRemindList);
        }

        mGroupMap = resetMapKey(mGroupMap);
        return mGroupMap;
    }

    private String timeKey(long m) {
        return TimeUtil.getYear(m) + TimeUtil.getMonth(m);
    }

    /**
     * 重新设置Map key值
     * 
     * @param oldMap
     * @return
     */
    public Map<String, List<Remind>> resetMapKey(Map<String, List<Remind>> oldMap) {
        i = 0;
        mNewMap = new HashMap<String, List<Remind>>();
        Iterator<String> it = oldMap.keySet().iterator();

        while (it.hasNext()) {
            String key = it.next();
            List<Remind> listWifiRemind = oldMap.get(key);
            mNewMap.put("" + i, listWifiRemind);
            i++;
        }

        return mNewMap;
    }

    /**
     * @return 分组数据
     */
    public Map<String, List<Remind>> getGroup() {
        return mNewMap;
    }

}
