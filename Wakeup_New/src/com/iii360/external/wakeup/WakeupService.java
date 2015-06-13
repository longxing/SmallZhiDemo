/******************************************************************************************
 * @file WakeupService.java
 *
 * @brief start a service to listen the wakeup event
 *
 * Code History:
 *      [2015-04-01] xiaohua lu, initial version, remove the phone state manager.
 *
 * Code Review:
 *
 *********************************************************************************************/
package com.iii360.external.wakeup;

import java.util.LinkedList;
import java.util.Queue;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.recognise.ILightController;
import com.iii360.base.umeng.OnlineConfigurationUtil;
import com.iii360.base.umeng.UmengOnlineConfig;
import com.iii360.external.recognise.RecogniseSystemBufferBuildFactory.IUSCStateListener;
import com.iii360.external.recognise.engine.AbstractBufferEngine;
import com.iii360.external.wakeup.AbstractWakeUpCore.ISpeechSensitive;
import com.iii360.external.wakeup.AbstractWakeUpCore.SendShowOrHiddenFlagInterface;
import com.iii360.external.wakeup.BufferManager.Result;

/**
 * @brief 启动一个独立的服务,负责监听麦克风语音输入,判断是否唤醒 几种唤醒条件: 1. 保持服务后台开启，但是中断唤醒一下，满足某一条件后再度开启，如 语音识别过程中，必须中断唤醒一下，语音识别结束之后再开启。 2.
 *        当进行开启唤醒的逻辑的时候，开启唤醒之前要判断一下，是不是可以开启唤醒，如果不能开启则延迟一定时间再来检测
 *
 */
public class WakeupService extends Service {

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////// Constant Definition
	// ////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////
	public static final String Tag = "[WAKEUP]";

	public static final int SERVICE_ID_START_BACK_WAKE_UP_LISTEN = 0;
	public static final int SERVICE_ID_STOP_BACK_WAKE_UP_LISTEN = 1;
	public static final int SERVICE_ID_RESTART_WAKE_UP_STATE = 3;

	public static final int DEFAULT_DELAY_TIME = 100; // 默认的开启唤醒延迟时间

	private String _mWakeUpType = "Speech"; // 唤醒引擎的类型 （1）思必驰引擎 “Speech” （2）聚熵唤醒引擎"JuShang"

	private static final String JS_WAKEUP_ENGINE = "JuShang";
	private static final String SPEECH_WAKEUP_ENGINE = "Speech";

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////// Implement Interface
	// //////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @brief 实现WakeupCore的回调接口
	 * 
	 */
	class ISpeechSensitiveBuffer implements ISpeechSensitive {
		private Queue<byte[]> bufferQueue = new LinkedList<byte[]>();
		private Queue<byte[]> bufferQueueData = new LinkedList<byte[]>();
		private int i = 1;
		private long time = 0;
		private int length = 0;
		private int event = 0;

		public ISpeechSensitiveBuffer(Context context) {
		}

		//
		// 接收到"小智"关键字 系统被唤醒回调函数, Listen-->WakenUp状态切换
		// 注意: 这个回调函数在 思必驰引擎 的回调线程中
		//
		@Override
		public void onStart() {
			LogManager.i(Tag, "==>WakeupService::ISpeechSensitiveBuffer::onStart(): ");
			length = 0;
			mLength = 0;
			if (mRecogniseDlg != null) {
				mRecogniseDlg.updateStateOnRunnable(ILightController.RECOGNISE_STATE_INIT, 0);
			}
		}

