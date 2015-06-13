package com.iii360.external.recognise.util;

import java.util.Calendar;

import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.sup.common.utl.TimerTicker;

public class RecordUpLoadRunnable implements Runnable {
	private BasicServiceUnion mUnion;
	private TimerTicker ticker;

	public RecordUpLoadRunnable(BasicServiceUnion union) {
		mUnion = union;
		pushStack();
		// run();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LogManager.e("push statck");
				RecordUpLoadHandler.upLoadFileToServer();
				pushStack();
			}
		}).start();
	}

	private void pushStack() {
		LogManager.e("push statck");
		if (mUnion != null) {
			LogManager.e("push statck");
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 10);
			// ticker = new TimerTicker(System.currentTimeMillis()+1000*10, true, 1, Calendar.DAY_OF_MONTH);
			ticker = new TimerTicker(calendar.getTimeInMillis(), true, 1, Calendar.DAY_OF_MONTH);
			mUnion.getTaskSchedu().pushStackatTime(this, ticker.getRunTime(), KeyList.TASKKEY_UPLOAD_RECORD_TO_SERVER);
		}
	}
}
