package com.iii.wifi.util;

import java.util.List;

import com.voice.common.util.IRemindService;
import com.voice.common.util.Remind;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class RemindUtil {
	private Context mContext;
	private IRemindService remindService;

	public RemindUtil(Context context) {
		mContext = context;
		init();
	}

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			remindService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			remindService = IRemindService.Stub.asInterface(service);

			// LogManager.e(getReMindList().size() +" REMIND ");

		}
	};

	// 绑定服务
	private void init() {
		Intent service = new Intent("com.voice.common.util.IRemindService");
		mContext.bindService(service, conn, Context.BIND_AUTO_CREATE);
	}

	// 解绑服务

	public void stopService() {
		if (conn != null && mContext != null) {
			mContext.unbindService(conn);
		}

	}

	public List<Remind> getReMindList() {
		try {
			if (remindService != null) {
				return remindService.getRemindList();
			} else {
				init();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			init();
		}
		return null;
	}

	public void deleteRemind(int id) {
		try {
			if (remindService != null) {
				remindService.deleteRemind(id);
			} else {
				init();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			init();
		}
	}

	public void addRemind(Remind remind) {
		try {
			if (remindService != null) {
				remindService.addRemind(remind);
			} else {
				init();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
