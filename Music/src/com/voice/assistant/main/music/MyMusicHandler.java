package com.voice.assistant.main.music;

import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.IMediaInterface;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.ITTSController.ITTSStateListener;
import com.voice.assistant.main.music.MediaPlayerService.OnBufferingOverdueListener;
import com.voice.assistant.main.newmusic.MusicUtil;

public class MyMusicHandler implements IMediaInterface {

	private static final String TAG = "MyMusicHandler";

	private MediaInfoList mMediaInfoList;
	private MediaInfo mCurrentMediaInfo;
	private boolean isPlaying;
	private boolean isInPlayState;
	private IMediaPlayerInterface mCurPlayer;
	private BaseContext mBaseContext;
	private BasicServiceUnion mUnion;
	private Context mContext;
	private boolean mIsFirstStart = false;
	private long mCurrentTime;
	protected Timer mTimer;
	protected TimerTask mTask;
	public int mPercent;
	private String playTTs;

	public interface OnCompletionListener {
		/**
		 * 
		 * @param mp
		 * @return 是否继续播
		 */
		public boolean onCompletion(MediaPlayer mp);
	}

	private OnErrorListener mSetOnErrorListener;
	private OnCompletionListener mSetOnCompletionListener;
	private OnEndOfLoopListener mSetOnEndOfLoopListener;
	private OnPreparedListener mSetOnPreparedListener;

	private boolean isTryReplay = false;

	// 歌曲欢迎词
	private static final String[] musicWait1Second = { "没问题", "（咳咳咳）", "遵命", "好的" };
	private static final String[] musicWait2Second = { "CD上有灰，我吹一下", "希望这首歌你会喜欢", "我帮你按下了播放键", "我去帮你翻翻CD架", "嘘，音乐要响了", "别急，等一会啊" };
	private static final String[] musicWait3Second = { "小智先去上个厕所，回来帮你放（停2秒）", "那些收藏在音乐里的记忆，我来放给你听", "好的，请你放松心情跟上节奏", "传说听音乐可以让奶牛产奶" };
	private static final String[] musicWait4Second = { "找呀找呀找音乐，我想把最美好的音乐都放给你听", "这是一首前奏响起就让你爱上它的音乐", "我找找看，（停3秒）找到你要听得歌了" };

	public MyMusicHandler(BasicServiceUnion union) {
		mUnion = union;
		mBaseContext = union.getBaseContext();
		mContext = mBaseContext.getContext();
	}

	@Override
	public void setMediaInfoList(Object info) {
		// TODO Auto-generated method stub
		isInPlayState = true;
		isTryReplay = false;
		mMediaInfoList = (MediaInfoList) info;
		LogManager.d(TAG, "set mMediaInfoList  " + mMediaInfoList.size());
	}

	public void addMediaInfoList(Object info) {
		MediaInfoList ml = (MediaInfoList) info;
		mMediaInfoList.addAll(ml.getAll());
		LogManager.d(TAG, "add mMediaInfoList  " + ml.size());
	}

	public MediaInfoList getMediaInfoList() {
		return mMediaInfoList;
	}

