package com.voice.common.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.sup.common.utl.stringPreHandlingModule;
import com.voice.assistant.main.KeyList;
import com.voice.assistant.main.MyApplication;
import com.voice.assistant.main.TTSControllerProxy;
import com.voice.common.util.ITTSService.Stub;
import com.voice.common.util.nlp.CommandExcuteTimeProcess;
import com.voice.common.util.time.TimeUnit;

public class TTSService extends Service {
	private BaseContext baseContext;
	private TTSControllerProxy mControllerProxy;
	private BasicServiceUnion mUnion;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	private ITTSService.Stub mBinder = new Stub() {

		@Override
		public void stopPlay() throws RemoteException {
			// TODO Auto-generated method stub
			mControllerProxy.stop();
		}

		@Override
		public void play(String ttsPlayContent) throws RemoteException {
			// TODO Auto-generated method stub
			mControllerProxy.play(ttsPlayContent);
		}

		@Override
		public void settype(int type) throws RemoteException {
			baseContext.setPrefInteger(KeyList.PKEY_TTS_PLAY_CHOOSE, type);
			mControllerProxy.setType(type);
		}

		@Override
		public void setLightStatusAndTime(String from, String to, boolean isOpen) throws RemoteException {
			// TODO Auto-generated method stub
			LogManager.e("from " + from + "  to " + to);
			if (isOpen) {
				baseContext.setPrefString(KeyList.PKEY_BUTTON_LINGHT_CLOSE_TIME, from);
				baseContext.setPrefString(KeyList.PKEY_BUTTON_LINGHT_OPEN_TIME, to);
			}
			// 确保时间能够被设置
			baseContext.setPrefBoolean(KeyList.PKEY_BUTTON_LINGHT_ON, isOpen);
			MyApplication app = (MyApplication) getApplication();
			app.updateLigthControl();
		}

		@Override
		public void setWeatherStatusAndTime(String time, boolean isOpen) throws RemoteException {
			// TODO Auto-generated method stub
			baseContext.setPrefBoolean(KeyList.PKEY_KEEP_WEATHER_BROADCAST, isOpen);
			if (time == null || time.length() <= 4) {
				return;
			}
			String hour = time.substring(time.length() - 4);
			String day = time.substring(0, time.length() - 4);
			String hourTime = hour.substring(0, 2) + "点" + hour.substring(2) + "分";

			String command = "";
			command = day;
			if (day.equals("一次")) {
				command = hourTime;
			} else if (day.equals("每天")) {
				command = "每天" + hourTime;
			} else if (day.equals("周末")) {
				command = "周六到周日" + hourTime;
			} else if (day.equals("工作日")) {
				command = "周一到周五" + hourTime;
			}
			command = command + "播报天气";
			baseContext.setPrefString(KeyList.PKEY_SAVE_SET_WEATHER_TIME, command);
			setWeatherBroad();
		}

		/**
		 * @deprecated
		 */
		@Override
		public void setWeatherEnable(boolean enable) throws RemoteException {
			// TODO Auto-generated method stub
			baseContext.setPrefBoolean(KeyList.PKEY_KEEP_WEATHER_BROADCAST, enable);
			setWeatherBroad();
		}

		/**
		 * @deprecated
		 */
		@Override
		public void setWeatherTime(String time) throws RemoteException {
			// TODO Auto-generated method stub
			if (time == null || time.length() <= 4) {
				return;
			}
			String hour = time.substring(time.length() - 4);
			String day = time.substring(0, time.length() - 4);
			String hourTime = hour.substring(0, 2) + "点" + hour.substring(2) + "分";

			String command = "";
			command = day;
			if (day.equals("一次")) {
				command = hourTime;
			} else if (day.equals("每天")) {
				command = "每天" + hourTime;
			} else if (day.equals("周末")) {
				command = "周六到周日" + hourTime;
			} else if (day.equals("工作日")) {
				command = "周一到周五" + hourTime;
			}
			command = command + "播报天气";
			baseContext.setPrefString(KeyList.PKEY_SAVE_SET_WEATHER_TIME, command);
			setWeatherBroad();
		}

		/**
		 * @deprecated
		 */
		@Override
		public void setLightOn(boolean isOn) throws RemoteException {
			// TODO Auto-generated method stub
			LogManager.e("isOn " + isOn);
			baseContext.setPrefBoolean(KeyList.PKEY_BUTTON_LINGHT_ON, isOn);
			MyApplication app = (MyApplication) getApplication();
			app.updateLigthControl();
		}

		/**
		 * @deprecated
		 */
		@Override
		public void setLightTime(String from, String to) throws RemoteException {
			// TODO Auto-generated method stub
			LogManager.e("from " + from + "  to " + to);
			baseContext.setPrefString(KeyList.PKEY_BUTTON_LINGHT_CLOSE_TIME, from);
			baseContext.setPrefString(KeyList.PKEY_BUTTON_LINGHT_OPEN_TIME, to);
			// 确保时间能够被设置
			baseContext.setPrefBoolean(KeyList.PKEY_BUTTON_LINGHT_ON, true);
			MyApplication app = (MyApplication) getApplication();
			app.updateLigthControl();
		}

	};

	private void setWeatherBroad() {
		int id = baseContext.getPrefInteger(KeyList.PKEY_KEEP_WEATHER_TASKID);
		mUnion.getTaskSchedu().removeTaskById(id);
		boolean enable = baseContext.getPrefBoolean(KeyList.PKEY_KEEP_WEATHER_BROADCAST);
		if (enable) {
			String text = baseContext.getPrefString(KeyList.PKEY_SAVE_SET_WEATHER_TIME);
			if (text != null && text.length() > 0) {
				TimeUnit tu = CommandExcuteTimeProcess.getInstance(mUnion).getTp().handText(text);
				if (tu != null) {
					final String commandString = stringPreHandlingModule.numberTranslator(text).replace(tu.Time_Expression, "");
					id = mUnion.getTaskSchedu().pushStackWithTask(mUnion, commandString, tu.getTimeTicker());
					baseContext.setPrefInteger(KeyList.PKEY_KEEP_WEATHER_TASKID, id);
				}
			}
		}
	}

	public void onCreate() {
		super.onCreate();
		MyApplication app = (MyApplication) getApplication();
		mUnion = app.getUnion();
		baseContext = mUnion.getBaseContext();
		mControllerProxy = (TTSControllerProxy) mUnion.getTTSController();
	};
}
