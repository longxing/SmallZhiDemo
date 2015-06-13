package com.iii360.external.recognise;

import android.content.Context;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.recognise.ILightController;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.iii360.external.recognise.engine.IRecogniseEngine;
import com.iii360.external.recognise.engine.IRecogniseEngine.IRecogniseListenrAdapter;

/**
 * 用于长按事件的语音识别，根据识别状态 开关LED灯、控制灯的动画，根据声音的大小调整灯的亮度
 * 
 * 通过 IRecogniseListenrAdapter 回调 SpeechOnLineRecognizer 识别结果
 * 
 * 通过 IOnResultListener 接口 将 IRecogniseListenrAdapter 回调上来的结果传给RecogniseSystemProxy
 * 
 * @author Peter
 * @data 2015年4月23日下午8:26:46
 */

public class RecogniseSystem implements IRecogniseSystem {

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////MembersVariables///////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////

	private static final String TAG = "RecogiseSystem";

	// 识别结果状态
	private static final int DETAIL_STATE_NORMAL = -1;
	private static final int DETAIL_STATE_ERROR = 0;
	private static final int DETAIL_STATE_RESULT = 1;
	protected int mState = STATE_NORMAL;

	protected IRecogniseEngine speechOnlineEngine = null; // 思必驰在线识别引擎
	protected ILightController lightControler = null; // led的控制器 WakeUpLightControl
	protected IOnResultListener mOnResultListener = null;
	private boolean mIsCancel = false; // 是否取消识别
	private boolean mIsEnd = false;
	private int mDetailState = DETAIL_STATE_NORMAL;
	private Context mContext = null;

	private BaseContext mBaseContext = null;

	// 默认的实现，用来桥接语音识别厂商的不同.本类同IRecogniseDlg,IOnResultListener,，各家语音识别厂商交互。
	protected IRecogniseListenrAdapter speechOnlineStateListenr = new IRecogniseListenrAdapter() {

		@Override
		public void onInit() {
			// TODO Auto-generated method stub
			LogManager.i(TAG, "onInit");
			mIsEnd = false;
			if (lightControler != null) {
				lightControler.updateStateOnRunnable(ILightController.RECOGNISE_STATE_INIT, 0);

			}
		}

		@Override
		public void onRmsChanged(int level) {
			// TODO Auto-generated method stub
			if (mDetailState == DETAIL_STATE_ERROR || mDetailState == DETAIL_STATE_RESULT || mState == STATE_STOP_CAPTURE_AND_START_RECOGNING || mIsEnd) {
				LogManager.e("onRmsChanged level is " + level + "mDetailState is " + mDetailState + " mIsEnd is " + mIsEnd);
				return;
			}
			LogManager.i(TAG, "onRmsChanged");
			if (lightControler != null) {
				lightControler.updateStateOnRunnable(ILightController.RECOGNISE_STATE_VOICE_LEVEL_CHANGE, level);
			}
		}

		@Override
		public void onResults(String results) {
			// TODO Auto-generated method stub
			LogManager.i(TAG, "onResults");
			if (lightControler != null) {
				lightControler.updateStateOnRunnable(ILightController.RECOGNISE_STATE_SUCCESS, 0);
			}
			mState = STATE_NORMAL;
			if (mOnResultListener != null && !mIsCancel) {
				if (results == null || "".equals(results)) {
					mOnResultListener.onError(ERROR_ICANNOT_HEAR);
				} else {
					mOnResultListener.onResult(results);
				}
			}
		}

		@Override
		public void onError(int error) {
			// TODO Auto-generated method stub
			if (lightControler != null) {
				lightControler.updateStateOnRunnable(ILightController.RECOGNISE_STATE_ERROR, error);
				if (KeyList.IS_TTS_DEBUG) {
					IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
					ITTSController mITTSController = (ITTSController) gloableHeap.getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);
					mITTSController.syncPlay("唤醒发生错误");
				}
			}
			mState = STATE_NORMAL;
			if (mOnResultListener != null && !mIsCancel) {
				LogManager.i(TAG, "onError");
				mOnResultListener.onError(error);
				mDetailState = DETAIL_STATE_ERROR;
			}
		}

		/**
		 * 思必驰，资源销毁时调用
		 */
		@Override
		public void onEndOfSpeech() {
			// TODO Auto-generated method stub
			LogManager.i(TAG, "onEndOfSpeech");
			try {
				if (lightControler != null) {
					speechOnlineEngine.startRecogniseFeedBack();
					lightControler.updateStateOnRunnable(ILightController.RECOGNISE_STATE_RECONISING, 0);
				}
				mOnResultListener.onEnd();
				mIsEnd = true;
			} catch (Exception e) {
				LogManager.printStackTrace(e);
			}
		}

