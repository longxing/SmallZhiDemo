package com.voice.assistant.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.external.wakeup.HandleWakeup;
import com.iii360.voiceassistant.aidl.IWakeUpController;
import com.voice.assistant.main.KeyList;
import com.voice.assistant.main.MyApplication;

public class ControlWeakupService extends Service {
	private BaseContext mBaseContext;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		MyApplication app = (MyApplication) getApplication();
		mBaseContext = app.getUnion().getBaseContext();
//		android.util.Log.e("hefeng", "start  ControlWeakupService");
		LogManager.d("hefeng","start  ControlWeakupService");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return contoller;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	private IWakeUpController.Stub contoller = new IWakeUpController.Stub() {

		@Override
		public void open() throws RemoteException {
			// TODO Auto-generated method stub
//			Log.w("hefeng", "open weakup");
			LogManager.d("hefeng", "open weakup");
			mBaseContext.setPrefBoolean(KeyList.PKEY_IS_VOICE_SOUND_WAVE, false);
			HandleWakeup.startWakeup(ControlWeakupService.this);
		}

		@Override
		public void close() throws RemoteException {
			// TODO Auto-generated method stub
//			Log.w("hefeng", "close weakup");
			LogManager.d("hefeng", "close weakup");
			mBaseContext.setPrefBoolean(KeyList.PKEY_IS_VOICE_SOUND_WAVE, true);
			HandleWakeup.stopWakeup(ControlWeakupService.this);
		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		startService(new Intent(this, ControlWeakupService.class));
	}

}
