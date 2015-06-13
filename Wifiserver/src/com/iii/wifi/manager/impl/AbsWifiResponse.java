package com.iii.wifi.manager.impl;

import android.content.Context;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;

public abstract class AbsWifiResponse {
    protected Gson gson = new Gson();
    /**
     * 返回数据
     */
    protected String mResult;

    public AbsWifiResponse() {
        // TODO Auto-generated constructor stub
        mResult = null;
    }

    /**
     * 处理响应数据
     * 
     * @param obj
     * @param context
     * @return
     */
    public abstract String getResponse(WifiJSONObjectInfo obj, Context context);

}
