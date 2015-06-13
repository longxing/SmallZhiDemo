package com.iii360.base.common.utl;

import com.iii360.base.common.utl.LogManager;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * 音量逐渐增大减小效果
 * 
 * @author Administrator
 * 
 */
public class ControllerVolume {
	private AudioManager mAudioManager;
	private final static int MUSIC_VOLUME_TIME = 80;
	private int mCurrentVolume;
	private int mCurrentVolumeTemp;
	private boolean mIsIncrase = false;
	private int MaxVolume = 8;
	private BaseContext mBaseContext;

	/**
	 * 控制状态
	 */
	private boolean mStatus = false;
    /**
     * 控制开关
     */
    private boolean mSwitch = true;
	
	public ControllerVolume(Context context) {
		// TODO Auto-generated constructor stub
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		MaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mBaseContext = new BaseContext(context);
	}

	public int getCurrentVloume() {
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mBaseContext.setGlobalInteger(KeyList.PKEY_CONTROL_VOLUME_VALUE, current);
		return current;
	}

	public void setVloume(int volume) {
		LogManager.e("volume: " + volume);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
	}

	private Thread mChangeVolumeThread;

	/**
	 * 声音逐渐增加或减小,再还原
	 * 
	 * @param isIncrease
	 */
	public void autoIncreaseVloume(final boolean isIncrease) {
		mIsIncrase = isIncrease;
		mBaseContext.setGlobalBoolean(KeyList.PKEY_CONTROL_VOLUME_SWITCH, true);
		
		if (!mStatus) {
			mCurrentVolume = getCurrentVloume();
			mCurrentVolumeTemp = mCurrentVolume - 1;
			if (mIsIncrase) {
				mCurrentVolumeTemp = 0;
				setVloume(mCurrentVolumeTemp);
				if (Decreaselistener != null) {
					Decreaselistener.onIncreaseVolume();
				}
			}
		}
		if (mChangeVolumeThread != null) {
			try {
				mChangeVolumeThread.stop();
			} catch (Exception e) {
				// TODO: handle exception
				LogManager.printStackTrace();
			}
		}

//		mChangeVolumeThread = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
				// TODO Auto-generated method stub
				mStatus = true;
				mBaseContext.setGlobalBoolean(KeyList.PKEY_CONTROL_VOLUME_FLAG, true);
				while (mCurrentVolumeTemp > -1 && mCurrentVolumeTemp < MaxVolume + 1) {
				    
				    mSwitch = mBaseContext.getGlobalBoolean(KeyList.PKEY_CONTROL_VOLUME_SWITCH, true);
				    if(!mSwitch){
				        LogManager.w("!mSwitch= break");
				        break;
				    }
				    
					if (mCurrentVolumeTemp == mCurrentVolume) {
						break;
					}
					if (mIsIncrase) {
						mCurrentVolumeTemp++;
					} else {
						mCurrentVolumeTemp--;
					}

					setVloume(mCurrentVolumeTemp);

					try {
						Thread.sleep(MUSIC_VOLUME_TIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					LogManager.e("mCurrentVolumeTemp" + mCurrentVolumeTemp);
				}
				mStatus = false;
				restoreVolume();
				mBaseContext.setGlobalBoolean(KeyList.PKEY_CONTROL_VOLUME_FLAG, false);
//			}
//		});
//		mChangeVolumeThread.start();

	}

	/**
	 * 还原原本声音音量
	 */
	public void restoreVolume() {
		setVloume(mCurrentVolume);
		if (Decreaselistener != null) {
			Decreaselistener.onDecreaseVolume();
		}
	}

	private ChanageVloumeListener Decreaselistener;

	public void setListener(ChanageVloumeListener listener) {
		this.Decreaselistener = listener;
	}

	public interface ChanageVloumeListener {
		/**
		 * 声音逐渐变小后的回调
		 */
		public void onDecreaseVolume();

		/**
		 * 声音开始变大
		 */
		public void onIncreaseVolume();
	}
}
