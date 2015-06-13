package com.voice.upgrade.receiver;

import com.iii360.base.common.utl.LogManager;
import com.voice.upgrade.service.UpgradeService;
import com.voice.upgrade.util.TTSUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShutdownBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		LogManager.d("shutdown service");
		TTSUtil tts = TTSUtil.getInstance(context);
		// 是否在自动更新
		if (UpgradeService.isInstalling()) {
			tts.play("正在更新系统，请不要断开电源");
		} else {
			tts.play("正在关机");
		}
	}

}
