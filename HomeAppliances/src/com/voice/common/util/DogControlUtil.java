package com.voice.common.util;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.iii360.base.common.utl.LogManager;
import com.smallzhi.homeappliances.control.DogClient;
import com.smallzhi.homeappliances.util.CoreUtil.OnGetDevAdd;
import com.voice.common.util.nlp.HouseCommandParse;

public class DogControlUtil {
	private IDogControlService mControlService;
	public boolean inited = false;
	private Context context;
	private static DogControlUtil controlUtil;
	private int mFindedDogSize = 0;
	private List<CommandInfo> commands;

	private IChangeListener mChangeListener = new IChangeListener.Stub() {

		@Override
		public void onCommandChange() throws RemoteException {
			// TODO Auto-generated method stub
			buildDogCommand();
		}
	};
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			LogManager.e("dog service connect fail");
			inited = false;
			mControlService = null;
			if (mOnGetDevAdd != null) {
				mOnGetDevAdd.onDisConnect();
			}
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LogManager.e("dog service connect sucess");
			mControlService = IDogControlService.Stub.asInterface(service);
			try {
				mControlService.setCommandChangeListen(mChangeListener);
				LogManager.e("set listenr " + mChangeListener);
			} catch (RemoteException e1) {
				// e1.printStackTrace();
				LogManager.printStackTrace(e1);
			}
			inited = true;

			if (mOnGetDevAdd != null) {
				mOnGetDevAdd.onGetAdd(null);
			}
		}
	};

	public static DogControlUtil getInstance(Context context) {
		if (controlUtil == null) {
			controlUtil = new DogControlUtil(context);
		}
		return controlUtil;
	}

	private DogControlUtil(Context context) {
		this.context = context;
	}

	public void init() {
		Intent service = new Intent("com.voice.common.util.IDogControlService");
		if (!context.bindService(service, mConnection, context.BIND_AUTO_CREATE)) {
			if (mOnGetDevAdd != null) {
				mOnGetDevAdd.onDisConnect();
			}
		}
	}

	public List<CommandInfo> getCommands() {
		if (inited) {
			try {
				return mControlService.getCommand();
			} catch (RemoteException e) {
				LogManager.printStackTrace(e);
				init();
			}
		} else {
			init();
		}
		return null;
	}

	public boolean sendCommand(String info) {

		if (inited) {
			try {
				return mControlService.sendCommand(info);

			} catch (RemoteException e) {
				LogManager.printStackTrace(e);
				init();
			}
		} else {
			init();
		}
		return false;
	}

	public void destory() {
		controlUtil = null;
	}

	private OnGetDevAdd mOnGetDevAdd;

	public void setOnConnect(OnGetDevAdd onGetDevAdd) {
		mOnGetDevAdd = onGetDevAdd;
		if (inited) {
			mOnGetDevAdd.onGetAdd(null);
		} else {
			init();
		}
	}
	
	
	

	public void buildDogCommand() {
		if (commands != null) {
			for (CommandInfo info : commands) {
				HouseCommandParse.getInstanse().removeContent(info.getCommandName());
			}
		}
		commands = getCommands();

		if (commands != null) {
			for (CommandInfo info : commands) {
				LogManager.e("dog's command :" + info.getCommandName());
				HouseCommandParse.getInstanse().addContent(info.getCommandName(), info.getCommandContent(), DogClient.TYPE);
			}
		}

	}

}
