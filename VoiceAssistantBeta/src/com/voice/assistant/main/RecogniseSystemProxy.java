package com.voice.assistant.main;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.iii360.base.common.utl.AbstractReceiver;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.ICommandEngineSensitive;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.ITTSSensitive;
import com.iii360.base.inf.parse.ICommandEngine;
import com.iii360.base.inf.parse.ITextDisposer;
import com.iii360.base.inf.recognise.ILightController;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.iii360.base.umeng.UmengUtil;
import com.iii360.external.recognise.RecogniseSystemBufferBuildFactory;
import com.iii360.external.wakeup.HandleWakeup;
import com.iii360.sup.common.utl.PinYinUtil;
import com.voice.assistant.main.music.MyMusicHandler;
import com.voice.assistant.utl.DlanMusicController;

/**
 * @author Jerome.Hu 识别系统的一个替身。 主要用来对识别结果的统一处理。
 * 
 *         通过 mDefaultOnResultListener ，mOnResultListener 回调结果，处理结果。
 * 
 */
public class RecogniseSystemProxy extends AbstractReceiver implements ITextDisposer, IRecogniseSystem, ICommandEngineSensitive, ITTSSensitive {
	public static final String AKEY_SHOW_EXTRA_INFO = "AKEY_SHOW_USC_INFO";
	public static final String AKEY_HIDDEN_EXTRA_INFO = "AKEY_HIDDEN_USC_INFO";
	public static final String AKEY_SHOW_VOICE_INPUT = "AKEY_SHOW_VOICE_INPUT";

	private ILightController lightController;
	private IRecogniseSystem mRecSystem;
	private HandleWakeup mWakeupRecSystem;
	private IOnResultListener mDefaultOnResultListener;
	private IOnResultListener mOnResultListener;
	private ICommandEngine mCommandEngine;
	private Context mContext;
	private BaseContext mBaseContext;
	private ITTSController mTtsController;
	private BasicServiceUnion mUnion;

	private String pinyinstartTagRegex = "(xiaozhi|xiaozi|xiazhi|xiazi|xiaochi)";
	private String opriteStartRegex = "^(dakai|guanbi)";

	private Pattern mPinyinStartPatter = Pattern.compile(pinyinstartTagRegex);
	private Pattern mOpritePatter = Pattern.compile(opriteStartRegex);
	private BroadcastReceiver mBroadcastReceiver;
	// 是否暂停了本地音乐或者网络音乐
	private boolean isSelfStopMusic = false;
	// 是否暂停了dlna或者airplayer
	private boolean isSelfStopMusicForDlna = false;

	private DlanMusicController mDlanMusicController;

