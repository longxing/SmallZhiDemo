package com.voice.common.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.iii.wifi.dao.info.WifiMusicInfo;

public class MusicParserUtils {

    /**
     * @param playStatus
     *            0没有播放的歌曲1正在播放2暂停
     * @param jsonData
     *            歌曲json列表
     * @return
     */
    public static List<WifiMusicInfo> getMusicList(String playStatus, String jsonData) {
        List<WifiMusicInfo> list = new ArrayList<WifiMusicInfo>();
        if (TextUtils.isEmpty(jsonData)) {
            WifiMusicInfo info = new  WifiMusicInfo();
            info.setPlayStatus(playStatus);
            list.add(info);
            return list;
        }

        if (TextUtils.isEmpty(playStatus)) {
            playStatus = "0";
        }

        WifiMusicInfo info;
        try {
            JSONObject object = new JSONObject(jsonData);
            JSONArray array = object.getJSONArray("musics");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                info = new WifiMusicInfo();
                info.setPlayStatus(playStatus);
                try {
                    String param = obj.getString("id");
                    if (!TextUtils.isEmpty(param)) {
                        info.setMusicId(param);
                    } else {
                        continue;
                    }

                    param = obj.getString("songName");
                    if (!TextUtils.isEmpty(param)) {
                        info.setName(param);
                    } else {
                        continue;
                    }

                    param = obj.getString("singerName");
                    if (!TextUtils.isEmpty(param)) {
                        info.setAuthor(param);
                    }
                    
                    param = obj.getString("isCollected");
                    if (!TextUtils.isEmpty(param)) {
                        info.set_isCollected(Boolean.valueOf(param));
                    }

                    list.add(info);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //error = WIFI_INFO_ERROR;
        }

        return list;
    }
}
