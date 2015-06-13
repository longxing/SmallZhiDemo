package com.voice.assistant.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.base.data.CommandInfo;
import com.base.util.AmazingBoxThread;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.ControlInterface;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.umeng.UmengOnlineConfig;
import com.iii360.external.recognise.util.RecordUpLoadRunnable;
import com.iii360.sup.common.utl.Animate;
import com.iii360.sup.common.utl.HomeConstants;
import com.iii360.sup.common.utl.SystemUtil;
import com.smallzhi.homeappliances.control.ClientManager;
import com.smallzhi.homeappliances.control.ClientManager.NewOnGetDevAdd;
import com.voice.assistant.hardware.ButtonHandler;
import com.voice.assistant.hardware.ButtonHandler.TouchStatus;
import com.voice.assistant.hardware.IHardWare;
import com.voice.assistant.hardware.WakeUpLightControl;
import com.voice.assistant.main.activity.AssistantBaseActivity;
import com.voice.assistant.main.newmusic.SyncMusicRunable;
import com.voice.assistant.utl.TTStype;
import com.voice.common.util.nlp.HouseCommandProcess;

public class AssistantMainActivity extends AssistantBaseActivity {
	private static String TAG = "AssistantMainActivity";

	private View mVoiceInput;
	private View mTextInput;
	private View mBtnSendText;
	private EditText mEditText;
	private View mBtnToVoiceInput;
	private View mBtnToTextInput;

	private TextView mRlRestInfoForRecognise;

	private String mBroadcastText;
	private boolean mIsWeatherBroadcast;
	private static final int HANDLER_MESSAGE_STRART_RECOGNISE = 0;
	private WakeLock mWakeLock;

	private boolean mIsCalledOnStop = false;

	private boolean isKeyTouched = false;

