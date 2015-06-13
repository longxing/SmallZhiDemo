package com.iii360.external.wakeup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.ITTSController;
import com.iii360.external.recognise.RecogniseSystemBufferBuildFactory;
import com.iii360.external.recognise.RecogniseSystemBufferBuildFactory.IUSCStateListener;
import com.iii360.external.recognise.engine.BufferRecognizer;
import com.iii360.external.recognise.engine.IBufferRecogniseEngine;
import com.iii360.external.recognise.engine.IBufferRecogniseEngine.IStateListener;
import com.iii360.external.recognise.engine.SpeechOnEndListener;
import com.iii360.sup.common.utl.PinYinUtil;

/**
 * 处理唤醒时的语音结果 （1） 没有配置家电只有在线识别结果，配置过家电，同时存在在线，离线识别结果
 * 
 * @author Peter
 * @data 2015年4月28日下午1:52:07
 */

public class BufferManager {

	/***********************************************************************************/
	/******************************** Members Variables **********************************/
	/***********************************************************************************/

	private static final String TAG = " WakeUp_New  BufferManager ";

	private IBufferRecogniseEngine mBufferRecogniseEngine = null;
	private Context mContext = null;
	private int mWaitTime = 10 * 1000;
	private Result mResult = null;
	private Handler mHandler = new Handler();
	private BaseContext mBaseContext = null;
	private boolean mWait = true;
	private boolean mWaitResult = false;
	private boolean mLoseResult = false;
	private String mWaitResultInfo = null;
	private String mResultInfo = null;

	/**
	 * 目前此接口只是用于更新灯的状态
	 */
	public interface Result {
		public void onResult(String result);

		public void onError(String error);
	}

	/**
	 * 
	 * @param context
	 * @param result
	 * @param listener
	 */
	public BufferManager(Context context, Result result, IUSCStateListener listener) {
		mContext = context;
		mResult = result;
		mWaitResult = false;
		mLoseResult = false;
		mBaseContext = new BaseContext(mContext);
		mBufferRecogniseEngine = new BufferRecognizer(context, RecogniseSystemBufferBuildFactory.ENGINE_TYPE_BUFFER_ONLINE_AISPEECH,
				RecogniseSystemBufferBuildFactory.ENGINE_TYPE_BUFFER_LOCAL_AISPEECH, listener);
		mBufferRecogniseEngine.setStateListener(new BufferRecogniseStateImp());

	}

	// 识别引擎，检测到语音输入结束，关闭唤醒
	public void setOnEndListener(SpeechOnEndListener listener) {
		mBufferRecogniseEngine.setOnEndListener(listener);
	}

	/**
	 * 思必驰在线识别结果处理
	 * 
	 * @param isError
	 * @param errorCode
	 * @param result
	 */
	private void onSpeechResultForOnline(Boolean isError, String result) {
		mHandler.removeCallbacks(mRunnable);
		if (result != null && !result.equals("")) {
			sendCloud(mContext, result);
			mResult.onResult(result);
		} else {
			mResult.onError("error");
		}
	}

