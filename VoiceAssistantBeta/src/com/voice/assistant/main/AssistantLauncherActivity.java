package com.voice.assistant.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.iii360.base.common.utl.BaseActivity;
import com.iii360.base.common.utl.LogManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class AssistantLauncherActivity extends BaseActivity {
	private static String TAG ="AssistantLauncherActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogManager.d(TAG,"onCreate:" + MyApplication.SystemDoingCurrentTime + new SimpleDateFormat(MyApplication.Date_Fomort).format(new Date()));
		mBaseContext.setPrefBoolean(KeyList.PKEY_ASSISTANT_WAKE_UP, true);
		mBaseContext.setPrefBoolean(KeyList.PKEY_ASSISTANT_WAKE_UP_ALWAYS_RUN, true);

		mBaseContext.setPrefBoolean(KeyList.PKEY_IS_NEED_SCREEN_OFF_OPEN_WAKE_UP, true);
		mBaseContext.setPrefBoolean(KeyList.PKEY_IS_NEED_WIFI_THEN_OPEN_WAKE_UP, true);
		mBaseContext.setGlobalBoolean(KeyList.PKEY_NEED_START_IMEDIATELEY_AFTER_RECOGNISE, false);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		LogManager.d(TAG,"AssistantLauncherActivity startActivity:" + MyApplication.SystemDoingCurrentTime + new SimpleDateFormat(MyApplication.Date_Fomort).format(new Date()));
		Intent intent = new Intent(AssistantLauncherActivity.this, AssistantMainActivity.class);
		startActivity(intent);
		finish();
	}
}
