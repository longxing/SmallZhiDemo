package com.base.platform;


import android.content.Context;

import com.base.app.AppsManager;
import com.iii360.sup.common.utl.LogManager;
import com.parser.iengine.EngineGroup;
import com.parser.iengine.IEngine;
import com.parser.iengine.RequestParams;
import com.parser.iengine.crf.CRFUtil;

/**
 * @author Taurus
 * @
 *
 */
final public class Platform  implements IPlatform {
    
    
    public static final String SESSION_TYPE_NATIVE = "0";
    
    public static final String SESSION_TYPE_REMOTE = "1";
    
    
    private String mPackageName;
    private String mUserId;
    private Context mContext;
    private IEngine mIEngine;
    


    /**
     * @return 当前设置的数据监听器
     */
    public OnDataReceivedListener getOnDataReceivedListener() {
        OnDataReceivedListener l = null;
        if(mIEngine != null) {
            l = mIEngine.getOnDataReceivedListener();
        }
        return l;
    }
    
    /**
     * 设置数据监听器
     * @param OnDataReceivedListener 从用户端传入的监听器
     * @description:设置数据接收监听
     */
    public void setOnDataReceivedListener(OnDataReceivedListener l) {
        mIEngine.setOnDataReceivedListener(l);
    }
    
    /**
     * @param context 应用程序环境变量
     * @param appId 应用程序id
     */
    private Platform(Context context, OnDataReceivedListener l) {
        //LogManager.initLogManager(true, false);
        CRFUtil.init(context, context.getPackageName());
        
        mContext = context;
       // mUserId = appId;
        mIEngine = new EngineGroup(mContext);
        mIEngine.setOnDataReceivedListener(l);
        
        mPackageName = context.getPackageName();
    }
    

    
//    /**
//     * 释放资源
//     */
//    public static void release() {
//    	if(mPlatform != null) {
//    		mPlatform = null;
//    	}
//    	
//    }
    
    /**
     * Only for VoiceAssistant 360
     */
    public void setAdditionalParams(String params) {
    	mIEngine.setAdditionalParams(params);
    }
    
    /**
     * Only for VoiceAssistant 360
     */
    public void sendRemoteSession(String text) {
    	LogManager.e("sendRemoteSession"+text);;
        mIEngine.input(text, makeRemoteParams());
    }
    
    /**
     * Only for VoiceAssistant 360
     */
    public void sendRemoteSession(String text, String params) {
    	LogManager.e("sendRemoteSession"+text+params);;
        if(params != null) {
            mIEngine.input(text, params + "&" + makeRemoteParams());
        } else {
            sendRemoteSession(text);
        }
        
    }
    
    /**
     * 发送会话
     * @param text 待解析的文本
     */
    public void sendSession(String text) {
    	LogManager.e("sendSession"+text);;
        String appId = RequestParams.PARAM_APP_ID + "=" + AppsManager.getAppId(mContext);
        String robotId = RequestParams.PARAM_ROBOT_ID + "=" + AppsManager.getRobotId(mContext, mPackageName);
        mIEngine.input(text, appId + "&" + robotId);
    }
    
    /**
     * 发送会话
     * @param text 待解析的文本
     * @param params p1=v1&p2=v2...
     *        support: app_id,robot_id
     *
     */
    public void sendSession(String text, String params) {
    	LogManager.e("sendSession"+text+params);;
    	
        if(params == null || params.trim().equals("")) {
            sendSession(text);
        } else {
            mIEngine.input(text, params);
        }
    }
    
    private String makeRemoteParams() {
        String appId = RequestParams.PARAM_APP_ID + "=" + AppsManager.getAppId(mContext);
        String sessionType = RequestParams.PARAM_SESSION_TYPE + "=" + SESSION_TYPE_REMOTE;
        return appId + "&" + sessionType;
    }
    

    

    /**
     * 获取平台操作实例
     * @param context
     * @param l 数据接收监听
     * @return Platform Instance
     */
    public static Platform getPlatformInstance(Context context, OnDataReceivedListener l) {
        
//        if(mPlatform == null) {
//            mPlatform = new Platform(context, l);
//        }
        
        return new Platform(context, l);
    }
    

    
    
}