	/**
	 * 同时有在线，离线识别结果，根据的返回结果的置信度，确定采用哪个识别结果
	 * 
	 * @param isError
	 * @param errorCode
	 * @param result
	 */
	private synchronized void onSpeechResult(boolean isError, int errorCode, String result) {
		LogManager.d("callback result in thread:" + Thread.currentThread().getName() + "----result:" + result);
		try {
			if (mLoseResult) {
				mLoseResult = false;
				mWaitResult = false;
				return;
			}
			if (!mWaitResult) {
				if (isError) {
					mWaitResultInfo = "error";
					mWaitResult = true;
					mLoseResult = false;
				} else {
					String[] resultList = result.replace(" ", "").split(",");
					if (resultList[0].equals("1")) {
						if (Double.parseDouble(resultList[1]) >= 0.5) {
							if (resultList.length >= 3 && checkResult(resultList[2])) {
								if (KeyList.IS_TTS_DEBUG) {
									mWaitResultInfo = result;
									mWaitResult = true;
									mLoseResult = false;
								} else {
									mLoseResult = true;
									mWaitResult = false;
									mHandler.removeCallbacks(mRunnable);
									sendCloud(mContext, "0," + resultList[2]);
									mResult.onResult("0," + resultList[2]);
								}
							} else {
								mWaitResultInfo = result;
								mWaitResult = true;
								mLoseResult = false;
							}
						} else {
							mWaitResultInfo = result;
							mWaitResult = true;
							mLoseResult = false;
						}
					} else {
						mWaitResultInfo = result;
						mWaitResult = true;
						mLoseResult = false;
					}
				}
			} else {
				if (isError) {
					if (mWaitResultInfo.equals("error")) {
						mHandler.removeCallbacks(mRunnable);
						HandleWakeup.startWakeup(mContext);
						mResult.onError("error");
					} else {
						String[] resultList = mWaitResultInfo.replace(" ", "").split(",");
						if (resultList[0].equals("1")) {
							if (Double.parseDouble(resultList[1]) >= 0.5) {
								if (resultList.length >= 3 && checkResult(resultList[2])) {
									mHandler.removeCallbacks(mRunnable);
									sendCloud(mContext, "0," + resultList[2]);
									mResult.onResult("0," + resultList[2]);
								} else {
									mLoseResult = true;
									mHandler.removeCallbacks(mRunnable);
									HandleWakeup.startWakeup(mContext);
									mResult.onError("error");
								}
							} else {
								mLoseResult = true;
								mHandler.removeCallbacks(mRunnable);
								HandleWakeup.startWakeup(mContext);
								mResult.onError("error");
							}
						} else {
							mLoseResult = true;
							mHandler.removeCallbacks(mRunnable);
							sendCloud(mContext, result);
							mResult.onResult(result);
						}
					}
				} else {
					if (mWaitResultInfo.equals("error")) {
						String[] resultList = result.replace(" ", "").split(",");
						if (resultList[0].equals("1")) {
							if (Double.parseDouble(resultList[1]) >= 0.5) {
								if (resultList.length >= 3 && checkResult(resultList[2])) {
									mHandler.removeCallbacks(mRunnable);
									sendCloud(mContext, "0," + resultList[2]);
									mResult.onResult("0," + resultList[2]);
								} else {
									mHandler.removeCallbacks(mRunnable);
									HandleWakeup.startWakeup(mContext);
									mResult.onError("error");
								}
							} else {
								mHandler.removeCallbacks(mRunnable);
								HandleWakeup.startWakeup(mContext);
								mResult.onError("error");
							}
						} else {
							mHandler.removeCallbacks(mRunnable);
							sendCloud(mContext, result);
							mResult.onResult(result);
						}
					} else {
						String[] resultList = mWaitResultInfo.replace(" ", "").split(",");
						String[] ress = result.replace(" ", "").split(",");
						LogManager.e(result);
						LogManager.e(mWaitResultInfo);
						if (resultList[0].equals("1")) {
							if (Double.parseDouble(resultList[1]) >= 0.5) {
								if (resultList.length < 3 || !checkResult(resultList[2])) {
									mLoseResult = true;
									mHandler.removeCallbacks(mRunnable);
									sendCloud(mContext, result);
									mResult.onResult(result);
								} else {
									mHandler.removeCallbacks(mRunnable);
									sendCloud(mContext, "0," + resultList[2]);
									mResult.onResult("0," + resultList[2]);
								}
							} else {
								mLoseResult = true;
								mHandler.removeCallbacks(mRunnable);
								sendCloud(mContext, result);
								mResult.onResult(result);
							}
						} else {
							if (Double.parseDouble(ress[1]) >= 0.5) {
								if (ress.length < 3 || !checkResult(ress[2])) {
									mLoseResult = true;
									mHandler.removeCallbacks(mRunnable);
									sendCloud(mContext, mWaitResultInfo);
									mResult.onResult(mWaitResultInfo);
								} else {
									mHandler.removeCallbacks(mRunnable);
									sendCloud(mContext, "0," + ress[2]);
									mResult.onResult("0," + ress[2]);
								}
							} else {
								mLoseResult = true;
								mHandler.removeCallbacks(mRunnable);
								sendCloud(mContext, mWaitResultInfo);
								mResult.onResult(mWaitResultInfo);
							}
							;
						}
					}
				}
				mWaitResult = false;
				mLoseResult = false;
			}
		} catch (Exception e) {

		}
	}

