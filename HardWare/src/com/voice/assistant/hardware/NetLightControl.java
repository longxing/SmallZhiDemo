package com.voice.assistant.hardware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.recognise.ILightController;
import com.iii360.sup.common.utl.Animate;
import com.iii360.sup.common.utl.HomeConstants;
import com.iii360.sup.common.utl.net.WTBroadcast;
import com.iii360.sup.common.utl.net.WTBroadcast.EventHandler;

public class NetLightControl {

	public State currentState = State.DISCONNECTED;
	public State thirdPartState = State.DISCONNECTED;

	private IHardWare mHardWare;

	private String mLightName = IHardWare.LIGHT_NET;

	private BasicServiceUnion mUnion;

	private WakeUpLightControl wakeUpLightControl;

	private Animate animate = new Animate();// 播放动画线程

	private int mode = ILightController.MODE_NORMAL;

	private boolean isFirstLannch = true;
	
	private static final String TAG = "HardWare NetLightControl";

	public NetLightControl(IHardWare hardWare, BasicServiceUnion union) {
		mHardWare = hardWare;
		mUnion = union;
		this.wakeUpLightControl = (WakeUpLightControl) mUnion.getBaseContext()
				.getGlobalObject(KeyList.GKEY_WAKEUP_LIGHT_CONTROL);
		LogManager.d(TAG,"new hardware");

		adjust();

		WTBroadcast.ehList.add(wifiEventHandler);

		BroadcastReceiver receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				LogManager.d(TAG,action);
				if (action.equals(KeyList.AKEY_START_CONFIG_THIRDPART)) {
					updateThridPartStatus(State.CONNECTING);
				} else if (action.equals(KeyList.AKEY_END_CONFIG_THIRDPART)) {
					updateThridPartStatus(State.CONNECTED);
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(KeyList.AKEY_START_CONFIG_THIRDPART);
		filter.addAction(KeyList.AKEY_END_CONFIG_THIRDPART);
		mUnion.getBaseContext().getContext().registerReceiver(receiver, filter);
	}

	/**
	 * 纠正网络状态
	 */
	public void adjust() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mUnion
				.getBaseContext().getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		wifiEventHandler.handleConnectChange(mWiFiNetworkInfo.getState());
	}

	public EventHandler wifiEventHandler = new EventHandler() {

		@Override
		public void wifiStatusNotification() {
			// TODO Auto-generated method stub
			// updateState(LightState.DISCONNECT);
		}

		@Override
		public void scanResultsAvailable() {
			// TODO Auto-generated method stub

		}

		@Override
		public void handleConnectChange(State status) {
			// TODO Auto-generated method stub
			LogManager.d(TAG,"state change " + status);
			if(status==State.CONNECTED){
				mUnion.getBaseContext().setPrefBoolean(KeyList.PKEY_IS_VOICE_SOUND_WAVE, false);
				mUnion.getBaseContext().setPrefBoolean(HomeConstants.ABOX_CONNECT, false);
			}
			updateState(status);
		}
	};

	public void updateState(final State state) {
		Animate.post(new Runnable() {

			@Override
			public void run() {
				State preState = currentState;
				currentState = state;
				if (mode != ILightController.MODE_NORMAL) {
					return;
				}
				LogManager.d(TAG,"doing state " + state);
				switch (currentState) {
				case CONNECTED:// 连接成功-》网络灯和唤醒灯常亮
					
					// 联网标志
					mUnion.getBaseContext().setGlobalBoolean(
							KeyList.GKEY_BOOL_IS_CONNECT_WIFIGATE, true);
					// 保证声纹标志位被设置
					mUnion.getBaseContext().setPrefBoolean(KeyList.PKEY_IS_VOICE_SOUND_WAVE, false);
					// 常亮网络灯
					animate.stop();
					mHardWare.controlLight(mLightName, IHardWare.LIGHT_ON);
					// 开启唤醒功能
					wakeUpLightControl.updateModeOnRunnable(ILightController.MODE_NORMAL);
					mUnion.getRecogniseSystem().startWakeup();
					isFirstLannch = false;
					break;
				
				case UNKNOWN:
				case DISCONNECTED:// 断开连接-》唤醒灯长灭|网络灯闪烁
					// 联网标志
					mUnion.getBaseContext().setGlobalBoolean(
							KeyList.GKEY_BOOL_IS_CONNECT_WIFIGATE, false);
					// 首次启动时保持灯原状态
					if (!isFirstLannch) {
						NetLightControl.this.wakeUpLightControl
								.updateModeOnRunnable(ILightController.MODE_NORMAL);
					}
					mUnion.getRecogniseSystem().stopWakeup();
					
					isFirstLannch = false;
					
//					// 灯无变化
//					if (preState == State.CONNECTING
//							|| preState == State.DISCONNECTED) {
//						return;
//					}
					// 网络灯闪烁
					wifiDisconnected();
					return;
				case CONNECTING:// 连接中-》唤醒灯闪烁|网络灯闪烁
					if (preState == currentState) {
						return;
					}
					// 联网标志
					mUnion.getBaseContext().setGlobalBoolean(
							KeyList.GKEY_BOOL_IS_CONNECT_WIFIGATE, false);
					
					if (!mUnion.getBaseContext().getGlobalBoolean(
							KeyList.GKEY_IS_MUSIC_IN_PLAYING)) {
						// 非播放歌曲时才使用，网络连接指示灯
						wakeUpLightControl.updateModeOnRunnable(ILightController.MODE_WARN);
					}
					wifiDisconnected();
					break;
				default:
					break;
				}
			}
		});
	}
	
	private void wifiDisconnected() {
		// 网络灯闪烁
		animate.start(new Runnable() {
			private boolean aniLightOn = false;
			
			@Override
			public void run() {
				try {
					if (!aniLightOn) {
						// 亮
						mHardWare.controlLight(mLightName,
								IHardWare.LIGHT_ON);
					} else {
						// 灭
						mHardWare.controlLight(mLightName,
								IHardWare.LIGHT_CLOSE);
					}
					aniLightOn = !aniLightOn;
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

			}
		});
	}

	private void hurry() {
		animate.start(new Runnable() {
			private boolean aniLightOn = false;

			@Override
			public void run() {
				try {
					if (!aniLightOn) {
						// 亮
						mHardWare.controlLight(mLightName,
								IHardWare.LIGHT_ON);
					} else {
						// 灭
						mHardWare.controlLight(mLightName,
								IHardWare.LIGHT_CLOSE);
					}
					aniLightOn = !aniLightOn;
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				
			}
		});
	}

	// ----------------------------//
	public void updateThridPartStatus(final State state) {
		Animate.post(new Runnable() {

			@Override
			public void run() {
				thirdPartState = state;
				switch (thirdPartState) {
				case CONNECTED:// 连接成功
					// 恢复正常状态
					wakeUpLightControl.updateModeOnRunnable(ILightController.MODE_NORMAL);
					break;
				case CONNECTING:// 配置连接中
					//
					wakeUpLightControl
							.updateModeOnRunnable(ILightController.MODE_CONFIGING_THRIDPART);
					break;
				default:
					break;
				}
			}
		});

	}

	public void updateMode(final int aMode) {
		Animate.post(new Runnable() {
			
			@Override
			public void run() {
				mode = aMode;
				switch (mode) {
				case ILightController.MODE_NORMAL:
					updateState(currentState);
					break;
				case ILightController.MODE_HURRY:
					hurry();
					break;
				}
			}
		});
		
	}

	public int getMode() {
		return this.mode;
	}

}
