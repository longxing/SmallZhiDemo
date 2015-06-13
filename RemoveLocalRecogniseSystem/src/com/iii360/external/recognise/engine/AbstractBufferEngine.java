package com.iii360.external.recognise.engine;

import android.content.Context;
import android.os.Handler;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.external.recognise.util.RecordUpLoadHandler;

public abstract class AbstractBufferEngine implements IBufferRecogniseEngine {

	protected BaseContext mBaseContext;
	protected Handler mHandler;

	private RecordUpLoadHandler mRecordUpLoadHandler;
	protected IStateListener mStateListener;
	protected Context mContext;
	protected SpeechOnEndListener mSpeechOnEndListener;
	private boolean isNeedBackResult = false; // 没有配置家电不需要返回离线识别结果

	public AbstractBufferEngine(Context context) {
		mContext = context;
		mBaseContext = new BaseContext(mContext);
		mHandler = new Handler();
		mRecordUpLoadHandler = new RecordUpLoadHandler();
	}

	protected void beforeStartRecord() {
		mRecordUpLoadHandler.beforeStartRecord();
	}

	protected void record(byte[] buffer) {
		mRecordUpLoadHandler.record(buffer);
	}

	protected String stopRecord(boolean hasResult, String result) {
		return mRecordUpLoadHandler.stopRecordToSaveFile(hasResult, result);
	}

	protected abstract void start();

	protected abstract void stop();

	protected abstract void cancel();

	public Context getContext() {
		return mContext;
	}

	@Override
	public void setStateListener(IStateListener stateListener) {
		// TODO Auto-generated method stub
		this.mStateListener = stateListener;
	}
    /**
     * 初始化引擎，并设置参数
     * @param  arg[0] 是否开启端点检测
     * @param  arg[1] 设置端点测时间 
     */
	public void initEngineAndsetEngineParam(Object ...arg) {
        
	}

	public boolean isNeedBackResult() {
		return isNeedBackResult;
	}

	public void setNeedBackResult(boolean isNeedBackResult) {
		this.isNeedBackResult = isNeedBackResult;
	}

	@Override
	public void setOnEndListener(SpeechOnEndListener listener) {
		mSpeechOnEndListener = listener;
	}

	protected byte[] generateNewFixLengthBuffer(byte[] buffer, int bufferLength) {
		if (buffer == null) {
			return null;
		} else {
			if (buffer.length == bufferLength) {
				return buffer;
			} else {
				byte[] newBuffer = new byte[bufferLength];
				System.arraycopy(buffer, 0, newBuffer, 0, bufferLength);
				return newBuffer;
			}
		}
	}

	public void onDestory() {
	}

}
