package com.voice.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.common.utl.TaskSchedu;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.sup.common.utl.TaskExcuter;
import com.iii360.sup.common.utl.TimerTicker;
import com.voice.assistant.main.MyApplication;

public class RemindService extends Service {
	private BaseContext baseContext;
	private TaskSchedu mSchedu;
	private BasicServiceUnion mUnion;
	private String removeNotice = "^(提醒我|叫我)";
	private Pattern pattern = Pattern.compile(removeNotice);

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	private IRemindService.Stub mBinder = new IRemindService.Stub() {

		@Override
		public List<Remind> getRemindList() throws RemoteException {
			// TODO Auto-generated method stub

			ArrayList<TaskExcuter> mList = mSchedu.getRunTask();
			ArrayList<Remind> result = new ArrayList<Remind>();

			for (TaskExcuter excuter : mList) {
				Remind remind = new Remind(excuter.timeUnit);
				String needHand = excuter.needHandle;
				Matcher m = pattern.matcher(excuter.needHandle);
				if (m.find()) {
					needHand = needHand.replace(m.group(), "");
				}
				LogManager.e(needHand );
				remind.setInfo(excuter.creatTime, excuter.id, needHand);
				result.add(remind);
			}
			return result;
		}

		@Override
		public void deleteRemind(int id) throws RemoteException {
			// TODO Auto-generated method stub
			mSchedu.removeTaskById(id);
		}

		@Override
		public void addRemind(Remind r) throws RemoteException {
			TimerTicker ticker = new TimerTicker(r.BaseTime, r.repeatFlag, r.repeatDistance, r.repeatType);
			ticker.setAvalibe(r.avalibeFlag, r.avalibeFrom, r.avalibeTo);
			mSchedu.pushStackWithTask(mUnion, r.needHand, ticker);
		}

	};

	public void onCreate() {
		MyApplication app = (MyApplication) getApplication();
		mUnion = app.getUnion();
		baseContext = mUnion.getBaseContext();
		mSchedu = mUnion.getTaskSchedu();
	};
}
