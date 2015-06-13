package com.iii360.voiceassistant.semanteme.common;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.base.data.CommandInfo;
import com.base.platform.OnDataReceivedListener;
import com.base.platform.Platform;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.ICommandEngine;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.iii360.sup.common.utl.HomeConstants;
import com.iii360.sup.common.utl.SupKeyList;
import com.iii360.sup.common.utl.UploadVoiceLogToServer;
import com.iii360.voiceassistant.semanteme.command.CommandFactory;
import com.voice.assistant.main.KeyList;
import com.voice.common.util.nlp.CommandExcuteTimeProcess;
import com.voice.common.util.nlp.HouseCommandProcess;

/**
 * 命令解析引擎
 * 
 * @author Peter
 * @data 2015年4月9日上午10:30:41
 */
public class CommandEngine implements ICommandEngine {

	/******************** Memeber variables ******************/
	private static final String Tag = "CommandEngine";
	private Platform mPlatform = null;
	public Context mContext = null;
	private OnDataReceivedListener mDataReceivedListener = null;
	private BaseContext base = null;
	private BasicServiceUnion mUnion = null;
	private Handler mHandler = null;

	/*********************** Constructor Method *****************************/

	public CommandEngine(BasicServiceUnion union) {
		mUnion = union;
		mContext = mUnion.getBaseContext().getContext();
		initNlpPlatform();
		mHandler = new Handler();
		base = new BaseContext(mContext);
	}

