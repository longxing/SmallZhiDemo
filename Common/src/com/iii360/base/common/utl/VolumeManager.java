package com.iii360.base.common.utl;

import android.content.Context;
import android.media.AudioManager;

public class VolumeManager {
	private static final int STEP = 2;
	private AudioManager mAudioManager;

	public VolumeManager(Context context) {
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}

	/**
	 * 
	 * @param type
	 *            type传以下值： AudioManager.STREAM_MUSIC
	 *            AudioManager.STREAM_VOICE_CALL AudioManager.STREAM_RING
	 * @return true: 设置成功 false: 设置失败
	 */
	public boolean increaseVolume(int type) {
		final int maxVolume = mAudioManager.getStreamMaxVolume(type);
		int volue = getVolume(type);
		if (volue >= maxVolume) {
			return false;
		} else {
			volue += STEP;
			if (volue > maxVolume) {
				volue = maxVolume;
			}

			mAudioManager.setStreamVolume(type, volue, 0);
			return true;
		}
	}

	public boolean deCreaseVolume(int type) {
		int volue = getVolume(type);
		if (volue <= 0) {
			return false;
		} else {
			volue -= STEP;
			if (volue < 0) {
				volue = 0;
			}
			mAudioManager.setStreamVolume(type, volue, 0);
			return true;
		}
	}

	private int getVolume(int type) {
		final int volume = mAudioManager.getStreamVolume(type);
		return volume;
	}

}
