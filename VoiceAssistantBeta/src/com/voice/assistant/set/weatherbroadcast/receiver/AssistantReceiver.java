package com.voice.assistant.set.weatherbroadcast.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.sup.common.utl.LogManager;
import com.voice.assistant.main.KeyList;

public class AssistantReceiver extends BroadcastReceiver {

	private BaseContext mBaseContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		mBaseContext = new BaseContext(context);

		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {

			try {
				String data = intent.getDataString();
				String newPackageName = data.substring(8);

				if (newPackageName.equals(KeyList.PKEY_TTS_SHENGDATINGTING_PACKAGENAME)) {
					mBaseContext.setPrefBoolean(KeyList.PKEY_TTS_IS_INSTALL_SHENGDA, true);
					mBaseContext.setPrefInteger(KeyList.PKEY_TTS_PLAY_CHOOSE, KeyList.TTS_PLAYER_TYPE_SHENGDA);
					Intent i = new Intent();
					i.setAction(KeyList.AKEY_CHANGE_TTS_ENGINE);
					context.sendBroadcast(i);

				} else if (newPackageName.equals(KeyList.PKEY_TTS_XUNFEI_PACKAGENAME)) {
					mBaseContext.setPrefBoolean(KeyList.PKEY_TTS_IS_INSTALL_XUNFEI, true);
					mBaseContext.setPrefInteger(KeyList.PKEY_TTS_PLAY_CHOOSE, KeyList.TTS_PLAYER_TYPE_XUNFEI);
					Intent i = new Intent();
					i.setAction(KeyList.AKEY_CHANGE_TTS_ENGINE);
					context.sendBroadcast(i);

				} else if (newPackageName.equals("com.google.android.voicesearch")) {
				}
			} catch (Exception e) {
				LogManager.printStackTrace(e, "AssistantReceiver", "onReceive");
			}
		} else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			try {
				String data = intent.getDataString();
				String packageName = data.substring(8);
				if (packageName.equals(KeyList.PKEY_TTS_SHENGDATINGTING_PACKAGENAME)) {
					mBaseContext.setPrefBoolean(KeyList.PKEY_TTS_IS_INSTALL_SHENGDA, false);
					if (mBaseContext.getPrefString(KeyList.PKEY_TTS_PLAY_CHOOSE, "0").equals(
							KeyList.TTS_PLAYER_TYPE_SHENGDA + "")) {
						mBaseContext.setPrefString(KeyList.PKEY_TTS_PLAY_CHOOSE, "0");
					}
					Intent i = new Intent();
					i.setAction(KeyList.AKEY_CHANGE_TTS_ENGINE);
					context.sendBroadcast(i);
				} else if (packageName.equals(KeyList.PKEY_TTS_XUNFEI_PACKAGENAME)) {
					mBaseContext.setPrefBoolean(KeyList.PKEY_TTS_IS_INSTALL_XUNFEI, false);
					if (mBaseContext.getPrefString(KeyList.PKEY_TTS_PLAY_CHOOSE, "0").equals(
							KeyList.TTS_PLAYER_TYPE_XUNFEI + "")
							|| mBaseContext.getPrefString(KeyList.PKEY_TTS_PLAY_CHOOSE, "0").equals(
									KeyList.TTS_PLAYER_TYPE_XUNFEI_NET + "")) {
						mBaseContext.setPrefString(KeyList.PKEY_TTS_PLAY_CHOOSE, "0");
					}
					Intent i = new Intent();
					i.setAction(KeyList.AKEY_CHANGE_TTS_ENGINE);
					context.sendBroadcast(i);
				} else if (packageName.equals("com.google.android.voicesearch")) {
				}
			} catch (Exception e) {
				LogManager.printStackTrace(e, "AssistantReceiver", "onReceive");
			}

		}

	}

}
