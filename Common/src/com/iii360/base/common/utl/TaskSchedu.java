package com.iii360.base.common.utl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TimerTask;

import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.sup.common.utl.ITimeUnit;
import com.iii360.sup.common.utl.SuperTaskSchedu;
import com.iii360.sup.common.utl.TaskExcuter;
import com.iii360.sup.common.utl.TimerTicker;
import com.iii360.sup.common.utl.file.ObjUtil;

public class TaskSchedu extends SuperTaskSchedu {

	private static final long serialVersionUID = 1L;
//	private static final String excuteArray_savePath = "/data/data/com.voice.assistant.main/models/task";
	private static final String excuteArray_savePath = "/mnt/sdcard/com.voice.assistant.main/models/task";
	/**
	 * 程序重启后第一次加载
	 * 
	 * @param mUnion
	 */
	public void loadTask(final BasicServiceUnion mUnion) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					//Fixme why sleep 20000s?
//					Thread.sleep(20000);
					LogManager.e("loadTask readly");
					if(new File(excuteArray_savePath).exists()){
						excuterArray = (ArrayList<com.iii360.sup.common.utl.TaskExcuter>) ObjUtil
								.loadFrom(excuteArray_savePath);
						LogManager.e("Load Task & taskSize " + excuterArray.size());
						for (final TaskExcuter t : excuterArray) {
							final TaskRunable runable = new TaskRunable(t, mUnion, TaskSchedu.this);
							if (t.timeUnit.getRunTime() > System.currentTimeMillis() && !t.isExcuted) {
								pushStackatTime(runable, t.timeUnit.getRunTime());
							}
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LogManager.printStackTrace(e);

				}
			}
		}).start();
		// LogManager.e("!!!!!!!!!!!!!!!!!!Load Task & taskSize " +
		// excuterArray.size());

	}

	public void saveTask() {
		try {
			ObjUtil.saveTo(excuteArray_savePath, excuterArray);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace(e);
		}
	}

	/**
	 * 用这个方法加入的任务会被存起来，用于在程序重启时重新加入
	 */
	public int pushStackWithTask(BasicServiceUnion mUnion, String needHand, TimerTicker timeUnit) {

		return pushStackWithTask(mUnion, needHand, timeUnit, false);
	}

	/**
	 * 用这个方法加入的任务会被存起来，用于在程序重启时重新加入
	 */
	public int pushStackWithTask(BasicServiceUnion mUnion, String needHand, TimerTicker timeUnit, boolean isSysCommand) {

		TaskExcuter excuter = new TaskExcuter();
		excuter.needHandle = needHand;
		excuter.timeUnit = timeUnit;
		excuter.isSystemCommand = isSysCommand;
		excuter.creatTime = System.currentTimeMillis();
		LogManager.e((timeUnit.getRunTime() - System.currentTimeMillis()) + "");
		TaskRunable runable = new TaskRunable(excuter, mUnion, this);
		if (timeUnit.getRunTime() > System.currentTimeMillis()) {
			excuter.id = pushStackatTime(runable, timeUnit.getRunTime());
		}
		excuterArray.add(excuter);
		saveTask();
		return excuter.id;
	}

	public void removeTaskByString(String needHand) {
		LogManager.e("remove " + needHand);
		for (TaskExcuter t : excuterArray) {
			if (t.needHandle.contains(needHand)) {
				removeTaskById(t.id);
				return;
			}
		}
		LogManager.e("not remove " + needHand);
	}

	public class TaskRunable implements Runnable {
		TaskExcuter mExcuter;
		BasicServiceUnion mUnion;
		TaskSchedu taskSchedu;

		public TaskRunable(TaskExcuter excuter, BasicServiceUnion union, TaskSchedu taskSchedu) {
			mExcuter = excuter;
			mUnion = union;
			this.taskSchedu = taskSchedu;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			LogManager.e(" mUnion==null" + (mUnion == null));
			LogManager.e(" mUnion.getCommandEngine()==null" + (mUnion.getCommandEngine() == null));

			mUnion.getBaseContext().setPrefBoolean("PKEY_REMIND_IS_REPEAT", isRepeat(mExcuter.timeUnit));
			
			boolean isRepeat = mExcuter.timeUnit.getReapteFlag();
			
			String text = mExcuter.needHandle;
			if (isRepeat) {
				pushStackatTime(this, mExcuter.timeUnit.getRunTime(), mExcuter.id);
			} else {
				mExcuter.isExcuted = true;
				saveTask();
			}
			if (isRepeat && (mExcuter.timeUnit.repeatType == Calendar.HOUR_OF_DAY 
					|| mExcuter.timeUnit.repeatType == Calendar.MINUTE ||
					mExcuter.timeUnit.repeatType == Calendar.SECOND)) {
				text = "不可延时的" + text;
			}
			mUnion.getCommandEngine().handleText(text);
			// taskSchedu.removeTaskById(mExcuter.id);
		}

		private boolean isRepeat(TimerTicker t) {
			if (t.repeatFlag || t.avalibeFlag) {
				return true;
			}
			return false;

		}
	}

	@Override
	public void onTaskChanged() {
		saveTask();
	}

}
