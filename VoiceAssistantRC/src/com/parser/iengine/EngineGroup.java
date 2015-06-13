package com.parser.iengine;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;

import com.base.data.CommandInfo;
import com.base.platform.OnDataReceivedListener;
import com.base.platform.Platform;
import com.base.util.KeyManager;
import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.inf.recognise.ILightController;
import com.iii360.sup.common.utl.Animate;
import com.iii360.sup.common.utl.HomeConstants;
import com.iii360.sup.common.utl.LogManager;
import com.voice.base.BaseVoiceContext;
import com.voice.recognise.IRecogniseDlg;

/**
 * 语义解析引擎组：思必驰在线语义，AmazingBoxEngine 家电在线语义引擎、Control4Engine 语义引擎（目前不在使用）
 * 
 * 
 * @author Peter
 * @data 2015年4月10日下午2:32:28
 */

public class EngineGroup extends BaseVoiceContext implements IEngine {

	/******************************* Member Variables **************************************************/
	private ArrayList<AbstractEngine> mEngineList = new ArrayList<AbstractEngine>();
	private RequestParams mParams;
	private OnDataReceivedListener mListener;
	AbstractEngine mRootEngine;
	private Handler mHandler = new Handler();
	private CommandInfo mCmdInfo;
	private Context mContext;
	public RunExecute mRunExecute = new RunExecute();

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public EngineGroup(Context context) {
		super(context);
		mContext = context;
		init();
	}

	/**
	 * 
	 * 初始化语义解析列表
	 */
	private void init() {
		if (HomeConstants.SW_ABOX) {
			mEngineList.add(new AmazingBoxEngine(getContext()));
		}
		if (HomeConstants.SW_CONTROL_FOUR) {
			mEngineList.add(new Control4Engine(getContext()));
		}
		mEngineList.add(new NativeEngine(getContext()));
		mEngineList.add(new NewRemoteEngine(getContext()));
	}

	@Override
	public void input(final String text, String params) {
		addSessionId();
		mParams = new RequestParams(params + "&question=" + text + "&session_id=" + getSessionId());
		new ProgressThread(text).start();
	}

	private void dispatchCommand(CommandInfo info) {

		if (info != null) {
			LogManager.i("input text:" + info._question);
			LogManager.i("CommandName:" + info._commandName);
			if (info._packageName == null || info._packageName.equals("")) {

			}
		}
		setGlobalObject(KeyManager.GKEY_OBJ_LAST_MSG, info);
		performReceivedListener(info);

	}

	private void performReceivedListener(CommandInfo info) {
		if (mListener != null) {
			if (info != null) {
				if (info._sessionId == getSessionId()) {
					mListener.onDataReceived(info);
				} else {
					LogManager.w("sessionId is invaild!");
					LogManager.w("old id:" + info._sessionId);
					LogManager.w("cur id:" + getSessionId());
				}
			} else {
				mListener.onError(Platform.DATA_ERROR_NO_MATCH);
			}

		}
	}

	@Override
	public void setOnDataReceivedListener(OnDataReceivedListener l) {
		mListener = l;
	}

	@Override
	public OnDataReceivedListener getOnDataReceivedListener() {
		// TODO Auto-generated method stub
		return mListener;
	}

	@Override
	public void setAdditionalParams(String params) {
		for (AbstractEngine engine : mEngineList) {
			engine.setAdditionalParams(params);
		}
	}
   /**
    * 
    * @author Peter
    * @data 2015年4月10日下午2:42:19
    */
	private class ProgressThread extends Thread {
		private String mText;

		public ProgressThread(String text) {
			mText = text;
		}
		@Override
		public void run() {

			for (AbstractEngine engine : mEngineList) {
				mCmdInfo = engine.parse(mText, mParams);
				if (mCmdInfo != null) {
					mCmdInfo._question = mText;
					postRunnable(mRunExecute);
					return;
				}
			}
			postRunnable(mRunExecute);
		}
	}

	protected void postRunnable(Runnable r) {
		mHandler.post(r);
	}
	
	public class RunExecute implements Runnable {

		@Override
		public void run() {

			try {
				if (mCmdInfo != null) {
					LogManager.e(mCmdInfo._commandName + "... " + mCmdInfo._answer);
					dispatchCommand(mCmdInfo);
				}
			} catch (Exception e) {
				LogManager.printStackTrace(e);
				// 回收灯动画结束
				Animate.post(new Runnable() {

					@Override
					public void run() {
						IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
						ILightController wakeupLightControl = (ILightController) gloableHeap.getGlobalObjectMap().get(KeyList.GKEY_WAKEUP_LIGHT_CONTROL);
						wakeupLightControl.reconiseStopAnimation();
					}
				});
			}

		}

	}

}
