package com.iii360.base.common.utl;


import android.content.Context;
import android.content.Intent;

public class WakeupUtil  {
	public static final String PKEY_ASSISTANT_WAKE_UP = "PKEY_ASSISTANT_WAKE_UP";
	public static final String PKEY_ASSISTANT_WAKE_UP_ALWAYS_RUN = "PKEY_ASSISTANT_WAKE_UP_ALWAYS_RUN";
	
	public static final String PAUSE_CHECK = "CHECK_WAKEUP_STATE_RECEIVER_PAUSE_CHECK";
	public static final String RESUME_CHECK = "CHECK_WAKEUP_STATE_RECEIVER_RESUME_CHECK";
	public static  void onResume(Context context) {
		if( getPrefWakeupState(context) == 0 ) {
			return ;
		}
		sendBroadcast(context, RESUME_CHECK);
	}
	public static  void onPause(Context context) {
		if( getPrefWakeupState(context) == 0 ) {
			return ;
		}
		sendBroadcast(context, PAUSE_CHECK);
	}
	
	public static  void sendBroadcast(Context context,String action ) {
		Intent intent = new Intent();
		intent.setAction(action);
		context.sendBroadcast(intent);
	}
	
	public static int getPrefWakeupState(Context context) {
		BaseContext mBaseContext = new BaseContext(context);
		final boolean setWakeupTrue = mBaseContext.getPrefBoolean(PKEY_ASSISTANT_WAKE_UP, false);
        final boolean isAlwaysRunInBack = mBaseContext.getPrefBoolean(PKEY_ASSISTANT_WAKE_UP_ALWAYS_RUN, false);
        int selectWhich = 0;
        if (setWakeupTrue) {
            if (!isAlwaysRunInBack) {
                selectWhich = 1;
            } else {
                selectWhich = 2;
            }
        }
        return selectWhich;
	}
}
