/******************************************************************************************
 * @file WakeupCore.java
 *
 * @brief  kernel of wake up module
 *         唤醒模块应该至少有三种状态机：  
 *         	Listen   ---- 不断输入语音数据,进行检测判断
 *           WakenUp  ---- 语音中接收到 “小智”关键字，唤醒系统，继续接收语音输入
 *           Stopped  ---- 停止语音数据输入,开始其他操作
 *         这这三个状态是一个完整的唤醒模块的周期       
 *
 * Code History:
 *      [2015-04-01] xiaohua lu, initial version, change the code format and add some comments.
 *
 * Code Review:
 *
 *********************************************************************************************/

package com.iii360.external.wakeup;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import android.content.Context;
import android.os.Handler;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.external.recognise.engine.SpeechOnEndListener;
import com.jushang.wakeup.JSError;
import com.jushang.wakeup.JSLocalWakeupEngine;
import com.jushang.wakeup.JSLocalWakeupListener;

/**
 * @brief 唤醒核心处理类，通过第三方的唤醒引擎 AILocalWakeupEngine 来处理唤醒操作 麦克风的PCM语音数据输入、唤醒判断等都是由 第三方的引擎来做得
 */
public class JushangWakeupCore extends AbstractWakeUpCore {

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////// Member Variables /////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////

	public static final String Tag = "[JushangWakeupCore]";

	private JSLocalWakeupEngine mEngine = null; // 第三方的唤醒引擎
	private Context mContext = null; // 上下文,由外部调用者传入
	private ISpeechSensitive mSpeechSensitive = null; // WakeupCore对外回调接口，由外部调用者传入
	private SendShowOrHiddenFlagInterface mSendShowOrHiddenFlagInterface; // 外部传入的回调，由WakeupCore在启动、停止监听唤醒的时候调用

	private Queue<byte[]> slienceQueue = new LinkedList<byte[]>(); // 静音数据缓冲队列
	private boolean isCallOnWakeupofSpeech = false; // 当前是否已经唤醒状态
	private boolean isCallStart = false; // 当前是否已经启动录音监听
	private UploadReceivedThread uploadReceivedThread = null; // 音频上传处理模块

	// public static final String path = "/mnt/sdcard/jushangWakeupAudio/";
	// private Queue<byte[]> testBuffer = new LinkedList<byte[]>();
	// private long bufferLength = 0;

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////// Public Functions /////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////
	public JushangWakeupCore(Context context, BufferManager manager) {
		mContext = context;
		manager.setOnEndListener(new SpeechOnEndListener() {

			@Override
			public void onEnd() {
				stopWakup();
			}
		});

	}

	/**
	 * @brief 设置回调接口
	 * 
	 */
	public void setSpeechSensitive(ISpeechSensitive speechSensitive, SendShowOrHiddenFlagInterface sendShowOrHiddenFlagInterface) {
		this.mSpeechSensitive = speechSensitive;
		this.mSendShowOrHiddenFlagInterface = sendShowOrHiddenFlagInterface;
		uploadReceivedThread = new UploadReceivedThread(speechSensitive);
		uploadReceivedThread.start();
		init();
	}

	/**
	 * @brief 启动 WakeupCore 引擎，开始监听语音输入
	 */
	public void start() {
		mSendShowOrHiddenFlagInterface.onShow();
		if (!isCallStart) {
			LogManager.i(Tag, "==>JushangWakeupCore::start(): Enter, starting listen");
			mEngine.start();
			isCallStart = true;
			LogManager.i(Tag, "   JushangWakeupCore::start(): Exit");
		}
	}

