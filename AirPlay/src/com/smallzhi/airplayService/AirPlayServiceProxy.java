package com.smallzhi.airplayService;


import com.iii360.sup.common.utl.LogManager;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

public class AirPlayServiceProxy {
	protected AirPlayService mUpnpService;
	private Context mycontext;
	private static final String TAG = "SmallZhi AirPlayProxy";
	

	
	public AirPlayServiceProxy(Context context) {
		// TODO Auto-generated constructor stub
		this.mycontext = context;
	}
	
	public void StartService() {
		LogManager.i(TAG,"StartService  start...");
		Intent intent = new Intent(mycontext, AirPlayService.class);
		mycontext.startService(intent);
		LogManager.i(TAG,"StartService  end...");
	}
	
	public void StopService() {
		LogManager.i(TAG,"StopService start...");
		LogManager.i(TAG,"StopService end...");
	}
}
