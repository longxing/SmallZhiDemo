package com.iii360.sup.common.utl;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MessageQueueNew {
	// 消息线程
	private Thread looperThread = null;
	// 用于接收动
	private Handler handler = null;

	public MessageQueueNew() {
		this(Thread.NORM_PRIORITY);
	}

	public MessageQueueNew(int priority) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Runnable runnable = (Runnable) msg.obj;
				LogManager.e("开始执行消息：----------------------");
				runnable.run();
			}

		};
	}

	public void post(Runnable runnable) {
		Message msg = Message.obtain();
		msg.obj = runnable;
		LogManager.e("发送消息：----------------------");
		handler.sendMessage(msg);
	}

	public boolean inThread() {
		return Thread.currentThread().equals(looperThread);
	}

}
