package com.iii.wifi.manager.impl;

import com.iii360.sup.common.utl.LogManager;

import android.util.Log;



public class WifiResponseFactory extends AbsWifiResponseFactory {

    @Override
    public <T extends AbsWifiResponse> T createResponse(String className) {
        // TODO Auto-generated method stub
        AbsWifiResponse response = null;

        try {
            response = (AbsWifiResponse) Class.forName(className).newInstance();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogManager.e(Log.getStackTraceString(e));
        }

        return (T) response;
    }

}
