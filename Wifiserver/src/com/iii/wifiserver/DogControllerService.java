package com.iii.wifiserver;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.iii.client.WifiConfig;
import com.iii.wifi.connect.WifiControl;
import com.iii.wifi.connect.WifiControl.WifiControlListener;
import com.iii.wifi.connect.WifiControl.WifiControlState;
import com.iii.wifi.connect.WifiUtils;
import com.iii.wifi.dao.imf.WifiDeviceDao;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.inter.IWifiDeviceDao;
import com.iii.wifi.thirdpart.inter.JSHouseCommand;
import com.iii.wifi.thirdpart.manager.HouseDevicesFindThread;
import com.iii.wifi.util.BoxSystemUtils;
import com.iii.wifi.util.CustomSocketServer;
import com.iii.wifi.util.FileProperties;
import com.iii.wifi.util.HardwareUtils;
import com.iii.wifi.util.KeyList;
import com.iii.wifi.util.RemindUtil;
import com.iii.wifi.util.TTSUtil;
import com.iii.wifi.util.WaitUtil;
import com.iii.wifi.util.WifiHelp;
import com.iii.wifiserver.push.JSPushManager;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.MessageQueue;
import com.iii360.sup.common.utl.NetWorkUtil;
import com.iii360.sup.common.utl.SuperBaseContext;
import com.iii360.sup.common.utl.SystemUtil;
import com.iii360.sup.common.utl.net.UdpClient;
import com.iii360.sup.common.utl.net.WifiHelp.ConnectResut;
import com.voice.assistant.main.newmusic.MusicData;
import com.voice.assistant.main.newmusic.MusicData.MusicServiceListener;
import com.voice.common.util.CommandInfo;
import com.voice.common.util.IChangeListener;
import com.voice.common.util.IDogControlService;
import com.voice.voicesoundwave.SoundWaveControl;
import com.voice.voicesoundwave.SoundWaveControl.StreamDecoderInterface;

public class DogControllerService extends Service {

	private static final String Tag = "DogControllerService";
	private static IChangeListener mListen = null;
	private SuperBaseContext mBaseContext = null;

	private FileProperties mFileProperties = null;

	private JSHouseCommand mHouseCommand = null;
	private String apName;
	private String apPasswd;
	private HouseDevicesFindThread findDevicesThread = null;
	private CustomSocketServer mCustomSocketServer;
	private CustomSocketServer mNewCustomSocketServer;

	private boolean mSendUdpFlag = true;

	private static Boolean ISIntent = false;
	private static Context wifiContext;

	/**
	 * 打开声纹配置的开关标记
	 */
	private boolean isOpenSoundWave = true;

	private WifiControl mWifiControl;

