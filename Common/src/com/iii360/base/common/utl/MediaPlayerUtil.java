package com.iii360.base.common.utl;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * 音量逐渐增大减小效果
 * 
 * @author Administrator
 * 
 */
public class MediaPlayerUtil {
	private final static long MUSIC_VOLUME_TIME = 20;// 帧数
	private final static long POWER_VOLUMN_TIME = 1;// 加速度, <0为加速;>0为减速
	private final static float CHANGE_VOLUME_SPEED = 0.04f;// 阶梯值

	public static void autoIncreaseVloume(Context context,
			MediaPlayer mediaPlayer, boolean isIncrease) {
		BaseContext mBaseContext = new BaseContext(context);
		mBaseContext.setGlobalBoolean(KeyList.PKEY_CONTROL_VOLUME_SWITCH, true);

		float minVolume = 0.0f;
		float maxVolume = 1.0f;
		float currentVolume = maxVolume;
		if (isIncrease) {
			currentVolume = minVolume;
		} else {
			currentVolume = maxVolume;
		}
		mediaPlayer.setVolume(currentVolume, currentVolume);
		
		long volumeTime = MUSIC_VOLUME_TIME;
		for (;;) {
			volumeTime = volumeTime + POWER_VOLUMN_TIME;
			if (isIncrease) {
				currentVolume = currentVolume + CHANGE_VOLUME_SPEED;
			} else {
				currentVolume = currentVolume - CHANGE_VOLUME_SPEED;
			}
			if (currentVolume < minVolume || currentVolume > maxVolume) {
				break;
			}
			mediaPlayer.setVolume(currentVolume, currentVolume);
			try {
				Thread.sleep(volumeTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LogManager.e("currentVolume: " + currentVolume);
		}

	}

}