	/**
	 * @brief 停止监听语音输入
	 */
	public void stop() {
		mSendShowOrHiddenFlagInterface.onHidden();
		if (isCallStart) {
			LogManager.i(Tag, "==>JushangWakeupCore::stop(): Enter, stopping listen");
			BaseContext baseContext = new BaseContext(mContext);
			baseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_BUFF_RECOGNING, false);
			baseContext.setGlobalBoolean(KeyList.GKEY_IS_WAKEUP_TO_RECOGNISE, false);
			mEngine.stop();
			isCallStart = false;
			LogManager.i(Tag, "   JushangWakeupCore::stop(): Exit");
		}
	}

	/**
	 * @brief 完全释放引擎,调用之后，该引擎被释放，不可再被使用
	 */
	public void destroy() {
		LogManager.i(Tag, "==>JushangWakeupCore::destroy(): Enter, destroying engine");
		mEngine.stop();
		mEngine.destory();
		slienceQueue.clear();
		isCallOnWakeupofSpeech = false;
		isCallStart = false;
		uploadReceivedThread = null;
		LogManager.i(Tag, "   JushangWakeupCore::destroy(): Exit");
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////// Member Functions /////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @brief 实现第三方唤醒引擎的回调接口
	 */
	private class AIJuShangListenerImpl implements JSLocalWakeupListener {
		// private boolean isWakeup;
		@Override
		public void onInit(int status) {
			LogManager.v(Tag, "==>JushangWakeupCore::AISpeechListenerImpl::onInit(): status = " + status);
			if (status == JSError.INTENGER_INT_SUCCESS) {
				// isWakeup = false;
				start();
				mSendShowOrHiddenFlagInterface.onShow();
			} else {
				mSendShowOrHiddenFlagInterface.onHidden();
			}
		}

		@Override
		public void onBeginningOfSpeech() {
			LogManager.v(Tag, "==>JushangWakeupCore::AISpeechListenerImpl::onBeginningOfSpeech(): ");
			// 在这里将之前缓存的音频(600ms)与从这一时刻开始的onBufferReceived音频做合并
			// isWakeup = false;
		}

		@Override
		public void onEndOfSpeech(long time) {
			LogManager.v(Tag, "==>JushangWakeupCore::AISpeechListenerImpl::onEndOfSpeech(): ");
			// if (isWakeup) {
			// stopWakup();
			// }
			// isWakeup = false;
		}

		@Override
		public void onError(JSError arg0) {
			LogManager.e(Tag, "==>JushangWakeupCore::AISpeechListenerImpl::onError(): " + arg0.toString());
		}

		@Override
		public void onRmsChanged(float arg0) {
		}

		//
		// @brief 第三方的引擎 PCM数据回调过来
		//
		@Override
		public void onBufferReceived(byte[] buffer, long size) {
			if (null == buffer || buffer.length <= 0) {
				LogManager.e(Tag, "==>WakeupCore::AISpeechListenerImpl::onBufferReceived(): [ERROR] buffer is null or no data!");
				return;
			}
			// testBuffer.offer(buffer);
			// bufferLength += buffer.length;
			// LogManager.i(Tag, "==>WakeupCore::AISpeechListenerImpl::onBufferReceived(): arrived, dataLen = " + buffer.length);
			if (isCallOnWakeupofSpeech) {
				synchronized (slienceQueue) {
					Queue<byte[]> queue = slienceQueue;
					slienceQueue = new LinkedList<byte[]>();
					uploadReceivedThread.upLoadBufferReceived(buffer, queue, false);
				}
			} else {
				byte[] bufferNew = new byte[buffer.length];
				System.arraycopy(buffer, 0, bufferNew, 0, buffer.length);
				offerSilenceBuffer(bufferNew);
			}
		}

		//
		// @brief 第三方的引擎唤醒回调
		//
		@Override
		public void onWakeup(String wakeupWord) {
			LogManager.i(Tag, "==>JushangWakeupCore::AISpeechListenerImpl::onWakeup(): arrived, word = " + wakeupWord);

			BaseContext baseContext = new BaseContext(mContext);

			LogManager.i(Tag, "   JushangWakeupCore::AISpeechListenerImpl::onWakeup(): already waken up, stop wakeup listen!");
			baseContext.setGlobalString(KeyList.STRING_WAKEUP_WORD, wakeupWord);
			mSpeechSensitive.onEvent(ISpeechSensitive.EVENT_WAKE_UP); // 回调给外部，触发唤醒事件
			mSpeechSensitive.onStart();
			isCallOnWakeupofSpeech = true;
			// isWakeup = true;

			// 10秒后关闭唤醒监听
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (isCallOnWakeupofSpeech) {
						LogManager.i(Tag, "   JushangWakeupCore::AISpeechListenerImpl::onWakeup(): stop wakeup");
						stopWakup();
					}
				}

			}, 10 * 1000);
		}

		@Override
		public void onReadyForSpeech() {
			LogManager.i(Tag, "==>JushangWakeupCore::AISpeechListenerImpl::onReadyForSpeech(): ");
			BaseContext baseContext = new BaseContext(mContext);
			baseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_WAKEUP, true);
			// isWakeup = false;
		}

		@Override
		public void onRecordReleased() {
			LogManager.i(Tag, "==>JushangWakeupCore::AISpeechListenerImpl::onReadyForSpeech(): 思必驰引擎释放! ");
			BaseContext baseContext = new BaseContext(mContext);
			baseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_WAKEUP, false);
			// isWakeup = false;
		}
	}

	/**
	 * @brief 初始化相关第三方唤醒引擎
	 * 
	 */
	private void init() {
		LogManager.i(Tag, "==>JushangWakeupCore::init(): create local wakeup engine...");
		mEngine = JSLocalWakeupEngine.getInstance();
		mEngine.setSimulateSpeed(false);
		mEngine.setStopOnWakeupSuccess(false);
		mEngine.setVadEnable(false);
		mEngine.setVolEnable(false);
		mEngine.init(mContext, new AIJuShangListenerImpl());
	}

	/**
	 * @brief 停止唤醒监听操作
	 * 
	 */
	protected void stopWakup() {
		if (isCallOnWakeupofSpeech) {
			LogManager.i(Tag, "==>JushangWakeupCore::stopWakup(): ");

			synchronized (slienceQueue) {
				Queue<byte[]> queue = slienceQueue;
				LogManager.i(Tag, "   JushangWakeupCore::stopWakup(): jiangshenglan slienceQueue size is " + slienceQueue.size());
				slienceQueue = new LinkedList<byte[]>();
				uploadReceivedThread.upLoadBufferReceived(null, queue, true);
				// mSpeechSensitive.onStop();
				isCallOnWakeupofSpeech = false;
			}
			// if (bufferLength > 0) {
			// File file = new File(path);
			// if (!file.exists()) {
			// file.mkdir();
			// }
			// try {
			// AudioFileUtil.generateWAVFile(path + "wakeup_" + System.currentTimeMillis() + ".wav", testBuffer, bufferLength);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
		} else {
			// if (bufferLength > 0) {
			// File file = new File(path);
			// if (!file.exists()) {
			// file.mkdir();
			// }
			// try {
			// AudioFileUtil.generateWAVFile(path + "nowakekup_" + System.currentTimeMillis() + ".wav", testBuffer, bufferLength);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
		}
		// bufferLength = 0;
		// testBuffer.clear();
		// slienceQueue.clear();
	}

	/**
	 * @brief 缓存"静音数据"
	 * 
	 * @param buffer
	 */
	protected void offerSilenceBuffer(byte[] buffer) {
		slienceQueue.offer(buffer);
		if (slienceQueue.size() > 7) // 这里20是经验值
		{
			slienceQueue.poll();
		}
	}

	//
	// 音频数据缓冲向前移动处理模块，后台启动一个线程，不断的将录入的音频数据buffer向前移动
	//
	private class UploadReceivedThread extends Thread {
		private ISpeechSensitive mSpeechSensitive;
		private Queue<byte[]> queue;
		private Queue<byte[]> bufferQueue;
		private boolean isStop;
		private boolean isDataStop;

		public UploadReceivedThread(ISpeechSensitive mSpeechSensitive) {
			super();
			this.isStop = false;
			this.isDataStop = false;
			this.mSpeechSensitive = mSpeechSensitive;
			this.bufferQueue = new LinkedList<byte[]>();
			this.queue = null;
		}

		public void upLoadBufferReceived(byte[] buffer, Queue<byte[]> queue, boolean isStop) {
			if (this.isStop) {
				return;
			}
			this.isStop = isStop;
			if (this.queue == null) {
				this.queue = queue;
			}
			if (buffer != null && buffer.length > 0) {
				byte[] bufferNew = new byte[buffer.length];
				System.arraycopy(buffer, 0, bufferNew, 0, buffer.length);
				synchronized (this.bufferQueue) {
					this.bufferQueue.offer(buffer);
				}
			}
			if (isStop) {
				this.isDataStop = true;
			}
			synchronized (this) {
				this.notifyAll();
			}
		}

		@Override
		public void run() {
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (InterruptedException e) {

			}
			byte queueBuffer[] = null;
			byte buffer[] = null;
			while (!this.isDataStop || !bufferQueue.isEmpty() || !queue.isEmpty()) {
				try {
					while ((queueBuffer = queue.poll()) != null) {
						// audioSentive.onBufferReceived(queueBuffer);
						mSpeechSensitive.onBufferReceived(queueBuffer);
					}
				} catch (NoSuchElementException e) {
					e.printStackTrace();
				}

				synchronized (this.bufferQueue) {
					if ((buffer = bufferQueue.poll()) != null) {
						mSpeechSensitive.onBufferReceived(buffer);
					}
				}
			}

			mSpeechSensitive.onStop();
			queue.clear();
			bufferQueue.clear();
			queue = null;
			bufferQueue = null;
			JushangWakeupCore.this.destroy(); // [Warning] 这个地方在内部线程销毁自身对象,是有问题的
			this.isStop = false;
			this.isDataStop = false;
		}

	}

}
