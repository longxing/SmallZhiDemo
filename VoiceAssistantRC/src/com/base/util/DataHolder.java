package com.base.util;

import java.util.HashMap;

public class DataHolder {
    
    private int mSessionId = 0;
    //private int mMode;
    //private int mInputMode;
    public final static int INPUT_MODE_VOICE = 0;
    public final static int INPUT_MODE_TEXT = 1;
    //private boolean mIsClose ;
    //private boolean mIsSlient;
    //private boolean mIsBlueTooth;

    
    private static HashMap<String, Boolean> mBooleanMap = new HashMap<String, Boolean>();
    private static HashMap<String, Integer> mIntMap = new HashMap<String, Integer>();
    private static HashMap<String, String> mStringMap = new HashMap<String, String>();
    private static HashMap<String, Long> mLongMap = new HashMap<String, Long>();
    private static HashMap<String, Object> mObjectMap = new HashMap<String, Object>();
    
    public static Object getGlobalObject(String key, Object defVal) {
        Object temp = mObjectMap.get(key);
        return (temp == null) ? defVal : temp;
    }
    
    public static Object getGlobalObject(String key) {
        return mObjectMap.get(key);
    }
    
    public static boolean getGlobalBoolean(String key, boolean defVal) {
        Boolean temp = mBooleanMap.get(key);
        return (temp == null) ? defVal : temp;
    }
    
    public static int getGlobalInteger(String key, int defVal) {
        Integer temp = mIntMap.get(key);
        return (temp == null) ? defVal : temp;
    }
    
    public static String getGlobalString(String key, String defVal) {
        String temp = mStringMap.get(key);
        return (temp == null) ? defVal : temp;
    }
    
    public static long getGlobalLong(String key, long defVal) {
        Long temp = mLongMap.get(key);
        return (temp == null) ? defVal : temp;
    }
    
    public static boolean getGlobalBoolean(String key) { 
        return getGlobalBoolean(key, false);
    }
    
    public static int getGlobalInteger(String key) {
        return getGlobalInteger(key, 0);
    }
    
    public static String getGlobalString(String key) {
        return mStringMap.get(key);
    }
    
    public static long getGlobalLong(String key) {
        return getGlobalLong(key, 0L);
    }
    
    
    public static void setGlobalBoolean(String key, boolean value) {
        mBooleanMap.put(key, value);
    }
    
    public static void setGlobalInteger(String key, int value) {
        mIntMap.put(key, value);
    }
    
    public static void setGlobalString(String key, String value) {
        mStringMap.put(key, value);
    }
    
    public static void setGlobalLong(String key, long value) {
        mLongMap.put(key, value);
    }
   
    public static void setGlobalObject(String key, Object value) {
        mObjectMap.put(key, value);
    }
    
//    public static String getInputModeName() {      
//        return (getGlobalInteger(KeyManager.GKEY_INT_INPUT_MODE) == INPUT_MODE_VOICE) ? "语音输入" : "文本输入";
//    }
    
//    public int getInputMode() {
//        return mInputMode;
//    }
//    
//    public void setInputMode(int inputMode) {
//        mInputMode = inputMode;
//    }
    
    
//	public boolean isAutoAnswer() {
//		return mIsAutoAnswer;
//	}
//
//	public void setAutoAnswer(boolean isAutoAnswer) {
//		mIsAutoAnswer = isAutoAnswer;
//	}

	public static int getSessionId() {

        return 0;
    }
    
//    public static void resetSessionId() {
//        LogManager.d("MainApplication", "resetSessionId", "cur Id:" + mSessionId);
//        mSessionId = 0;
//    }
//    
//    public static void addSessionId() {
//        if(getGlobalInteger(KeyList.GKEY_INT_MODE) == VoiceRecognise.RECOGNIZE_MODE_COMMON) {
//            mSessionId++;
//        }
//        LogManager.d("MainApplication", "addSessionId", "cur Id:" + mSessionId);
//    }
    
//    public void setMode(int mode) {
//        mMode = mode;
//    }
//    
//    public int getMode() {
//        return mMode;
//    }
}
