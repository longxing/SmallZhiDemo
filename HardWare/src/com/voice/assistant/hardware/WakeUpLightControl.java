package com.voice.assistant.hardware;

import java.util.Calendar;

import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.recognise.ILightController;
import com.iii360.sup.common.utl.Animate;
import com.iii360.sup.common.utl.TimerTicker;

public class WakeUpLightControl implements ILightController {
	
	private static final String TAG = "HardWare WakeUpLightControl";
	// 模式
	private int mode = MODE_WARN;
	// 正常模式下的状态
	private int mState = RECOGNISE_STATE_CLOSE;
	private int mLevel;
	private IHardWare mHardWare;
	private String lightName = IHardWare.LIGHT_WAKE_UP;
	private String greenLight = IHardWare.LIGHT_LOGO;
	private BasicServiceUnion mUnion;
	// 客户端设置LED关闭时间段，true表示唤醒灯正在点亮
	private boolean isLightOn = true;
	// 唤醒灯动画
	private Animate animation = new Animate();
	// logo灯动画
	private Animate logoAnimation = new Animate();

	public WakeUpLightControl(IHardWare hardWare, BasicServiceUnion union) {
		mHardWare = hardWare;
		mUnion = union;
	}

	// 第一次进入音量控制灯亮度，先是全亮
	private boolean isFirstVoiceChange = true;
	// 错误在先的识别，应当被忽略
	private boolean hasError = false;

	@Override
	public void updateState(final int state, final Object params) {
		LogManager.d(TAG,"change updateState: " + state + " , " + params);
		mState = state;
		mLevel = (Integer) params;

		if (mode != MODE_NORMAL) {
			// 非正常工作情况下，不可改变灯的状态
			return;
		}

		int leve = 1;
		boolean connectWifi = mUnion.getBaseContext().getGlobalBoolean(KeyList.GKEY_BOOL_IS_CONNECT_WIFIGATE);
		/*
		 * 唤醒直接识别过程： INIT=> VOICE_LEVEL_CHANGE录音过程 => NORMAL唤醒结束=>
		 * reconiseStopAnimation() 识别过程： INIT=> VOICE_LEVEL_CHANGE录音过程 =>
		 * RECONISING开始识别 => RECOGNISE_STATE_SUCCESS成功识别 /
		 * RECOGNISE_STATE_ERROR识别错误
		 */
		switch (state) {
		// ////////////////////唤醒状态////////////////////////
		case RECOGNISE_STATE_NORMAL:// 唤醒对话结束，等待识别结果
			reconiseStartAnimation();
			return;
			// ////////////////////识别状态////////////////////////
		case RECOGNISE_STATE_RECONISING:// 识别对话结束，等待识别结果
			// 发生过错误后，紧接着的识别是错误的，应当忽略
			if (hasError) {
				hasError = false;
				return;
			}
			//
			reconiseStartAnimation();
			return;
			// ////////////////////通用状态////////////////////////
		case RECOGNISE_STATE_INIT:// 开始对话
			return;
		case RECOGNISE_STATE_VOICE_LEVEL_CHANGE:// 对话中，灯亮度变化
			mHardWare.controlLight(greenLight, IHardWare.LIGHT_CLOSE);
			if (isFirstVoiceChange) {// 第一次闪烁为最亮
				// 亮灯动画
				mHardWare.controlLight(lightName, IHardWare.LIGHT_ON);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mHardWare.controlLight(lightName, IHardWare.LIGHT_CLOSE);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				isFirstVoiceChange = false;
			}
			leve = mLevel + 2;
			mHardWare.controlLight(lightName, leve);
			return;
		case RECOGNISE_STATE_ERROR:// 识别或唤醒中发生错误
			// 关闭动画后，标识为发生了错误
			reconiseStopAnimation();
			hasError = true;
			return;
		case RECOGNISE_STATE_SUCCESS:// 识别录音成功，马上要恢复正常状态
			// 唤醒与识别同时
			reconiseStopAnimation();
			return;
			// ////////////////////手动状态////////////////////////
		case RECOGNISE_STATE_OPEN:// 唤醒体系被开启，联网的情况下，常亮
			if (isLightOn && connectWifi) {
				leve = IHardWare.LIGHT_OFF;
			} else {
				leve = IHardWare.LIGHT_CLOSE;
			}
			// 关闭动画后，调节灯
			reconiseStopAnimation();
			mHardWare.controlLight(greenLight, IHardWare.LIGHT_CLOSE);
			mHardWare.controlLight(lightName, leve);
			return;

		case RECOGNISE_STATE_CLOSE:// 唤醒体系被关闭，断网的情况下，常灭
			leve = IHardWare.LIGHT_CLOSE;
			mHardWare.controlLight(greenLight, IHardWare.LIGHT_CLOSE);
			mHardWare.controlLight(lightName, leve);
			return;
		default:
			LogManager.d(TAG,"default");
			break;
		}
	}

