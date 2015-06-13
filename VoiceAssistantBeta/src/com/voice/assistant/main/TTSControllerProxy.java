package com.voice.assistant.main;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

//import com.example.airplay.util.AirplayVolume;
import com.iii360.base.common.utl.AbstractReceiver;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.common.utl.MainConfigs;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.smallzhi.TTS.Engine.TTSPlayerFactory;
import com.smallzhi.TTS.Main.TTSSameple;
import com.voice.assistant.utl.AirPlayMusicController;
import com.voice.assistant.utl.VolumeManager;

/**
 * TTS控制类
 * 
 * @author Peter
 * @data 2015年4月22日上午10:56:02
 */
public class TTSControllerProxy extends AbstractReceiver implements ITTSController {

	private static final String Tag = "TTSControllerProxy";

	private ITTSController mRealTTSController = null;
	private ITTSStateListener mTTSStateListener;

	private IRecogniseSystem mRecSystem;
	private Context mContext;
	private BaseContext mBaseContext;
	public static boolean mIsPlaying = false;
	private VolumeManager mVolumeManager;
	private int mAlarmVolume;
//	private AirplayVolume mAirplayVolume;
	private AirPlayMusicController mAirPlayMusicController;
	private int mAirplayVol;
	private TTSSameple ttsSameple = null;

	private ITTSStateListener mMasterTtsStateListener = new ITTSStateListener() {

		@Override
		public void onStart() {
			LogManager.e("mMasterTtsStateListener");
			mHandler.sendEmptyMessage(0);
		}

		@Override
		public void onInit() {
			LogManager.e("mMasterTtsStateListener");
			mHandler.sendEmptyMessage(1);
		}

		@Override
		public void onError() {
			LogManager.e("mMasterTtsStateListener");
			mHandler.sendEmptyMessage(2);
		}

		@Override
		public void onEnd() {
			LogManager.e("mMasterTtsStateListener");
			mHandler.sendEmptyMessage(3);
		}
	};
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			final int what = msg.what;
			switch (what) {
			case 0:
				mDefaultTTSStateListener.onStart();
				if (mTTSStateListener != null) {
					mTTSStateListener.onStart();
				}

				break;
			case 1:
				mDefaultTTSStateListener.onInit();
				if (mTTSStateListener != null) {
					mTTSStateListener.onInit();
				}

				break;
			case 2:
				mDefaultTTSStateListener.onError();
				if (mTTSStateListener != null) {
					mTTSStateListener.onError();
				}

				break;
			case 3:
				mDefaultTTSStateListener.onEnd();
				mIsPlaying = false;
				if (mTTSStateListener != null) {
					mTTSStateListener.onEnd();
				}

				break;
			default:
				break;
			}
		}
	};

	private ITTSStateListener mDefaultTTSStateListener = new ITTSStateListener() {
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			LogManager.i(Tag, "mDefaultTTSStateListener -------------onStart");
			if (!mIsPlaying) {
				// 如果正在进行着音量渐变效果，则关闭
				if (mBaseContext.getGlobalBoolean(KeyList.PKEY_CONTROL_VOLUME_FLAG, false)) {
					mBaseContext.setGlobalBoolean(KeyList.PKEY_CONTROL_VOLUME_SWITCH, false);
					mAlarmVolume = mBaseContext.getGlobalInteger(KeyList.PKEY_CONTROL_VOLUME_VALUE, 0);
				} else {
					mAlarmVolume = mVolumeManager.getCurrAlarmVolume();
	//				mAirplayVol = mAirplayVolume.getCurrentV(mContext);
				}
			}

//			if (mAirPlayMusicController.isAirplay() && mAirplayVolume.getCurrentV(mContext) > 2) {
//				mAirplayVolume.setAirplayVolume(mContext, 2);
//			}

			mIsPlaying = true;
			if (mRecSystem != null && !mBaseContext.getGlobalBoolean(KeyList.GKEY_CURRENT_CITYCODE_IS_NULL)) {
				mRecSystem.stopCaptureVoice();
			}
		}

		@Override
		public void onInit() {
			// TODO Auto-generated method stub
			LogManager.i(Tag, "mDefaultTTSStateListener----onInit");
		}

		@Override
		public void onError() {
			// TODO Auto-generated method stub
			LogManager.i(Tag, "mDefaultTTSStateListener----onError");
			mIsPlaying = false;
			mBaseContext.setGlobalLong(KeyList.GKEY_LONG_CHATMODE_BEGINTIME, System.currentTimeMillis());
			if (mRecSystem != null) {
				if (mBaseContext.getGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE)) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				mRecSystem.startCaptureVoice();
			}
			mBaseContext.setGlobalBoolean(KeyList.PKEY_NEED_START_IMEDIATELEY_AFTER_RECOGNISE, false);
			if (mAirPlayMusicController.isAirplay()) {
		//		mAirplayVolume.setAirplayVolume(mContext, mAirplayVol);
			}
		}

		@Override
		public void onEnd() {
			// TODO Auto-generated method stub
			LogManager.i(Tag, "mDefaultTTSStateListener----onEnd");
			mIsPlaying = false;

			mBaseContext.setGlobalLong(KeyList.GKEY_LONG_CHATMODE_BEGINTIME, System.currentTimeMillis());
			if (mRecSystem != null) {
				if (mBaseContext.getGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE)) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				mRecSystem.startCaptureVoice();
			}

			mBaseContext.setGlobalBoolean(KeyList.PKEY_NEED_START_IMEDIATELEY_AFTER_RECOGNISE, false);
			mVolumeManager.setAlarmVolume(mAlarmVolume);
