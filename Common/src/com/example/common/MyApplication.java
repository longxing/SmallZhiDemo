package com.example.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.graphics.drawable.Drawable;

import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.IAppContainer;
import com.iii360.base.inf.IContactsContainer;
import com.iii360.base.umeng.IUmengConfigurationContainer;

public class MyApplication extends Application implements IGloableHeap, IAppContainer, IContactsContainer,
		IUmengConfigurationContainer {

	protected Map<String, Boolean> mGlobalBooleanMap = new HashMap<String, Boolean>();
	protected Map<String, Integer> mGlobalIntMap = new HashMap<String, Integer>();
	protected Map<String, String> mGlobalStringMap = new HashMap<String, String>();
	protected Map<String, Long> mGlobalLongMap = new HashMap<String, Long>();
	protected Map<String, Object> mGlobalObjectMap = new HashMap<String, Object>();
	protected Map<String, Float> mGlobalFloatMap = new HashMap<String, Float>();
	protected Map<String, Drawable> drawable = new HashMap<String, Drawable>();
	protected Map<String, String> mUmengConfiguration = new HashMap<String, String>();
	
	protected static String DB_NAME = "VA";
//	protected static String DB_PATH = "/data/data/com.voice.assistant.main/databases/";
	protected static String DB_PATH = "/mnt/sdcard/databases/";
	protected List<Object> mAppList = new ArrayList<Object>();
	protected Map<String, Object> mMapNumber = new HashMap<String, Object>();
	protected List<Object> mListName = new ArrayList<Object>();
	protected BasicServiceUnion mUnion;
	
	
	public static String SystemDoingCurrentTime = "method execute current time:";
	public static String Date_Fomort = "yyyy:MM:dd HH:mm:ss.sss";
	
	public String getStr() {
		return "";
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public Map<String, String> getGloabalString() {
		// TODO Auto-generated method stub
		return mGlobalStringMap;
	}

	@Override
	public Map<String, Object> getGlobalObjectMap() {
		// TODO Auto-generated method stub
		return mGlobalObjectMap;
	}

	@Override
	public Map<String, Integer> getGlobalIntegerMap() {
		// TODO Auto-generated method stub
		return mGlobalIntMap;
	}

	@Override
	public Map<String, Float> getGlobalFloatMap() {
		// TODO Auto-generated method stub
		return mGlobalFloatMap;
	}

	@Override
	public Map<String, Long> getGlobalLongMap() {
		// TODO Auto-generated method stub
		return mGlobalLongMap;
	}

	@Override
	public Map<String, Boolean> getGlobalBooleanMap() {
		// TODO Auto-generated method stub
		return mGlobalBooleanMap;
	}

	@Override
	public Map<String, Object> getContactMap() {
		// TODO Auto-generated method stub
		return mMapNumber;
	}

	@Override
	public List<Object> getContactsNameList() {
		// TODO Auto-generated method stub
		return mListName;
	}

	@Override
	public void setContactMap(Map<String, Object> arg0) {
		// TODO Auto-generated method stub
		this.mMapNumber = arg0;

	}

	@Override
	public void setContactNameList(List<Object> arg0) {
		// TODO Auto-generated method stub
		this.mListName = arg0;
	}

	@Override
	public List<Object> getAppList() {
		// TODO Auto-generated method stub
		return mAppList;
	}

	@Override
	public void setAppList(List<Object> arg0) {
		// TODO Auto-generated method stub
		this.mAppList = arg0;

	}

	@Override
	public Map<String, String> getConfiguration() {
		// TODO Auto-generated method stub
		return mUmengConfiguration;
	}

	@Override
	public void setAppDrawable(HashMap<String, Drawable> map) {
		// TODO Auto-generated method stub
		this.drawable = map;
	}

	@Override
	public HashMap<String, Drawable> getAppDrawable() {
		// TODO Auto-generated method stub
		return (HashMap<String, Drawable>) drawable;
	}
	
	public BasicServiceUnion getUnion() {
		return mUnion;
	}

}
