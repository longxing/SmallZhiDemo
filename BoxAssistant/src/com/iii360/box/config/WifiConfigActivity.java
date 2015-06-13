package com.iii360.box.config;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iii.client.WifiConfig;
import com.iii.wifi.dao.info.WifiUserInfo;
import com.iii.wifi.dao.manager.WifiCRUDForUser;
import com.iii.wifi.dao.manager.WifiCRUDForUser.ResultForUserListener;
import com.iii360.box.MyApplication;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.connect.OnLineBoxHandler;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WaitUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.util.WifiInfoUtils;
import com.iii360.box.util.WifiUtils;
import com.iii360.box.view.IView;
import com.iii360.box.view.MyProgressDialog;
import com.voice.common.util.WifiSecurity;
import com.voice.voicesoundwave.SoundWaveControl;

/**
 * 盒子wifi配置
 * 
 * @author hefeng
 * 
 */
public class WifiConfigActivity extends BaseActivity implements IView {
	public final static int HANDLER_TOAST_ERROR = 0;
	public final static int HANDLER_USE_SOUND_WAVE_CONFIG_TIMEOUT = 2;
	private static final int HANDLER_CONNECT_AP_TIMEOUT = 3;
	private static final int HANDLER_SEND_WIFI_SUCCESS = 4;
	private static final int HANDLER_GET_SOUND_STATE = 5;
	private static final int HANDLER_SEND_OR_CONNECT_AP_FAIL = 6;
	private static final int HANDLER_WIFI_SCAN = 7;
	private TextView mWifiSsidTv;
	private EditText mWifiPwdEt;
	private String mWifiSSID;
	private String mWifiPwd;
	private TextView mConfigToastTv;
	private boolean mSendModel;
	private Button confirmBtn;
	private MyProgressDialog progressDialog;
	private boolean isConnectApAction;
	private boolean soundtimeoutOrCancel;
	private ConnectionDialog connectionDialog;
	private long screenOffTimeout;
	private TextView noPwdTv;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent == null) {
				return;
			}
			String action = intent.getAction();

			Parcelable parcelable = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action) && (null != parcelable)) {

				NetworkInfo networkInfo = (NetworkInfo) parcelable;
				State state = networkInfo.getState();

				switch (state) {
				case CONNECTED:// wifi连接成功

					if (WifiUtils.isConnectWifi(context, KeyList.BOX_WIFI_SSID) && isConnectApAction) {
						mHandler.removeMessages(HANDLER_CONNECT_AP_TIMEOUT);
						mHandler.postDelayed(new Runnable() {
							public void run() {
								sendPhoneSSID();
							}
						}, 1000);
					}
					break;
				case DISCONNECTED:// wifi连接失败
				case UNKNOWN:// 未知错误
					break;
				default:
					break;
				}
			} else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
			}

		}
	};
	private WakeLock wakeLock;
	private int wifiNetworkId = -1;
	private AudioManager audioManager;
	private WifiManager wifiManager;

	public static boolean muteAudioFocus(Context context, boolean bMute) {
		if (context == null) {
			return false;
		}
		int SDK_INT = android.os.Build.VERSION.SDK_INT;
		if (SDK_INT < 8) {
			// 2.1以下的版本不支持下面的API：requestAudioFocus和abandonAudioFocus
			return false;
		}
		boolean bool = false;
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (bMute) {
			int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		} else {
			int result = am.abandonAudioFocus(null);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		}
		return bool;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_config6);
		this.initViews();
		this.initDatas();
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		wifiManager.startScan();
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(receiver, filter);
		initWifiData();
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "my lock");
		try {
			screenOffTimeout = Settings.System.getLong(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initWifiData() {
		if (WifiUtils.isConnectWifi(context)) {
			confirmBtn.setClickable(true);
			wifiNetworkId = WifiInfoUtils.getWifiNetWorkId(context);
			mWifiSsidTv.setText(WifiInfoUtils.getWifiSsid(context).replaceAll("\"", ""));
			boolean b;
			try {
				b = WifiInfoUtils.isEncryption(context);
			} catch (Exception e) {
				e.printStackTrace();
				ToastUtils.show(context, R.string.main_connect_wifi);
				return;
			}
			if (!b) {
				noPwdTv.setVisibility(View.VISIBLE);
				noPwdTv.setText("当前网络没有加密");
				mWifiPwdEt.setVisibility(View.GONE);
			} else {
				noPwdTv.setVisibility(View.GONE);
				noPwdTv.setText("");
				mWifiPwdEt.setVisibility(View.VISIBLE);
			}
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (connectionDialog != null && !isFinishing()) {
			connectionDialog.dismiss();
		}
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		try {
			screenOffTimeout = Settings.System.getLong(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		noPwdTv = (TextView) findViewById(R.id.wifi_config_nopwd_tv);
		confirmBtn = (Button) findViewById(R.id.wifi_config_confirm_btn);
		mWifiSsidTv = (TextView) findViewById(R.id.wifi_config_ssid_tv);
		mWifiPwdEt = (EditText) findViewById(R.id.wifi_config_pwd_et);
		findApDialog = new FindApDialog(this);
		confirmDialog = new ConfirmDialog(this);
		confirmDialog.setReTryClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (confirmDialog != null && !isFinishing())
					confirmDialog.dismiss();
				if (findApDialog == null || isFinishing())
					return;
				findApDialog.show();
			}
		});
		confirmDialog.setUseWaveClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (confirmDialog != null && !isFinishing())
					confirmDialog.dismiss();
				requestBox();
			}
		});
		mWifiPwdEt.setKeyListener(new NumberKeyListener() {
			private char[] chars = null;

			@Override
			public int getInputType() {
				return InputType.TYPE_CLASS_TEXT;
			}

			@Override
			protected char[] getAcceptedChars() {
				if (chars == null) {
					chars = new char[253];
					int c = 0;
					for (int i = 0; i < 256; i++) {
						if (i == 9 || i == 10 || i == 32) {
							continue;
						}
						chars[c++] = (char) i;
					}
				}
				return chars;
			}
		});
		mConfigToastTv = (TextView) findViewById(R.id.wifi_config_toast_tv);
		this.setViewHead("添加新音箱");
		progressDialog = new MyProgressDialog(this);
		progressDialog.setMessage(getString(R.string.ba_connecting_box));
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				mHandler.removeMessages(HANDLER_CONNECT_AP_TIMEOUT);
				if (!WifiUtils.isConnectWifi(context, mWifiSSID)) {
					WifiUtils.connectToConfigredWifi(context, mWifiSSID, wifiNetworkId);
				}
			}
		});
		connectionDialog = new ConnectionDialog(this);
		connectionDialog.setCanceledOnTouchOutside(false);
		connectionDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				// Log.i("info", "onCancel");
			}
		});
		connectionDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				// Log.i("info", "onDismiss");
				soundtimeoutOrCancel = true;
				mHandler.removeMessages(HANDLER_GET_SOUND_STATE);
				SoundWaveControl.getInstance(context).stopSendData();
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				muteAudioFocus(context, false);
				mHandler.removeMessages(HANDLER_USE_SOUND_WAVE_CONFIG_TIMEOUT);
				mHandler.postDelayed(delayReleaseLock, screenOffTimeout);

			}
		});
		// dialog.setCancelable(false);
		findViewById(R.id.wifi_config_linearlayout).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(mWifiPwdEt.getWindowToken(), 0);
				}
			}
		});
	}

	private Runnable delayReleaseLock = new Runnable() {
		public void run() {
			if (wakeLock.isHeld())
				wakeLock.release();
		}
	};

	@Override
	public void initDatas() {
		// TODO Auto-generated method stub
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!WifiUtils.isConnectWifi(context)) {
					ToastUtils.show(context, R.string.main_connect_wifi);
					return;
				}
				if (WifiUtils.isConnectWifi(context, KeyList.BOX_WIFI_SSID)) {
					ToastUtils.show(context, "当前网络为音箱热点，请切换网络");
					// startToActvitiyNoFinish(UnConnectWifiActivity.class);
					return;
				}
				// TODO Auto-generated method stub
				boolean b;
				try {
					b = WifiInfoUtils.isEncryption(context);
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtils.show(context, R.string.main_connect_wifi);
					return;
				}
				sendData(b);
			}
		});

		if (getIntent() != null) {
			mSendModel = getIntent().getBooleanExtra(KeyList.IKEY_BY_WIFI_SWITCH_WIFI, false);
		}

		String pwd = getPrefString(KeyList.PKEY_INPUT_WIFI_PASSWORD);
		mWifiPwdEt.setText(pwd);
		boolean b;
		try {
			b = WifiInfoUtils.isEncryption(context);
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtils.show(context, R.string.main_connect_wifi);
			return;
		}
		if (!b) {
			noPwdTv.setVisibility(View.VISIBLE);
			noPwdTv.setText("当前网络没有加密");
			mWifiPwdEt.setVisibility(View.GONE);
		}
	}

	protected void cleanActivitys() {
		List<Activity> acts = MyApplication.getInstance().getActivityList();
		for (Activity ac : acts) {
			if (ac instanceof WifiConfigActivity) {
				continue;
			}
			ac.finish();
		}
	}

	private void sendData(boolean b) {

		mWifiSSID = mWifiSsidTv.getText().toString();
		mWifiPwd = mWifiPwdEt.getText().toString();
		if (!b) {
			mWifiPwd = "";
		}
		if (b && TextUtils.isEmpty(mWifiPwd)) {
			ToastUtils.show(context, R.string.ba_password_is_null);
			return;
		}

		if (b && mWifiPwd.length() < 5) {
			ToastUtils.show(context, "您的密码不足5位，请重新输入");
			return;
		}
		mConfigToastTv.setVisibility(View.GONE);
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mConfigToastTv.setVisibility(View.GONE);
				findApDialog.show();
			}
		}, 100);
	}

	public void requestBox() {
		if (audioManager.isMusicActive()) {
			muteAudioFocus(context, true);
		}
		setPrefString(KeyList.PKEY_INPUT_WIFI_PASSWORD, mWifiPwd);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String info = mWifiSSID + "|" + mWifiPwd + "|" + WifiSecurity.getCurrentWifiSecurity(context);
				LogManager.i("发送的网络数据：  " + info);
				LogManager.i("mSendModel : " + mSendModel);
				if (mSendModel) {
					changeHeziWifi();
				} else {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							if (connectionDialog != null) {
								connectionDialog.show();
								wakeLock.acquire();
							}
						}
					});
					soundtimeoutOrCancel = false;
					SoundWaveControl.getInstance(WifiConfigActivity.this).sendData(info);
					mHandler.sendEmptyMessageDelayed(HANDLER_USE_SOUND_WAVE_CONFIG_TIMEOUT, 70000);
					mHandler.sendEmptyMessage(HANDLER_GET_SOUND_STATE);
				}
			}
		}).start();
	}

	private void changeHeziWifi() {
		LogManager.i("发送的网络数据：  mWifiSSID=" + mWifiSSID + "||mWifiPwd=" + mWifiPwd);

		WifiCRUDForUser wifiUser = new WifiCRUDForUser(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
		WifiUserInfo wifiInfo = new WifiUserInfo();
		wifiInfo.setName(mWifiSSID);
		wifiInfo.setPassWord(mWifiPwd);
		wifiUser.add(wifiInfo, new ResultForUserListener() {
			@Override
			public void onResult(String type, String errorCode, String userName, String userPassWord) {
				// TODO Auto-generated method stub
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					LogManager.i("发送给盒子网络信息成功");
				} else {
					LogManager.i("发送给盒子网络信息错误");
					mHandler.sendEmptyMessage(HANDLER_TOAST_ERROR);
				}
			}
		});
	}

	private void changeHeziWifi(final String ip) {
		LogUtil.e("wificonfig发送的网络数据：  mWifiSSID=" + mWifiSSID + "||mWifiPwd=" + mWifiPwd);
		WifiCRUDForUser wifiUser = new WifiCRUDForUser(context, ip, WifiConfig.TCP_DEFAULT_PORT);
		WifiUserInfo wifiInfo = new WifiUserInfo();
		wifiInfo.setName(mWifiSSID);
		wifiInfo.setPassWord(mWifiPwd);
		wifiInfo.setEncrypt(WifiSecurity.getWifiSecurity(context, mWifiSSID, wifiNetworkId) + "");
		wifiUser.add(wifiInfo, new ResultForUserListener() {
			@Override
			public void onResult(String type, String errorCode, String userName, String userPassWord) {
				WifiUtils.connectToConfigredWifi(context, mWifiSSID, wifiNetworkId);
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					LogUtil.e("wificonfig发送给盒子网络信息成功");
					mHandler.sendEmptyMessage(HANDLER_SEND_WIFI_SUCCESS);
				} else {
					LogUtil.e("wificonfig发送给盒子网络信息失败");
					Message msg = new Message();
					msg.what = HANDLER_SEND_OR_CONNECT_AP_FAIL;
					msg.obj = "发送账号和密码失败，请重试";
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int what = msg.what;
			switch (what) {
			case HANDLER_GET_SOUND_STATE:
				LogManager.d("sound status" + SoundWaveControl.getInstance(context).status);
				if (SoundWaveControl.getInstance(context).status != 1) {
					mHandler.sendEmptyMessageDelayed(HANDLER_GET_SOUND_STATE, 1000);
				} else {
					mHandler.removeMessages(HANDLER_GET_SOUND_STATE);
					if (connectionDialog != null && !isFinishing()) {
						connectionDialog.dismiss();
					}
					if (!soundtimeoutOrCancel) {
						mHandler.removeMessages(HANDLER_USE_SOUND_WAVE_CONFIG_TIMEOUT);
						AfterSendSSID();
					}

				}
				break;
			case HANDLER_TOAST_ERROR:

				mConfigToastTv.setVisibility(View.VISIBLE);
				mConfigToastTv.setText(R.string.ba_setting_box_wifi_fail);

				break;
			case HANDLER_SEND_WIFI_SUCCESS:
				AfterSendSSID();
				break;
			case HANDLER_SEND_OR_CONNECT_AP_FAIL:
				if (progressDialog != null && !isFinishing()) {
					progressDialog.dismiss();
				}
				mConfigToastTv.setVisibility(View.VISIBLE);
				mConfigToastTv.setText(msg.obj + "");
				confirmDialog.show();

				break;

			case HANDLER_USE_SOUND_WAVE_CONFIG_TIMEOUT:
				if (progressDialog != null && !isFinishing()) {
					progressDialog.dismiss();
				}
				if (connectionDialog != null && !isFinishing()) {
					connectionDialog.dismiss();
				}
				soundtimeoutOrCancel = true;
				mHandler.removeMessages(HANDLER_GET_SOUND_STATE);
				SoundWaveControl.getInstance(context).stopSendData();
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				String toast = getString(R.string.wifi_config_error_toast);
				mConfigToastTv.setText(toast);
				mConfigToastTv.setVisibility(View.VISIBLE);
				break;
			case HANDLER_CONNECT_AP_TIMEOUT:
				mHandler.postDelayed(new Runnable() {
					public void run() {
						sendPhoneSSID();
					}
				}, 1000);
				break;
			case HANDLER_WIFI_SCAN:
				wifiManager.startScan();
				mHandler.sendEmptyMessageDelayed(HANDLER_WIFI_SCAN, 5000);
				break;
			default:
				break;
			}
		}

	};

	protected void sendPhoneSSID() {
		if (WifiUtils.isConnectWifi(context, KeyList.BOX_WIFI_SSID)) {
			changeHeziWifi(KeyList.TCP_REQUEST_IP);
		} else {
			Message msg = new Message();
			msg.what = HANDLER_SEND_OR_CONNECT_AP_FAIL;
			msg.obj = "连接音箱热点失败，请重试";
			mHandler.sendMessage(msg);
			WifiUtils.connectToConfigredWifi(context, mWifiSSID, wifiNetworkId);
			LogUtil.e("ap连接超时");
		}
	}

	private int currentVolume;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean result = super.onKeyDown(keyCode, event);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			};
		}.start();
		return result;
	}

	@Override
	protected void onDestroy() {
		muteAudioFocus(context, false);
		if (findApDialog != null && !isFinishing())
			findApDialog.dismiss();
		findApDialog = null;
		if (connectionDialog != null && !this.isFinishing())
			connectionDialog.dismiss();
		connectionDialog = null;
		if (progressDialog != null && !this.isFinishing())
			progressDialog.dismiss();
		progressDialog = null;
		if (confirmDialog != null && !isFinishing())
			confirmDialog.dismiss();
		confirmDialog = null;
		super.onDestroy();
		unregisterReceiver(receiver);
		mHandler.removeCallbacksAndMessages(null);
		if (isConnectApAction && !WifiUtils.isConnectWifi(context, mWifiSSID)) {
			if (mWifiSSID != null && !mWifiSSID.equals("") && !mWifiSSID.equals(KeyList.BOX_WIFI_SSID)) {
				WifiUtils.connectToConfigredWifi(context, mWifiSSID, wifiNetworkId);
			}

		}
		setPrefString(KeyList.KEY_CONNECTING_AP, "");
		mHandler.removeMessages(HANDLER_GET_SOUND_STATE);
		mHandler.removeMessages(HANDLER_WIFI_SCAN, null);

		SoundWaveControl.getInstance(context).stopSendData();
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}

	private void AfterSendSSID() {
		mConfigToastTv.setVisibility(View.VISIBLE);
		mConfigToastTv.setText("wifi账号和密码发送成功");
		if (progressDialog != null) {
			progressDialog.setMessage("账号和密码发送成功\n" + getString(R.string.ba_connecting_box));
			progressDialog.show();
		}
		if (!wakeLock.isHeld())
			wakeLock.acquire();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				setPrefString(KeyList.KEY_CONNECTING_AP, "");
				if (progressDialog != null && !isFinishing()) {
					progressDialog.dismiss();
				}
				cleanActivitys();
				new OnLineBoxHandler(WifiConfigActivity.this).handle();
			}
		}, 15000);
	}

	private class ChangeTimeTask implements Runnable {
		private int time;

		public ChangeTimeTask(int time) {
			this.time = time;
		}

		public void run() {
			WaitUtils.sleep(1000);
			while (findApDialog != null && !isFinishing() && findApDialog.isShowing() && !isFindApCancel && time > 0) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if (WifiConfig.AP_MODE_OFF && WifiUtils.searchWifi(context, KeyList.BOX_WIFI_SSID)) {
							setPrefString(KeyList.KEY_CONNECTING_AP, "yes");
							if (findApDialog != null && !isFinishing()) {
								findApDialog.dismiss();
							}
							boolean success = WifiUtils.connectWifi(context, KeyList.BOX_WIFI_SSID, KeyList.BOX_WIFI_PASSWORD);
							mConfigToastTv.setVisibility(View.GONE);
							if (success) {
								isConnectApAction = true;
								setPrefString(KeyList.PKEY_INPUT_WIFI_PASSWORD, mWifiPwd);
								mHandler.sendEmptyMessageDelayed(HANDLER_CONNECT_AP_TIMEOUT, 30000);
								if (progressDialog != null) {
									progressDialog.setMessage("正在发送账号和密码，请稍等");
									progressDialog.show();
								}
							} else {
								setPrefString(KeyList.KEY_CONNECTING_AP, "");
								ToastUtils.show(context, "连接音箱热点失败");
								mHandler.post(new Runnable() {
									public void run() {
										if (findApDialog != null && !isFinishing()) {
											findApDialog.dismissAndShowConfirmDialog();
										}
									}
								});
							}

						} else {
							if (findApDialog == null || isFinishing())
								return;
							findApDialog.changeTime(time);
						}
					}
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				time--;
			}
			if (findApDialog == null || !findApDialog.isShowing()) {
				return;
			}
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (findApDialog != null && !isFinishing()) {
						findApDialog.dismissAndShowConfirmDialog();
					}
				}
			});
		}
	}

	private FindApDialog findApDialog;
	private boolean isFindApCancel;

	class FindApDialog extends Dialog {
		private TextView showTv;
		private Thread thread;
		private ImageView progressIv;

		public FindApDialog(Context context) {
			super(context, R.style.MyDialog);
		}

		@Override
		public void show() {
			super.show();
			mHandler.removeCallbacksAndMessages(null);
			mHandler.sendEmptyMessage(HANDLER_WIFI_SCAN);
			progressIv.setImageDrawable(getResources().getDrawable(R.drawable.wificonfig_find_ap_anim));
			AnimationDrawable anim = (AnimationDrawable) progressIv.getDrawable();
			anim.stop();
			anim.start();
			isFindApCancel = false;
			changeTime(15);
			thread = new Thread(new ChangeTimeTask(15));
			thread.start();

		}

		public void changeTime(int time) {
			showTv.setText(time + "秒");
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.find_ap_dialog);
			setCancelable(false);
			showTv = (TextView) findViewById(R.id.tv2);
			progressIv = (ImageView) findViewById(R.id.wificonfig_find_ap_progress_iv);
			showTv.setVisibility(View.GONE);

		}

		@Override
		public void onBackPressed() {
			super.onBackPressed();
			dismiss();
			isFindApCancel = true;
		}

		@Override
		public void dismiss() {
			super.dismiss();
			try {
				thread.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mHandler.removeMessages(HANDLER_WIFI_SCAN, null);
		}

		public void dismissAndShowConfirmDialog() {
			dismiss();
			confirmDialog.show();
		}
	}

	class ConnectionDialog extends Dialog {

		private ImageView animIv;

		public ConnectionDialog(Context context) {
			super(context, R.style.MyDialog);
		}

		@Override
		public void show() {
			super.show();
			animIv.setImageDrawable(getResources().getDrawable(R.drawable.box_connect_wifi_anim));
			AnimationDrawable anim = (AnimationDrawable) animIv.getDrawable();
			anim.stop();
			anim.start();
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.wifi_connection_dialog);
			TextView tv1 = (TextView) findViewById(R.id.tv1);
			tv1.setText("正在声波配置");
			TextView tv2 = (TextView) findViewById(R.id.tv2);
			tv2.setText("请将手机喇叭靠近音箱的麦克风");
			animIv = (ImageView) findViewById(R.id.wifi_connect_imageview);

		}
	}

	private ConfirmDialog confirmDialog;

	private class ConfirmDialog extends Dialog {
		private TextView titleTv;
		private Button reTryBtn;
		private Button useWaveBtn;
		private android.view.View.OnClickListener reTryClickListener;
		private android.view.View.OnClickListener useWaveClickListener;

		public ConfirmDialog(Context context) {
			super(context, R.style.MyDialog);

		}

		public void setReTryClickListener(android.view.View.OnClickListener reTryClickListener) {
			this.reTryClickListener = reTryClickListener;
			if (reTryBtn == null)
				return;
			reTryBtn.setOnClickListener(reTryClickListener);
		}

		public void setUseWaveClickListener(android.view.View.OnClickListener useWaveClickListener) {
			this.useWaveClickListener = useWaveClickListener;
			if (useWaveBtn == null)
				return;
			useWaveBtn.setOnClickListener(useWaveClickListener);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.view_wificonfig_confirm_dialog);
			init();
		}

		private void init() {
			titleTv = (TextView) findViewById(R.id.exit_dialog_title_tv);
			reTryBtn = (Button) findViewById(R.id.ba_cancel_btn);
			useWaveBtn = (Button) findViewById(R.id.ba_confirm_btn);
			reTryBtn.setOnClickListener(reTryClickListener);
			useWaveBtn.setOnClickListener(useWaveClickListener);
			titleTv.setText("没有寻找到音箱热点，请确认音箱是否开机");
			reTryBtn.setText("重试");
			useWaveBtn.setText("使用声波");
		}
	}
}