	@Override
	public void release() {

		MediaPlayerService.messageQueue().post(new Runnable() {

			@Override
			public void run() {
				mBaseContext.setPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN", false);
				mBaseContext.setPrefBoolean("PKEY_CUREENT_MUSIC_IS_AIRPLAY", false);

				pause();
				isTryReplay = false;
				isInPlayState = false;
				mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING, false);
				if (mCurPlayer != null) {
					mCurPlayer.release();
				}
			}
		});
	}

	@Override
	public void playNext() {
		MediaPlayerService.messageQueue().post(new Runnable() {

			@Override
			public void run() {
				if (mMediaInfoList == null) {
					return;
				}
				// 是否继续播放
				boolean next = true;
				// 是否为 最后一首歌
				if (mMediaInfoList.isLast()) {
					LogManager.d(TAG, "playNext,OnEndOfLoop");
					if (mSetOnEndOfLoopListener != null) {
						next = mSetOnEndOfLoopListener.onEndOfLoop();
					}
				}

				if (next) {
					isTryReplay = false;
					mCurrentMediaInfo = mMediaInfoList.getNext();
					initPlayer();
					// 广播给客户端
					String json = "";
					if (mMediaInfoList != null) {
						json = MediaUtil.mediaDesc(mMediaInfoList.get(), mUnion.getBaseContext().getContext()).toString();
					}
					LogManager.d(TAG, "playNext 下一首，歌曲信息：" + json);
					sendWifiserverBroadcast("playNext", json);
				}
			}
		});
	}

	@Override
	public void playPre() {
		MediaPlayerService.messageQueue().post(new Runnable() {

			@Override
			public void run() {
				if (mMediaInfoList == null) {
					return;
				}
				isTryReplay = false;
				mCurrentMediaInfo = mMediaInfoList.getPrev();
				initPlayer();
				// 广播给客户端
				String json = "";
				if (mMediaInfoList != null) {
					json = MediaUtil.mediaDesc(mMediaInfoList.get(), mContext).toString();
				}
				LogManager.d(TAG, "playPre 上一首，歌曲信息：" + json);
				sendWifiserverBroadcast("playPre", json);
			}
		});
	}

	@Override
	public void pause() {
		MediaPlayerService.messageQueue().post(new Runnable() {

			@Override
			public void run() {
				LogManager.i(TAG, "messageQueue-------onpause");
				if (mCurPlayer != null) {
					mCurPlayer.pause();
				}
				innerPause();
			}
		});
	}

	private void innerPause() {
		isPlaying = false;
		mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, false);
		mUnion.getRecogniseSystem().startWakeup();
		// 广播给客户端
		String json = "";
		if (mMediaInfoList != null) {
			json = MediaUtil.mediaDesc(mMediaInfoList.get(), mContext).toString();
		}
		sendWifiserverBroadcast("pause", json);
	}

	@Override
	
	
	
	
	
	public void resume() {
		MediaPlayerService.messageQueue().post(new Runnable() {

			@Override
			public void run() {
				LogManager.printStackTrace();
				mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, true);
				mUnion.getRecogniseSystem().stopWakeup();
				if (mCurPlayer != null) {
					mCurPlayer.start();
				}
				isPlaying = true;
				// 广播给客户端
				String json = "";
				if (mMediaInfoList != null) {
					json = MediaUtil.mediaDesc(mMediaInfoList.get(), mContext).toString();
				}
				sendWifiserverBroadcast("resume", json);
			}
		});
	}

	public void recogniseResume() {
		MediaPlayerService.messageQueue().post(new Runnable() {

			@Override
			public void run() {
				LogManager.printStackTrace();
				mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, true);
				mUnion.getRecogniseSystem().stopWakeup();
				if (mCurPlayer != null) {
					mCurPlayer.simpleStart();
				}
				isPlaying = true;
				// 广播给客户端
				String json = "";
				if (mMediaInfoList != null) {
					json = MediaUtil.mediaDesc(mMediaInfoList.get(), mContext).toString();
				}
				sendWifiserverBroadcast("resume", json);
			}
		});
	}

	@Override
	public void start() {
		MediaPlayerService.messageQueue().post(new Runnable() {

			@Override
			public void run() {
				mBaseContext.setPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN", false);
				if (isTryReplay) {
					LogManager.d(TAG, "start" + "重试播放本首歌歌");
				} else {
					mCurrentMediaInfo = mMediaInfoList.get();
				}
				isTryReplay = false;
				mIsFirstStart = true;
				initPlayer();
				// 广播给客户端
				String json = "";
				if (mMediaInfoList != null) {
					json = MediaUtil.mediaDesc(mMediaInfoList.get(), mContext).toString();
				}
				sendWifiserverBroadcast("start", json);
			}
		});
	}

	private void sendWifiserverBroadcast(String type, String param) {
		Intent i = new Intent();
		i.setAction("com.iii.wifiserver.receiver.MusicChangeReceiver.control");
		i.putExtra("type", type);
		i.putExtra("data", param);
		Log.d(TAG, "sendWifiserverBroadcast  " + type + "   " + param);
		mContext.sendBroadcast(i);
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		if (mCurPlayer != null) {
			return mCurPlayer.isPlaying();
		}
		return false;
	}

	@Override
	public boolean isInPlayState() {
		// TODO Auto-generated method stub
		return isInPlayState;
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		if (mCurPlayer != null) {
			return mCurPlayer.getDuration();
		}
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		if (mCurPlayer != null) {
			return mCurPlayer.getCurrentPosition();
		}
		return 0;
	}

	private String welcome(int downloadPer) {
		if (!mUnion.getBaseContext().getGlobalBoolean(KeyList.GKEY_IS_PLAY_WELCOME, true)) {
			// 恢复标志位
			mUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_PLAY_WELCOME, true);
			return null;
		}
		String playTTs = "";
		int playType = mBaseContext.getGlobalInteger(KeyList.GKEY_PLAY_TYPE, KeyList.GKEY_PLAY_TYPE_MUSIC);
		switch (playType) {
		case KeyList.GKEY_PLAY_TYPE_STORY:
			break;
		case KeyList.GKEY_PLAY_TYPE_JOKE:
			break;
		default:
			if (downloadPer > 30) {
				playTTs = "";
			} else if (downloadPer > 20) {
				playTTs = musicWait1Second[new Random().nextInt(musicWait1Second.length)];
			} else if (downloadPer > 14) {
				playTTs = musicWait2Second[new Random().nextInt(musicWait2Second.length)];
			} else if (downloadPer > 8) {
				playTTs = musicWait3Second[new Random().nextInt(musicWait3Second.length)];
			} else {
				playTTs = musicWait4Second[new Random().nextInt(musicWait4Second.length)];
			}
		}

		return playTTs;
	}

	/**
	 * 是否立刻播放
	 */
	private boolean isPlayImme = true;

	public void initPlayer() {
		isPlayImme = true;
		isTryReplay = false;
		IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
		ITTSController mITTSController = (ITTSController) gloableHeap.getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);
		if (KeyList.IS_TTS_DEBUG && KeyList.IS_PLAYER_DEBUG) {
			KeyList.SEMANTEME_RECOGNIZER_FINISH = System.currentTimeMillis();
			mITTSController.syncPlay("当前播放的是" + mMediaInfoList.size() + "首中的第" + (mMediaInfoList.getIndex() + 1) + "首");
		}
		LogManager.i(TAG, "initPlayer 播放列表：" + (mMediaInfoList.getIndex() + 1) + "/" + mMediaInfoList.size());

		if (mCurrentMediaInfo == null) {
			// 播放列表结束，恢复唤醒
			release();
			return;
		}
		File file = new File(mCurrentMediaInfo._path);
		mCurPlayer = new MusicPlayer(mContext);
		LogManager.d("initPlayer:" + mCurrentMediaInfo._path);
		if (mCurrentMediaInfo._isFromNet || !file.exists() || file.length() == 0) {
			LogManager.d("initPlayer:" + mIsFirstStart);
			if (mIsFirstStart) {
				int downloadPer = mPercent;
				LogManager.d(TAG, "initPlayer,downloadPer" + downloadPer);
				playTTs = welcome(downloadPer);
				LogManager.d(TAG, "initPlayer playertts ," + playTTs);
				ITTSStateListener ttsStateListener = new ITTSStateListener() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
					}

					@Override
					public void onInit() {
						// TODO Auto-generated method stub
					}

					@Override
					public void onError() {
						onEnd();
					}

					@Override
					public void onEnd() {
						MediaPlayerService.messageQueue().post(new Runnable() {

							@Override
							public void run() {
								mUnion.getTTSController().setListener(null);
								mUnion.getRecogniseSystem().stopWakeup();
								if (mCurPlayer != null && isPlaying) {
									mCurPlayer.simpleStart();
								}
							}
						});
					}
				};
				// 是否需要欢迎词
				if (playTTs != null && playTTs.length() > 0) {
					isPlayImme = false;
					mUnion.getTTSController().setListener(ttsStateListener);
					new Thread() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();
							try {
								sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							mUnion.getTTSController().play(playTTs);
						}
					}.start();
				} else {
					// 对于网络歌曲，由下载处来播放
					mIsFirstStart = false;
				}

			}
		} else {
			// 恢复标志位
			mUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_PLAY_WELCOME, true);
		}

		mIsFirstStart = false;

		// 先关唤醒灯 - 再播放
		mUnion.getRecogniseSystem().stopWakeup();
		mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, true);
		mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING, true);
		// 监听器
		mCurPlayer.setOnCompletionListener(mOnCompletionListener);
		mCurPlayer.setOnErrorListener(mErrorListener);
		mCurPlayer.setOnBufferingOverdueListener(mBufferingOverdueListener);
		mCurPlayer.setOnPreparedListener(mOnPreparedListener);
		// 加载歌曲
		mCurPlayer.setMediaInfo(mCurrentMediaInfo);
		// 预加载下一首