	private void sendBroadcastForMusic(Context context, boolean isStart) {

		if (isStart) {
			if (mDlanMusicController.isDlan() && isSelfStopMusicForDlna) {
				isSelfStopMusicForDlna = false;
				LogManager.e("mDlanMusicController.paly()");
				mDlanMusicController.paly();
			} else if (isSelfStopMusic) {
				isSelfStopMusic = false;
				((MyMusicHandler) mUnion.getMediaInterface()).recogniseResume();
			}
			isSelfStopMusicForDlna = false;
		} else {
			isSelfStopMusicForDlna = false;
			isSelfStopMusic = false;
			if (mBaseContext.getGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, false)) {
				if (mDlanMusicController.isDlan()) {
					isSelfStopMusicForDlna = true;
					LogManager.e("mDlanMusicController.pause()");
					mDlanMusicController.pause();
				} else if (mUnion.getMediaInterface() != null) {
					isSelfStopMusic = true;
					((MyMusicHandler) mUnion.getMediaInterface()).pause();
				}
			}
		}

	}

	/**
	 * 创建识别代理类对象
	 * 
	 * @param recogniseDlg
	 * @param context
	 * @param union
	 */

	public RecogniseSystemProxy(ILightController controller, Context context, BasicServiceUnion union) {
		lightController = controller;
		mContext = context;
		mUnion = union;
		mBaseContext = new BaseContext(mContext);
		mDlanMusicController = new DlanMusicController(context);
		getDefaultResultListener();
		initRecogniseSystem();
		addActionMapping(KeyList.AKEY_CHANGE_RECOGNISE_ENGINE, new OnReceiverListener() {

			@Override
			public void onReceiver(Context context, Intent intent) {
				// TODO Auto-generated method stub
				initRecogniseSystem();
			}
		});
		register(mContext);

	}

	// 获取默认结果监听
	private IOnResultListener getDefaultResultListener() {
		if (mDefaultOnResultListener == null) {
			mDefaultOnResultListener = new IOnResultListener() {
				@Override
				public void onResult(String text) {
					// 在线语音Debug播报
					String ttsmsg = text;
					if (KeyList.IS_TTS_DEBUG) {
						KeyList.VOICE_RECOGNIZER_FINISH = System.currentTimeMillis();
						ITTSController mTTSController = (ITTSController) ((IGloableHeap) mContext.getApplicationContext()).getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);
						mTTSController.syncPlay("正在在线语音识别");
						ttsmsg = "空字符串";
						if (text.length() > 0) {
							ttsmsg = "在线语音识别结果为" + text.replaceAll("[\\d,]", "") + "耗时" + (KeyList.VOICE_RECOGNIZER_FINISH - KeyList.VOICE_RECOGNIZER_BEGIN) + "毫秒";
						}
						mTTSController.syncPlay(ttsmsg);
					}

					// TODO Auto-generated method stub
					LogManager.d("text is " + text);
					if (text != null && !"".equals(text)) {
						mCommandEngine.handleText(text);
					} else {
						String str = "对不起，没听清！";
						mTtsController.play(str);

					}
					// 重置
					mBaseContext.setGlobalBoolean(KeyList.GKEY_FORCE_RECOGNISE, false);
				}

				@Override
				public void onError(int errorCode) {
					ITTSController mTTSController = (ITTSController) ((IGloableHeap) mContext.getApplicationContext()).getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);
					// 只有按键识别发生的错误，或者调试，才会允许播报
					if (mBaseContext.getGlobalBoolean(KeyList.GKEY_FORCE_RECOGNISE) || mBaseContext.getGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE, false) || KeyList.IS_TTS_DEBUG) {
						String str = "没听清，请再说一遍！";
						switch (errorCode) {
						case IRecogniseSystem.ERROR_NETWORK:
							if (mBaseContext.getGlobalBoolean(KeyList.GKEY_BOOL_IS_CONNECT_WIFIGATE)) {
								str = "网速过慢，请检查网络";
							} else {
								str = "音箱未联网，请扫描音箱底部二维码下载小智助手配置网络";
							}
							break;
						default:
							KeyList.VOICE_RECOGNIZER_FINISH = System.currentTimeMillis();
							if (KeyList.IS_TTS_DEBUG) {
								mTTSController.play("识别发生错误");
							}
							str = "没听清，请再说一遍！";
						}
						mTTSController.play(str);
					}

					int number = mBaseContext.getGlobalInteger(KeyList.GKEY_INT_AUTO_CHAT_MODE_NUMBER);
					if (number < 1) {
						mBaseContext.setGlobalInteger(KeyList.GKEY_INT_AUTO_CHAT_MODE_NUMBER, ++number);
					} else {
						mBaseContext.setGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE, false);
						startCaptureVoice();
					}
					// 重置
					mBaseContext.setGlobalBoolean(KeyList.GKEY_FORCE_RECOGNISE, false);
				}

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
				}

				@Override
				public void onEnd() {
					// TODO Auto-generated method stub

				}
			};
		}

		return mDefaultOnResultListener;

	}

	/**
	 * 初始化识别系统 通过的识别系统工厂类，创建识别引擎
	 */
	private void initRecogniseSystem() {

		mWakeupRecSystem = new HandleWakeup(mContext, lightController);

		RecogniseSystemBufferBuildFactory.setMusicRes(R.raw.tts_voice_start, R.raw.yiya_voice_start, R.raw.yiya_voice_stop, R.raw.tone_error_ogg);

		mRecSystem = RecogniseSystemBufferBuildFactory.buildRecogniseSystem(mContext, RecogniseSystemBufferBuildFactory.ENGINE_TYPE_BUFFER_DOUBLE_DEFAULT);
		mRecSystem.bindRecogniseButton(lightController);

		final IOnResultListener inOnResultListener = new IOnResultListener() {
			@Override
			public void onResult(String text) {
				// TODO Auto-generated method stub
				LogManager.e(text);
				// 保存原始的语音文本
				if (mBaseContext.getGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_CAUSE) != KeyList.VOICE_COMMAND_CAUSE) {
					mBaseContext.setGlobalString(KeyList.RECOR__VOICE_RECONGINISE_RESULT, text);
				}
				if (System.currentTimeMillis() - mBaseContext.getGlobalLong(KeyList.GKEY_LONG_CHATMODE_BEGINTIME) < 20000) {
					mBaseContext.setGlobalLong(KeyList.GKEY_LONG_CHATMODE_BEGINTIME, System.currentTimeMillis());
				} else {
					mBaseContext.setGlobalBoolean(KeyList.GKEY_BOOL_CHATMODE, false);
				}
				if (!"".equals(text)) {
					/**
					 * 长按操作更改为：如果只有唤醒词，播报“请吩咐”，重新开始识别。
					 */
					matchAwakeWork(text, true);
				} else {
					String str = "没听清，请再说一遍！";
					mTtsController.play(str);
					int number = mBaseContext.getGlobalInteger(KeyList.GKEY_INT_AUTO_CHAT_MODE_NUMBER);
					LogManager.e(number + "");
					if (number < 1) {
						mBaseContext.setGlobalInteger(KeyList.GKEY_INT_AUTO_CHAT_MODE_NUMBER, ++number);
					} else {
						mBaseContext.setGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE, false);
						startCaptureVoice();
					}
				}
				sendBroadcastForMusic(mContext, true);
			}

			@Override
			public void onError(int errorCode) {
				// TODO Auto-generated method stub
				if (mOnResultListener != null) {
					mOnResultListener.onError(errorCode);
					mBaseContext.setGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE, false);
					startCaptureVoice();
				} else {
					mDefaultOnResultListener.onError(errorCode);
				}
				sendBroadcastForMusic(mContext, true);
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				LogManager.e(" Main recognise onResultListener !onStart()");
				LogManager.i("");
				if (mTtsController != null) {
					mTtsController.stop();
				}
				if (mOnResultListener != null) {
					mOnResultListener.onStart();
				} else {
					mDefaultOnResultListener.onStart();
				}
			}

			@Override
			public void onEnd() {
				// TODO Auto-generated method stub

			}
		};

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(KeyList.AKEY_HANDLE_CLOUD_RECOGNISE);

		mBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if (intent.getAction().equals(KeyList.AKEY_HANDLE_CLOUD_RECOGNISE)) {
					String type = intent.getStringExtra("type");
					if (type == null || type.equals("")) {
						mWakeupRecSystem.startCaptureVoice();
						return;
					}
					if (type.equals("1")) { // 离线识别引擎
						mBaseContext.setGlobalInteger(KeyList.RECONGINSE_ENGINE_TYPE, 1);
					} else {
						mBaseContext.setGlobalInteger(KeyList.RECONGINSE_ENGINE_TYPE, 2);
					}
					String result = intent.getStringExtra("value");
					String confidence = intent.getStringExtra("confidence");
					// 设置唤醒词事件
					mBaseContext.setGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_CAUSE, KeyList.AWAKEN_WORD_CAUSE);
					// 保存原始文本
					mBaseContext.setGlobalString(KeyList.RECOR__VOICE_RECONGINISE_RESULT, result);
					LogManager.e("reciverd::" + result);
					double d = 60;
					if (result == null || result.equals("无匹配") || result.length() < 2) {
						mWakeupRecSystem.startCaptureVoice();
						return;
					}
					result = result.replaceAll(" ", "");
					if (confidence != null) {
						d = Double.parseDouble(confidence);
					}
					// String head = result.substring(0, 2);
					String pinyin = PinYinUtil.getPinYin(result);
					LogManager.e(pinyin + " head finded " + mPinyinStartPatter.matcher(pinyin).find());
					boolean xiaozhimatch = mPinyinStartPatter.matcher(pinyin).find();
					if (xiaozhimatch) {
						List<String> pinyins = PinYinUtil.getPinYinSet(result);
						boolean first = false;
						int i = 0;
						for (String singlePinyin : pinyins) {
							LogManager.e(singlePinyin);
							i++;
							if (first) {
								first = false;
								if (singlePinyin.startsWith("zhi") || singlePinyin.startsWith("zi")) {
									LogManager.e(i + "  sub i");
									result = result.substring(i);
									break;
								}
							} else if (singlePinyin.startsWith("xia")) {
								first = true;
							}
						}
					}
					LogManager.e("result  " + result);
					boolean opriteMatch = mOpritePatter.matcher(pinyin).find();

					if (xiaozhimatch || opriteMatch) {
						if (result.length() <= 1) {
							// 仅出现唤醒词
							mBaseContext.setGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_CAUSE, KeyList.AWAKEN_WORD_CAUSE);

							Pattern pinyinStartPatter = Pattern.compile("(xiaozhi)");
							if (pinyinStartPatter.matcher(pinyin).find()) {
								// 只包含xiaozhi的才会被唤醒
								mBaseContext.setGlobalBoolean(KeyList.PKEY_NEED_START_IMEDIATELEY_AFTER_RECOGNISE, true);
								mBaseContext.setGlobalBoolean(KeyList.GKEY_FORCE_RECOGNISE, true);
								startCaptureVoice();
								mBaseContext.setGlobalBoolean(KeyList.PKEY_NEED_START_IMEDIATELEY_AFTER_RECOGNISE, false);
							} else {
								startCaptureVoice();
							}
							// 统计唤醒词唤醒
							mBaseContext.sendUmengEvent(UmengUtil.WAKEUP_WORD_TO_WAKEUP, UmengUtil.WAKEUP_WORD_TO_WAKEUP_CONTENT);
						} else {
							if (d > 52) {
								// 一句话唤醒
								mBaseContext.setGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_CAUSE, KeyList.VOICE_COMMAND_CAUSE);
								LogManager.e("try " + result);
								// 直接唤醒词用来执行
								inOnResultListener.onResult(result);
								if (mBaseContext.getGlobalBoolean(KeyList.GKEY_BOOL_CHATMODE)) {
									mBaseContext.setGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE, true);
									mBaseContext.setGlobalInteger(KeyList.GKEY_INT_AUTO_CHAT_MODE_NUMBER, 0);
									mBaseContext.setGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_MODE, KeyList.AUTO_CHATED_MODE);
								}
								// 统计一句话唤醒
								mBaseContext.sendUmengEvent(UmengUtil.A_WORD_WAKEUP, UmengUtil.A_WORD_WAKEUP_CONTENT);
							} else {
								mTtsController.play("没听清，请再说一遍");
							}

						}
					} else {
						// mTtsController.play("对不起,没听清");
						startCaptureVoice();
					}
				}

			}
		};

		mContext.registerReceiver(mBroadcastReceiver, mFilter);
		mRecSystem.setOnResultListener(inOnResultListener);
	}

	/**
	 * 
	 * @param text voice original text
	 * @param isPlay is or not paly voice reminder
	 */
	public void matchAwakeWork(String result, boolean isPlay) {
		String pinyin = PinYinUtil.getPinYin(result);
		LogManager.d(" before dispose of result_0 :" + result);
		boolean xiaozhimatch = mPinyinStartPatter.matcher(pinyin).find();
		if (xiaozhimatch) {
			List<String> pinyins = PinYinUtil.getPinYinSet(result);
			boolean first = false;
			int i = 0;
			for (String singlePinyin : pinyins) {
				LogManager.e(singlePinyin);
				i++;
				if (first) {
					first = false;
					if (singlePinyin.startsWith("zhi") || singlePinyin.startsWith("zi")) {
						LogManager.e(i + "  sub i");
						result = result.substring(i);
						break;
					}
				} else if (singlePinyin.startsWith("xia")) {
					first = true;
				}
			}
		}
		LogManager.d(" before dispose of result_1 :" + result);
		if (result.length() <= 1 && xiaozhimatch) {
			Pattern pinyinStartPatter = Pattern.compile("(xiaozhi)");
			if (pinyinStartPatter.matcher(pinyin).find()) {
				// 只包含xiaozhi的才会被唤醒
				LogManager.d("before dispose of result_2 :" + result);
				mBaseContext.setGlobalBoolean(KeyList.PKEY_NEED_START_IMEDIATELEY_AFTER_RECOGNISE, true);
				mBaseContext.setGlobalBoolean(KeyList.GKEY_FORCE_RECOGNISE, true);
				startCaptureVoice();
				mBaseContext.setGlobalBoolean(KeyList.PKEY_NEED_START_IMEDIATELEY_AFTER_RECOGNISE, false);
			} else {
				LogManager.d("before dispose of result_3 :" + result);
				startCaptureVoice();
			}
		} else {
			LogManager.d("before dispose of result_4 :" + result);
			// 直接唤醒词用来执行
			dispose(result);
			if (mBaseContext.getGlobalBoolean(KeyList.GKEY_BOOL_CHATMODE)) {
				mBaseContext.setGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE, true);
				mBaseContext.setGlobalInteger(KeyList.GKEY_INT_AUTO_CHAT_MODE_NUMBER, 0);
				mBaseContext.setGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_MODE, KeyList.AUTO_CHATED_MODE);
			}
		}
	}

	@Override
	public void startCaptureVoice() {

		if (System.currentTimeMillis() - mBaseContext.getGlobalLong(KeyList.GKEY_LONG_CHATMODE_BEGINTIME) < 20000) {
			mBaseContext.setGlobalLong(KeyList.GKEY_LONG_CHATMODE_BEGINTIME, System.currentTimeMillis());
		} else {
			mBaseContext.setGlobalBoolean(KeyList.GKEY_BOOL_CHATMODE, false);
		}
		// 获取事件类型
		if ((mBaseContext.getGlobalBoolean(KeyList.PKEY_NEED_START_IMEDIATELEY_AFTER_RECOGNISE) || mBaseContext.getGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE)) && mRecSystem != null) {
			// 防止唤醒唤醒切换识别中，被其他线程，重新开启唤醒
			mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_WAKEUP_TO_RECOGNISE, true);
			mRecSystem.cancelRecognising();
			// 暂停正在播放的音乐
			sendBroadcastForMusic(mContext, false);
			mWakeupRecSystem.stopCaptureVoice();
			mRecSystem.startCaptureVoice();
			if (mBaseContext.getGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING) && !mBaseContext.getGlobalBoolean(KeyList.GKEY_BOOL_IS_CONNECT_WIFIGATE)) {
				mUnion.getMediaInterface().resume();
			}
		} else if (mWakeupRecSystem != null) {
			boolean city_Code_Error = mBaseContext.getGlobalBoolean(KeyList.GKEY_CURRENT_CITYCODE_IS_NULL, false);
			int city_Code_Error_Count = mBaseContext.getGlobalInteger(KeyList.GKEY_CURRENT_CITYCODE_ERROR_COUNT, 0);
			if (city_Code_Error && mRecSystem != null && city_Code_Error_Count != 0) {// 查询天气时，没有找到城市cityCode继续识别处理
				mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_WAKEUP_TO_RECOGNISE, true);
				mRecSystem.cancelRecognising();
				mWakeupRecSystem.stopCaptureVoice();
				mRecSystem.startCaptureVoice();
				mBaseContext.setGlobalBoolean(KeyList.GKEY_CURRENT_CITYCODE_IS_NULL, false);
			} else {
				// 口述唤醒词方式唤醒
				mWakeupRecSystem.startCaptureVoice();

				// 播报天气，城市code不是连续报错，清除错误次数记录
				mBaseContext.setGlobalInteger(KeyList.GKEY_CURRENT_CITYCODE_ERROR_COUNT, 0);
			}
		} else {
			LogManager.e("other reason");
		}
	}

	@Override
	public void stopCaptureVoice() {
		// LogManager.printStackTrace();
		if (mWakeupRecSystem != null) {
			mWakeupRecSystem.stopCaptureVoice();
		}
		if (mRecSystem != null) {
			mRecSystem.stopCaptureVoice();
		}
	}

	@Override
	public void cancelRecognising() {
		// TODO Auto-generated method stub
		if (mWakeupRecSystem != null) {
			mWakeupRecSystem.cancelRecognising();
		}
		if (mRecSystem != null) {
			mRecSystem.cancelRecognising();
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		LogManager.e("destroy");
		if (mWakeupRecSystem != null) {
			mWakeupRecSystem.destroy();
			mWakeupRecSystem = null;
		}
		if (mRecSystem != null) {
			mRecSystem.destroy();
			mRecSystem = null;
		}
		mContext.unregisterReceiver(mBroadcastReceiver);
		unRegister(mContext);
	}

	@Override
	public void setOnResultListener(IOnResultListener onResultListener) {
		// TODO Auto-generated method stub
		mOnResultListener = onResultListener;
	}

	@Override
	public void setCommandEngine(ICommandEngine commandEngine) {
		// TODO Auto-generated method stub
		mCommandEngine = commandEngine;
	}

	@Override
	public ICommandEngine getCommandEngine() {
		// TODO Auto-generated method stub
		return mCommandEngine;
	}

	@Override
	public ITTSController getTTSController() {
		// TODO Auto-generated method stub
		return mTtsController;
	}

	@Override
	public void setTTSController(ITTSController ttsController) {
		// TODO Auto-generated method stub
		mTtsController = ttsController;
	}

	@Override
	public void dispose(String text) {
		// TODO Auto-generated method stub
		LogManager.e("text is " + text);

		text = delInvalidText(text);
		if (mOnResultListener != null) {
			LogManager.e("mOnResultListener " + mOnResultListener);
			mOnResultListener.onResult(text);
		} else {
			mDefaultOnResultListener.onResult(text);
		}
	}

	private static String trimSpecWord(String src) {
		if (src.length() > 1) {
			String end = src.substring(src.length() - 1);
			String start = src.substring(0, src.length() - 1);
			end = end.replaceAll("[!.,;:'?！~、，。；：‘？]", "");
			return start + end;
		}
		return src;
	}

	protected String delInvalidText(String text) {
		String ret = "";
		Pattern pattern = Pattern.compile("(我是谁)(。?|？?)(\\(a[\\d]{8}\\))(。?|？?)");

		if (text != null) {
			ret = text.trim();

			if (!ret.equals("")) {
				Matcher matcher = pattern.matcher(text);
				if (matcher.matches()) {
					ret = "我是谁";
				}
			}

			ret = trimSpecWord(ret);

		}
		ret = ret.replace(" ", "");
		return ret;
	}

	@Override
	public void dispatchUserAction() {
		// TODO Auto-generated method stub
		if (mRecSystem != null) {
			mRecSystem.dispatchUserAction();
		}
	}

	@Override
	public void bindRecogniseButton(ILightController recogniseButton) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startWakeup() {
		mWakeupRecSystem.startCaptureVoice();
	}

	@Override
	public void stopWakeup() {
		mWakeupRecSystem.stopCaptureVoice();
	}

}
