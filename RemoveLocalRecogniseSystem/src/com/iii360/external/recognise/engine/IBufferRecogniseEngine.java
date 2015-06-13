package com.iii360.external.recognise.engine;

public interface IBufferRecogniseEngine {
	public interface IStateListener{
		/**
		 * 第一段音频buffer传入时回调
		 */
		public void onStart();
		/**
		 * 最后一段buffer传入时回调
		 */
		public void onEndofBuffer();
		/**
		 * 结果回调
		 * @param result
		 */
		public void onResult(String result);
		/**
		 * 错误回调
		 * @param errorCode
		 */
		public void onError(int errorCode);
	}
	/**
	 * 音频缓冲区读写接口
	 * @param isLast 当最后一段buffer传完之后，会继续调用，同时 buffer置为null,bufferLength 置为-1.
	 * @param buffer pcmdata
	 * @param bufferLength 当前缓冲区大小
	 */
	public void writePCMData(boolean isLast,byte[] buffer,int bufferLength);
	
	public void setStateListener(IStateListener stateListener);
	
	public void setOnEndListener(SpeechOnEndListener listener);
	
	public void onDestory();
}