//		preLoading();

		mBaseContext.setGlobalString(KeyList.GKEY_STR_CURRENT_MEDIAINFO, mCurrentMediaInfo._Id);

		LogManager.d(TAG, "initPlayer logMusic start " + System.currentTimeMillis());

		MusicUtil.logMusic(MusicUtil.COMMAND_START, mBaseContext);

		LogManager.d(TAG, "initPlayer logMusic end " + System.currentTimeMillis());

		mCurrentMediaInfo._musicInfo.mBaseNum -= 100;
		if (mCurrentMediaInfo._musicInfo.mPlayTime == 0) {
			mCurrentMediaInfo._musicInfo.mPlayTime = System.currentTimeMillis();
		} else {
			mCurrentMediaInfo._musicInfo.mSecondPlayTime = System.currentTimeMillis();
		}

		isPlaying = true;
		// 播放
		if (isPlayImme) {
			mCurPlayer.simpleStart();
		}
	}

	private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			if (mSetOnPreparedListener != null) {
				mSetOnPreparedListener.onPrepared(mp);
			}
		}
	};
	/***
	 * 歌曲播放完成，播放下手歌曲
	 */
	private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			LogManager.d(TAG, "mOnCompletionListener ------onCompletion");
			boolean next = true;
			if (mSetOnCompletionListener != null) {
				next = mSetOnCompletionListener.onCompletion(mp);
			}
			if (next) {
				LogManager.d(TAG, "mOnCompletionListener ------playerNext");
				playNext();
			}
		}
	};
	//
	private OnErrorListener mErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			LogManager.e("On Error " + what);
			switch (what) {
			case 100:// 表示url无效
				if (mMediaInfoList.getIndex() == mMediaInfoList.size() - 1) {
					LogManager.e(TAG, "没有下一首，停止播放");
					mUnion.getTTSController().play("歌曲暂时无法播放");
					innerPause();
				} else {
					LogManager.e(TAG, "尝试切换到下一首");
					playNext();
				}
				break;
			default:
			case -38:// 表示当前音乐下载过慢 - 暂停，尝试再次播放时重新播这首歌
				LogManager.e(TAG, "当前音乐下载过慢 - 暂停，再次播放时,尝试再次重新播这首歌");
				innerPause();
				isTryReplay = true;
				break;
			}
			if (mSetOnErrorListener != null) {
				mSetOnErrorListener.onError(mp, what, extra);
			}
			return true;
		}
	};

	private OnBufferingOverdueListener mBufferingOverdueListener = new OnBufferingOverdueListener() {

		@Override
		public void onBufferingUpdate(MediaPlayer mp, long newBufferPercentage) {
			if (mCurPlayer.isPlaying()) {
				MediaPlayerService.messageQueue().post(new Runnable() {

					@Override
					public void run() {
						mUnion.getTTSController().play("歌曲暂时无法播放");
						if (mCurPlayer != null) {
							mCurPlayer.simplePause();
						}
						innerPause();
					}
				});
			}
		}

	};

	@Override
	public void setPlayMode(int mode) {
		// TODO Auto-generated method stub
		mMediaInfoList.setPlayMode(mode);
	}

	public void update() {

		if (mCurPlayer != null && mCurPlayer.isPlaying()) {

			mCurrentTime = mCurPlayer.getCurrentPosition();

			if (mBaseContext.getGlobalBoolean(com.iii360.base.common.utl.KeyList.GKEY_BOOL_CHATMODE)) {
				mBaseContext.setPrefLong(com.iii360.base.common.utl.KeyList.GKEY_LONG_CHATMODE_BEGINTIME, System.currentTimeMillis());
			}
			LogManager.d(TAG, mCurrentTime + "");

		}
	}

	@Override
	public void setOnParePare(OnPreparedListener listener) {
		mSetOnPreparedListener = listener;
	}

	public void setOnComplation(OnCompletionListener listener) {
		mSetOnCompletionListener = listener;
	}

	public OnCompletionListener getOnComplation() {
		return mSetOnCompletionListener;
	}

	@Override
	public void setOnError(OnErrorListener listener) {
		// TODO Auto-generated method stub
		mSetOnErrorListener = listener;
	}

	@Override
	@Deprecated
	/**
	 * @see #setOnComplation(OnCompletionListener)
	 */
	public void setOnComplation(android.media.MediaPlayer.OnCompletionListener listener) {

	}

	@Override
	public void setOnEndOfLoopListener(OnEndOfLoopListener listener) {
		this.mSetOnEndOfLoopListener = listener;
	}

	public MediaInfo getCurrentMediaInfo() {
		return mCurrentMediaInfo;
	}

	@Override
	public int getPlayMode() {
		return MediaInfoList.PLAY_MODE;
	}

	@Override
	public void setPlayType(int type) {
		// TODO Auto-generated method stub

	}

	@Override
	public MediaPlayer getMediaPlayer() {
		if (mCurPlayer != null && mCurPlayer.getMediaPlayerService() != null) {
			return mCurPlayer.getMediaPlayerService().getMediaPlayer();
		}
		return null;
	}

}
