package com.iii360.box.util;

import java.util.Comparator;

import com.iii.wifi.dao.info.WifiMusicInfo;

/**
 * 按照拼音字母排序
 * 
 * @author Administrator
 * 
 */
public class PinyinComparator implements Comparator<Object> {
    @Override
    public int compare(Object o1, Object o2) {
        
        WifiMusicInfo info1 = (WifiMusicInfo) o1 ;
        WifiMusicInfo info2 = (WifiMusicInfo) o2 ;
        
        String str1 = PingYinUtil.getPingYin(info1.getName());
        String str2 = PingYinUtil.getPingYin(info2.getName());
        return str1.compareTo(str2);
    }

}