	/**
	 * 调试模式调用
	 * 
	 * @param isError
	 * @param errorCode
	 * @param result
	 */
	private synchronized void checkSpeechEnd(boolean isError, int errorCode, String result) {
		if (!mWait) {
			String strOn = "云端识别结果";
			String str = "本地识别结果";
			if (isError) {
				if (mResultInfo.equals("识别发生错误")) {
					str += mResultInfo;
					strOn += mResultInfo;
				} else {
					String[] resultList = mResultInfo.replace(" ", "").split(",");
					if (resultList[0].equals("1")) {
						String resLoc = "空字符串";
						if (resultList.length >= 3) {
							resLoc = resultList[2];
						}
						BaseContext baseContext = new BaseContext(mContext);
						str += resLoc + "识别时间" + baseContext.getGlobalLong(KeyList.PKEY_STRING_SPEECH_LOCAL_TIME) + "置信度为" + resultList[1];
						strOn += mResultInfo;
					} else {
						String resLoc = "空字符串";
						if (resultList.length >= 2) {
							resLoc = resultList[1];
						}
						BaseContext baseContext = new BaseContext(mContext);
						strOn += resLoc + "识别时间" + baseContext.getGlobalLong(KeyList.PKEY_STRING_SPEECH_ONLINE_TIME);
						str += mResultInfo;
					}
				}
			} else {
				if (mResultInfo.equals("识别发生错误")) {
					String[] resultList = result.replace(" ", "").split(",");
					if (resultList[0].equals("1")) {
						String resLoc = "空字符串";
						if (resultList.length >= 3) {
							resLoc = resultList[2];
						}
						BaseContext baseContext = new BaseContext(mContext);
						str += resLoc + "识别时间" + baseContext.getGlobalLong(KeyList.PKEY_STRING_SPEECH_LOCAL_TIME) + "置信度为" + resultList[1];
						strOn += mResultInfo;
					} else {
						String resLoc = "空字符串";
						if (resultList.length >= 2) {
							resLoc = resultList[1];
						}
						BaseContext baseContext = new BaseContext(mContext);
						strOn += resLoc + "识别时间" + baseContext.getGlobalLong(KeyList.PKEY_STRING_SPEECH_ONLINE_TIME);
						str += mResultInfo;
					}
				} else {
					String[] resultList = result.replace(" ", "").split(",");
					String[] results = mResultInfo.replace(" ", "").split(",");
					if (resultList[0].equals("1")) {
						BaseContext baseContext = new BaseContext(mContext);
						String resLoc = "空字符串";
						String resOnline = "空字符串";
						if (resultList.length >= 3) {
							resLoc = resultList[2];
						}
						if (results.length >= 2) {
							resOnline = results[1];
						}
						str += resLoc + "识别时间" + baseContext.getGlobalLong(KeyList.PKEY_STRING_SPEECH_LOCAL_TIME) + "置信度为" + resultList[1];
						strOn += resOnline + "识别时间" + baseContext.getGlobalLong(KeyList.PKEY_STRING_SPEECH_ONLINE_TIME);
						;
					} else {
						BaseContext baseContext = new BaseContext(mContext);
						String resLoc = "空字符串";
						String resOnline = "空字符串";
						if (resultList.length >= 2) {
							resLoc = resultList[1];
						}
						if (results.length >= 3) {
							resOnline = results[2];
						}
						strOn += resLoc + "识别时间" + baseContext.getGlobalLong(KeyList.PKEY_STRING_SPEECH_ONLINE_TIME);
						str += resOnline + "识别时间" + baseContext.getGlobalLong(KeyList.PKEY_STRING_SPEECH_LOCAL_TIME) + "置信度为" + results[1];
						;
					}
				}
			}
			LogManager.e(str + " " + strOn);
			ITTSController mTTSController = (ITTSController) ((IGloableHeap) mContext.getApplicationContext()).getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);
			mTTSController.syncPlay(str);
			mTTSController.syncPlay(strOn);
			int bufferSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
			AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
			track.play();
			BaseContext baseContext = new BaseContext(mContext);
			File file = new File(baseContext.getGlobalString(KeyList.PKEY_STRING_SPEECH_LOCAL_FILE_PATH));
			FileInputStream input;
			try {
				input = new FileInputStream(file);
				byte[] buffer = new byte[4096];
				int length = 0;
				while ((length = input.read(buffer)) > 0) {
					track.write(buffer, 0, length);
				}
				track.stop();
				track.release();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mWait = true;
			mHandler.removeCallbacks(mRunnable);
			HandleWakeup.startWakeup(mContext);
		} else {
			if (isError) {
				mResultInfo = "识别发生错误";
			} else {
				mResultInfo = result;
			}
			mWait = false;
		}
	}

