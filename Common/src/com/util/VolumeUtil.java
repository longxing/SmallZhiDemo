package com.util;

import android.content.Context;
import android.media.AudioManager;

import com.iii360.base.common.utl.LogManager;

public class VolumeUtil {
	/**
	 * 渐变音量，不能同时渐变两次增加，或者连续两次降低
	 */
	private static boolean isIncreaseVolume = false;
	private static boolean isDecreaseVolume = false;

	private static int currentVolume = 0;
	private static Object obj = new Object();

	public static boolean isIncreaseVolume() {
		return isIncreaseVolume;
	}

	public static void setIncreaseVolume(boolean isIncreaseVolume) {
		VolumeUtil.isIncreaseVolume = isIncreaseVolume;
	}

	public static boolean isDecreaseVolume() {
		return isDecreaseVolume;
	}

	public static void setDecreaseVolume(boolean isDecreaseVolume) {
		VolumeUtil.isDecreaseVolume = isDecreaseVolume;
	}

	/**
	 * 通过的音频管理器，渐变音量到指定值
	 * 
	 * @param manager
	 * @param current
	 */
	private static void decreaseVolume(final AudioManager manager, final int current) {
		LogManager.i("VolumeUtil", "decreaseVolume ----currentValue:" + current);
		for (int i = current; i >= 1; i--) {
			try {
				Thread.sleep(100);
				setIncreaseVolume(false);
				setDecreaseVolume(true);
				manager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);// 15刻度
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 通过的音频管理器，渐变音量到指定值
	 * 
	 * @param manager
	 * @param current
	 */
	private static void increaseVolume(final AudioManager manager, final int current) {
		LogManager.i("VolumeUtil", "increaseVolume ----currentValue:" + current);
		for (int i = 1; i <= current; i++) {
			try {
				Thread.sleep(100);
				setIncreaseVolume(true);
				setDecreaseVolume(false);
				manager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);// 15刻度
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// 自动渐变音量
	public static void autoIncreaseVolume(final Context context) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				synchronized (obj) {
					LogManager.i("VolumeUtil", "autoIncreaseVolume ----isIncreaseVolume  statue:" + isIncreaseVolume);
					if (!isIncreaseVolume) {
						AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
						increaseVolume(manager, currentVolume);
					}
				}
			}

		}.start();
	}

	// 自动渐变音量
	public static void autoDecreaseVolume(final Context context) {
		new Thread() {
			public void run() {
				synchronized (obj) {
					LogManager.i("VolumeUtil", "autoDecreaseVolume ----isDecreaseVolume  statue:" + isDecreaseVolume);
					if (!isDecreaseVolume) {
						AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
						currentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
						decreaseVolume(manager, currentVolume);
					}
				}
			};
		}.start();

	}

}
