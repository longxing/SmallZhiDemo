package com.iii360.test;

import com.iii360.base.umeng.UmengUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MainService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		UmengUtil.onEvent(this, "MainServiceOnCreate", null);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		UmengUtil.onEvent(this, "MainServiceOnDestroy", null);
	}
}
