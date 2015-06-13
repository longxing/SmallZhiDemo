package com.iii360.external.recognise.engine;

//hujinrong begin

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Vibrator;

import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.ITTSController;

/**
 * 带音频,震动反馈的语音识别
 * 
 * @author Jerome.Hu
 * 
 */
public abstract class AbstractRecogniseEngine implements IRecogniseEngine {

	public static final int VIBRATE_TIME = 500;
	protected Context mContext;
	private int mStartMusicChatModeRes;
	private int mStartMusicRes;
	private int mConfirmMusicRes;
	private int mCancelMusicRes;

	// 防止播放两次识别的情况出现。
	private boolean mIsNeedPlayConfirmMusic = true;
	// 默认采用音频反馈
	private int mFeedBackTypeFlag = FEED_BACK_TYPE_MUSIC;
	// private MediaPlayer mStartMusicMediaPlayer;

	private Vibrator mVibrator;
	private int mStartMusicChatModeId;
	private int mStartMusicId;

	private SoundPool mSoundpool;

	private AbstractRecogniseEngine(Context context) {
		mContext = context;

	}

	public AbstractRecogniseEngine(Context context, int startMusicRes, int mStartMusicResChatMode, int confirmMusicRes, int cancelMusicRes, int pFeedBackType) {
		this(context, startMusicRes, mStartMusicResChatMode, confirmMusicRes, cancelMusicRes);
		mFeedBackTypeFlag = pFeedBackType;
	}

