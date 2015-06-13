package com.iii.wifiserver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.BatteryManager;

import com.iii.wifi.util.KeyList;
import com.iii.wifiserver.R;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;

public class BatteryBroadcastReciver extends BroadcastReceiver {

	private boolean flag1 = true;
	private boolean flag2 = true;
	private boolean isCharg = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		LogManager.d("BatteryBroadcastReciver", "Battery level changed intent:"+intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
			int level = intent.getIntExtra("level", 0);
			int total = intent.getIntExtra("scale", 100);
			// 设置电量信息配置参数
			new SuperBaseContext(context).setPrefString(KeyList.PKEY_BATTERY_LEVEL, (level * 100) / total + "");
			int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
			float per = (float) level / total;
			if (!isCharging) {
				isCharg = true;
				if (per <= 0.2 && per > 0.15 && flag1) {
					if (KeyList.TTSUtil.isWorking()) {
						KeyList.TTSUtil.playContent("电量不足百分之二十请及时充电");
					}
					flag1 = false;
				} else if (per <= 0.15 && flag2) {
					if (KeyList.TTSUtil.isWorking()) {
						KeyList.TTSUtil.playContent("电量不足百分之十五请及时充电");
					}
					flag2 = false;
				}
			} else {
				if (isCharg && KeyList.TTSUtil.isWorking()) {
					//
					// SoundPool mSoundpool = new SoundPool(4,
					// AudioManager.STREAM_ALARM, 100);
					// AudioManager mgr = (AudioManager)
					// context.getSystemService(Context.AUDIO_SERVICE);
					// float streamVolumeCurrent =
					// mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
					// float streamVolumeMax =
					// mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
					// float volume = streamVolumeCurrent / streamVolumeMax;
					// mSoundpool.play(mSoundpool.load(context,
					// R.raw.yiya_voice_start, 1), volume, volume, 1, 0, 1f);

					MediaPlayer mMediaPlayer = MediaPlayer.create(context, R.raw.yiya_voice_start);
					mMediaPlayer.start();

					// KeyList.TTSUtil.playContent("正在充电");
					isCharg = false;
				}
				flag1 = true;
				flag2 = true;
			}
		}
	}
}
