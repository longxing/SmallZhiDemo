package com.voice.assistant.main.music;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.common.utl.MediaPlayerUtil;
import com.iii360.base.umeng.UmengUtil;
import com.iii360.sup.common.utl.MessageQueue;
import com.voice.assistant.main.music.httpproxy.HttpGetProxyUtils;
import com.voice.assistant.main.music.httpproxy.HttpProxy;
import com.voice.assistant.main.newmusic.MusicInfoManager;

public class MediaPlayerService {
	private Context context;

	private MediaPlayer mMediaPlayer;
	private static final String TAG = "Music MediaPlayerService";

	private static MessageQueue messageQueue = new MessageQueue(Thread.MAX_PRIORITY);

	private static MediaPlayerService self = null;

	private int duration = 0;

	private int currentPosition = 0;

	private int bufferPercentage = 0;

	private boolean canPause = false;

	private boolean canSeekBackward = false;

	private boolean canSeekForward = false;

	private boolean isPlaying = false;

	private boolean hasReady = false;

	// ****** 缓冲相关
	private long bufferingLowStart = 0;// 缓存不足发生的开始时间

	private boolean isBufferingCauseStop = false;// 是否缓冲造成了暂停

	static long bufferingLowOverdue = 30 * 1000;// 缓存不足，超时时间

	// ****** 监听器
	private OnErrorListener onErrorListener = null;

	private OnPreparedListener onPreparedListener = null;

	private OnCompletionListener onCompletionListener = null;

	private OnBufferingOverdueListener onBufferingOverdueListener = null;

	public final static int MEDIA_ERROR_TRY = 100;

	private BaseContext mBaseContext;

	public interface OnBufferingOverdueListener {
		public void onBufferingUpdate(MediaPlayer mp, long newBufferPercentage);
	}

	private MediaPlayerService(Context context) {
		this.context = context;
		this.mBaseContext = new BaseContext(context);
		mMediaPlayer = new MediaPlayer();
		initPlayer();
	}

	public MediaPlayer getMediaPlayer() {
		return mMediaPlayer;
	}

	public static MediaPlayerService getInstance(Context context) {
		if (self == null) {
			self = new MediaPlayerService(context);
		}
		return self;
	}

	public static MessageQueue messageQueue() {
		return messageQueue;
	}