	private AbstractRecogniseEngine(Context context, int startMusicRes, int mStartMusicResChatMode, int confirmMusicRes, int cancelMusicRes) {
		this(context);
		setStartMusicChat(mStartMusicResChatMode);
		setStartMusic(startMusicRes);
		setConfirmMusic(confirmMusicRes);
		setCancelMusicRes(cancelMusicRes);
		try {
			init();
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
	}

	protected void setStartMusicChat(int res) {
		this.mStartMusicChatModeRes = res;
	}

	protected void setStartMusic(int res) {
		this.mStartMusicRes = res;
	}

	protected void setConfirmMusic(int res) {
		this.mConfirmMusicRes = res;
	}

	protected void setCancelMusicRes(int res) {
		this.mCancelMusicRes = res;
	}

	public void init() {
		final int soundNumber = 3;
		mSoundpool = new SoundPool(soundNumber, AudioManager.STREAM_ALARM, 0);
		mStartMusicId = mSoundpool.load(getContext(), mStartMusicRes, 1);
		mStartMusicChatModeId = mSoundpool.load(getContext(), mStartMusicChatModeRes, 1);
		mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
	}

	abstract protected void realStart();

	@Override
	public void startRecogniseFeedBack() {
		LogManager.e("startRecogniseFeedBack");
		// 002-hefeng begin
		synchronized (mContext) {
			if (mFeedBackTypeFlag == FEED_BACK_TYPE_NONE) {
				return;
			} else if (mFeedBackTypeFlag == FEED_BACK_TYPE_MUSIC) {
				if (mIsNeedPlayConfirmMusic) {
					stopAll();
					// mSoundpool.play(mConfirmMusicId, 1, 1, 0, 0, 1);
					mIsNeedPlayConfirmMusic = false;
				}
			} else if (mFeedBackTypeFlag == FEED_BACK_TYPE_VIBRATE) {
				vibrate();
			}
			// 002-hefeng end
		}
	}

	public Context getContext() {
		return mContext;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		startCaptureVoiceFeedBack();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		startRecogniseFeedBack();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		cancelRecogniseFeedBack();
	}

	@Override
	public void startCaptureVoiceFeedBack() {
		// TODO Auto-generated method stub
		LogManager.e("startCaptureVoiceFeedBack");
		// 002-hefeng begin
		synchronized (mContext) {
			try {
				long delay = VIBRATE_TIME;
				switch (mFeedBackTypeFlag) {
				case FEED_BACK_TYPE_MUSIC:
					// 不联网直接提示没有连接网络
					final IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
					if (!gloableHeap.getGlobalBooleanMap().get(KeyList.GKEY_BOOL_IS_CONNECT_WIFIGATE)) {
						// 关闭识别灯
						ITTSController mTTSController = (ITTSController) gloableHeap.getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);

						gloableHeap.getGlobalBooleanMap().put(KeyList.GKEY_FORCE_RECOGNISE, false);
						gloableHeap.getGlobalBooleanMap().put(KeyList.PKEY_STRING_SPEECH_LOGO_IS_START, false);
						gloableHeap.getGlobalBooleanMap().put(KeyList.GKEY_IS_WAKEUP_TO_RECOGNISE, false);

						mTTSController.play("音箱未联网，请扫描音箱底部二维码下载小智助手配置网络");
						return;
					}
					// 正常工作
					Boolean isChatMode = gloableHeap.getGlobalBooleanMap().get(KeyList.GKEY_BOOL_AUTO_CHATED_MODE);
					if (isChatMode != null && isChatMode) {
						LogManager.e("use temp play music");
						if (!(gloableHeap.getGlobalBooleanMap().get(KeyList.GKEY_CURRENT_CITYCODE_IS_NULL) != null && gloableHeap.getGlobalBooleanMap().get(KeyList.GKEY_CURRENT_CITYCODE_IS_NULL))) {
							mSoundpool.play(mStartMusicChatModeId, 1, 1, 0, 0, 1);
						}
						delay = 300;
					} else if (mStartMusicRes > 0) {
						LogManager.e("use orient play music");
						if (!(gloableHeap.getGlobalBooleanMap().get(KeyList.GKEY_CURRENT_CITYCODE_IS_NULL) != null && gloableHeap.getGlobalBooleanMap().get(KeyList.GKEY_CURRENT_CITYCODE_IS_NULL))) {
							mSoundpool.play(mStartMusicId, 1, 1, 0, 0, 1);
						}
						long musicTime = 2000;
						delay = musicTime - VIBRATE_TIME;
					}
					break;
				case FEED_BACK_TYPE_VIBRATE:
					vibrate();
					break;
				}
				// 识别
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						realStart();
					}
				}, delay);
			} catch (Exception e) {
				e.printStackTrace();
				LogManager.printStackTrace(e);

			}
		}
		// 002-hefeng end
	}

	@Override
	public void cancelRecogniseFeedBack() {
		// TODO Auto-generated method stub
		LogManager.e("cancelRecogniseFeedBack");
		// 002-hefeng begin
		synchronized (mContext) {
			switch (mFeedBackTypeFlag) {
			case FEED_BACK_TYPE_NONE:
				return;
			case FEED_BACK_TYPE_MUSIC:
				stopAll();
				mIsNeedPlayConfirmMusic = true;
				break;
			case FEED_BACK_TYPE_VIBRATE:
				vibrate();
				break;
			}
			// 002-hefeng end
		}
	}

	private void vibrate() {
		final int m100 = 100;
		final int m10 = 10;
		mVibrator.vibrate(new long[] { m100, m10, m100, m100 }, -1);
	}

	// 002-hefeng begin
	private void stopAll() {
		Intent intent = new Intent();
		intent.setAction("SERVICE_STOP_BACK_WAKE_UP_LISTEN");
		mContext.sendBroadcast(intent);

		IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
		if (mSoundpool != null) {
			// 当前是否为聊天模式
			Boolean isChatMode = gloableHeap.getGlobalBooleanMap().get(KeyList.GKEY_BOOL_AUTO_CHATED_MODE);
			if (isChatMode != null && isChatMode) {
				mSoundpool.stop(mStartMusicChatModeId);
			} else {
				mSoundpool.stop(mStartMusicId);
			}
		}
	}
}
