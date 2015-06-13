package com.iii.wifi.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;
import com.voice.common.util.ITTSService;

public class TTSUtil {

	private ITTSService mTTSService;
	private Context context;
	private boolean firstInit = false;
	private String tempString = null;

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			firstInit = false;
			mTTSService = null;
			LogManager.e("tts server connect false");
		}

		/**
		 * 服务连接成功
		 */
		public void onServiceConnected(ComponentName name, IBinder service) {
			firstInit = true;
			mTTSService = ITTSService.Stub.asInterface(service);
			LogManager.e("tts server connect sucess");
			if (tempString != null) {
				playContent(tempString);
				tempString = null;
			}
			// playContent("远程  tts 绑定 成功");
		}
	};
	private SuperBaseContext mPreferenceUtil;

	public TTSUtil(Context context) {
		this.context = context;
		initService(context);
		mPreferenceUtil = new SuperBaseContext(context);
	}

	private void initService(Context context) {
		LogManager.e("start bind");
		Intent service = new Intent("com.voice.common.util.ITTSService");
		context.bindService(service, mConnection, Context.BIND_AUTO_CREATE);
	}

	// 停止服务
	public void stopServices() {
		if (mConnection != null && context != null) {
			context.unbindService(mConnection);
		}

	}

	public void playContent(String content) {
		LogManager.e(content);
		if (firstInit) {
			try {
				mTTSService.play(content);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				initService(context);
			}
		} else {
			LogManager.e("set tempString " + tempString);
			tempString = content;
			initService(context);

		}
	}

	public void stopPlay() {
		if (firstInit) {
			try {
				mTTSService.stopPlay();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				initService(context);
			}
		} else {
			initService(context);
			tempString = null;
		}
	}

	public void setType(int type) {
		if (firstInit) {
			try {
				mTTSService.settype(type);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
				initService(context);
			}
		} else {
			initService(context);
		}
	}

	/**
	 * peter
	 * 
	 * @param from
	 * @param to
	 * @param isOpen
	 */
	public void setLightStateAndTime(String from, String to, boolean isOpen) {
		if (firstInit) {
			try {
				LogManager.e("from " + from + " to " + to);
				mTTSService.setLightStatusAndTime(from, to, isOpen);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
				initService(context);
			}
		} else {
			initService(context);
		}
	}

	/**
	 * peter set timer weather report
	 * 
	 * @param time
	 */
	public void setWeatherStateAndTime(String time, boolean isOpen) {
		if (firstInit) {
			try {
				mTTSService.setWeatherStatusAndTime(time, isOpen);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
				initService(context);
			}
		} else {
			initService(context);
		}
	}

	/**
	 * @deprecated
	 * @param enable
	 */
	public void setWeatherEnable(boolean enable) {
		if (firstInit) {
			try {
				mTTSService.setWeatherEnable(enable);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
				initService(context);
			}
		} else {
			initService(context);
		}
	}

	/**
	 * @deprecated
	 * @param time
	 */
	public void setWeatherTime(String time) {
		if (firstInit) {
			try {
				mTTSService.setWeatherTime(time);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
				initService(context);
			}
		} else {
			initService(context);
		}
	}

	/**
	 * @deprecated
	 * @param enable
	 */
	public void setLightEnable(boolean enable) {
		if (firstInit) {
			try {
				LogManager.e("enable " + enable);
				mTTSService.setLightOn(enable);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
				initService(context);
			}
		} else {
			initService(context);
		}
	}

	/**
	 * @deprecated
	 */
	public void setLightTime(String from, String to) {
		if (firstInit) {
			try {
				LogManager.e("from " + from + " to " + to);
				mTTSService.setLightTime(from, to);
			} catch (RemoteException e) {
				// TODO: handle exception
				e.printStackTrace();
				initService(context);
			}
		} else {
			initService(context);
		}
	}

	/**
	 * 根据是否设置休眠模式，判断TTS是否在工作
	 * 
	 * @return
	 */
	public boolean isWorking() {
		if (mPreferenceUtil == null)
			mPreferenceUtil = new SuperBaseContext(context);
		String ledSwitch = mPreferenceUtil.getPrefString(KeyList.PKEY_LED_STATUS, "false");
		try {
			if ("true".equals(ledSwitch)) {
				String hhmm = new SimpleDateFormat("HHmm").format(new Date());
				String string = mPreferenceUtil.getPrefString(KeyList.PKEY_LED_TIME);
				String from = string.substring(0, 4);
				String to = string.substring(4);
				// 是否在休眠时间内
				if (from.compareTo(to) >= 0) {
					if (hhmm.compareTo(from) >= 0)
						return false;
					if (hhmm.compareTo(to) <= 0)
						return false;
				} else {
					if (hhmm.compareTo(from) >= 0 && hhmm.compareTo(to) <= 0) {
						return false;
					}
				}

			}
		} catch (Exception e) {
		}

		return true;
	}
}