//			if (mAirPlayMusicController.isAirplay()) {
//				mAirplayVolume.setAirplayVolume(mContext, mAirplayVol);
//			}
		}
	};

	public TTSControllerProxy(Context context) {
		mContext = context;
		mBaseContext = new BaseContext(mContext);
		initTTSController();
		addActionMapping(KeyList.AKEY_CHANGE_TTS_ENGINE, new OnReceiverListener() {

			@Override
			public void onReceiver(Context context, Intent intent) {
				// TODO Auto-generated method stub
				initTTSController();
			}
		});
		register(mContext);
		mVolumeManager = new VolumeManager(context);
//		mAirplayVolume = new AirplayVolume();
		mAirPlayMusicController = new AirPlayMusicController(mContext);
	}

	public TTSControllerProxy(IRecogniseSystem recSystem, Context context) {
		mContext = context;
		mBaseContext = new BaseContext(mContext);
		mRecSystem = recSystem;

		initTTSController();
		addActionMapping(KeyList.AKEY_CHANGE_TTS_ENGINE, new OnReceiverListener() {

			@Override
			public void onReceiver(Context context, Intent intent) {
				// TODO Auto-generated method stub
				initTTSController();
			}
		});

		register(mContext);
		mVolumeManager = new VolumeManager(context);
//		mAirplayVolume = new AirplayVolume();
		mAirPlayMusicController = new AirPlayMusicController(mContext);
	}

	/**
	 * 实例化tts播报的对象
	 * 
	 * @return
	 */
	private ITTSController initTTSController() {
		if (ttsSameple == null) {
			try {
				TTSSameple.initContext(mContext);
				int role = mBaseContext.getPrefInteger(KeyList.PKEY_TTS_PLAY_CHOOSE, 0);
				int speed = mBaseContext.getPrefInteger(KeyList.PKEY_SET_VOICESPEED, 50);
				ttsSameple = new TTSSameple(TTSPlayerFactory.TYPE_XUNFEI, role, speed);
				ttsSameple.setListener(mMasterTtsStateListener);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LogManager.printStackTrace(e);
			}

		}
		return ttsSameple;
	}

	/**
	 * 获取TTS播报对象
	 * 
	 * @return
	 */
	private ITTSController getRealTTSController() {
		if (mRealTTSController == null) {
			mRealTTSController = initTTSController();
		}
		return mRealTTSController;
	}

	@Override
	public void setListener(ITTSStateListener ttsStateListener) {
		// TODO Auto-generated method stub
		ITTSStateListener listener = mTTSStateListener;
		mTTSStateListener = ttsStateListener;
		if (mIsPlaying) {
			getRealTTSController().stop();
			if (listener != null) {
				listener.onEnd();
			}
			listener = null;
		}
		getRealTTSController().setListener(mMasterTtsStateListener);
	}

	@Override
	public void play(String text) {
		if (text == null || text.equals("")) {
			if (mMasterTtsStateListener != null) {
				mMasterTtsStateListener.onError();
			}
			return;
		}
		getRealTTSController().play(text);
	}

	public void play(String... strings) {
		getRealTTSController().play(strings);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if (mIsPlaying) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					LogManager.e("stop TTS !!!");
					getRealTTSController().stop();
					mIsPlaying = false;
				}
			}).start();
		}

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		getRealTTSController().destroy();
		unRegister(mContext);
	}

	@Override
	public void setRecSystem(IRecogniseSystem recogniseSystem) {
		// 如果是盒子的话就不用设置播报的时候停止监听了
		if (!MainConfigs.HEZIMODE) {
			mRecSystem = recogniseSystem;
		}

	}

	@Override
	public void setType(int type) {
		// TODO Auto-generated method stub
		getRealTTSController().setType(type);
	}

	/**
	 * 支持debug语音播报
	 */
	@Override
	public void syncPlay(String text) {
		getRealTTSController().syncPlay(text);
	}

	@Override
	public void playMore(String... text) {
		// TODO Auto-generated method stub
		getRealTTSController().playMore(text);
	}

}