	// 处理主程序的rest事件
	public final static String ASS_MSG_RESET = "ASS_RESET";
	private AssistanceMsgReceiver assReceiver = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogManager.d("WifiServer onCreate=============>>>");
		initData();
		bindMainProgramInterface();
		netCheck(false);
		addPush();
		selectHouseDevicesCount();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogManager.d(Tag, "WifiServer onStartComman======>> ");
		mSendUdpFlag = true;
		return super.onStartCommand(intent, flags, startId);
	}

	public void addPush() {
		if (HardwareUtils.SW_HEZI_PUSH) {
			JSPushManager.init(this);
		}
	}

	// check 家电配置的数量
	public void selectHouseDevicesCount() {
		new Thread() {
			public void run() {
				NotificationUtils.show(DogControllerService.this);
				IWifiDeviceDao dao = new WifiDeviceDao(DogControllerService.this);
				List<WifiDeviceInfo> list = dao.selectByAll();
				LogManager.d("current already setted house devices count:" + list.size());
				mBaseContext.setPrefInteger(KeyList.HOUSE_MECHINE_SETTING_COUNT, list.size());
			}
		}.start();

	}

	/**
	 * 初始化数据
	 */
	private void initData() {

		wifiContext = this;
		KeyList.messageQueue = new MessageQueue();
		IntentFilter filter = new IntentFilter();
		filter.addAction(KeyList.PKEY_SEND_BROADCAST_NETCHECK);
		filter.addAction(KeyList.AKEY_STOP_SOUND_WAVE);
		filter.addAction(KeyList.AKEY_RESTORE_SOUND_WAVE);
		registerReceiver(receiver, filter);
		assReceiver = new AssistanceMsgReceiver();
		IntentFilter assFilter = new IntentFilter();
		assFilter.addAction(ASS_MSG_RESET);
		registerReceiver(assReceiver, assFilter);

		mHouseCommand = new JSHouseCommand(this);
		mBaseContext = new SuperBaseContext(getApplicationContext());
		mFileProperties = new FileProperties();
		mBaseContext.setPrefString(KeyList.PKEY_TCP_PORT, mFileProperties.getTcpPort());
		mBaseContext.setPrefString(KeyList.PKEY_UDP_PORT, mFileProperties.getUdpPort());
		LogManager.w("tcpPort : " + mFileProperties.getTcpPort() + "||udpPort : " + mFileProperties.getUdpPort());
	}

	/**
	 * 绑定主程序接口
	 */
	private void bindMainProgramInterface() {
		KeyList.TTSUtil = new TTSUtil(this);
		KeyList.REMIND_UTIL = new RemindUtil(this);
		KeyList.sMusicData = new MusicData(this);
		KeyList.sMusicData.setMusicListener(new MusicServiceListener() {
			@Override
			public void onPlayEnd() {
				// TODO Auto-generated method stub
				UdpClient.getInstance(mBaseContext, true).sendBroadcast(HardwareUtils.ACTION_MUSIC_STOP.getBytes());
			}
		});

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				break;
			case KeyList.HOST_CONNECT_SUCESS:
				LogManager.e("start UDP tick");
				KeyList.LOCAL_IP = NetWorkUtil.getLocalIpAddress();
				mBaseContext.setGlobalBoolean(KeyList.GKEY_WIFI_ENABLE_DOG_FOUND, true);
				startUdpTick();

				if (mBaseContext.getPrefBoolean(KeyList.PKEY_WELCOME_TAG, false)) {
					if (KeyList.TTSUtil.isWorking())
						KeyList.TTSUtil.playContent("连接路由器成功！");
				} else {
					mBaseContext.getPrefBoolean(KeyList.PKEY_WELCOME_TAG, true);
					if (KeyList.TTSUtil.isWorking())
						KeyList.TTSUtil.playContent(getString(R.string.first_connect_net_ok_toast));
				}

				closeSettingWifi();

				// mWeakupControl.openWeakup();
				break;
			case KeyList.HOST_CONNECT_FAIL:
				LogManager.e("HOST_CONNECT_FAIL");
				KeyList.WIFI_HELP.release();
				KeyList.LOCAL_IP = NetWorkUtil.getLocalIpAddress();
				mBaseContext.setGlobalBoolean(KeyList.GKEY_WIFI_ENABLE_DOG_FOUND, false);
				startSoundWave();
				if (KeyList.TTSUtil.isWorking())
					KeyList.TTSUtil.playContent("连接失败，请检查如下事项……");

				break;
			case KeyList.AP_CREAT_SUCESS:
				KeyList.LOCAL_IP = NetWorkUtil.getLocalIpAddress();
				startUdpTick();

				break;
			case KeyList.AP_CREAT_FAIL:
				KeyList.LOCAL_IP = NetWorkUtil.getLocalIpAddress();
				LogManager.e("wifyState:AP_CREAT_FAIL");
				WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				try {
					Class localClass = manager.getClass();
					Class[] arrayOfClass = new Class[2];
					arrayOfClass[0] = WifiConfiguration.class;
					arrayOfClass[1] = Boolean.TYPE;
					Method localMethod = localClass.getMethod("setWifiApEnabled", arrayOfClass);
					WifiManager localWifiManager = manager;
					Object[] arrayOfObject = new Object[2];
					arrayOfObject[0] = null;
					arrayOfObject[1] = Boolean.valueOf(false);
					localMethod.invoke(localWifiManager, arrayOfObject);
				} catch (Exception localException) {
					localException.printStackTrace();
				}
				startSoundWave();
				break;
			case KeyList.DOG_SET_OVER:
				joinWifi();
				break;
			case KeyList.START_CHECK_NET_CONNECT_STATUS:
				netCheck(true);
				break;
			default:
				break;
			}
		};
	};

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			LogManager.e(action);
			if (action.equals(KeyList.PKEY_SEND_BROADCAST_NETCHECK)) {
				netCheck(false);

			} else if (action.equals(KeyList.AKEY_STOP_SOUND_WAVE)) {
				LogManager.e("AKEY_STOP_SOUND_WAVE");
				isOpenSoundWave = false;
				SoundWaveControl.getInstance(DogControllerService.this).stopListent();

			} else if (action.equals(KeyList.AKEY_RESTORE_SOUND_WAVE)) {
				LogManager.e("AKEY_RESTORE_SOUND_WAVE");
				isOpenSoundWave = true;
				netCheck(false);
			}
		}
	};

	public void netCheck(final boolean isBySoundWave) {

		apName = mBaseContext.getPrefStringNew(KeyList.PKEY_WIFINAME, WifiConfig.WIFI_DEFAULT_SSID);
		apPasswd = mBaseContext.getPrefStringNew(KeyList.PKEY_WIFIPASSWD, WifiConfig.WIFI_DEFAULT_PASSWORD);

		LogManager.e("wifi : ssid=" + apName + "||pwd=" + apPasswd);

		if (KeyList.WIFI_HELP == null) {
			KeyList.WIFI_HELP = new WifiHelp(getApplicationContext(), handler);
		}
		if (WifiHelp.isConnectWifi(this)) {
			LogManager.d(Tag, "current sustem is connected  wifi====>>");

			new Thread(new Runnable() {
				@Override
				public void run() {
					ISIntent = false;
					// TODO Auto-generated method stub
					ISIntent = WifiUtils.pingCheck();
					if (false == ISIntent) {
						if (KeyList.TTSUtil.isWorking())
							KeyList.TTSUtil.playContent("小智没有接入互联网，请检查路由器设备是否接入互联网");
					}
				}
			}).start();

			closeSettingWifi();
			if (KeyList.TTSUtil.isWorking() && !new SuperBaseContext(this).getPrefBoolean("PKEY_IS_UNCAUGHT_EXCEPTION", false)) {

				KeyList.TTSUtil.playContent(getString(R.string.connect_net_ok_toast));
			} else {
				new SuperBaseContext(this).setPrefBoolean("PKEY_IS_UNCAUGHT_EXCEPTION", false);
			}
			startUdpTick();
			// mWeakupControl.openWeakup();

			KeyList.LOCAL_IP = NetWorkUtil.getLocalIpAddress();
			mBaseContext.setGlobalBoolean(KeyList.GKEY_WIFI_ENABLE_DOG_FOUND, true);

			if (findDevicesThread == null) {
				findDevicesThread = new HouseDevicesFindThread(getApplicationContext());
				findDevicesThread.start();
			}
		} else {
			LogManager.e("当前系统没有连接网络");

			new Thread(new Runnable() {
				@Override
				public void run() {

					if (apName.length() > 0) {
						if (false == WifiHelp.isConnectWifi(wifiContext)) {
							if (isBySoundWave) {
								if (KeyList.TTSUtil.isWorking())
									KeyList.TTSUtil.playContent(getString(R.string.connect_net_toast));
							} else {
								if (KeyList.TTSUtil.isWorking())
									KeyList.TTSUtil.playContent(getString(R.string.connect_net_toast2));
							}
							joinWifi();
						}
					} else {
						if (WifiConfig.DEBUG_OFF) {
							if (KeyList.TTSUtil.isWorking())
								KeyList.TTSUtil.playContent("当前没有连接网络，开启声纹配置或ap模式");
						}

						startSoundWave();
					}
				}
			}).start();
		}

		if (mCustomSocketServer == null) {
			mCustomSocketServer = new CustomSocketServer(getApplicationContext(), WifiConfig.TCP_DEFAULT_PORT);
		}
		if (mNewCustomSocketServer == null) {
			mNewCustomSocketServer = new CustomSocketServer(getApplicationContext(), WifiConfig.TCP_PORT);
		}
	}

	/**
	 * 连接网络
	 */
	public void joinWifi() {
		LogManager.e("连接网络 joinWifi apName=" + apName + "||=" + apPasswd);

		if (apName.length() > 0) {
			closeSettingWifi();
			mWifiControl = new WifiControl(this);
			mWifiControl.setWifiInfo(apName, apPasswd);

			mWifiControl.setWifiListener(new WifiControlListener() {
				@Override
				public void onResult(WifiControlState state) {
					// TODO Auto-generated method stub
					LogManager.i("joinWifi : " + state);

					if (state == WifiControlState.STATE_CONNECT_WIFI_OK) {
						// closeSettingWifi();

						new Thread(new Runnable() {
							public void run() {
								ISIntent = false;
								// TODO Auto-generated method stub
								ISIntent = WifiUtils.pingCheck();
								if (false == ISIntent) {
									if (KeyList.TTSUtil.isWorking())
										KeyList.TTSUtil.playContent("小智没有接入互联网，请检查路由器设备是否接入互联网");
								}
							}
						}).start();

						handler.sendEmptyMessageDelayed(KeyList.HOST_CONNECT_SUCESS, 3000);

						if (findDevicesThread == null) {
							findDevicesThread = new HouseDevicesFindThread(getApplicationContext());
							findDevicesThread.start();
						}
					} else if (state == WifiControlState.STATE_NOT_HAVE_WIFI) {
						startSoundWave();
					} else if (state == WifiControlState.STATE_OPEN_WIFI_FAIL) {
						startSoundWave();
						handler.sendEmptyMessageDelayed(KeyList.HOST_CONNECT_FAIL, 8000);

					} else if (state == WifiControlState.STATE_CONNECT_WIFI_FAIL) {
						startSoundWave();
						handler.sendEmptyMessageDelayed(KeyList.HOST_CONNECT_FAIL, 4000);
					}

					// mWifiControl.destroy();
				}
			});
			new Thread(mWifiControl).start();
		}
	}

	/**
	 * 开启声纹配置
	 */
	public void startSoundWave() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				createAp();
				// mWeakupControl.closeWeakup();
				WaitUtil.sleep(5000);
				if (isOpenSoundWave) {
					LogManager.i("service creatAP  listent");
					SoundWaveControl.getInstance(DogControllerService.this).listent(new StreamDecoderInterface() {
						@Override
						public void onResult(String value) {
							// TODO Auto-generated method stub
							LogManager.e("service getdata value=" + value + "------- openWeakup");

							closeSettingWifi();

							if (value != null) {
								value = value.replaceAll("\n", "");
								LogManager.i("value===============" + value);

								if (value.contains("|")) {
									String[] values = value.split("\\|");
									mBaseContext.setPrefStringNew(KeyList.PKEY_WIFINAME, values[0]);
									if (values.length == 2) {
										LogManager.i("wifi(old) value===============" + values[0] + "|" + values[1]);

										mBaseContext.setPrefStringNew(KeyList.PKEY_WIFIPASSWD, values[1]);
										mBaseContext.setPrefString(KeyList.PKEY_WIFICIPHERTYPE, null);

									} else if (values.length == 3) {
										LogManager.i("wifi(new) value===============" + values[0] + "|" + values[1] + "|" + values[2]);
										mBaseContext.setPrefStringNew(KeyList.PKEY_WIFIPASSWD, values[1]);
										mBaseContext.setPrefString(KeyList.PKEY_WIFICIPHERTYPE, values[2]);

									} else {
										LogManager.i("wifi value===============" + values[0]);
										mBaseContext.setPrefStringNew(KeyList.PKEY_WIFIPASSWD, null);
										mBaseContext.setPrefString(KeyList.PKEY_WIFICIPHERTYPE, null);
									}
									handler.sendEmptyMessage(KeyList.START_CHECK_NET_CONNECT_STATUS);
								}
							}
						}

					});
				}
			}
		}).start();
	}

	/**
	 * 创建ap模式
	 */
	private void createAp() {
		boolean isOpenAp = isOpenAp();
		LogManager.d("method execute current time:" + new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.sss").format(new Date()) + "isOpenAp:" + isOpenAp);
		if (isOpenAp) {
			LogManager.e("打开ap模式");

			KeyList.WIFI_HELP.creatAP(new ConnectResut() {
				@Override
				public void onConnect(boolean result) {
					// TODO Auto-generated method stub
					if (result) {
						handler.sendEmptyMessage(KeyList.AP_CREAT_SUCESS);
					} else {
						handler.sendEmptyMessage(KeyList.AP_CREAT_FAIL);
					}
				}
			}, KeyList.PKEY_APNAME, KeyList.PKEY_APPASSWD);
		}
	}

	private boolean isOpenAp() {
		String productName = SystemUtil.getProductName();
		boolean isOpenAp;
		if (productName == null) {
			isOpenAp = false;
		} else {

			try {
				productName = productName.toLowerCase();
				LogManager.d("productName:" + productName);
				int index = productName.indexOf("v");
				if (index == -1) {
					throw new Exception();
				}

				int version = Integer.parseInt(productName.substring(index + 1));
				LogManager.d("version:" + version);
				if (productName.contains("xiaozhi") && version >= 2) {
					isOpenAp = true;
				} else {
					isOpenAp = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				isOpenAp = false;
			}
		}
		return isOpenAp;
	}

	private void closeSettingWifi() {
		SoundWaveControl.getInstance(DogControllerService.this).stopListent();
		boolean isOpenAp = isOpenAp();
		if (isOpenAp) {
			KeyList.WIFI_HELP.closeAP();
			WaitUtil.sleep(800);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogManager.e("WifiServer destory ... ");
		unregisterReceiver(receiver);
		unregisterReceiver(assReceiver);

		if (findDevicesThread != null) {
			findDevicesThread.stopRuning();
		}
		KeyList.WIFI_HELP = null;

		// 解除绑定的服务
		if (KeyList.TTSUtil != null) {
			KeyList.TTSUtil.stopServices();
		}
		if (KeyList.REMIND_UTIL != null) {
			KeyList.REMIND_UTIL.stopService();
		}
		if (KeyList.sMusicData != null) {
			KeyList.sMusicData.unBindServer();
		}

		mSendUdpFlag = false;
		handler.removeCallbacksAndMessages(null);
		if (mWifiControl != null) {
			mWifiControl.destroy();
		}
		if (findDevicesThread != null) {
			findDevicesThread.unRegistHuanTengReceiver();
		}
	}

	/**
	 * 开始发送udp广播或者组播
	 */
	private void startUdpTick() {
		searchDeviceByBrodCast();
		// searchedDevicesByMutiCast();
	}

	/**
	 * 通过广播的形式让助手端发现盒子
	 */
	private void searchDeviceByBrodCast() {
		new Thread(new Runnable() {
			public void run() {
				sendBroadCast();
			}
		}).start();
	}

	/**
	 * 老的发现设备的机制，通过盒子端不断的发送广播
	 */
	private void sendBroadCast() {
		while (mSendUdpFlag) {
			KeyList.LOCAL_IP = NetWorkUtil.getLocalIpAddress();
			String sender = KeyList.PKEY_APNAME + KeyList.PKEY_WIFI_IP_TAG + KeyList.LOCAL_IP + "SerialNumberStart=" + BoxSystemUtils.getSerialNumber() + "=SerialNumberEnd"
					+ KeyList.PKEY_WIFI_END_TAG;

			try {
				if (KeyList.UDP_CLIENT != null) {
					KeyList.UDP_CLIENT.send(sender.getBytes(), WifiHelp.getBroadcastAddress(DogControllerService.this).getHostAddress(), WifiConfig.UDP_DEFAULT_PORT);
				} else {
					KeyList.UDP_CLIENT = UdpClient.getInstance(new SuperBaseContext(DogControllerService.this), true);
					LogManager.e("udp == null");
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				LogManager.printStackTrace(e1);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LogManager.printStackTrace(e);
			}
		}
	}

	private final IDogControlService.Stub mBinder = new IDogControlService.Stub() {
		@Override
		public List<CommandInfo> getCommand() throws RemoteException {
			return mHouseCommand.getCommand();
		}

		@Override
		public boolean sendCommand(String commandid) throws RemoteException {
			LogManager.i("excute home command = " + commandid);
			return mHouseCommand.sendCommand(commandid);
		}

		@Override
		public void setCommandChangeListen(IChangeListener listen) throws RemoteException {
			// TODO Auto-generated method stub
			LogManager.e("setListener " + listen);
			mListen = listen;
		}

	};

	private static IChangeListener listen = new IChangeListener() {
		@Override
		public IBinder asBinder() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onCommandChange() throws RemoteException {
			// TODO Auto-generated method stub
			LogManager.e("on Command CHange");
			if (mListen != null) {
				LogManager.e("on Command CHange2");
				mListen.onCommandChange();
			}
		}

	};

	public static IChangeListener getCommandListen() {
		return listen;
	}

	/**
	 * 设置家电配置的数量
	 * 
	 * @param context
	 * @param isAdd
	 */
	public static void setHouseDevice(Context context, boolean isAdd) {
		SuperBaseContext baseContext = new SuperBaseContext(context);
		int houseDevices = baseContext.getPrefInteger(KeyList.HOUSE_MECHINE_SETTING_COUNT);
		if (isAdd) {
			houseDevices++;
		} else {
			houseDevices--;
		}
		baseContext.setPrefInteger(KeyList.HOUSE_MECHINE_SETTING_COUNT, houseDevices);
	}

	public class AssistanceMsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(ASS_MSG_RESET)) {
				// do some wifi server destory function
				destory();
			}
		}

	}

	// should add all function
	// (1) stop now TTS playing
	private void destory() {
		if (KeyList.TTSUtil != null) {
			KeyList.TTSUtil.stopPlay();
		}
	}

}
