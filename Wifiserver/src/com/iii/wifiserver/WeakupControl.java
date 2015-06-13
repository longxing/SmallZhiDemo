package com.iii.wifiserver;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.iii360.sup.common.utl.LogManager;
import com.iii360.voiceassistant.aidl.IWakeUpController;

/**
 * @author hefeng
 *
 */
public class WeakupControl {
	private IWakeUpController mWakeUpController;
	private Context context;

	private boolean isOpenDelay = false;

	public WeakupControl(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public void closeWeakup() {
		LogManager.e("WeakupControl AKEY_CLOSE_WEAKEUP : " + (mWakeUpController != null));

		if (mWakeUpController != null) {
			try {
				mWakeUpController.close();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			isOpenDelay = false;
		}
	}

	public void openWeakup() {
		LogManager.e("WeakupControl AKEY_OPEN_WEAKEUP : " + (mWakeUpController != null));

		if (mWakeUpController != null) {
			try {
				mWakeUpController.open();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			isOpenDelay = true;
		}
	}

	public void bindControlService() {
		LogManager.i("WeakupControl bindControlService");
		if (connection != null && mWakeUpController != null) {
			context.unbindService(connection);
		}
		context.bindService(new Intent(IWakeUpController.class.getName()), connection, Service.BIND_AUTO_CREATE);
	}

	public void unbindControlService() {
		context.unbindService(connection);
	}

	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			LogManager.e("onServiceDisconnected");

			mWakeUpController = null;
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			// TODO Auto-generated method stub
			LogManager.e("onServiceConnected");

			mWakeUpController = IWakeUpController.Stub.asInterface(arg1);

			if (isOpenDelay) {
				openWeakup();
			}
		}
	};

}
