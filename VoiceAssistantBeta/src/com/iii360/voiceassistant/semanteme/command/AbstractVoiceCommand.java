package com.iii360.voiceassistant.semanteme.command;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.ITTSController.ITTSStateListener;
import com.iii360.base.inf.IViewContainer;
import com.iii360.base.inf.IVoiceWidget;
import com.iii360.base.inf.parse.ICommandEngine;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.iii360.base.inf.recognise.IRecogniseSystem.IOnResultListener;
import com.iii360.base.umeng.UmengUtil;
import com.voice.assistant.main.KeyList;

/**
 * 所有 VoiceCommand 基类.
 * 
 * @author Jerome.Hu
 * 
 */
public abstract class AbstractVoiceCommand implements IVoiceCommand {
	/**
	 * String message;
	 */
	public static final String MESSAGE = "msg";
	private static final String TTS_CONTROLLER_ERROR_IS_NULL = "TTSController is null!";

	public interface OnRecognizerEndListener {
		public void onRecognizerEnd(String text);
	}

	protected Context mContext;
	protected BaseContext mBaseContext;
	protected IRecogniseSystem mRecSystem;
	protected ITTSController mTTSController;
	protected IViewContainer mViewContainer;
	private Map<String, Object> mData;
	private String mCommandName;
	private String mCommandDesc;

	private ICommandEngine mCommandEngine;

	private BasicServiceUnion mUnion;

	public AbstractVoiceCommand(BasicServiceUnion union, CommandInfo commandInfo, String commandName, String commandDesc) {
		mUnion = union;
		if (mUnion != null) {
			mTTSController = mUnion.getTTSController();
			mRecSystem = mUnion.getRecogniseSystem();
			mViewContainer = mUnion.getViewContainer();
			mCommandEngine = mUnion.getCommandEngine();
			mContext = mUnion.getBaseContext().getContext();
			mCommandName = commandName;
			mCommandDesc = commandDesc == null ? "" : commandDesc;
			mBaseContext = mUnion.getBaseContext();
			UmengUtil.onEvent(mContext, commandName, mCommandDesc);
		}

	}

	public Map<String, Object> getMap() {
		if (mData == null) {
			mData = new HashMap<String, Object>();
		}
		return mData;
	}

	protected void sendAnswerSession(String text) {
		mUnion.getMainThreadUtil().sendNormalWidget(text);
	}

	protected void sendAnswerSessionNeedClearScreen(String text, boolean isNeedClear) {
		mUnion.getMainThreadUtil().sendNormalWidget(text);
	}

	protected void sendQuestionSession(String text) {
		// mData = getMap();
		// mData.put(MESSAGE, text);
		// mComminicator.sendQuestionSession(text, mContext, true);
		mUnion.getMainThreadUtil().sendQuestionWidget(text);
	}

	protected void startRecogniseImediatelyAfterTtsOver() {
		mBaseContext.setGlobalBoolean(KeyList.PKEY_NEED_START_IMEDIATELEY_AFTER_RECOGNISE, true);
	}


	protected String getCommandName() {
		return mCommandName;
	}

	protected void sendAnswerSession(String text, boolean displayText) {
		LogManager.d("text is" + text + " disPlayText is " + displayText);
		if (displayText) {
			sendAnswerSession(text);
		} else {
			if (mTTSController != null) {
				mTTSController.play(text);
			} else {
				LogManager.d(TTS_CONTROLLER_ERROR_IS_NULL);
			}
		}
	}

	protected void sendTTSpaly(String text) {
		LogManager.d("text is" + text);
		if (mTTSController != null) {
			mTTSController.play(text);
		} else {
			LogManager.d(TTS_CONTROLLER_ERROR_IS_NULL);
		}
	}

	/**
	 * 发送声音，不显示widgetAnswer.可以注册监听器
	 * 
	 * @param text
	 * @param ttsStateListener
	 */
	protected void sendAnswerSessionNoShow(String text, ITTSStateListener ttsStateListener) {
		if (mTTSController != null) {
			mTTSController.setListener(ttsStateListener);
			mTTSController.play(text);
		} else {
			LogManager.d(TTS_CONTROLLER_ERROR_IS_NULL);
		}
	}

	/**
	 * 只显示WidgetAnswer，但是不播放声音。
	 * 
	 * @param text
	 */
	protected void sendAnswerSessionNoTts(String text) {
		LogManager.e("no tts " + text);
		// WidgetAnswer widgetAnswer = new WidgetAnswer(mContext, text);
		// widgetAnswer.setVoiceEnable(false);
		// if (mViewContainer != null) {
		// mViewContainer.pushNewWidget(widgetAnswer);
		// }
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		if (mData != null) {
			mData.clear();
			mData = null;
		}
	}

	/**
	 * 
	 * @param onRecognizerEndListener
	 *            需要获取语音识别的数据并处理。设置一个onRecognizerEndListener;
	 *            当不需要获取此数据后，需要传入一个null.
	 */
	protected void setOnRecogniseEndListener(final OnRecognizerEndListener onRecognizerEndListener) {
		if (mRecSystem != null) {
			if (onRecognizerEndListener != null) {
				LogManager.d("OnRecognizerEndListener is register!");
				mRecSystem.setOnResultListener(new IOnResultListener() {

					@Override
					public void onResult(String text) {
						// TODO Auto-generated method stub
						onRecognizerEndListener.onRecognizerEnd(text);
					}

					@Override
					public void onError(int errorCode) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onEnd() {
						// TODO Auto-generated method stub

					}
				});
			} else {
				mRecSystem.setOnResultListener(null);
			}
		} else {
			LogManager.d("RecogniseSystem is null ,cannot recognise .");
		}
	}

	/**
	 * 立即开始识别。
	 */
	protected void startRecogniseImmediately() {
		if (mRecSystem != null) {
			mRecSystem.startCaptureVoice();
		} else {
			LogManager.d("RecogniseSystem is null ,cannot recognise .");
		}
	}

	protected void play(String text) {
		if (mTTSController != null) {
			mTTSController.play(text);
		} else {
			LogManager.e("ttsController is null");
		}
	}

	protected void sendToServer(final String text) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mCommandEngine.handleText(text, true, false);
			}
		};

		if (mCommandEngine != null) {
			runnable.run();
		}
	}

	protected void sendWidget(IVoiceWidget widget) {
		getUnion().getMainThreadUtil().pushNewWidget(widget);
	}

	public BasicServiceUnion getUnion() {
		return mUnion;
	}

	@Override
	public IVoiceCommand execute() {
		// 是否支持TTS DEBUG模式
		if (com.iii360.base.common.utl.KeyList.IS_TTS_DEBUG) {
			mUnion.getTTSController().syncPlay("识别结果为" + mCommandDesc + "命令");
		}
		return null;
	}
}
