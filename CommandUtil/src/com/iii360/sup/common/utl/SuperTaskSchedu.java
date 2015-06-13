package com.iii360.sup.common.utl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.iii360.sup.common.utl.RunAbleStack.RunExcute;
import com.iii360.sup.common.utl.file.ObjUtil;

public abstract class SuperTaskSchedu implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected RunAbleStack mAbleStack;
	protected Thread mThread;
	private HashMap<Integer, Runnable> mTaskRunableHash = new HashMap<Integer, Runnable>();
	protected ArrayList<TaskExcuter> excuterArray = new ArrayList<TaskExcuter>();

	private final int MAX_VALUE = Integer.MAX_VALUE - 100;
	private long SYSTEM_TIME;
	private long SYSTEM_TIME_OLD;

	public SuperTaskSchedu() {

		mAbleStack = new RunAbleStack();
		mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					SYSTEM_TIME_OLD = SYSTEM_TIME;
					SYSTEM_TIME = System.currentTimeMillis();
					if (!mAbleStack.isEmpty()) {
						RunExcute r = mAbleStack.peek();
						long distance = SYSTEM_TIME - r.getExcuteTime();
						if (distance > 0) {
							LogManager.i("real pop one " + mAbleStack.size());
							r = mAbleStack.pop();
							excuterArray.remove(r.getmRunnable());
							r.getmRunnable().run();
						}
					}

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		mThread.start();

	}

	public int pushStack(Runnable r) {

		RunExcute runExcute = mAbleStack.new RunExcute();
		runExcute.setExcuteTime(System.currentTimeMillis());
		runExcute.setmRunnable(r);
		mAbleStack.push(runExcute);
		int id = new Random().nextInt(MAX_VALUE);
		mTaskRunableHash.put(id, r);
		return id;
	}

	public int pushStackDelay(Runnable r, long DelayTime) {
		return pushStackatTime(r, System.currentTimeMillis() + DelayTime);
	}

	public int pushStackatTime(Runnable r, long Time) {

		LogManager.printStackTrace();
		LogManager.e("push new task  " + (Time - System.currentTimeMillis()));
		RunExcute runExcute = mAbleStack.new RunExcute();
		runExcute.setExcuteTime(Time);
		runExcute.setmRunnable(r);
		mAbleStack.push(runExcute);
		int id = new Random().nextInt(MAX_VALUE);
		mTaskRunableHash.put(id, r);
		return id;
	}

	public void pushStackatTime(Runnable r, long Time, int id) {
		LogManager.e("push new task");
		RunExcute runExcute = mAbleStack.new RunExcute();
		runExcute.setExcuteTime(Time);
		runExcute.setmRunnable(r);

		if (mTaskRunableHash.containsKey(id)) {
			mAbleStack.removeExcute(mTaskRunableHash.get(id));
			mTaskRunableHash.remove(id);
		}

		mAbleStack.push(runExcute);
		mTaskRunableHash.put(id, r);
	}

	public void removeStack(Runnable r) {
		mAbleStack.removeExcute(r);
	}

	/**
	 * 用来根据ID移除任务的
	 */
	public void removeTaskById(int id) {
		LogManager.e("remove task " + id);
		if (mTaskRunableHash.containsKey(id)) {
			removeStack(mTaskRunableHash.get(id));
		}
		for (TaskExcuter t : excuterArray) {
			if (t.id == id) {
				excuterArray.remove(t);
				break;
			}
		}
		onTaskChanged();
	}

	public ArrayList<TaskExcuter> getRunTask() {
		return excuterArray;
	}

	/**
	 * 时间变化后 通知更新时间
	 */
	public void updateTime() {
		long distance;
		synchronized (mAbleStack) {
			LogManager.e(SYSTEM_TIME_OLD + " system Time " + System.currentTimeMillis());
			distance = System.currentTimeMillis() - SYSTEM_TIME_OLD;
			mAbleStack.updateTime(distance);
			SYSTEM_TIME_OLD = System.currentTimeMillis();
			SYSTEM_TIME = SYSTEM_TIME_OLD;
		}
		for (TaskExcuter excute : excuterArray) {
			excute.timeUnit.BaseTime += distance;
		}
		onTaskChanged();
	}

	public abstract void onTaskChanged();

}
