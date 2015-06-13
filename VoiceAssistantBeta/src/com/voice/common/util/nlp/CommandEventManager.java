package com.voice.common.util.nlp;

import java.util.ArrayList;

/**
 * 对一些组合命令，场景命令进行处理，
 * 
 * 比如说 “开窗帘”。。。。“停” 把“停”和“开窗帘”绑定起来
 * 
 * @author jushang
 */
public class CommandEventManager {
	ArrayList<ExcuteSentece> mArrayList = new ArrayList<ExcuteSentece>();
	ExcuteSentece mLastExcuteSentece;

	public void addExcuteCommand(SentenceObject s) {
		ExcuteSentece e = new ExcuteSentece(s, System.currentTimeMillis());
		mArrayList.add(e);
		mLastExcuteSentece = e;
	}

	class ExcuteSentece {
		private SentenceObject excute;
		private long excuteTime;

		public ExcuteSentece(SentenceObject excute, long excuteTime) {
			this.excute = excute;
			this.excuteTime = excuteTime;
		}
	}
//
//	/**
//	 * Completion new order use last 100 seconds order
//	 * 
//	 * @param s
//	 */
//	public void autoCompletion(SentenceObject s) {
//		long lastTime = 100 * 1000;
//
//		if (mLastExcuteSentece != null && (System.currentTimeMillis() - mLastExcuteSentece.excuteTime) < lastTime) {
//			for (TextObject to : mLastExcuteSentece.excute.getmArrayList()) {
//				if (!s.getmTypes().contains(to.getType())) {
//					s.appendTextObject(to);
//				}
//			}
//		}
//
//	}

}
