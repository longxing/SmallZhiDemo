package com.iii360.external.recognise.engine;

import android.content.Context;

import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.external.recognise.RecogniseSystemBufferBuildFactory;
import com.iii360.external.recognise.RecogniseSystemBufferBuildFactory.IUSCStateListener;

/**
 * 识别引擎管理类 没有配置家电不走本地识别
 * 
 * @author Peter
 * @data 2015年4月3日下午3:30:39
 */

public class BufferRecognizer extends AbstractBufferEngine {
	private AbstractBufferEngine mOnLineAbstractBufferEngine;
	private AbstractBufferEngine mOffLineAbstractBufferEngine;
	private boolean mIsHaveData = false;
	private int mOnLineType;
	private int mOffLineType;
	private boolean isSettedHomeAppliances = false; // 是否配置过家电

	private IUSCStateListener mBufferRecognizerListener;

	public BufferRecognizer(Context context, int onLine, int offLine, IUSCStateListener listener) {
		super(context);
		this.mOnLineType = onLine;
		this.mOffLineType = offLine;
		this.mBufferRecognizerListener = listener;
		mOnLineAbstractBufferEngine = RecogniseSystemBufferBuildFactory.buildBufferRecogniseEngine(mContext, mOnLineType, mBufferRecognizerListener);
		mOffLineAbstractBufferEngine = RecogniseSystemBufferBuildFactory.buildBufferRecogniseEngine(mContext, mOffLineType, mBufferRecognizerListener);

	}

	public void setOnEndListener(SpeechOnEndListener listener) {
		mOffLineAbstractBufferEngine.setOnEndListener(listener);
		mOnLineAbstractBufferEngine.setOnEndListener(listener);

	};

	@Override
	public void writePCMData(boolean isLast, byte[] buffer, int bufferLength) {
		// TODO Auto-generated method stub
		buffer = generateNewFixLengthBuffer(buffer, bufferLength);
		/**
		 * 没有配置家电不走离线识别
		 */
		if (isLast && (buffer == null || buffer.length == 0)) {
			stop();
			mIsHaveData = false;
		} else {
			if (!mIsHaveData) {
				start();
				mIsHaveData = true;
				beforeStartRecord();
			}
			
//			if (isSettedHomeAppliances) {
//				mOffLineAbstractBufferEngine.setNeedBackResult(true);
//				mOffLineAbstractBufferEngine.writePCMData(isLast, buffer, bufferLength);
//			} else {
//				mOffLineAbstractBufferEngine.setNeedBackResult(false);
//			}
			
			mOnLineAbstractBufferEngine.writePCMData(isLast, buffer, bufferLength);
		}
	}

	@Override
	protected void start() {
		int houseDevicesCount = mBaseContext.getPrefInteger(KeyList.HOUSE_MECHINE_SETTING_COUNT, 0);
		isSettedHomeAppliances = houseDevicesCount > 0 ? true : false;
		LogManager.d("current home appliances count:" + houseDevicesCount + "------isSettedHomeAppliances:" + isSettedHomeAppliances);
//		if (isSettedHomeAppliances) {
			mOffLineAbstractBufferEngine.initEngineAndsetEngineParam(true,900);
			mOnLineAbstractBufferEngine.initEngineAndsetEngineParam(true,900);
//		}else {
//			mOffLineAbstractBufferEngine.initEngineAndsetEngineParam(false,0);
//			mOnLineAbstractBufferEngine.initEngineAndsetEngineParam(true, 900);
//		}

		// 在线识别
		mOffLineAbstractBufferEngine.setStateListener(mStateListener);
		mOffLineAbstractBufferEngine.start();
		mOnLineAbstractBufferEngine.setStateListener(mStateListener);
		mOnLineAbstractBufferEngine.start();

	}

	@Override
	protected void stop() {
		// TODO Auto-generated method stub
		mOffLineAbstractBufferEngine.stop();
		mOnLineAbstractBufferEngine.stop();
	}

	@Override
	protected void cancel() {
		// TODO Auto-generated method stub
		mOffLineAbstractBufferEngine.cancel();
		mOnLineAbstractBufferEngine.cancel();

	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub
		mOffLineAbstractBufferEngine.onDestory();
		mOnLineAbstractBufferEngine.onDestory();
	}

}
