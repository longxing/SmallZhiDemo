package com.iii360.sup.common.utl;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ContentResolver;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;
/**
 * 
 * @author Jerome.Hu.
 * 屏幕控制。包括
 * 1.屏幕亮度调整
 * 2.去除锁屏。
 *
 */
public class ScreenManager {
	/**
	 * 
	 */
	private static final int MAX_BRIGHTNESS = 255;
	private static final int DEFAULT_BRIGHTNESS = 30;
	private static final int DEFAULT_STEP = 10;
	private Context mContext;

	private KeyguardLock mKeyguardLock;
	private WakeLock mWakelock;

	public ScreenManager(Context context) {
		mContext = context;
	}
	/**
	 * 
	 * @param brightness 屏幕亮度。
	 * @throws SettingNotFoundException
	 */
	private void adjustScreenBrightness(int brightness)
			throws SettingNotFoundException {
		final ContentResolver contentResolver = mContext.getContentResolver();
		final int brightnessMode = Settings.System.getInt(contentResolver,
				Settings.System.SCREEN_BRIGHTNESS_MODE);
		if ( brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ) {
			Settings.System.putInt(contentResolver,
					Settings.System.SCREEN_BRIGHTNESS_MODE,
					Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		}
		if (mContext instanceof Activity) {
			final WindowManager.LayoutParams layoutParams = ((Activity) mContext)
					.getWindow().getAttributes();
			layoutParams.screenBrightness = (float) brightness / MAX_BRIGHTNESS;
			((Activity) mContext).getWindow().setAttributes(layoutParams);
		}
		android.provider.Settings.System.putInt(contentResolver,
				android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
		LogManager.d("now brightNess is " + brightness);
	}

	private int getScreenBrightness() throws SettingNotFoundException {
		final ContentResolver contentResolver = mContext.getContentResolver();
		final int brightnessMode = Settings.System.getInt(contentResolver,
				Settings.System.SCREEN_BRIGHTNESS_MODE);
		if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
			return DEFAULT_BRIGHTNESS;
		}
		int brightness = 0;
		try {
			brightness = Settings.System.getInt(contentResolver,
					Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return brightness;
	}

	public boolean riseBrightness() throws SettingNotFoundException {
		int brightness = getScreenBrightness();

		if (brightness == MAX_BRIGHTNESS) {
			return false;
		}
		brightness += DEFAULT_STEP;
		if (brightness >= DEFAULT_BRIGHTNESS) {
			brightness = MAX_BRIGHTNESS;
		}
		adjustScreenBrightness(MAX_BRIGHTNESS);
		return true;
	}

	public boolean reduceBrightness() throws SettingNotFoundException {
		int brightness = getScreenBrightness();

		if (brightness == DEFAULT_BRIGHTNESS) {
			return false;
		}
		brightness -= DEFAULT_STEP;
		if (brightness >= DEFAULT_BRIGHTNESS) {
			brightness = MAX_BRIGHTNESS;
		}
		adjustScreenBrightness(MAX_BRIGHTNESS);
		return true;
	}

	public void unLockAndLightScreen() {
		final PowerManager pm = (PowerManager) mContext
				.getSystemService(Context.POWER_SERVICE);
		if (!pm.isScreenOn()) {
			mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
			mWakelock.acquire();
		}
		final KeyguardManager mKeyguardManager = (KeyguardManager) mContext
				.getSystemService(Context.KEYGUARD_SERVICE);
		if (mKeyguardLock == null) {
			mKeyguardLock = mKeyguardManager.newKeyguardLock("");
			mKeyguardLock.disableKeyguard();
		}
	}

	public void unLignthAndLock() {
		if (mKeyguardLock != null) {
			mKeyguardLock.reenableKeyguard();
			mKeyguardLock = null;
		}
		if (mWakelock != null) {
			mWakelock.release();
			mWakelock = null;
		}
	}

}
