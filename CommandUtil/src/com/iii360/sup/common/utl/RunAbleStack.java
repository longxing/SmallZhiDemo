package com.iii360.sup.common.utl;

import java.io.Serializable;

/**
 * 
 * @author jushang
 * 
 */

public class RunAbleStack implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RunExcute runExcute;

	// Handler
	// Looper

	public void push(RunExcute r) {
		// mrunStk.add(r);
		// mrunStk.
		RunExcute tempExcute = runExcute;
		RunExcute pre = null;
		if (tempExcute == null) {
			runExcute = r;
			return;
		}
		while (tempExcute != null) {
			if (r.getExcuteTime() > tempExcute.getExcuteTime()) {
				pre = tempExcute;
				tempExcute = tempExcute.next;
			} else {
				r.next = tempExcute;
				if (pre != null) {
					pre.next = r;
				} else {
					runExcute = r;
				}
				return;
			}
		}
		pre.next = r;

	}

	public RunExcute pop() {
		// return mrunStk.poll();
		RunExcute pre = runExcute;
		runExcute = runExcute.next;
		return pre;
	}

	public RunExcute peek() {
		return runExcute;
	}

	public boolean isEmpty() {
		return runExcute == null;
	}

	public int size() {
		int size = 0;
		RunExcute TempExcute = runExcute;
		while (TempExcute != null) {
			TempExcute = TempExcute.next;
			size++;
		}
		return size;
	}

	public void removeExcute(Runnable r) {
		RunExcute tempExcute = runExcute;
		RunExcute pre = null;
		while (tempExcute != null) {

			if (tempExcute.mRunnable == r) {
				if (pre == null) {
					runExcute = runExcute.next;
					break;
				} else {
					pre.next = tempExcute.next;
					break;
				}

			} else {
				pre = tempExcute;
				tempExcute = tempExcute.next;
			}
		}
	}

	public class RunExcute implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private RunExcute next;
		private Runnable mRunnable;
		private long excuteTime;

		public RunExcute() {
		}

		public Runnable getmRunnable() {
			return mRunnable;
		}

		public void setmRunnable(Runnable mRunnable) {
			this.mRunnable = mRunnable;
		}

		public long getExcuteTime() {
			return excuteTime;
		}

		public void setExcuteTime(long excuteTime) {
			this.excuteTime = excuteTime;
		}
	}

	public void updateTime(long distance) {
		RunExcute tempExcute = runExcute;

		while (tempExcute != null) {
			tempExcute.excuteTime += distance;
			tempExcute = tempExcute.next;
		}
	}

}