		/**
		 * 取消录音时调用
		 */
		@Override
		public void onEnd() {
			// TODO Auto-generated method stub
			LogManager.i(TAG, "onEnd");
			mState = STATE_NORMAL;
			mIsEnd = true;
			mOnResultListener.onEnd();
			if (lightControler != null) {
				lightControler.updateStateOnRunnable(ILightController.RECOGNISE_STATE_SUCCESS, 0);
			}

		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			// TODO Auto-generated method stub
			LogManager.i("RecogniseListenerAdapter", "onBufferReceived");
		}

		@Override
		public void onBeforeInit() {
			// TODO Auto-generated method stub
			LogManager.i("RecogniseListenrAdapter", "onBeforeInit");
			mDetailState = DETAIL_STATE_NORMAL;
		}
	};

	public RecogniseSystem(IRecogniseEngine engine, Context context) {
		LogManager.i("AbstractRecogiseSystem Constructor!");
		mBaseContext = new BaseContext(context);
		mContext = context;
		this.speechOnlineEngine = engine;
		setRecognitionListener(speechOnlineStateListenr);
	}

	/**
	 * 绑定UI与识别引擎
	 */
	public void setRecognitionListener(IRecogniseListenrAdapter adapter) {
		LogManager.i("setRecogniseListener");
		this.speechOnlineEngine.setRecognitionAdapter(adapter);
	}

	/**
	 * 设置回调接口
	 */
	@Override
	public void setOnResultListener(IOnResultListener onResultListener) {
		LogManager.i("setOnResultListener");
		this.mOnResultListener = onResultListener;
	}

	/**
	 * 点击事件处理中心
	 */
	public void dispatchUserAction() {
		if (mState == STATE_NORMAL) {
			startCaptureVoice();
		} else if (mState == STATE_STARTED_CAPTURE_VOICE) {
			stopCaptureVoice();
		} else if (mState == STATE_STOP_CAPTURE_AND_START_RECOGNING) {
			cancelRecognising();
		}
	}

	/**
	 * 开始语音识别
	 */
	@Override
	public void startCaptureVoice() {
		// TODO Auto-generated method stub

		if (mState == STATE_STARTED_CAPTURE_VOICE) {
			LogManager.e("startCaptureVoice  return");
			return;
		}
		mState = STATE_STARTED_CAPTURE_VOICE;
		LogManager.i("startCaptureVoice");

		mBaseContext.setPrefBoolean(KeyList.PKEY_IS_USE_LOCAL_ENGINE, false);

		LogManager.e("now use defaultEngine !!!");

		if (mOnResultListener != null) {
			mOnResultListener.onStart();
		}
		speechOnlineEngine.start();
		LogManager.e("startCaptureVoice");
		mIsCancel = false;
	}

	/**
	 * 
	 * 停止监听，进入语音识别过程
	 * 
	 */
	@Override
	public void stopCaptureVoice() {
		// TODO Auto-generated method stub
		if (mState == STATE_STOP_CAPTURE_AND_START_RECOGNING) {
			return;
		}
		mState = STATE_STOP_CAPTURE_AND_START_RECOGNING;
		speechOnlineEngine.stop();
		LogManager.e("stopCaptureVoice");
	}

	/**
	 * 取消语音识别整个过程
	 */
	@Override
	public void cancelRecognising() {
		// TODO Auto-generated method stub
		LogManager.e("cancelRecognising");
		if (mState == STATE_NORMAL) {
			return;
		}
		if (speechOnlineEngine != null) {
			mIsCancel = true;
			speechOnlineEngine.cancel();
			mState = STATE_NORMAL;
		}

		// 回调
		if (mOnResultListener != null) {
			mOnResultListener.onEnd();
		}
	}

	/**
	 * 绑定识别按钮
	 */
	public void bindRecogniseButton(ILightController recogniseButton) {
		// TODO Auto-generated method stub
		lightControler = recogniseButton;
	}

	/**
	 * 销毁相关资源
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		if (speechOnlineEngine != null) {
			if (mState != STATE_NORMAL) {
				speechOnlineEngine.cancel();
			}
			speechOnlineEngine.destroy();
		}
	}

	@Override
	public void startWakeup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopWakeup() {
		// TODO Auto-generated method stub

	}
}
