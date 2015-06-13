package com.iii360.box.util;

import android.content.Context;

import com.iii.wifi.dao.manager.WifiCRUDForWeatherTime.ResultForWeatherTimeListener;
import com.iii.wifi.dao.manager.WifiCreateAndParseSockObjectManager;
import com.iii.wifi.dao.manager.WifiForCommonOprite;
import com.iii360.box.R;

public class WifiCRUDUtil {
    public static boolean isSuccessAll(String errorCode) {
        if (errorCode.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS)
                || errorCode.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_REPEAT)) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isSuccess(String errorCode) {
        if (errorCode.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isExist(String errorCode) {
        if (errorCode.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_REPEAT)) {
            return true;
        } else {
            return false;
        }
    }

    public static void playTTS(final Context context, String content) {
        WifiForCommonOprite oprite = new WifiForCommonOprite(BoxManagerUtils.getBoxTcpPort(context), BoxManagerUtils.getBoxIP(context));
        oprite.playTTS(content, new ResultForWeatherTimeListener() {
            @Override
            public void onResult(String type, String errorCode, String result) {
                // TODO Auto-generated method stub
                if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
                    LogManager.e("tts error");
                    ToastUtils.show(context, R.string.ba_tts_error);
                }
            }
        });
    }
    
    
    /***
     * terry 重载playTTS,ip地址指定
     * @param ip
     * @param context
     * @param content
     */
    public static void playTTS(final String ip ,final Context context,String content) {
    	WifiForCommonOprite oprite = new WifiForCommonOprite(BoxManagerUtils.getBoxTcpPort(context),ip);
    	oprite.playTTS(content, new ResultForWeatherTimeListener() {
    		@Override
    		public void onResult(String type, String errorCode, String result) {
    			// TODO Auto-generated method stub
    			if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
    				LogManager.e("tts error");
    				ToastUtils.show(context, R.string.ba_tts_error);
    			}
    		}
    	});
    }
    public static void playTTS(final String ip ,int port ,final Context context,String content) {
    	WifiForCommonOprite oprite = new WifiForCommonOprite(port,ip);
    	oprite.playTTS(content, new ResultForWeatherTimeListener() {
    		@Override
    		public void onResult(String type, String errorCode, String result) {
    			// TODO Auto-generated method stub
    			if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
    				LogManager.e("tts error");
    				ToastUtils.show(context, R.string.ba_tts_error);
    			}
    		}
    	});
    }
}