	private AmazingBoxThread mAmazingBoxThread;
	/**
	 * 广播接受者。用来更改主界面相关的信息。
	 */
	private BroadcastReceiver mBroadcastReceiverForMainActivity = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}
			String action = intent.getAction();
			if (action == null || "".equals(action)) {
				return;
			} else if (RecogniseSystemProxy.AKEY_HIDDEN_EXTRA_INFO.equals(action)) {
				hiddenExtraInfo();
			} else if (RecogniseSystemProxy.AKEY_SHOW_EXTRA_INFO.equals(action)) {
				String text = intent.getStringExtra("value");
				showExtraInfo(text);
			} else if (action.equals(KeyList.AKEY_SYS_CLOSE_ACTION)) {
				LogManager.i("get end broad cast& finsh");
				// finish();
			} else if (action.equals(KeyList.AKEY_RESET_SCREEN_LIGHTNESS)) {
				int light = intent.getIntExtra("screen_brightness", 0);
				try {
					adjustScreenBrightness(light);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LogManager.printStackTrace(e);
				}
			} else if (KeyList.AKEY_LINECONTROL_START_RECOGNISE.equals(action)) {
				mStartRecogniseHandler.sendEmptyMessage(HANDLER_MESSAGE_STRART_RECOGNISE);
			} else if (RecogniseSystemProxy.AKEY_SHOW_VOICE_INPUT.equals(action)) {
				showVoice();
			} else if (KeyList.AKEY_SHOW_ANSWER.equals(action)) {
				String text = intent.getStringExtra("value");
				sendAnswerSession(text, true);

			} else if (HomeConstants.AKEY_TTS_PLAY.equals(action)) {
				String text = intent.getStringExtra(HomeConstants.TTS_PLAY_CONTENT);
				LogManager.i("tts play =" + text);
				// 播报
				final ITTSController mTTSController = mUnion.getTTSController();
				mTTSController.play(text);
			}
		}
	};

	private void registerReceiver(Context context) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(RecogniseSystemProxy.AKEY_HIDDEN_EXTRA_INFO);
		intentFilter.addAction(RecogniseSystemProxy.AKEY_SHOW_EXTRA_INFO);
		intentFilter.addAction(KeyList.AKEY_SHOW_USAGE);
		intentFilter.addAction(RecogniseSystemProxy.AKEY_SHOW_VOICE_INPUT);
		intentFilter.addAction(KeyList.AKEY_SYS_CLOSE_ACTION);
		intentFilter.addAction(KeyList.AKEY_RESET_SCREEN_LIGHTNESS);
		intentFilter.addAction(KeyList.AKEY_LINECONTROL_START_RECOGNISE);
		intentFilter.addAction(KeyList.AKEY_SHOW_ANSWER);
		intentFilter.addAction(HomeConstants.AKEY_TTS_PLAY);
		context.registerReceiver(mBroadcastReceiverForMainActivity, intentFilter);
	}

	private void unRegisterReceiver(Context context) {
		context.unregisterReceiver(mBroadcastReceiverForMainActivity);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.activity_main);
		LogManager.d(TAG, "setContentView" + MyApplication.SystemDoingCurrentTime + new SimpleDateFormat(MyApplication.Date_Fomort).format(new Date()));
		initViews();
		initBasicServiceUnion();

		boxBootSetting();
		LogManager.d(TAG, "boxBootSetting" + MyApplication.SystemDoingCurrentTime + new SimpleDateFormat(MyApplication.Date_Fomort).format(new Date()));

		BoxHardWareInit();
		LogManager.d(TAG, "BoxHardWareInit" + MyApplication.SystemDoingCurrentTime + new SimpleDateFormat(MyApplication.Date_Fomort).format(new Date()));

		registerReceiver(this);
		InitDataAndObjectInSubThread();
		HouseHoldElectricalAppliancesSetting();
		LogManager.d(TAG, "HouseHoldElectricalAppliancesSetting" + MyApplication.SystemDoingCurrentTime + new SimpleDateFormat(MyApplication.Date_Fomort).format(new Date()));
	}

	private void InitDataAndObjectInSubThread() {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
//				UmengOnlineConfig.getOnLineConfigAndProcess(AssistantMainActivity.this);
				onNewIntent(getIntent());
				new SyncMusicRunable(mUnion);
				new RecordUpLoadRunnable(mUnion);
				uncatchExecptionForRestartMainApplication();
				LogManager.d(TAG, "uncatchExecptionForRestartMainApplication" + MyApplication.SystemDoingCurrentTime + new SimpleDateFormat(MyApplication.Date_Fomort).format(new Date()));
			}
		}.start();
	}

	/**
	 * 家电的设置
	 */
	private void HouseHoldElectricalAppliancesSetting() {
		if (HomeConstants.SW_ABOX) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					LogManager.e("start abox server ....");
					mBaseContext.setPrefBoolean(HomeConstants.ABOX_CONNECT, false);
					mBaseContext.setPrefBoolean(HomeConstants.ABOX_CONNECT_TTS, true);
					mAmazingBoxThread = new AmazingBoxThread(AssistantMainActivity.this);
					mAmazingBoxThread.start();
				}
			}).start();
		}
	}

	private void launchClientManager() {
		ClientManager clientManager = new ClientManager(getApplicationContext());
		clientManager.setOngetDev(new NewOnGetDevAdd() {
			@Override
			public void onGetAdd(ControlInterface control, final String result) {
				// TODO Auto-generated method stub
				mUnion.setControlInterface(control);
				HouseCommandProcess.getInstace(mUnion);
			}

			@Override
			public void onDisConnect() {
				// TODO Auto-generated method stub
				// getUnion().getTTSController().play("连接主机失败");
			}
		});
	}

	/**
	 * 盒子开机启动设置
	 */
	private void boxBootSetting() {
		
		// *******************欢迎词***************** //
		new Thread(new Runnable() {
			
			private boolean isAppFirstRun() {
				
				final String flagFile = "/mnt/sdcard/com.voice.assistant.main/properties/app_non_first_run_flag";
				boolean bFirstRunFlag =  false;
				
				File file = new File(flagFile);
				try {
					
					if (file != null && !file.exists()) {
						bFirstRunFlag = true;
						file.createNewFile();
					}
					else {
						bFirstRunFlag = false;
					}
					
				}
				catch(IOException e) {
					e.printStackTrace();
				}
								
				return bFirstRunFlag;
			}
			
			@Override
			public void run() {
				// 总音量-兼容
				AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				int curr = manager.getStreamVolume(AudioManager.STREAM_ALARM);
				if (curr == 0) {
					initVolume();
				}

				// 等2s为了，让主程序进程加载完毕
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				// **************开机播报**************** //
				// boolean helpTTS = mBaseContext.getPrefBoolean(KeyList.PKEY_IS_VOICE_SOUND_WAVE, true);
				boolean bFirstRunFlag = isAppFirstRun();
				LogManager.e("bFirstRunFlag = " + bFirstRunFlag);
				
				if (bFirstRunFlag) {					
					// 重置声音
					initVolume();
					// 欢迎词
					ButtonHandler emptyButtonHandler = new ButtonHandler() {

						@Override
						public void onShortClick() {
							// TODO Auto-generated method stub
						}

						@Override
						public void onLongClick() {
						}

						@Override
						public void onLongLongClick() {
						}

						@Override
						public void onClickInTouch() {
							// TODO Auto-generated method stub
						}

						@Override
						public void onLongClickInTouch() {
						}

					};
					// 保存按钮
					ButtonHandler buttonhandler = IHardWare.buttonHandlers.get(IHardWare.BUTTON_LOGO);
					ButtonHandler addbutton = IHardWare.buttonHandlers.get(IHardWare.BUTTON_VOLUME_DECREASE);
					ButtonHandler removebutton = IHardWare.buttonHandlers.get(IHardWare.BUTTON_VOLUME_INCREASE);
					ButtonHandler resetButtonHandler = IHardWare.buttonHandlers.get(IHardWare.BUTTON_RESET);
					// 屏蔽按钮
					emptyButtonHandler.prepare();
					IHardWare.buttonHandlers.put(IHardWare.BUTTON_LOGO, emptyButtonHandler);
					emptyButtonHandler.prepare();
					IHardWare.buttonHandlers.put(IHardWare.BUTTON_VOLUME_DECREASE, emptyButtonHandler);
					emptyButtonHandler.prepare();
					IHardWare.buttonHandlers.put(IHardWare.BUTTON_VOLUME_INCREASE, emptyButtonHandler);
					emptyButtonHandler.prepare();
					IHardWare.buttonHandlers.put(IHardWare.BUTTON_RESET, emptyButtonHandler);
					// 播报
					final ITTSController mTTSController = mUnion.getTTSController();
					mTTSController.play("主人，我的名字叫小智。", "请扫描说明书中的二维码下载安装小智助手。", "点击楼狗按钮……点击红心……点击垃圾桶减小音量。");

					// 恢复按钮
					buttonhandler.prepare();
					IHardWare.buttonHandlers.put(IHardWare.BUTTON_LOGO, buttonhandler);
					addbutton.prepare();
					IHardWare.buttonHandlers.put(IHardWare.BUTTON_VOLUME_DECREASE, addbutton);
					removebutton.prepare();
					IHardWare.buttonHandlers.put(IHardWare.BUTTON_VOLUME_INCREASE, removebutton);
					resetButtonHandler.prepare();
					IHardWare.buttonHandlers.put(IHardWare.BUTTON_RESET, resetButtonHandler);
				}
				boolean a = mBaseContext.getGlobalBoolean(KeyList.PKEY_UPDATE_SYSTEM, false);
				LogManager.e("开机启动：AssistantMainActivity" + a);
				launchClientManager();
			}
		}).start();

	}

	/**
	 * 盒子硬件初始化，垃圾桶，红心，logo 按钮初始化，并设置监听事件
	 */
	private void BoxHardWareInit() {
		final ButtonHandler voiceUpButtonhandler = (ButtonHandler) IHardWare.buttonHandlers.get(IHardWare.BUTTON_VOLUME_INCREASE);
		final ButtonHandler buttonhandler = (ButtonHandler) IHardWare.buttonHandlers.get(IHardWare.BUTTON_LOGO);
		final ButtonHandler voiceDownButtonhandler = (ButtonHandler) IHardWare.buttonHandlers.get(IHardWare.BUTTON_VOLUME_DECREASE);

		Button b = (Button) findViewById(R.id.logo);
		b.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					buttonhandler.onStatusClick(TouchStatus.TOUCH_BEGIN);
					break;
				case MotionEvent.ACTION_MOVE:
					buttonhandler.onStatusClick(TouchStatus.TOUCH_ED);
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					buttonhandler.onStatusClick(TouchStatus.TOUCH_END);
					break;

				default:
					break;
				}

				return false;
			}
		});

		Button b2 = (Button) findViewById(R.id.voiceadd);
		b2.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					voiceUpButtonhandler.onStatusClick(TouchStatus.TOUCH_BEGIN);
					break;
				case MotionEvent.ACTION_MOVE:
					voiceUpButtonhandler.onStatusClick(TouchStatus.TOUCH_ED);
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					voiceUpButtonhandler.onStatusClick(TouchStatus.TOUCH_END);
					break;

				default:
					break;
				}
				return false;
			}
		});
		Button b3 = (Button) findViewById(R.id.voiceremove);
		b3.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					voiceDownButtonhandler.onStatusClick(TouchStatus.TOUCH_BEGIN);
					break;
				case MotionEvent.ACTION_MOVE:
					voiceDownButtonhandler.onStatusClick(TouchStatus.TOUCH_ED);
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					voiceDownButtonhandler.onStatusClick(TouchStatus.TOUCH_END);
					break;

				default:
					break;
				}
				return false;
			}
		});
	}

	/**
	 * 拦截不可捕获的异常，并且重启主程序
	 */
	private void uncatchExecptionForRestartMainApplication() {

		// 拦截异常
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				mBaseContext.setPrefBoolean(KeyList.PKEY_IS_UNCAUGHT_EXCEPTION, true);
				LogManager.printMessageToServer(throwable.toString());
				LogManager.printStackTraceToServer();
				// 如果灯闪烁，则关闭
				Animate.post(new Runnable() {

					@Override
					public void run() {
						WakeUpLightControl wakeUpLightControl = (WakeUpLightControl) mBaseContext.getGlobalObject(KeyList.GKEY_WAKEUP_LIGHT_CONTROL);
						wakeUpLightControl.reconiseStopAnimation();
					}
				});
				// 忽略IllegalStateException错误
				if (throwable.getClass() == IllegalStateException.class) {
					// mUnion.getRecogniseSystem().stopWakeup();

					// 针对麦克风占用，回收标志位
					mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_WAKEUP_TO_RECOGNISE, false);

					mUnion.getRecogniseSystem().startWakeup();
					LogManager.e(throwable.getClass().toString() + " is Ignore!");
					return;
				}
				throwable.printStackTrace();
				// 重启activity
				Context application = AssistantMainActivity.this.getApplicationContext();
				Intent intent = new Intent(application, AssistantLauncherActivity.class);

				PendingIntent restartIntent = PendingIntent.getActivity(application, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
				// 退出程序
				AlarmManager mgr = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
				// 关闭程序
				AssistantMainActivity.this.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
	}

	public void initVolume() {
		AudioManager manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		// 音乐播放
		manager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);// 15刻度
		// tts音量 改为 2
		manager.setStreamVolume(AudioManager.STREAM_ALARM, 1, 0);// 7刻度
	}

	private void initViews() {
		mVoiceInput = findViewById(R.id.voice_input);
		mTextInput = findViewById(R.id.text_input);
		mBtnSendText = findViewById(R.id.btnSendText);
		mEditText = (EditText) findViewById(R.id.edtText);

		mBtnToVoiceInput = findViewById(R.id.btnToVoiceInput);
		mBtnToTextInput = findViewById(R.id.btnToTextInput);

		mRlRestInfoForRecognise = (TextView) findViewById(R.id.tvRestInfoForRecognise);
		mEditText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				final int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
					break;
				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				default:
					break;
				}
				return false;
			}
		});
		mBtnToVoiceInput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideTextInput(mEditText);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mVoiceInput.setVisibility(View.VISIBLE);
						mTextInput.setVisibility(View.GONE);
					}
				}, 300);

			}
		});

		mBtnToTextInput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUnion.getRecogniseSystem().cancelRecognising();
				mVoiceInput.setVisibility(View.GONE);
				mTextInput.setVisibility(View.VISIBLE);
				setInputMode(SOFT_INPUT_ADJUST_RESIZE);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						showTextInput(mEditText);
					}
				}, 100);

			}
		});

		mBtnSendText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String str = mEditText.getText().toString();
				if (str != null && !"".equals(str)) {

					hideTextInput(mEditText);
					mEditText.setText("");
				}
			}
		});

		mVoiceInput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mVoiceInput.setVisibility(View.VISIBLE);
				mTextInput.setVisibility(View.GONE);
			}
		});

	}

	private Handler mStartRecogniseHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			if (msg.what == HANDLER_MESSAGE_STRART_RECOGNISE) {
				mUnion.getRecogniseSystem().startCaptureVoice();
			}
		}

	};

	private void checkTTSState() {
		boolean isFromWeather = mBaseContext.getPrefBoolean("isWeatherBroadcast", false);
		// if( !isFromWeather && mIsCalledOnStop) {
		// mTTSController.stop() ;
		// }
		if (isFromWeather || !mIsCalledOnStop) {
			return;
		}
		mUnion.getTTSController().stop();
	}

	@Override
	protected void onResume() {
		LogManager.e("onResume");
		super.onResume();
		checkTTSState();
		TTStype.checkTTSAPK(mBaseContext);
		NotificationManager objNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		objNotificationManager.cancel(KeyList.FINAL_INT_NOTIFY_MEDIA_ID);
		setScreenAlwayLight();
		mIsCalledOnStop = false;

	}

	@Override
	protected void onStop() {
		super.onStop();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// 区分是否是黑屏还是直接跳转到其他页面
				if (!SystemUtil.getTopActivity(getApplicationContext()).getPackageName().equals("com.voice.assistant.main")) {
					LogManager.e("mIsCalledOnstop");
					mIsCalledOnStop = true;
				}
			}
		}, 1000);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		queryWeatherBroadcast(intent);

	}

	@Override
	protected void onPause() {
		LogManager.e("onPause");
		super.onPause();
		checkIsTTSNeedStop();
		pauseWakeLock();
	}

	private void checkIsTTSNeedStop() {

	}

	@Override
	protected void onDestroy() {
		LogManager.d("onDestroy");
		// BMapManagerSingle.getInstance(this).destroy();
		super.onDestroy();
		unRegisterReceiver(this);
		removeWeatherInfo();
		if (mAmazingBoxThread != null) {
			mAmazingBoxThread.stopScanner();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogManager.e("onActivityResult is " + requestCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 设置主界面屏幕常亮
	 */
	private void setScreenAlwayLight() {
		if (getPrefBoolean(KeyList.PKEY_KEEP_AWAKE, false)) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "mytag");
			mWakeLock.acquire();
		}
	}

	/**
	 * 释放WakeLock
	 */
	private void pauseWakeLock() {
		if (getPrefBoolean(KeyList.PKEY_KEEP_AWAKE, false) && null != mWakeLock) {
			mWakeLock.release();
		}
	}

	private void queryWeatherBroadcast(Intent intent) {
		if (intent == null) {
			return;
		}
		mIsWeatherBroadcast = mBaseContext.getPrefBoolean("isWeatherBroadcast");
		mBroadcastText = mBaseContext.getPrefString("PKEY_SAVE_SPEECH_WEATHER_INFO", "");
		if (mIsWeatherBroadcast && mBroadcastText != null && !"".equals(mBroadcastText)) {
			boolean isNotification = intent.getBooleanExtra("NotificationValues", false);
			if (isNotification) {
				sendAnswerSessionNoTTs(mBroadcastText);
				mBaseContext.setPrefString("PKEY_SAVE_SPEECH_WEATHER_INFO", "");
				mBaseContext.setPrefBoolean("isWeatherBroadcast", false);
			}
		}
	}

	private void removeWeatherInfo() {
		mBaseContext.setPrefBoolean("isWeatherBroadcast", false);
	}

	/**
	 * 显示语音识别相关信息
	 */
	private void showExtraInfo(String text) {
		mRlRestInfoForRecognise.setText(text);
		mRlRestInfoForRecognise.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏语音识别相关信息
	 */
	private void hiddenExtraInfo() {
		mRlRestInfoForRecognise.setVisibility(View.INVISIBLE);
	}

	public void excuteCommand(String smsNumber, String smsTitle, String smsContent) {
		CommandInfo commandInfo = new CommandInfo();
		commandInfo.getArgList().add("");
		commandInfo.getArgList().add(smsNumber);

		// executeCommand(new CommandSendSms(getApplicationContext(),
		// commandInfo));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isInputMethodActivie()) {
			hideTextInput(mEditText);
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}

	// 现实话筒
	private void showVoice() {
		hideTextInput(mEditText);
		mVoiceInput.setVisibility(View.VISIBLE);
		mTextInput.setVisibility(View.GONE);
	}

	public ArrayList<String> readTxtFile(String filePath) {
		ArrayList<String> list = new ArrayList<String>();
		try {

			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					list.add(lineTxt);
				}
				read.close();
			} else {
				Toast.makeText(getApplicationContext(), "找不到指定的文件", 50).show();
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "读取文件内容出错", 50).show();
			e.printStackTrace();
		}
		return list;
	}

	public String getSDPath() {

		return Environment.getExternalStorageDirectory().getPath();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		ButtonHandler voiceUpButtonhandler = (ButtonHandler) IHardWare.buttonHandlers.get(IHardWare.BUTTON_VOLUME_INCREASE);
		ButtonHandler buttonhandler = (ButtonHandler) IHardWare.buttonHandlers.get(IHardWare.BUTTON_LOGO);
		ButtonHandler voiceDownButtonhandler = (ButtonHandler) IHardWare.buttonHandlers.get(IHardWare.BUTTON_VOLUME_DECREASE);
		ButtonHandler resetButtonhandler = (ButtonHandler) IHardWare.buttonHandlers.get(IHardWare.BUTTON_RESET);

		TouchStatus status;
		if (isKeyTouched) {
			status = TouchStatus.TOUCH_ED;
		} else {
			status = TouchStatus.TOUCH_BEGIN;
			isKeyTouched = true;
		}

		switch (keyCode) {
		case KeyEvent.KEYCODE_0:
			// buttonhandler.onStatusClick(status);
			break;
		case KeyEvent.KEYCODE_1:
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:// 红心键
			LogManager.e("KEYCODE_VOLUME_UP");
			voiceUpButtonhandler.onStatusClick(status);
			break;
		case KeyEvent.KEYCODE_2:
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:// 垃圾桶键
			LogManager.e("KEYCODE_VOLUME_DOWN");
			voiceDownButtonhandler.onStatusClick(status);
			break;
		case KeyEvent.KEYCODE_3:

			break;
		case KeyEvent.KEYCODE_LOGO:// logo键
			buttonhandler.onStatusClick(status);
			break;
		case KeyEvent.KEYCODE_SRST:// reset键
			resetButtonhandler.onStatusClick(status);
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		ButtonHandler voiceUpButtonhandler = (ButtonHandler) IHardWare.buttonHandlers.get(IHardWare.BUTTON_VOLUME_INCREASE);
		ButtonHandler buttonhandler = (ButtonHandler) IHardWare.buttonHandlers.get(IHardWare.BUTTON_LOGO);
		ButtonHandler voiceDownButtonhandler = (ButtonHandler) IHardWare.buttonHandlers.get(IHardWare.BUTTON_VOLUME_DECREASE);
		ButtonHandler resetButtonhandler = (ButtonHandler) IHardWare.buttonHandlers.get(IHardWare.BUTTON_RESET);

		isKeyTouched = false;
		switch (keyCode) {
		case KeyEvent.KEYCODE_0:
			buttonhandler.onStatusClick(TouchStatus.TOUCH_END);
			break;
		case KeyEvent.KEYCODE_1:
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:// 红心键
			LogManager.e("KEYCODE_VOLUME_UP");
			voiceUpButtonhandler.onStatusClick(TouchStatus.TOUCH_END);
			break;
		case KeyEvent.KEYCODE_2:
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:// 垃圾桶键
			LogManager.e("KEYCODE_VOLUME_DOWN");
			voiceDownButtonhandler.onStatusClick(TouchStatus.TOUCH_END);
			break;
		case KeyEvent.KEYCODE_3:

			break;
		case KeyEvent.KEYCODE_LOGO:// logo键
			buttonhandler.onStatusClick(TouchStatus.TOUCH_END);
			break;
		case KeyEvent.KEYCODE_SRST:// reset键
			resetButtonhandler.onStatusClick(TouchStatus.TOUCH_END);
			break;
		default:
			break;
		}

		return true;
	}
}
