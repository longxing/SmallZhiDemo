package com.iii360.base.common.utl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.PrivateCredentialPermission;

import android.os.Bundle;

import com.iii360.base.inf.IGlobalValueOperation;
import com.iii360.base.inf.IPrefrenceOperation;
import com.iii360.base.umeng.UmengActivity;


/**
 * Activity 的基类。所有Activity必须实现之
 * 
 * @author Jerome.Hu
 * 
 */
public class BaseActivity extends UmengActivity implements IGlobalValueOperation, IPrefrenceOperation {
	protected BaseContext mBaseContext;
	public static String TAG = "BaseActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mBaseContext = new BaseContext(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		WakeupUtil.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		WakeupUtil.onPause(this);
	}

	@Override
	public boolean getPrefBoolean(String key) {
		// TODO Auto-generated method stub
		return mBaseContext.getPrefBoolean(key);
	}

	@Override
	public boolean getPrefBoolean(String key, boolean defVal) {
		// TODO Auto-generated method stub
		return mBaseContext.getPrefBoolean(key, defVal);
	}

	@Override
	public void setPrefBoolean(String key, boolean value) {
		// TODO Auto-generated method stub
		mBaseContext.setPrefBoolean(key, value);
	}

	@Override
	public String getPrefString(String key) {
		// TODO Auto-generated method stub
		return mBaseContext.getPrefString(key);
	}

	@Override
	public String getPrefString(String key, String defVal) {
		// TODO Auto-generated method stub
		return mBaseContext.getPrefString(key, defVal);
	}

	@Override
	public void setPrefInteger(String key, int value) {
		// TODO Auto-generated method stub
		mBaseContext.setPrefInteger(key, value);
	}

	@Override
	public int getPrefInteger(String key) {
		// TODO Auto-generated method stub
		return mBaseContext.getPrefInteger(key);
	}

	@Override
	public int getPrefInteger(String key, int defVal) {
		// TODO Auto-generated method stub
		return mBaseContext.getPrefInteger(key, defVal);
	}

	@Override
	public void setPrefString(String key, String value) {
		// TODO Auto-generated method stub
		mBaseContext.setPrefString(key, value);
	}

	@Override
	public long getPrefLong(String key) {
		// TODO Auto-generated method stub
		return mBaseContext.getPrefLong(key);
	}

	@Override
	public long getPrefLong(String key, long defVal) {
		// TODO Auto-generated method stub
		return mBaseContext.getPrefLong(key, defVal);
	}

	@Override
	public void setPrefLong(String key, long value) {
		// TODO Auto-generated method stub
		mBaseContext.setPrefLong(key, value);
	}

	@Override
	public float getPrefFloat(String key) {
		// TODO Auto-generated method stub
		return mBaseContext.getPrefFloat(key);
	}

	@Override
	public float getPrefFloat(String key, float defVal) {
		// TODO Auto-generated method stub
		return mBaseContext.getPrefFloat(key, defVal);
	}

	@Override
	public void setPrefFloat(String key, float value) {
		// TODO Auto-generated method stub
		mBaseContext.setPrefFloat(key, value);
	}

	@Override
	public void setGlobalBoolean(String key, boolean value) {
		// TODO Auto-generated method stub
		mBaseContext.setGlobalBoolean(key, value);
	}

	@Override
	public void setGlobalString(String key, String value) {
		// TODO Auto-generated method stub
		mBaseContext.setGlobalString(key, value);
	}

	@Override
	public void setGlobalInteger(String key, Integer value) {
		// TODO Auto-generated method stub
		mBaseContext.setGlobalInteger(key, value);
	}

	@Override
	public void setGlobalLong(String key, Long value) {
		// TODO Auto-generated method stub
		mBaseContext.setGlobalLong(key, value);
	}

	@Override
	public void setGlobalObject(String key, Object value) {
		// TODO Auto-generated method stub
		mBaseContext.setGlobalObject(key, value);
	}

	@Override
	public boolean getGlobalBoolean(String key, boolean defVal) {
		// TODO Auto-generated method stub
		return mBaseContext.getGlobalBoolean(key, defVal);
	}

	@Override
	public String getGlobalString(String key, String defVal) {
		// TODO Auto-generated method stub
		return mBaseContext.getGlobalString(key, defVal);
	}

	@Override
	public int getGlobalInteger(String key, Integer defVal) {
		// TODO Auto-generated method stub
		return mBaseContext.getGlobalInteger(key, defVal);
	}

	@Override
	public long getGlobalLong(String key, Long defVal) {
		// TODO Auto-generated method stub
		return mBaseContext.getGlobalLong(key, defVal);
	}

	@Override
	public Object getGlobalObject(String key, Object defVal) {
		// TODO Auto-generated method stub
		return mBaseContext.getGlobalObject(key, defVal);
	}

	@Override
	public boolean getGlobalBoolean(String key) {
		// TODO Auto-generated method stub
		return mBaseContext.getGlobalBoolean(key);
	}

	@Override
	public float getGlobalFloat(String key) {
		// TODO Auto-generated method stub
		return mBaseContext.getGlobalFloat(key);
	}

	@Override
	public String getGlobalString(String key) {
		// TODO Auto-generated method stub
		return mBaseContext.getGlobalString(key);
	}

	@Override
	public int getGlobalInteger(String key) {
		// TODO Auto-generated method stub
		return mBaseContext.getGlobalInteger(key);
	}

	@Override
	public long getGlobalLong(String key) {
		// TODO Auto-generated method stub
		return mBaseContext.getGlobalLong(key);
	}

	@Override
	public Object getGlobalObject(String key) {
		// TODO Auto-generated method stub
		return mBaseContext.getGlobalObject(key);
	}
}