	@Override
	public int getState() {
		LogManager.d(TAG,"  WakeUpLightControl getState: " + mState);
		return mState;
	}

	public void upLightState() {
		// 客户端遥控器-设置使用唤醒灯的关闭时间段，在此时间段内灯不工作
		boolean isLightControlOpen = mUnion.getBaseContext().getPrefBoolean(KeyList.PKEY_BUTTON_LINGHT_ON);
		LogManager.d(TAG,"WakeUpLightControl", "led state update!");
		mUnion.getTaskSchedu().removeTaskById(KeyList.TASKKEY_LIGHT_AUTOOFF);
		mUnion.getTaskSchedu().removeTaskById(KeyList.TASKKEY_LIGHT_AUTOON);

		if (isLightControlOpen) {
			// 时间段内唤醒灯使用
			String from = mUnion.getBaseContext().getPrefString(KeyList.PKEY_BUTTON_LINGHT_CLOSE_TIME);
			String to = mUnion.getBaseContext().getPrefString(KeyList.PKEY_BUTTON_LINGHT_OPEN_TIME);

			if (from == null || from.equals("null") || to == null || to.equals("null")) {
				return;
			}
			final int ifrom = Integer.valueOf(from);
			final int ito = Integer.valueOf(to);
			int distance = 0;
			if (ifrom > ito) {
				distance = 2359;
			}

			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minte = c.get(Calendar.MINUTE);
			// 4位数时分值
			final int currentTime = hour * 100 + minte;
			LogManager.e(currentTime + "");
			// from定时关唤醒灯
			mUnion.getTaskSchedu().pushStack(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Calendar c = Calendar.getInstance();
					int hour = ifrom / 100;
					int minute = ifrom % 100;
					c.set(Calendar.HOUR_OF_DAY, hour);
					c.set(Calendar.MINUTE, minute);

					TimerTicker ticker = new TimerTicker(c.getTimeInMillis(), true, 1, Calendar.DAY_OF_MONTH);
					mUnion.getTaskSchedu().pushStackatTime(this, ticker.getRunTime(), KeyList.TASKKEY_LIGHT_AUTOOFF);
					isLightOn = false;
					updateStateOnRunnable(mState, mLevel);

					if (KeyList.IS_TTS_DEBUG) {
						KeyList.VOICE_RECOGNIZER_FINISH = System.currentTimeMillis();
						ITTSController mTTSController = (ITTSController) mUnion.getBaseContext().getGlobalObject(KeyList.GKEY_TTS_CONTORLLER);
						mTTSController.syncPlay("进入白昼模式，唤醒灯工作");
					}
					
				}
			});

