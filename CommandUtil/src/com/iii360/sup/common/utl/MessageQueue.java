package com.iii360.sup.common.utl;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;

public class MessageQueue {
	// 消息线程
	private Thread looperThread = null;
	// 用于接收动
	private Handler handler = null;

	private List<Runnable> preRunnable = new ArrayList<Runnable>();

	public MessageQueue() {
		this(Thread.NORM_PRIORITY);
	}

	public MessageQueue(int priority) {
		looperThread = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				handler = new Handler();
				for (Runnable runable : preRunnable) {
					try {
						runable.run();
					} catch (Exception e) {

					}
				}
				Looper.loop();
			}
		});
		looperThread.setPriority(priority);
		looperThread.start();
	}

	public void post(Runnable runnable) {
		if (handler == null) {
			preRunnable.add(runnable);
			return;
		}
		handler.post(runnable);
	}

	public boolean inThread() {
		return Thread.currentThread().equals(looperThread);
	}
}