	// 创建播放器
	private void initPlayer() {
		reset();
		// 创建播放器
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// mp.start();
				try {
					if (onPreparedListener != null) {
						onPreparedListener.onPrepared(mp);
					}
				} catch (IllegalStateException e) {
					LogManager.printStackTrace(e);
				} catch (Exception e) {
					LogManager.printStackTrace(e);
				}
			}
		});

		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				try {
					if (onCompletionListener != null) {
						LogManager.i(TAG, "OnCompletionListener  ----play completion");
						onCompletionListener.onCompletion(mp);
					}
				} catch (IllegalArgumentException e) {
					LogManager.printStackTrace(e, "MediaPlayerService", "onCompletion");
				} catch (IllegalStateException e) {
					LogManager.printStackTrace(e, "MediaPlayerService", "onCompletion");
				} catch (Exception e) {
					LogManager.printStackTrace(e);
				}
			}

		});

		mMediaPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {

				LogManager.e(TAG, " on Error " + what + "  " + extra);
				// 无法复用播放器
				hasReady = false;
				try {
					if (onErrorListener != null) {
						return onErrorListener.onError(mp, what, extra);
					}
				} catch (IllegalStateException e) {
					LogManager.printStackTrace(e);
				} catch (Exception e) {
					LogManager.printStackTrace(e);
				}
				return true;
			}

		});

		mMediaPlayer.setOnInfoListener(new OnInfoListener() {

			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				LogManager.d(TAG, "what:" + what + ",extra:" + extra);
				switch (what) {
				case 702:// buffer结束缓冲 -> 音乐恢复播放
					isBufferingCauseStop = false;
					break;
				case 703:// buffer不足造成 -> 音乐暂停播放
					isBufferingCauseStop = true;
					break;
				case 701:// buffer开始缓冲 -> buffer不足时发生
					break;
				default:
				}
				return false;
			}
		});

		mMediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

			@Override
			public void onBufferingUpdate(MediaPlayer mp, int newBufferPercentage) {

				if (mBaseContext.getPrefBoolean(KeyList.PKEY_CUREENT_MUSIC_IS_DLAN, false)) {
					return;
				}

				bufferPercentage = newBufferPercentage;
				/**
				 * 暂时未查到 -2147483648 是什么状态，但此状态下歌曲是在缓冲的
				 */
				LogManager.i(TAG, " buffer percent:" + newBufferPercentage + "%");

				if (bufferPercentage != -2147483648 && bufferPercentage < 0 || isBufferingCauseStop) {
					long now = System.currentTimeMillis();
					if (bufferingLowStart == 0) {
						bufferingLowStart = now;
						LogManager.d(TAG, "缓冲发生不足");
					} else {
						long s = now - bufferingLowStart;
						LogManager.d(TAG, "缓冲持续不足：" + s + "ms");
						if (s >= bufferingLowOverdue) {// 超过缓冲超时时间了
							LogManager.d("缓冲不足超时");
							bufferingLowStart = 0;
							// 暂停
							if (onBufferingOverdueListener != null) {
								onBufferingOverdueListener.onBufferingUpdate(mp, s);
							}
						}
					}

				} else {
					bufferingLowStart = 0;
					LogManager.d(TAG, "mediaPlayer buffer percent:" + newBufferPercentage + "%");
					// 当剩余时间不足时，开始预加载下一首歌曲，且仅预加载一次
				}
			}
		});

	}

	// ****************操作命令****************** //
	public void setMediaInfoList(String path) {
		if (!messageQueue.inThread()) {
			throw new RuntimeException("setMediaInfoList must run on MediaPlayerService handler Thread");
		}
		try {
			LogManager.d(TAG, "setMediaInfoList", "set Path:" + path);
			// 复用播放器
			hasReady = false;
			mMediaPlayer.reset();

			if (mBaseContext.getPrefBoolean(KeyList.PKEY_CUREENT_MUSIC_IS_DLAN, false)) {
				LogManager.d(TAG, "setMediaInfoList dlan音乐");
				mMediaPlayer.setDataSource(path);
				mMediaPlayer.prepareAsync();
				mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {
						// TODO Auto-generated method stub
						if (preparedListener != null) {
							preparedListener.onPrepared(mp);
						}
					}
				});
				// 统计第三方协议播放
				mBaseContext.sendUmengEvent(UmengUtil.THE_THIRD_PROTOCOL_PLAY, UmengUtil.THE_THIRD_PROTOCOL_PLAY_CONTENT);
			} else if (path.startsWith("http")) {
				LogManager.d(TAG, "setMediaInfoList 代理音乐");

				String localMusicCache = HttpGetProxyUtils.getLocalOrCacheMusicsPath(path);
				if (localMusicCache != null) {
					mMediaPlayer.setDataSource(localMusicCache);
				} else {
					String mypath = HttpProxy.proxyHttpUrl + path;
					mMediaPlayer.setDataSource(mypath);
				}
			} else {
				LogManager.d(TAG, "setMediaInfoList 普通音乐");
				if (path.startsWith(MusicInfoManager.MUSIC_SAVE_POSE)) {
					int currentLocalMusicId = Integer.parseInt(path.replace(MusicInfoManager.MUSIC_SAVE_POSE, ""));
					// 记录当前歌曲ID
					mBaseContext.setPrefInteger(KeyList.CURRENT_LOCAL_MUSIC_ID, currentLocalMusicId);
				}
				mMediaPlayer.setDataSource(path);
			}

			if (!mBaseContext.getPrefBoolean(KeyList.PKEY_CUREENT_MUSIC_IS_DLAN, false)) {
				mMediaPlayer.prepare();
			}

			// 阻塞播放器
			hasReady = true;
		} catch (IOException e) {
			LogManager.printStackTrace(e, "MediaPlayerService", "setMediaInfoList");
			LogManager.e("path:" + path + " is invaild!");
			// 无法复用播放器
			hasReady = false;
			try {
				if (onErrorListener != null) {
					onErrorListener.onError(mMediaPlayer, MEDIA_ERROR_TRY, currentPosition);
				}
			} catch (IllegalStateException e1) {
				LogManager.printStackTrace(e1);
			} catch (Exception e1) {
				LogManager.printStackTrace(e1);
			}
		} catch (Exception e) {
			LogManager.printStackTrace(e, "MediaPlayerService", "setMediaInfoList");
		}

	}

	private OnMusicPreparedListener preparedListener;

	public void setOnMusicPreparedListener(OnMusicPreparedListener preparedListener) {
		this.preparedListener = preparedListener;
	}

	/**
	 * Interface definition for a callback to be invoked when the media source is ready for playback.
	 */
	public interface OnMusicPreparedListener {
		/**
		 * Called when the media file is ready for playback.
		 * 
		 * @param mp the MediaPlayer that is ready for playback
		 */
		void onPrepared(MediaPlayer mp);
	}

	public void seekTo(int pos) {
		if (!messageQueue.inThread()) {
			throw new RuntimeException("seekTo must run on MediaPlayerService handler Thread");
		}
		try {
			if (mMediaPlayer != null) {
				if (hasReady) {
					int d = getDuration();
					if (pos <= d) {
						LogManager.d(TAG, "seekTo (" + pos + ")");
					} else {
						LogManager.d(TAG, "seekTo error pos > Duration :" + pos + "/" + d + ")");
					}
					mMediaPlayer.seekTo(pos);
				} else {
					LogManager.d(TAG, "seekTo(" + pos + ") is ignore! mediaPlayer is not ready");
				}
			}
		} catch (IllegalStateException e) {
			LogManager.printStackTrace(e);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}

	}

	public void start() {
		if (!messageQueue.inThread()) {
			throw new RuntimeException("start must run on MediaPlayerService handler Thread");
		}
		try {
			if (mMediaPlayer != null) {
				if (hasReady) {
					LogManager.d(TAG, "start()");
					mMediaPlayer.setVolume(0, 0);
					mMediaPlayer.start();
					// 声音渐进
					MediaPlayerUtil.autoIncreaseVloume(context, mMediaPlayer, true);
				} else {
					LogManager.d(TAG, "start() is ignore! mediaPlayer is not ready");
				}
			}
		} catch (IllegalStateException e) {
			LogManager.printStackTrace(e);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
	}

	public void pause() {
		if (!messageQueue.inThread()) {
			throw new RuntimeException("pause must run on MediaPlayerService handler Thread");
		}
		try {
			if (mMediaPlayer != null) {
				if (hasReady) {
					LogManager.i(TAG, "onpause()");
					// 声音渐出
					MediaPlayerUtil.autoIncreaseVloume(context, mMediaPlayer, false);

					mMediaPlayer.pause();
				} else {
					LogManager.i(TAG, "onpause() is ignore! mediaPlayer is not ready");
				}
			}
		} catch (IllegalStateException e) {
			LogManager.printStackTrace(e);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
	}

	// 取消声音渐变的
	public void simpleStart() {
		if (!messageQueue.inThread()) {
			throw new RuntimeException("start must run on MediaPlayerService handler Thread");
		}
		try {
			if (mMediaPlayer != null) {
				if (hasReady) {
					LogManager.d(TAG, "simpleStart()");
					mMediaPlayer.setVolume(1, 1);
					mMediaPlayer.start();
				} else {
					LogManager.d(TAG, "simpleStart() is ignore! mediaPlayer is not ready");
				}
			}
		} catch (IllegalStateException e) {
			LogManager.printStackTrace(e);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}

	}

	// 取消声音渐变的
	public void simplePause() {
		if (!messageQueue.inThread()) {
			throw new RuntimeException("simplePause must run on MediaPlayerService handler Thread");
		}
		try {
			if (mMediaPlayer != null) {
				if (hasReady) {
					LogManager.d(TAG, "simplePause()");
					mMediaPlayer.setVolume(1, 1);
					mMediaPlayer.pause();
				} else {
					LogManager.d(TAG, "simplePause() is ignore! mediaPlayer is not ready");
				}

			}
		} catch (IllegalStateException e) {
			LogManager.printStackTrace(e);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
	}

	public void dlnaStop() {
		if (!messageQueue.inThread()) {
			throw new RuntimeException("stop must run on MediaPlayerService handler Thread");
		}
		try {
			LogManager.d(TAG, "dlna-----stop");
			reset();
			mMediaPlayer.reset();

		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}

	}

	public void release() {
		if (!messageQueue.inThread()) {
			throw new RuntimeException("release must run on MediaPlayerService handler Thread");
		}

		try {
			if (mMediaPlayer != null) {
				LogManager.d(TAG, "release()");
				mMediaPlayer.release();
				reset();
			}
		} catch (IllegalStateException e) {
			LogManager.printStackTrace(e);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}

	}

	public void reset() {
		duration = 0;
		currentPosition = 0;
		bufferPercentage = 0;
		bufferingLowStart = 0;
		canPause = false;
		canSeekBackward = false;
		canSeekForward = false;
		isPlaying = false;
		hasReady = false;
		isBufferingCauseStop = false;
	}

	// ****************监听回调****************** //
	public void setOnErrorListener(OnErrorListener onErrorListener) {
		this.onErrorListener = onErrorListener;
	}

	public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
		this.onPreparedListener = onPreparedListener;
	}

	public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
		this.onCompletionListener = onCompletionListener;
	}

	public void setOnBufferingOverdueListener(OnBufferingOverdueListener onBufferingOverdueListener) {
		this.onBufferingOverdueListener = onBufferingOverdueListener;
	}

	// ****************状态位查询****************** //
	public int getDuration() {
		try {
			if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
				duration = mMediaPlayer.getDuration();
			}
		} catch (IllegalStateException e) {
			LogManager.printStackTrace(e);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
		return duration;
	}

	public int getCurrentPosition() {
		try {

			if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
				currentPosition = mMediaPlayer.getCurrentPosition();
			}
		} catch (IllegalStateException e) {
			LogManager.printStackTrace(e);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
		return currentPosition;
	}

	public boolean isPlaying() {
		try {
			if (mMediaPlayer != null) {
				isPlaying = mMediaPlayer.isPlaying();
			}
		} catch (IllegalStateException e) {
			LogManager.printStackTrace(e);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
		return isPlaying;
	}

	public int getBufferPercentage() {
		return bufferPercentage;
	}

	public boolean canPause() {
		return canPause;
	}

	public boolean canSeekBackward() {

		return canSeekBackward;
	}

	public boolean canSeekForward() {

		return canSeekForward;
	}

}
