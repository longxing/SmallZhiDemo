package com.iii360.sup.common.base;

import java.util.HashMap;
import java.util.Map;

import com.iii360.sup.common.utl.IGloableHeap;

import android.app.Application;

public class MyApplication extends Application implements IGloableHeap {

	private Map<String, Boolean> mGlobalBooleanMap = new HashMap<String, Boolean>();
	private Map<String, Integer> mGlobalIntMap = new HashMap<String, Integer>();
	private Map<String, String> mGlobalStringMap = new HashMap<String, String>();
	private Map<String, Long> mGlobalLongMap = new HashMap<String, Long>();
	private Map<String, Object> mGlobalObjectMap = new HashMap<String, Object>();
	private Map<String, Float> mGlobalFloatMap = new HashMap<String, Float>();
	
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
	
	

}
