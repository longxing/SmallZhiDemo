package com.smallzhi.TTS.Main;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.example.common.MyApplication;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.smallzhi.TTS.Auxiliary.PacageNotFoundException;
import com.smallzhi.TTS.Engine.ITTSPlayer;
import com.smallzhi.TTS.Engine.ITTSStatusListen;
import com.smallzhi.TTS.Engine.TTSPlayerFactory;
import com.smallzhi.TTS.Main.VoiceStateReceiver.OnStateChange;
import com.util.VolumeUtil;

/**
 * @author jushang
 * @date Jul 8, 2013
 */
public class TTSSameple implements ITTSController {
	private static final String TAG = "TTSSameple";
	private static int mDefaultType = TTSPlayerFactory.TYPE_XUNFEI;
	private ITTSPlayer mPlayer;
	private static Context mContext;
	private VoiceStateReceiver mReceiver;
	public static int CURRENT_STREAM_TYPE = AudioManager.STREAM_ALARM;
	/**
	 * 是否阻塞播报
	 */
	private boolean isSyncPlay = false;

	/**
	 * 这个方法需要在最开始的时候调用
	 * 
	 * @param context
	 * @data Jul 8, 2013 2:18:24 PM
	 * @Edit jushang ...
	 */
	public static void initContext(Context context) {
		TTSPlayerFactory.init(context);
		mContext = context;
	}

	public TTSSameple() throws PacageNotFoundException {
		this(mDefaultType);
	}

	public TTSSameple(int type) throws PacageNotFoundException {
		this(type, -1);
	}

	public TTSSameple(int type, int role) throws PacageNotFoundException {
		this(type, role, -1);
	}

	public TTSSameple(int type, int role, int speed) throws PacageNotFoundException {
		mPlayer = TTSPlayerFactory.creatPlayer(type);
		if (role >= 0) {
			mPlayer.setVoiceType(role);
		}
		if (speed >= 0) {
			mPlayer.setVoiceSpeed(speed);
		}

		mReceiver = new VoiceStateReceiver();
		IntentFilter f = new IntentFilter();
		f.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		f.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		f.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		mContext.registerReceiver(mReceiver, f);
		mReceiver.setOnStateChange(new OnStateChange() {

			@Override
			public void onChange(int type) {
				CURRENT_STREAM_TYPE = type;
			}
		});

	}

	public void play(String text) {
		if (mPlayer != null) {
			// setScoOn();
			mPlayer.play(text);
			isSyncPlay = false;
			// Log.e("123", text);
			LogManager.d(Thread.currentThread().toString() + "|play|" + System.currentTimeMillis());
		}
	}

	public void play(final String... strings) {
		if (mPlayer != null) {
			isSyncPlay = false;
			final ITTSStateListener listen = ttsStateListener;
			for (int i = 0; i < strings.length; i++) {
				String text = strings[i];
				//
				final int playIndex = i;
				setListener(new ITTSStateListener() {

					@Override
					public void onInit() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStart() {
						if (playIndex == 0) {
							if (listen != null) {
								listen.onStart();
							}
						}
					}

					@Override
					public void onError() {
						if (playIndex == strings.length - 1) {
							if (listen != null) {
								listen.onError();
								setListener(listen);
							}
						}
					}

					@Override
					public void onEnd() {
						if (playIndex == strings.length - 1) {
							if (listen != null) {
								listen.onEnd();
								setListener(listen);
							}
						}
					}
				});
				//
				syncPlay(text);
			}
		}
	}

	public void stop() {
		if (mPlayer != null) {
			// setScoOff();
			mPlayer.stop();
		}
	}

	public void setListen(ITTSStatusListen listen) {
		if (mPlayer != null) {
			mPlayer.setListen(listen);
		}
	}

	public void release() {
		if (mPlayer != null) {
			mPlayer.stop();
		}
	}

	private ITTSStateListener ttsStateListener;
	private boolean flag;
	private int current;

	@Override
	public void setListener(final ITTSStateListener ttsStateListener) {
		this.ttsStateListener = ttsStateListener;
		ITTSStatusListen listen = new ITTSStatusListen() {

			@Override
			public void onError() {
				if (ttsStateListener != null) {
					flag = false;
					VolumeUtil.autoIncreaseVolume(mContext);
					ttsStateListener.onError();
				}
				this.debugSupport();
			}

			@Override
			public void onEnd() {
				if (ttsStateListener != null) {
					flag = false;
					VolumeUtil.autoIncreaseVolume(mContext);
					ttsStateListener.onEnd();
				}
				this.debugSupport();
			}

			private void debugSupport() {
				if (isSyncPlay) {
					synchronized (TTSSameple.this) {
						TTSSameple.this.notifyAll();
						LogManager.d("sync tts end");
					}
				}
			}

			@Override
			public void onBegin() {
				
				if (ttsStateListener != null) {
					final MyApplication app = (MyApplication) mContext.getApplicationContext();
					if (app.getUnion().getMediaInterface().isPlaying()) {
						VolumeUtil.autoDecreaseVolume(mContext);
					} else {
						new Thread(new Runnable() {
							public void run() {
								flag = true;
								synchronized (TTSSameple.this) {
									while (flag) {
										if (app.getUnion().getMediaInterface().isPlaying()) {
											VolumeUtil.autoDecreaseVolume(mContext);
											break;
										}
									}
								}
							}
						}).start();
					}
					ttsStateListener.onStart();
				}
			}
		};
		if (mPlayer != null) {
			mPlayer.setListen(listen);
		}
	}

	@Override
	public void destroy() {
		if (mPlayer != null) {
			mPlayer.stop();
		}
		try {
			mContext.unregisterReceiver(mReceiver);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}

	}

	@Override
	public void setRecSystem(IRecogniseSystem recogniseSystem) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setType(int type) {
		// TODO Auto-generated method stub
		if (mPlayer != null) {
			mPlayer.setVoiceType(type);
		}
	}

	/**
	 * 阻塞播报
	 */
	@Override
	public synchronized void syncPlay(String text) {
		if (mPlayer != null) {
			try {
				mPlayer.play(text);
				isSyncPlay = true;
				LogManager.d("sync tts play");
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 该方法不阻塞，播报多个字符串，
	 */
	@Override
	public void playMore(final String... text) {
		// TODO Auto-generated method stub
		if (text == null || text.length == 0) {
			return;
		}
		if (mPlayer != null) {
			mPlayer.setListen(new ITTSStatusListen() {
				String[] array = text;
				int index = 1;

				@Override
				public void onError() {
					if (ttsStateListener != null)
						ttsStateListener.onError();
					String words = null;
					try {
						words = array[index++];
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (words != null) {
						mPlayer.play(words);
					} else {
						setListener(ttsStateListener);
					}

				}

				@Override
				public void onEnd() {
					if (ttsStateListener != null)
						ttsStateListener.onEnd();
					String words = null;
					try {
						words = array[index++];
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (words != null) {
						mPlayer.play(words);
					} else {
						setListener(ttsStateListener);
					}

				}

				@Override
				public void onBegin() {
					if (ttsStateListener == null)
						return;
					ttsStateListener.onStart();
				}
			});
			mPlayer.play(text[0]);
		}

	}
}