		//
		// 结束语音录入的回调函数, WakenUp-->Stopped 状态切换
		// 注意: 这个回调函数在 WakeupCore 内部缓冲区管理线程中
		//
		@Override
		public void onStop() {
			LogManager.i(Tag, "==>WakeupService::ISpeechSensitiveBuffer::onStop(): serviceId =  " + mServiceId + " event = " + event);

			if (mServiceId == SERVICE_ID_STOP_BACK_WAKE_UP_LISTEN) {
				return;
			}

			if (event == EVENT_WAKE_UP) {
				LogManager.i(Tag, "   WakeupService::ISpeechSensitiveBuffer::onStop(): EVENT_WAKEUP --> EVENT_NONE_EVENT");

				event = EVENT_NONE_EVNET;
				try {
					bufferManager.stop();
					WakeupCore_Destroy(); // [Warning] 这个地方有问题, 跟
											// WakeupCore内部线程重复调用了
											// 而且完全不应该在 WakeupCore 的回调函数中做销毁
											// WakeupCore实例的操作
				} catch (Exception e) {
					LogManager.e(Tag, "   WakeupService::ISpeechSensitiveBuffer::onStop(): [ERROR] exception of finish");
				}
			} else {
			}

			if (mRecogniseDlg != null) {
				mRecogniseDlg.updateStateOnRunnable(ILightController.RECOGNISE_STATE_NORMAL, 0);
			}
		}

		//
		// 音频PCM数据到到达回调
		// 注意: 这个回调函数在 WakeupCore 内部缓冲区管理线程中
		//
		@Override
		public void onBufferReceived(byte[] buffer) {
			if (null == buffer || buffer.length <= 0) {
				LogManager.e(Tag, "==>WakeupService::ISpeechSensitiveBuffer::onBufferReceived(): [ERROR] buffer is null or data len is 0");
				return;
			}

			// LogManager.i(Tag, "==>WakeupService::ISpeechSensitiveBuffer::onBufferReceived(): serviceId =  " + mServiceId + " event = " + event +
			// " dataLen = " + buffer.length);
			if (event == EVENT_WAKE_UP) // 当前已经是唤醒状态,则开始记录PCM数据
			{
				bufferQueue.offer(buffer);
				mBufferQueueData.offer(buffer);
				bufferManager.writePCMData(buffer);
				length += buffer.length;
				mLength += buffer.length;
			}

			if (mRecogniseDlg != null) {
				int i = 0;
				int j = 0;
				for (byte b : buffer) {
					if (b > 0) {
						i++;
						j += b;
					}
				}
				int result = 0;
				if (i != 0) {
					result = j / (i * 8);
				}
				mRecogniseDlg.updateStateOnRunnable(ILightController.RECOGNISE_STATE_VOICE_LEVEL_CHANGE, result);
			}
		}

		//
		// 记录当前事件状态
		// 注意: 这个回调函数在 WakeupCore 内部缓冲区管理线程中
		//
		@Override
		public void onEvent(int event) {
			LogManager.i(Tag, "==>WakeupService::ISpeechSensitiveBuffer::onEvent(): event = " + event);
			this.event = event;
		}

	}

	/**
	 * @brief 实现Service需要的IBinder
	 * 
	 */
	public class MyBinder extends Binder {
		public void bindRecogniseButton(ILightController recogniseButton) {
			mRecogniseDlg = recogniseButton;
		}
	}

