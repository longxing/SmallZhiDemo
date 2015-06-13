package com.smallzhi.clingservice.util;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.smallzhi.clingservice.media.MyMediaPlayerService;

public class DlnaReceiver extends BroadcastReceiver {

	private ActivityManager mActivityManager;
	private static final String TAG = "SmallZhiDLNA";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		 if (Constants.IKEY_VOLUME_CHANGED.equals(action)){
			 MyMediaPlayerService.getInstance(context).IPushVolomeChange();;
		 }
		
	}

}

