package com.voice.assistant.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.iii360.base.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;
import com.voice.assistant.main.MyApplication;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		LogManager.d("service launch");
		
		new SuperBaseContext(context).setPrefBoolean("PKEY_IS_UNCAUGHT_EXCEPTION", false);
		
		LogManager.d("BootBroadcastReceiver:" + MyApplication.SystemDoingCurrentTime + new SimpleDateFormat(MyApplication.Date_Fomort).format(new Date()));
		Intent intent = new Intent(context, com.voice.assistant.main.AssistantMainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		context.startActivity(intent);
	}
}