	/**
	 * 通过广播传递数据给 RecogniseSystemProxy
	 * 
	 * @param context
	 * @param text
	 */
	private void sendCloud(Context context, String text) {
		LogManager.e(TAG, "sendCloud is " + text);
		if (text == null) {
			HandleWakeup.startWakeup(mContext);
			return;
		}
		String[] resultList = text.replace(" ", "").split(",");
		Intent intent = new Intent();
		intent.setAction(KeyList.AKEY_HANDLE_CLOUD_RECOGNISE);

		if (resultList.length > 2) {
			intent.putExtra("type", resultList[0]);
			intent.putExtra("confidence", resultList[1]);
			intent.putExtra("value", resultList[2]);
		} else if (resultList.length == 2) {
			intent.putExtra("type", resultList[0]);
			intent.putExtra("value", resultList[1]);
		}
		context.sendBroadcast(intent);
	}

	/***
	 * 将pcm数据写入到识别引擎 思必驰在线 、思必驰离线引擎
	 * 
	 * @param buffer
	 */

	public void writePCMData(byte[] buffer) {
		LogManager.e("buffer is ", buffer.toString());
		LogManager.e("buffer size is ", buffer.length + "");
		mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_BUFF_RECOGNING, true);
		mBufferRecogniseEngine.writePCMData(false, buffer, buffer.length);
	}

	// 检查是否能匹配到唤醒词 “xiaozhi”
	private synchronized boolean checkResult(String result) {
		String pinyin = PinYinUtil.getPinYin(result);
		Pattern pinyinStartPatter = Pattern.compile("(xiaozhi)");
		if (pinyinStartPatter.matcher(pinyin).find()) {
			return true;
		}
		return false;
	}

	// 调试模式下调用 ，可以通过语音命令进入调试模式
	private synchronized void checkDebug(boolean isError, int errorCode, String result) {
		checkSpeechEnd(isError, errorCode, result);
		onSpeechResult(isError, errorCode, result);
	}

	// 本次录音完成时，掉用
	public void stop() {
		LogManager.d(TAG, "======>> stop  writeNullPcm  and start callback result timer");
		mBufferRecogniseEngine.writePCMData(true, null, 0);
		mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_BUFF_RECOGNING, false);
		mHandler.postDelayed(mRunnable, mWaitTime);
	}

	// 当前对象销毁时调用
	public void destroy() {
		mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_BUFF_RECOGNING, false);
		mBufferRecogniseEngine.onDestory();
		mWaitResult = false;
		mLoseResult = false;
	}

	/**
	 * 语音结果返回的超时设置
	 */
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			LogManager.d(TAG, "======>> callback result timeout to startWakeup");
			HandleWakeup.startWakeup(mContext);
		}
	};

	/*****************************************************************************
	 * *************************************************************************
	 * 识别结果状态监听实现类
	 * 
	 * @author Peter
	 * @data 2015年4月28日下午2:14:22
	 */
	private class BufferRecogniseStateImp implements IStateListener {

		@Override
		public void onStart() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onEndofBuffer() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onResult(String result) {
			// TODO Auto-generated method stub
			// 在线语音Debug播报
			if (KeyList.IS_TTS_DEBUG) {
				// checkSpeechEnd(false, 0, result);
				checkDebug(false, 0, result);
			} else {
				int houseDevicesCount = mBaseContext.getPrefInteger(KeyList.HOUSE_MECHINE_SETTING_COUNT, 0);
				if (houseDevicesCount > 0) {
//					onSpeechResult(false, 0, result);
					onSpeechResultForOnline(true, result);
				} else {
					onSpeechResultForOnline(true, result);
				}

			}
		}

		@Override
		public void onError(int errorCode) {
			// TODO Auto-generated method stub
			// 在线语音Debug播报
			if (KeyList.IS_TTS_DEBUG) {
				checkDebug(true, errorCode, null);
				// checkSpeechEnd(true, errorCode, null);
			} else {
				int houseDevicesCount = mBaseContext.getPrefInteger(KeyList.HOUSE_MECHINE_SETTING_COUNT, 0);
				if (houseDevicesCount > 0) {
//					onSpeechResult(true, errorCode, null);
					onSpeechResultForOnline(true, null);
				} else {
					onSpeechResultForOnline(true, null);
				}
			}
		}

	}

}
