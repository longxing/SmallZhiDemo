package com.iii.wifi.http.parsrer;

import org.json.JSONException;
import org.json.JSONObject;

import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii360.sup.common.utl.LogManager;

public class ParserMusic implements ParserData {
    private String playStatus;

    public ParserMusic(String playStatus) {
        // TODO Auto-generated constructor stub
        this.playStatus = playStatus;
    }

    //{"id":"415","songName":"415","singerName":"12"}
    @Override
    public WifiMusicInfo getParserData(String json) {
        // TODO Auto-generated method stub
        LogManager.e(json);
        
        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        WifiMusicInfo info = new WifiMusicInfo();
        String id = null;
        try {
            id = obj.getString("id");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String songName = null;
        try {
            songName = obj.getString("songName");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String singerName = null;
        try {
            singerName = obj.getString("singerName");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String isCollected = null;
        try {
        	isCollected = obj.getString("isCollected");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        info.setMusicId(id);
        info.setName(songName);
        info.setAuthor(singerName);
        info.set_isCollected(Boolean.valueOf(isCollected));
        info.setPlayStatus(playStatus);

        return info;
    }

}
