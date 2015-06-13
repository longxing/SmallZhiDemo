package com.smallzhi.TTS.Engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;

import com.iii360.base.common.utl.LogManager;
import com.iii360.sup.common.utl.stringPreHandlingModule;
import com.smallzhi.TTS.Main.TTSSameple;
import com.voice.common.util.nlp.SplitDict;

public class TTSCustom implements ITTSPlayer {

	private ITTSStatusListen mIttsStatusListen;
	private Context context;
	private MediaPlayer m;
	private static final String TTS_VOICE_DATAPATH = "/sdcard/VoiceAssistant/ttsVoice";
	private HashMap<String, String> ttsValues = new HashMap<String, String>();
	private SplitDict mDict = new SplitDict(null);

	public TTSCustom(Context context) {
		this.context = context;
		buidDate();
	}

	@Override
	public void play(String text) {
		// TODO Auto-generated method stub
		if (text == null || text.equals("")) {
			if (mIttsStatusListen != null) {
				mIttsStatusListen.onError();
			}
			return;
		}

		final String mText = stringPreHandlingModule.meanExtract(text);
		LogManager.e(mText);
		if (ttsValues.containsKey(mText)) {

			try {
				m = new MediaPlayer();
				m.setAudioStreamType(TTSSameple.CURRENT_STREAM_TYPE);
				m.setDataSource(context, Uri.fromFile(new File(ttsValues.get(mText))));
				m.prepareAsync();
				m.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer arg0) {
						// TODO Auto-generated method stub
						if (m == null) {
							LogManager.e(mText + " create fail ");
							return;
						}

						if (mIttsStatusListen != null) {
							mIttsStatusListen.onBegin();
						}

						m.start();
						LogManager.e("m.getDuration()" + m.getDuration());
					}
				});

				m.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						if (m != null) {
							m.release();
							m = null;
						}
						if (mIttsStatusListen != null) {
							mIttsStatusListen.onEnd();
						}
					}
				});

				m.setOnErrorListener(new OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						// TODO Auto-generated method stub
						if (m != null) {
							m.release();
							m = null;
						}
						if (mIttsStatusListen != null) {
							mIttsStatusListen.onError();
						}
						return false;
					}
				});

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogManager.printStackTrace(e);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogManager.printStackTrace(e);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					ArrayList<String> splitedValue = checkSubs(mText);
					int bufferSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
					AudioTrack track = new AudioTrack(AudioManager.STREAM_ALARM, 16000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
					track.play();
					if (mIttsStatusListen != null) {
						mIttsStatusListen.onBegin();
					}
					for (String s : splitedValue) {
						try {
							FileInputStream fins = new FileInputStream(ttsValues.get(s));
							fins.skip(100);
							byte[] values = new byte[1024];

							int i = 0;
							float lastsum = 1;
							while ((i = fins.read(values)) > 0) {
								int sum = 0;
								for (byte b : values) {
									sum += b;
								}
								float sumf = (Math.abs((sum / 1024.0f)) + lastsum) / 2;
								LogManager.v(" " + sumf);
								lastsum = sumf;
								if (sumf > 0.399) {
									track.write(values, 0, i);
								}
							}
							fins.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							if (mIttsStatusListen != null) {
								mIttsStatusListen.onError();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							if (mIttsStatusListen != null) {
								mIttsStatusListen.onError();
							}
						}
					}
					track.flush();
					track.stop();
					track.release();
					if (mIttsStatusListen != null) {
						mIttsStatusListen.onEnd();
					}
				}
			}).start();

		}

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if (m != null && m.isPlaying()) {
			try {
				m.stop();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	@Override
	public void setListen(ITTSStatusListen listen) {
		// TODO Auto-generated method stub
		mIttsStatusListen = listen;
	}

	@Override
	public void setVoiceType(int type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVoiceSpeed(int speed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setStreamType(int type) {
		// TODO Auto-generated method stub
		return false;
	}

	private void buidDate() {

		File f = new File(TTS_VOICE_DATAPATH);
		if (f.exists() && f.isDirectory()) {
			for (File voiceFile : f.listFiles()) {
				String fileName = voiceFile.getName();
				// LogManager.e(fileName);
				String name = fileName.substring(0, fileName.length() - 4);
				name = stringPreHandlingModule.meanExtract(name);
				if (name.length() < 30) {
					ttsValues.put(name, voiceFile.getAbsolutePath());
					LogManager.d(name);
					mDict.addWord(name);
				}
			}
		} else {
			LogManager.e("can't read sdcard");
			LogManager.e("/sdcard/" + new File("/sdcard/").exists());
			LogManager.e(TTS_VOICE_DATAPATH + new File(TTS_VOICE_DATAPATH).exists());
		}

	}

	public boolean isContain(String tts) {
		tts = stringPreHandlingModule.meanExtract(tts);
		if (ttsValues.containsKey(tts)) {
			return true;
		} else {
			if (checkSubs(tts) != null) {
				return true;
			}
		}
		if (ttsValues != null && ttsValues.size() == 0) {
			buidDate();
		}

		return false;
	}

	private ArrayList<String> checkSubs(String tts) {

		for (ArrayList<String> splitedValue : mDict.getSplitStrings(tts)) {
			if (splitedValue != null && splitedValue.size() > 0) {
				int i = 0;
				for (String s : splitedValue) {
					if (ttsValues.containsKey(s)) {
						i += s.length();
					}
				}
				if (i == tts.length()) {
					return splitedValue;
				}
			}
		}

		return null;
	}

}
