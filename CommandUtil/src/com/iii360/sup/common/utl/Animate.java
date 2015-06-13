package com.iii360.sup.common.utl;


public class Animate {
	// 动画消息线程
	private static MessageQueue messageQueue = new MessageQueue();

	// 动画线程
	private Thread animateThread = null;
	// 动画
	private Runnable doing = null;
	
	public Animate() {

	}
	
	public static void post(Runnable runnable) {
		messageQueue.post(runnable);
	}

	/**
	 * 开启动画，动画操作必须要在handler下完成
	 * 
	 * @param doing
	 * @return
	 */
	public Animate start(Runnable aDoing) {
		if (!messageQueue.inThread()) {
			throw new RuntimeException(
					"start must run on Animate handler Thread");
		}

		doing = aDoing;
		if (animateThread != null && animateThread.isAlive()) {
			return this;
		}
		// 准备线程
		animateThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					doing.run();
				}
			}
		});
		// 开始动画
		animateThread.start();
		return this;
	}

	public Animate frame(Runnable runnable) {
		if (!messageQueue.inThread()) {
			throw new RuntimeException(
					"start must run on Animate handler Thread");
		}

		post(runnable);
		return this;
	}

	public Animate stop() {
		return stop(0);
	}

	public Animate stop(final long delay) {
		if (!messageQueue.inThread()) {
			throw new RuntimeException(
					"start must run on Animate handler Thread");
		}
		// 关闭动画
		try {
			if (delay > 0) {
				Thread.sleep(delay);
			}
			if (animateThread != null && animateThread.isAlive()) {
				animateThread.interrupt();
				// 等待线程结束
				animateThread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return this;
	}
}