			// to定时开唤醒灯
			mUnion.getTaskSchedu().pushStack(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Calendar c = Calendar.getInstance();
					int hour = ito / 100;
					int minute = ito % 100;
					c.set(Calendar.HOUR_OF_DAY, hour);
					c.set(Calendar.MINUTE, minute);
					TimerTicker ticker = new TimerTicker(c.getTimeInMillis(), true, 1, Calendar.DAY_OF_MONTH);
					mUnion.getTaskSchedu().pushStackatTime(this, ticker.getRunTime(), KeyList.TASKKEY_LIGHT_AUTOON);
					isLightOn = true;
					updateStateOnRunnable(mState, mLevel);

					if (KeyList.IS_TTS_DEBUG) {
						KeyList.VOICE_RECOGNIZER_FINISH = System.currentTimeMillis();
						ITTSController mTTSController = (ITTSController) mUnion.getBaseContext().getGlobalObject(KeyList.GKEY_TTS_CONTORLLER);
						mTTSController.syncPlay("进入夜间模式，唤醒灯休眠");
					}
					
				}
			});

			// 立刻执行一次
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// int newito = ito + distance;
			LogManager.d(TAG,from + "-------" + currentTime + "--------" + ito);
			if (currentTime >= ifrom && currentTime <= ito) {
				isLightOn = false;
				updateStateOnRunnable(mState, mLevel);
				LogManager.d(TAG,"false");
			} else if (distance != 0 && (ifrom <= currentTime && currentTime <= distance)) {
				isLightOn = false;
				updateStateOnRunnable(mState, mLevel);
				LogManager.d(TAG,"false");
			} else if (distance != 0 && (0 <= currentTime && currentTime < ito)) {
				isLightOn = false;
				updateStateOnRunnable(mState, mLevel);
				LogManager.d(TAG,"false");
			} else {
				isLightOn = true;
				LogManager.d(TAG,"true");
				updateStateOnRunnable(mState, mLevel);
			}

		} else {
			// 不使用唤醒灯
			isLightOn = true;
			LogManager.d(TAG,"wakeup light  on true");
			updateState(mState, mLevel);

		}
	}

	public void updateMode(final int aMode) {
		LogManager.d(TAG,"doing mode: " + mode);
		if (mode == aMode) {
			// 防止重复
			LogManager.e("change mode: no change " + aMode);
			return;
		}
		mode = aMode;
		switch (mode) {
		case MODE_WARN:
			reconiseStopAnimation();
			warn();
			break;

		case MODE_CONFIGING_THRIDPART:
			connectingThridpart();
			break;
		case MODE_NORMAL:
			normal();
			break;
		}
	}

	public int getMode() {
		return this.mode;
	}

	/**
	 * 警告：闪烁蓝灯
	 */
	private void warn() {
		// 蓝灯闪烁动画
		animation.start(new Runnable() {
			private boolean aniLightOn = false;

			@Override
			public void run() {
				try {
					if (!aniLightOn) {
						// 亮灯
						mHardWare.controlLight(lightName, IHardWare.LIGHT_ON);
					} else {
						// 灭灯
						mHardWare.controlLight(lightName, IHardWare.LIGHT_CLOSE);
					}
					aniLightOn = !aniLightOn;
					// 间隔时间
					if (!Thread.currentThread().isInterrupted()) {
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
	}

	/**
	 * 正常模式
	 */
	private void normal() {
		animation.stop();
	}

	/**
	 * 连接第三方设备
	 */
	private void connectingThridpart() {
		// 绿灯闪烁动画
		logoAnimation.start(new Runnable() {
			private boolean logoAniLightOn = false;

			@Override
			public void run() {
				try {
					if (!logoAniLightOn) {
						// 亮灯
						mHardWare.controlLight(greenLight, IHardWare.LIGHT_ON);
					} else {
						// 灭灯
						mHardWare.controlLight(greenLight, IHardWare.LIGHT_CLOSE);
					}
					logoAniLightOn = !logoAniLightOn;
					// 间隔时间
					if (!Thread.currentThread().isInterrupted()) {
						Thread.sleep(500);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});

	}

	/**
	 * 识别过程中
	 */
	public void reconiseStartAnimation() {
		LogManager.d(TAG,"reconiseStartAnimation");
		// 蓝灯灭
		mHardWare.controlLight(lightName, IHardWare.LIGHT_CLOSE);
		// 绿灯闪烁
		logoAnimation.start(new Runnable() {
			private boolean logoAniLightOn = false;

			@Override
			public void run() {
				try {
					if (!logoAniLightOn) {
						// 亮灯
						mHardWare.controlLight(greenLight, IHardWare.LIGHT_ON);
					} else {
						// 灭灯
						mHardWare.controlLight(greenLight, IHardWare.LIGHT_CLOSE);
					}
					logoAniLightOn = !logoAniLightOn;
					if (!Thread.currentThread().isInterrupted()) {
						Thread.sleep(500);// 间隔时间
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});

	}

	public void reconiseStopAnimation() {
		LogManager.d(TAG,"reconiseStopAnimation");
		hasError = false;
		isFirstVoiceChange = true;
		logoAnimation.stop();
	}

	/**
	 * 单击，绿色闪烁一次
	 */
	public void onShortClick() {
		try {
			switch (mode) {
			case MODE_WARN:
			case MODE_CONFIGING_THRIDPART:
			case MODE_NORMAL:
				// 灭掉绿灯
				mHardWare.controlLight(greenLight, IHardWare.LIGHT_CLOSE);
				// 亮绿灯
				mHardWare.controlLight(greenLight, IHardWare.LIGHT_ON);
				// 间隔时间
				Thread.sleep(50);
				// 灭掉绿灯
				mHardWare.controlLight(greenLight, IHardWare.LIGHT_CLOSE);
				// 恢复灯
				// updateState(mState, mLevel);
			}
		} catch (Exception e) {
			LogManager.e(e.toString());
			Thread.currentThread().interrupt();
		}
	}

	public void updateStateOnRunnable(final int state, final Object params) {
		Animate.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateState(state, params);
			}
		});
	}

	public void getStateOnRunnable() {
		Animate.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getState();
			}
		});
	}

	public void upLightStateOnRunnable() {
		Animate.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				upLightState();
			}
		});
	}

	public void updateModeOnRunnable(final int aMode) {
		Animate.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateMode(aMode);
			}
		});
	}

	public void onShortClickOnRunnable() {
		Animate.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				onShortClick();
			}
		});
	}
}
