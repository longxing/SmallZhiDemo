package com.iii.wifi.http;

import org.json.JSONException;
import org.json.JSONObject;

import com.iii360.sup.common.utl.LogManager;

public class ParserMyTag {

    public static String getTag(String jsonTag) {
        //"{\"label\":\"幽默_低调_体贴_纯真_三分钟热度_台球_阳光_纠结_直率_执着_八卦_自信\"}";
        try {

            JSONObject obj = new JSONObject(jsonTag);
            String tag = obj.getString("label");
            LogManager.e("tag=======" + tag);
            return tag;

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }
}
