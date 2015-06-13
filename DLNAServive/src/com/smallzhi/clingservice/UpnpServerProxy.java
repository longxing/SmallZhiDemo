package com.smallzhi.clingservice;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.fourthline.cling.UpnpService;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.iii360.sup.common.utl.LogManager;
import com.smallzhi.clingservice.dlna.service.AndroidUpnpInitService;
import com.smallzhi.clingservice.dlna.service.IService;


public class UpnpServerProxy implements IService {
	protected UpnpService mUpnpService;
	private Context mycontext;
	private static final String TAG = "SmallZhiDLNA";

	private ActivityManager mActivityManager;
	private Timer timer = new Timer();
	private int delayTime = 30 * 1000;

	// private DlanUpnpBinder mDlanUpnpBinder;

	public UpnpServerProxy(Context context) {
		// TODO Auto-generated constructor stub
		this.mycontext = context;
		mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	}

	@Override
	public void bindService() {
		LogManager.i(TAG,"UpnpServerProxy bind start...");
		Intent intent = new Intent(mycontext, AndroidUpnpInitService.class);
		mycontext.startService(intent);
//		setTimerStartService();
		LogManager.i(TAG,"UpnpServerProxy bind end...");
	}

	private void setTimerStartService() {
		if (timer == null) {
			timer = new Timer();
		}
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!isServiceRunning(mycontext, "com.smallzhi.clingservice.dlna.service.AndroidUpnpInitService")) {
					mActivityManager.killBackgroundProcesses("com.smallzhi.clingservice.dlna.service.AndroidUpnpInitService");
					LogManager.i(TAG,"dlan start service ....");
					Intent intent = new Intent(mycontext, AndroidUpnpInitService.class);
					mycontext.startService(intent);
					delayTime = 5 * 1000;
				} else {
					LogManager.i(TAG,"dlan service running ....");
				}
			}

		}, delayTime, 10000);
	}

	/*
	 * 用来判断服务是否运行.
	 * 
	 * @param context
	 * 
	 * @param className 判断的服务名字
	 * 
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(50);
		if (serviceList != null && serviceList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	@Override
	public void unBindService() {

		if (mUpnpService != null) {
			mUpnpService.getRegistry().removeAllLocalDevices();
			mUpnpService.shutdown();
		}
		// context.unbindService(serviceConnection);
	}

}