	/**
	 * @brief 接收各种广播消息处理,包括 启动 和 停止 唤醒模块的工作
	 * 
	 */
	public class ReceiverForWakeup extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null)
				return;

			String action = intent.getAction();

			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				// 屏幕关闭，提交打开Recording.具体打开是否需要打开，逻辑由内部判断
				LogManager.i(Tag, "==>WakeupService::ReceiverForWakeup::onReceive(): SCREEN_OFF");
				postStartRecording(DEFAULT_DELAY_TIME);
			} else if (Intent.ACTION_SCREEN_ON.equals(action)) {
				// 屏幕开启，提交关闭Recording.具体是否需要关闭逻辑由内部判断
				LogManager.i(Tag, "==>WakeupService::ReceiverForWakeup::onReceive(): SCREEN_ON");
				WakeupCore_DestroyByCondition();
			} else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
				LogManager.i(Tag, "==>WakeupService::ReceiverForWakeup::onReceive(): CONNECTIVITY_CHANGE");
			} else if ("SERVICE_START_BACK_WAKE_UP_LISTEN".equals(action)) {
				// 启动录音、唤醒识别功能
				LogManager.i(Tag, "==>WakeupService::ReceiverForWakeup::onReceive(): SERVICE_START_BACK_WAKE_UP_LISTEN");
				postStartRecording(DEFAULT_DELAY_TIME);
			} else if ("SERVICE_STOP_BACK_WAKE_UP_LISTEN".equals(action)) {
				// 停止录音、唤醒识别功能
				LogManager.i(Tag, "==>WakeupService::ReceiverForWakeup::onReceive(): SERVICE_STOP_BACK_WAKE_UP_LISTEN");
				WakeupCore_Destroy();
			}
		}

	}

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////// Member Variables
	// /////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////

	private BaseContext mBaseContext = null; // 全局的上下文
	private MyBinder binder = null; // 实现Service必须需要的IBinder

	private AbstractWakeUpCore wakeupCore = null; // 核心唤醒处理模块
	private ISpeechSensitive mListener = null; // 设置给核心唤醒模块的接口
	private boolean mIsCalledStart = false; // 标记 WakeupCore 是否已经创建启动

	private Queue<byte[]> mBufferQueueData = new LinkedList<byte[]>(); // 缓冲PCM数据
	private int mLength = 0; // 记录当前接收到的PCM数据大小

	private ReceiverForWakeup mReceiver = null; // 接收广播消息,进行唤醒处理
	private int mServiceId = SERVICE_ID_STOP_BACK_WAKE_UP_LISTEN; // 当前服务接收到的ServiceId

	private ILightController mRecogniseDlg = null; // 语音识别处理接口
	private BufferManager bufferManager = null; // 用于语音识别的数据缓冲区管理器
												// 只看到分配,没有看到释放的地方,应该有泄漏

	private Handler mHandler2 = new Handler();

	private Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 2:
				// HandleWakeup.restartWakeup(getApplicationContext());
				break;

			default:
				break;
			}
		};
	};

	SendShowOrHiddenFlagInterface mSendShowOrHiddenFlagInterface = new SendShowOrHiddenFlagInterface() {

		@Override
		public void onShow() {
			// 开启唤醒灯
			ILightController wakeUpLightControl = (ILightController) mBaseContext.getGlobalObject(KeyList.GKEY_WAKEUP_LIGHT_CONTROL);
			wakeUpLightControl.updateStateOnRunnable(ILightController.RECOGNISE_STATE_OPEN, 0);
			if (HandleWakeup.isNeedWifiAndScreenOff(mBaseContext.getContext())) {
				mBaseContext.setPrefBoolean(KeyList.PKEY_IS_WAKE_UP_ACTIVE, true);
				sendShowOrHiddenFlagAction(true);
			}
		}

		@Override
		public void onHidden() {
			if (HandleWakeup.isNeedWifiAndScreenOff(mBaseContext.getContext())) {
				mBaseContext.setPrefBoolean(KeyList.PKEY_IS_WAKE_UP_ACTIVE, false);
				sendShowOrHiddenFlagAction(false);
			}
		}

	};

	/**
	 * @brief: 定时回调函数, 用来创建 WakeupCore 启动录音唤醒操作
	 * 
	 */
	private Runnable mStartRunnable = new Runnable() {

		@Override
		public void run() {
			LogManager.i(Tag, "==>WakeupService::mStartRunnable::run(): Enter");

			BaseContext baseContext = new BaseContext(WakeupService.this);
			if (mServiceId == SERVICE_ID_STOP_BACK_WAKE_UP_LISTEN) // 如果现在正在进行语音识别状态,
																	// post it
			{
				LogManager.i(Tag, "   WakeupService::mStartRunnable::run(): stop wakeup listen");
				mHandler.removeCallbacks(this);
				mHandler.postDelayed(this, DEFAULT_DELAY_TIME);
			} else {
				// 判断是否需要开启
				if (mIsCalledStart) {
					LogManager.i(Tag, "   WakeupService::mStartRunnable::run(): wakeupCore already started");
					return;
				}

				try {
					LogManager.i(Tag, "   WakeupService::mStartRunnable::run(): creating wakeupCore...");
					WakeupCore_Create();
					mIsCalledStart = true;
				} catch (Exception e) {
					e.printStackTrace();
					LogManager.i(Tag, "   WakeupService::mStartRunnable::run(): [ERROR] exception");
				}
			}

			LogManager.i(Tag, "   WakeupService::mStartRunnable::run(): Exit");
		}
	};

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////// Public and Override Functions
	// /////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onCreate() {
		LogManager.i(Tag, "==>WakeupService::onCreate(): Enter");
		super.onCreate();
		init();

		LogManager.i(Tag, "   WakeupService::onCreate(): Exit");
	};

	@Override
	public void onDestroy() {
		LogManager.i(Tag, "==>WakeupService::onDestroy(): Enter");

		super.onDestroy();
		mHandler.removeCallbacks(mStartRunnable); // 移除启动定时器
		WakeupCore_Destroy();
		unReginsterReceiver(this);

		LogManager.i(Tag, "   WakeupService::onDestroy(): Exit");
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (binder == null) {
			binder = new MyBinder();
		}
		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogManager.i(Tag, "==>WakeupService::onStartCommand(): Enter");

		/* auto restart recon */
		mHandler.removeMessages(2);
		if (!mBaseContext.getGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING)) {
			mHandler.sendEmptyMessageDelayed(2, 2 * 60 * 1000);
		}

		int ret = super.onStartCommand(intent, flags, startId);
		if (intent == null) {
			return ret;
		}

		mServiceId = intent.getIntExtra(KeyList.EKEY_WAKE_UP_SERVECE_ID, -1);
		int delayTime = intent.getIntExtra(KeyList.AKEY_DELEY_TIME, DEFAULT_DELAY_TIME);
		LogManager.i(Tag, "   WakeupService::onStartCommand(): mServiceId = " + mServiceId + " delayTime = " + delayTime);

		switch (mServiceId) {
		case SERVICE_ID_RESTART_WAKE_UP_STATE:
			WakeupCore_Destroy();
			postStartRecording(delayTime);
			break;

		case SERVICE_ID_START_BACK_WAKE_UP_LISTEN:
			postStartRecording(delayTime);
			break;

		case SERVICE_ID_STOP_BACK_WAKE_UP_LISTEN:
			WakeupCore_Destroy();
			break;
		}

		LogManager.i(Tag, "   WakeupService::onStartCommand(): Exit");
		return ret;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////// Member Functions
	// /////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////

	private void init() {
		OnlineConfigurationUtil onLineConfigurationUtil = new OnlineConfigurationUtil(WakeupService.this);
		_mWakeUpType = onLineConfigurationUtil.getOnLineParam(UmengOnlineConfig.UMKEY_WAKEUP_ENGINE_TYPE);
		LogManager.d(Tag, "==>>get wakeUp  engine  type :" + _mWakeUpType);
		mBaseContext = new BaseContext(this);
		mBaseContext.setPrefBoolean("PKEY_BOOLEAN_YUN_BUFFER", true);
		registerReceiver(this);
	}

	private void registerReceiver(Context context) {
		mReceiver = new ReceiverForWakeup();
		IntentFilter intentFilter = new IntentFilter();
		// intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		// intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		intentFilter.addAction("SERVICE_START_BACK_WAKE_UP_LISTEN");
		intentFilter.addAction("SERVICE_STOP_BACK_WAKE_UP_LISTEN");
		context.registerReceiver(mReceiver, intentFilter);
	}

	private void unReginsterReceiver(Context context) {
		context.unregisterReceiver(mReceiver);
	}

	// //////////////////////////////////////////////////////////////////////////////////
	/**
	 * @brief 延迟一段时间后, 创建语音识别buffer管理器，然后创建WakeupCore，启动语音录入识别功能 该方法中应该创建 WakeupCore
	 * @param delayTime 延迟时间
	 * 
	 */
	private void postStartRecording(final long delayTime) {
		LogManager.i(Tag, "==>WakeupService::postStartRecording(): Enter, delayTime = " + delayTime);

		if (bufferManager == null) {
			LogManager.i(Tag, "   WakeupService::postStartRecording(): create buffer manager...");

			bufferManager = new BufferManager(this, new Result() {

				@Override
				public void onResult(String result) {
					mRecogniseDlg.updateStateOnRunnable(ILightController.RECOGNISE_STATE_SUCCESS, 0);
					String resultString[] = result.split(",");
					if (resultString.length >= 2) {
						result = resultString[1];
					}
				}

				@Override
				public void onError(String error) {
					if (error == null) {
						return;
					}
					mRecogniseDlg.updateStateOnRunnable(ILightController.RECOGNISE_STATE_ERROR, 0);
				}

			}, new IUSCStateListener() {
				private int number = 0;

				@Override
				public void onLoadComplete(AbstractBufferEngine engine) {
					if (++number >= 2) {
						mHandler2.removeCallbacks(mStartRunnable);
						mHandler2.postDelayed(mStartRunnable, delayTime);
					}
				}
			});

		} else {
			mHandler2.removeCallbacks(mStartRunnable);
			mHandler2.postDelayed(mStartRunnable, delayTime);
		}

		LogManager.i(Tag, "   WakeupService::onStartCommand(): Exit");

	}

	//
	// 创建和销毁 核心唤醒模块 WakeupCore
	//
	private void WakeupCore_Create() {
		LogManager.i(Tag, "==>WakeupService::WakeupCore_Create(): Enter");
		if (mListener == null) {
			mListener = new ISpeechSensitiveBuffer(mBaseContext.getContext());
		}
		LogManager.d(Tag, "==>WakeupService::WakeupCore_Create():  wakeUp type ==>>:" + _mWakeUpType);
		if (JS_WAKEUP_ENGINE.equals(_mWakeUpType)) {
			wakeupCore = new JushangWakeupCore(this, bufferManager);
		} else {
			wakeupCore = new SpeechWakeupCore(this, bufferManager);
		}

		wakeupCore.setSpeechSensitive(mListener, mSendShowOrHiddenFlagInterface);
		LogManager.i(Tag, "   WakeupService::WakeupCore_Create(): Exit");
	}

	private void WakeupCore_Destroy() {
		LogManager.i(Tag, "==>WakeupService::WakeupCore_Destroy(): Enter");

		mBaseContext.setPrefBoolean(KeyList.PKEY_IS_WAKE_UP_ACTIVE, false);
		mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_WAKEUP, false);
		sendShowOrHiddenFlagAction(false);
		if (!mIsCalledStart) {
			return;
		}
		mHandler.removeCallbacks(mStartRunnable);
		wakeupCore.stop();
		wakeupCore.destroy();
		mIsCalledStart = false;

		LogManager.i(Tag, "   WakeupService::WakeupCore_Destroy(): Exit");
	}

	private void WakeupCore_DestroyByCondition() {
		// if (!WakeupUtil.isNeedOpenWakeup(this)) {
		WakeupCore_Destroy();
		// }
	}

	/**
	 * 开启主界面唤醒图标
	 * 
	 * @param isShow
	 */
	private void sendShowOrHiddenFlagAction(boolean isShow) {
		String flag = KeyList.AKEY_HIDDEN_WAKEUP_FLAG;
		if (isShow) {
			flag = KeyList.AKEY_SHOW_WAKEUP_FLAG;
		}
		Intent hiddenFlagintent = new Intent();
		hiddenFlagintent.setAction(flag);
		WakeupService.this.sendBroadcast(hiddenFlagintent);
	}

}