	private void initNlpPlatform() {
		final OnDataReceivedListener defaultOnDataReceivedListener = new OnDataReceivedListener() {
			@Override
			public void onError(int errorCode) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "errorCode is " + errorCode, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onDataReceived(CommandInfo commandInfo) {
				// TODO Auto-generated method stub
				String infoString = commandInfo.toCommandString();
				LogManager.i(Tag, "onDataReceived-----" + infoString);
				if (infoString != null) {
					praseCommandInfo(commandInfo, infoString);
				}
				if (commandInfo._commandName.equals("CommandHotel") || commandInfo._commandName.equals("CommandHotelEvaluation") || commandInfo._commandName.equals("CommandHotelInfo")
						|| commandInfo._commandName.equals("CommandHotelNavigation") || commandInfo._commandName.equals("CommandHotelPosition") || commandInfo._commandName.equals("CommandHotelPrice")
						|| commandInfo._commandName.equals("CommandHotelStar") || commandInfo._commandName.equals("CommandHotelFacilities")) {
					base.setPrefString(KeyList.PKEY_VALID_TIME, (100 * 1000) + "");
				} else if (commandInfo._commandName.equals("CommandPlayMedia")) {
					base.setPrefString(KeyList.PKEY_VALID_TIME, (1 * 1000) + "");
				} else {
					base.setPrefString(KeyList.PKEY_VALID_TIME, (3000 * 1000) + "");
				}
				if (!(commandInfo._commandName.equals("CommandChat") || commandInfo._commandName.equals("CommandConfirm") || commandInfo._commandName.equals("CommandStudy") || commandInfo._commandName
						.equals("CommandExtendData"))) {
					base.setGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE, false);
				}
				if (commandInfo._commandName.equals("CommandChatMode")) {
					base.setGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_MODE, KeyList.AUTO_CHATED_MODE);
				} else {
					base.setGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_MODE, KeyList.DEFAULT_MODE);
				}
				executeUseCommandInfo(commandInfo, mDoWhenCommandIsNull);
			}
		};
		mDataReceivedListener = defaultOnDataReceivedListener;
		mPlatform = Platform.getPlatformInstance(mContext, mDataReceivedListener);
	}

	private void praseCommandInfo(final CommandInfo commandInfo, final String infoString) {
		new Thread() {
			public void run() {
				String originVoiceText = base.getGlobalString(KeyList.RECOR__VOICE_RECONGINISE_RESULT);
				String[] infos = infoString.split("\\|\\|");
				if (infos.length >= 2) {
					String commandString = "未知";
					/**
					 * CommandPlayMedia||0||102102[张学友^爱情组曲][http://cdnmusic. hezi.360iii.net/hezimusic/102102.mp3], 以下方法解析歌曲信息为上传日志
					 */
					if (commandInfo._commandName.equals("CommandPlayMedia") || commandInfo._commandName.equals("CommandMediaControl") || commandInfo._commandName.equals("CommandStoryHezi")
							|| commandInfo._commandName.equals("CommandJoke")) {
						try {
							String firstInfo[] = infoString.split(",");
							if (firstInfo.length >= 1) {
								commandString = firstInfo[0];
								String musicInfo[] = commandString.split("\\|\\|");
								if (musicInfo.length >= 3 && !musicInfo[2].equals("")) {
									commandString = musicInfo[2].substring(musicInfo[2].indexOf("[") + 1, musicInfo[2].indexOf("]"));
									String mediainfo[] = commandString.split("\\^");
									if (mediainfo.length >= 2) {
										commandString = commandInfo._commandName + "||" + mediainfo[0] + "||" + mediainfo[1] + "||X||X";
									} else {
										commandString = commandInfo._commandName + "||" + musicInfo[1] + "||" + musicInfo[2].substring(0, musicInfo[2].indexOf("["));
									}
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
							LogManager.e(Tag, "parse commandInfo exception for upload log to server");
						}

					} else {
						commandString = commandInfo._commandName;
					}
					if (commandInfo._commandName != null && !commandInfo._commandName.equals("CommandMediaControl") && originVoiceText != null) {
						new UploadVoiceLogToServer(base, originVoiceText, commandString).start();
					}
				} else {
					if (originVoiceText != null && commandInfo._commandName != null) {
						new UploadVoiceLogToServer(base, originVoiceText, commandInfo._commandName).start();
					}

				}
			}
		}.start();
	}

	protected void sendAnswerSession(String string) {
		getUnion().getMainThreadUtil().sendNormalWidget(string);
	}

	public void handleText(String text, boolean isOnlyToServe, boolean isNeedShowWidget) {
		LogManager.i(Tag, "handleText:" + text + "---isOnlyToServe:" + isOnlyToServe);
		String originVoiceText = base.getGlobalString(KeyList.RECOR__VOICE_RECONGINISE_RESULT);
		sendRunnable();
		final Params params = new Params(mContext);
		mPlatform.setAdditionalParams(params.getCommonParams());

		// 语音结果的为空重新开启唤醒
		if (text == null || "".equals(text)) {
			mHandler.post(voiceTextNull);
			return;
		}

		// 记录语义开始解析时间
		SupKeyList.SEMANTEME_RECOGNIZER_BEGIN = System.currentTimeMillis();

		if (mUnion.getBaseContext().getPrefBoolean(HomeConstants.ABOX_CONNECT, false)) {
			if (!isOnlyToServe) {
				mPlatform.sendSession(text);
			} else {
				mPlatform.sendRemoteSession(text);
			}
		} else {
			boolean isSettingCheat = CommandExcuteTimeProcess.getInstance(mUnion).handText(text);
			if (isSettingCheat) {
				LogManager.i(Tag, "handleText setting Cheat is:" + isSettingCheat);
				// 记录语义解析结束时间
				SupKeyList.SEMANTEME_RECOGNIZER_FINISH = System.currentTimeMillis();
				new UploadVoiceLogToServer(base, originVoiceText, "CommandExcuteTimeProcess").start();
			} else {
				if (!isOnlyToServe) {
					if (HouseCommandProcess.getInstace(mUnion).handText(text)) {
						// 记录语义解析结束时间
						SupKeyList.SEMANTEME_RECOGNIZER_FINISH = System.currentTimeMillis();
						new UploadVoiceLogToServer(base, originVoiceText, "HouseCommand").start();
					} else {
						mPlatform.sendSession(text);
					}
				} else {
					mPlatform.sendRemoteSession(text);
				}
			}
		}
	}

	public void setOnDataReceivedListener(OnDataReceivedListener onDataReceivedListener) {
		mDataReceivedListener = onDataReceivedListener;
		if (mPlatform != null) {
			mPlatform.setOnDataReceivedListener(mDataReceivedListener);
		}
	}

	public void handleText(String text) {
		LogManager.i(Tag, "handleText ---- com from native  recongnise callback");
		handleText(text, false, true);
	}

	public BasicServiceUnion getUnion() {
		return mUnion;
	}

	public void handleCommandInfo(CommandInfo commandInfo) {
		executeUseCommandInfo(commandInfo, mDoWhenCommandIsNull);
	}

	protected void executeUseCommandInfo(CommandInfo commandInfo, Runnable doHasCheckedCommandNull) {
		LogManager.i(Tag, "executeUseCommandInfo commandInfo:" + commandInfo.toCommandString());
		// 记录语义解析结束时间
		SupKeyList.SEMANTEME_RECOGNIZER_FINISH = System.currentTimeMillis();
		if (commandInfo != null) {
			removedRunnable();
			// 空命令不做任何提示，也不进行响应的操作
			if (commandInfo._commandName == null || commandInfo._commandName.equals("")) {
				mUnion.getMainThreadUtil().sendNormalWidget("");
				return;
			}
			if (commandInfo._answer != null) {
				mUnion.getMainThreadUtil().sendNormalWidget(commandInfo._answer);
				return;
			}
			IVoiceCommand voiceCommand = CommandFactory.createCommand(commandInfo._commandName, mUnion, commandInfo);
			if (voiceCommand == null) {
				doHasCheckedCommandNull.run();
				return;
			}
			while (voiceCommand != null) {
				final IVoiceCommand nowCommand = voiceCommand;
				voiceCommand = voiceCommand.execute();
				nowCommand.release();
			}
		}
	}

	/**
	 * 当命令为空时，给出语音提示
	 */
	private Runnable mDoWhenCommandIsNull = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			sendAnswerSession("不好意思，小智不明白你说的话");
		}
	};

	/***
	 * 语音结果为空，空语音提示，开启唤醒
	 */
	private Runnable voiceTextNull = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			sendAnswerSession("");
		}
	};

	private void sendRunnable() {
		mHandler.removeCallbacks(mRunnableCheckNetWork);
		mHandler.postDelayed(mRunnableCheckNetWork, 12000);
	}

	private void removedRunnable() {
		LogManager.e("removed runnable!");
		mHandler.removeCallbacks(mRunnableCheckNetWork);
	}

	/**
	 * 网络请求超时提示
	 */
	private Runnable mRunnableCheckNetWork = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			LogManager.e("check netWork,error");
			// Toast.makeText(mContext, "您的网络出问题啦！", Toast.LENGTH_SHORT).show();
		}

	};

}
