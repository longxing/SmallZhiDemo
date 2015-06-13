package com.iii360.external.recognise.engine;

import android.content.Context;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.external.recognise.RecogniseSystemBufferBuildFactory;

/**
 * 目前，只是用在线识别，mIsOnLine =true始终为真
 * 
 * @author Peter
 * @data 2015年4月3日下午5:46:38
 */
public class RecognizerEngine extends AbstractRecogniseEngine {

	private IRecogniseBaseEngine mAbstractRecogniseEngineOnLine;
	protected IRecogniseListenrAdapter mAdapter;
	private RecognizerAdapter mRecognizerAdapter = new RecognizerAdapter();
	private BaseContext mBaseContext;

	public RecognizerEngine(Context context, int startMusicRes, int startMusicChatModeRes, int confirmMusicRes, int cancelMusicRes, int onLine) {
		super(context, startMusicRes, startMusicChatModeRes, confirmMusicRes, cancelMusicRes, FEED_BACK_TYPE_MUSIC);
		// TODO Auto-generated constructor stub
		mBaseContext = new BaseContext(context);
		mAbstractRecogniseEngineOnLine = RecogniseSystemBufferBuildFactory.createRecogniseBaseEngine(mContext, onLine);
	}

	@Override
	public void setRecognitionAdapter(IRecogniseListenrAdapter listener) {
		// TODO Auto-generated method stub
		mAdapter = listener;
		mAbstractRecogniseEngineOnLine.setRecognitionAdapter(mRecognizerAdapter);
	}

	@Override
	public void start() {
		super.start();
		LogManager.e("start()");
		mAbstractRecogniseEngineOnLine.start();

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
		try {
			if (mBaseContext.getGlobalBoolean(KeyList.GKEY_IS_NOW_RECOGNING, false)) {
				mAbstractRecogniseEngineOnLine.stop();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		super.cancel();
		mAbstractRecogniseEngineOnLine.cancel();
	}

	@Override
	protected void realStart() {
		// TODO Auto-generated method stub
		mAbstractRecogniseEngineOnLine.realStart();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		mAbstractRecogniseEngineOnLine.destroy();
	}

	public boolean isConnectedNetWork(Context context) {
		return true;
	}

	class RecognizerAdapter implements IRecogniseListenrAdapter {

		@Override
		public void onRmsChanged(int level) {
			// TODO Auto-generated method stub
			mAdapter.onRmsChanged(level);
		}

		@Override
		public void onResults(String results) {
			// TODO Auto-generated method stub
			mAdapter.onResults(results);
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
			// TODO Auto-generated method stub
			mAdapter.onBufferReceived(buffer);
		}

		@Override
		public void onError(int error) {
			// TODO Auto-generated method stub
			mAdapter.onError(error);
		}

		@Override
		public void onEndOfSpeech() {
			// TODO Auto-generated method stub
			mAdapter.onEndOfSpeech();
		}

		@Override
		public void onInit() {
			// TODO Auto-generated method stub
			mAdapter.onInit();
		}

		@Override
		public void onEnd() {
			// TODO Auto-generated method stub
			mAdapter.onEnd();
		}

		@Override
		public void onBeforeInit() {
			// TODO Auto-generated method stub
			mAdapter.onBeforeInit();
		}

	}
}
