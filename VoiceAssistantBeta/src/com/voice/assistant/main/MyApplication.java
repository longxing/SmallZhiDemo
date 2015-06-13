package com.voice.assistant.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Xml;

import com.base.file.FileUtil;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.common.utl.TaskSchedu;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.IAppContainer;
import com.iii360.base.inf.ICommandEngineSensitive;
import com.iii360.base.inf.IContactsContainer;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.ITTSSensitive;
import com.iii360.base.inf.parse.ICommandEngine;
import com.iii360.base.inf.parse.ITextDisposer;
import com.iii360.base.inf.recognise.ILightController;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.iii360.base.umeng.IUmengConfigurationContainer;
import com.iii360.base.umeng.UmengOnlineConfig;
import com.iii360.external.recognise.util.RecordUpLoadHandler;
import com.iii360.sup.common.utl.ShellUtils;
import com.iii360.voiceassistant.Bean.HardWare.LightControllerImp;
import com.iii360.voiceassistant.semanteme.common.CommandEngine;
import com.smallzhi.clingservice.UpnpServerProxy;
import com.voice.assistant.hardware.HardWare;
import com.voice.assistant.hardware.IHardWare;
import com.voice.assistant.hardware.MainButtonhandler;
import com.voice.assistant.hardware.NetLightControl;
import com.voice.assistant.hardware.ResetButtonHandler;
import com.voice.assistant.hardware.VoiceButtonHandler;
import com.voice.assistant.hardware.WakeUpLightControl;
import com.voice.assistant.main.music.MyMusicHandler;
import com.voice.assistant.main.music.db.MusicInfoUtils;
import com.voice.assistant.main.music.httpproxy.DownLoadHttpProxyListener;
import com.voice.assistant.main.music.httpproxy.HttpProxy;
import com.voice.assistant.service.ControlWeakupService;
import com.voice.common.util.MainThreadUtil;
import com.voice.common.util.nlp.CommandExcuteTimeProcess;
import com.voice.common.util.nlp.HouseCommandProcess;

public class MyApplication  extends com.example.common.MyApplication implements IGloableHeap, IAppContainer, IContactsContainer, IUmengConfigurationContainer {

	private static String TAG = "main application MyApplication";

	protected ICommandEngine mCommandEngine;
	protected IRecogniseSystem mRecogniseSystem;
	protected ITTSController mTTSController;
	protected TaskSchedu mTaskSchedu;
	protected ITextDisposer mTextDisposer;
	protected MainThreadUtil mainThreadUtil;

	protected BaseContext mBaseContext;
	protected Handler mHandler;
	private WakeUpLightControl control;
	private IHardWare hardWare;
	private ILightController mDlg;
	private MyMusicHandler mMusicHandler;
	private UpnpServerProxy mUpnpServerProxy;
	public static Context context;

	private static IntentFilter s_intentFilter;
	private String copyPath = "/mnt/sdcard/com.voice.assistant.main";
	static {
		s_intentFilter = new IntentFilter();
		s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
		s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
	}

	private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				mUnion.getTaskSchedu().updateTime();
			}
		}
	};

	public String getStr() {
		return "";
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		super.onCreate();
		//First to start wifiserver
		startService(new Intent(this, ControlWeakupService.class));
		
		LogManager.d(TAG, "onCreate" + SystemDoingCurrentTime + new SimpleDateFormat(Date_Fomort).format(new Date()));
		ShellUtils.readSerialNumber2(this);
		mBaseContext = new BaseContext(getApplicationContext());
		copyPropertiesFileToSdcard();
		context = this;

		UmengOnlineConfig.getOnLineConfigAndProcess(getApplicationContext());
		// DeleteNoUsedCacheFile();
		copyDataBase();
		LogManager.d(TAG, "copyDataBase:" + SystemDoingCurrentTime + new SimpleDateFormat(Date_Fomort).format(new Date()));
		mHandler = new Handler();
		mUnion = new BasicServiceUnion();

		mUnion.setBaseContext(mBaseContext);
		// ****************************************//
		// 语义解析系统
		mCommandEngine = new CommandEngine(mUnion);
		// ****************************************//
		// 识别系统
		// final View engineUi = findViewById(R.id.recButton);
		hardWare = new HardWare(getApplicationContext());
		mBaseContext.setGlobalObject(KeyList.GKEY_HARDWARE, hardWare);
		control = new WakeUpLightControl(hardWare, mUnion);
		mBaseContext.setGlobalObject(KeyList.GKEY_WAKEUP_LIGHT_CONTROL, control);

		mBaseContext.setPrefBoolean(KeyList.PKEY_CUREENT_MUSIC_IS_DLAN, false);
//		mBaseContext.setPrefBoolean(Constants.PKEY_CUREENT_MUSIC_IS_AIRPLAY, false);

		mUpnpServerProxy = new UpnpServerProxy(MyApplication.this);
		mUpnpServerProxy.bindService();

//		if (Constants.OPEN_AIRPLAY) {
//			startService(new Intent(this, AirPlayServer.class));
//		}

		mTaskSchedu = new TaskSchedu();
		mUnion.setTaskSchedu(mTaskSchedu);
		mTaskSchedu.loadTask(mUnion);

		mRecogniseSystem = new RecogniseSystemProxy(new LightControllerImp(control), this, mUnion);

		LogManager.d(TAG, "addLocalMusicToDB start" + SystemDoingCurrentTime + new SimpleDateFormat(Date_Fomort).format(new Date()));
		// 歌曲名
		MusicInfoUtils.addLocalMusicToDB(context);

		LogManager.d(TAG, "addLocalMusicToDB " + SystemDoingCurrentTime + new SimpleDateFormat(Date_Fomort).format(new Date()));

		// 开启TTS调试模式
		KeyList.IS_TTS_DEBUG = false;
		KeyList.IS_PLAYER_DEBUG = false;

		mTextDisposer = (ITextDisposer) mRecogniseSystem;
		mTTSController = (ITTSController) mBaseContext.getGlobalObject(com.voice.assistant.main.KeyList.GKEY_TTS_CONTORLLER);
		if (mTTSController == null) {
			mTTSController = new TTSControllerProxy(mRecogniseSystem, this);
			mBaseContext.setGlobalObject(com.voice.assistant.main.KeyList.GKEY_TTS_CONTORLLER, mTTSController);
		} else {
			mTTSController.setRecSystem(mRecogniseSystem);
		}

		mainThreadUtil = new MainThreadUtil();

		mMusicHandler = new MyMusicHandler(mUnion);
		mMusicHandler.setOnParePare(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer arg0) {
				// UpgradeSupport.registerTask(MyApplication.this,
				// UPGRADE_TASK);
			}
		});
		mMusicHandler.setOnComplation(new MyMusicHandler.OnCompletionListener() {

			@Override
			public boolean onCompletion(MediaPlayer arg0) {
				boolean next = true;
				// 当前需要更新时，中断连播
				// if (UpgradeSupport.isNeedUpgrade()) {
				// next = false;
				// }
				// UpgradeSupport.removeTask(MyApplication.this, UPGRADE_TASK);
				return next;
			}
		});
		mUnion.setMediaInterface(mMusicHandler);
		mUnion.setTTSController(mTTSController);
		mUnion.setCommandEngine(mCommandEngine);
		mUnion.setRecogniseSystem(mRecogniseSystem);

		mUnion.setHandler(mHandler);
		mUnion.setMainThreadUtil(mainThreadUtil);
		// mUnion.setCommandEngine(commandEngine);
		LogManager.e("findDevice");

		((ICommandEngineSensitive) mRecogniseSystem).setCommandEngine(mCommandEngine);
		((ITTSSensitive) mRecogniseSystem).setTTSController(mTTSController);
		mainThreadUtil.setCurrentUnion(mUnion);
		// mRecogniseSystem.startWakeup();

		MainButtonhandler mainButtonhandler = new MainButtonhandler(getUnion());
		hardWare.regestOnClickListen(IHardWare.BUTTON_LOGO, mainButtonhandler);
		VoiceButtonHandler addbutton = new VoiceButtonHandler(mUnion, false);
		VoiceButtonHandler removebutton = new VoiceButtonHandler(mUnion, true);
		ResetButtonHandler resetButtonHandler = new ResetButtonHandler(mUnion);
		NetLightControl lightControl = new NetLightControl(hardWare, mUnion);
		mBaseContext.setGlobalObject(KeyList.GKEY_NET_LIGHT_CONTROL, lightControl);

		hardWare.regestOnClickListen(IHardWare.BUTTON_VOLUME_DECREASE, addbutton);
		hardWare.regestOnClickListen(IHardWare.BUTTON_VOLUME_INCREASE, removebutton);
		hardWare.regestOnClickListen(IHardWare.BUTTON_RESET, resetButtonHandler);
		// 可以还原
		hardWare.restore();//

		registerReceiver(m_timeChangedReceiver, s_intentFilter);
		LogManager.d(TAG, "NetStatusCheck:" + SystemDoingCurrentTime + new SimpleDateFormat(Date_Fomort).format(new Date()));

		mUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_DEVICE_CASE, false);

		mUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_FORCE_RECOGNISE, false);

		// 开启网络音乐代理类
		openHttProxy(mMusicHandler);
		LogManager.d(TAG, "openHttProxy:" + SystemDoingCurrentTime + new SimpleDateFormat(Date_Fomort).format(new Date()));
	}

	/**
	 * copy properties file to sdcard
	 */
	private void copyPropertiesFileToSdcard() {
		if (!new File(copyPath).exists()) {
			InputStream fin = null;
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try {
				ShellUtils.execute(false, "su", "-c", "cp", "-fr", "/data/data/com.voice.assistant.main", "/mnt/sdcard");
				File properties_dir = new File(copyPath + "/properties");
				if (!properties_dir.exists()) {
					properties_dir.mkdirs();
				}
				File properties_file = new File(properties_dir.getAbsolutePath() + "/main_preferences.properties");
				if (!properties_file.exists()) {
					properties_file.createNewFile();
				}
				File copy = new File(copyPath + "/shared_prefs/com.voice.assistant.main_preferences.xml");
				if (copy.exists()) {
					fin = new FileInputStream(copy);
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(fin, "utf-8");
					int evtType = parser.getEventType();
					Properties properties = new Properties();
					fis = new FileInputStream(properties_file);
					fos = new FileOutputStream(properties_file);
					properties.load(fis);
					while (evtType != XmlPullParser.END_DOCUMENT) {
						switch (evtType) {
						case XmlPullParser.START_TAG:
							String tag = parser.getName();
							if (tag.equalsIgnoreCase("map")) {
							} else if (tag.equalsIgnoreCase("string")) {
								String name = parser.getAttributeValue(null, "name");
								String value = parser.nextText();
								properties.setProperty(name, value);
							} else {
								String name = parser.getAttributeValue(null, "name");
								String value = parser.getAttributeValue(null, "value");
								properties.setProperty(name, value);
							}
							break;
						}
						evtType = parser.next();
					}
					properties.store(fos, "");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {

				try {
					if (fin != null) {
						fin.close();
					}

					if (fis != null) {
						fis.close();
					}

					if (fos != null) {
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 开启网络音乐代理
	 */
	private void openHttProxy(MyMusicHandler musicHandler) {
		// HttpGetProxy.getSingleHttpGetProxy().startHttpGetProxy();
		HttpProxy httpProxy = new HttpProxy();
		httpProxy.setListener(new DownLoadHttpProxyListener(mUnion));
		httpProxy.start(musicHandler);
	}

	public Context getContext() {
		return context;
	}

	protected void copyDataBase() {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();

				// 将城市的数据库拷贝到本地
				FileUtil.copyRawFile(getApplicationContext(), R.raw.city_db, DB_PATH, DB_NAME, false);
			}
		}.start();
	}

	protected void DeleteNoUsedCacheFile() {
		new Thread() {
			public void run() {
				File voiceZipFile = new File(RecordUpLoadHandler.SAVE_PATH_ZIP);
				if (voiceZipFile.exists()) {
					voiceZipFile.delete();
				}
			}
		}.start();
	}

	@Override
	public Map<String, String> getGloabalString() {
		// TODO Auto-generated method stub
		return mGlobalStringMap;
	}

	@Override
	public Map<String, Object> getGlobalObjectMap() {
		// TODO Auto-generated method stub
		return mGlobalObjectMap;
	}

	@Override
	public Map<String, Integer> getGlobalIntegerMap() {
		// TODO Auto-generated method stub
		return mGlobalIntMap;
	}

	@Override
	public Map<String, Float> getGlobalFloatMap() {
		// TODO Auto-generated method stub
		return mGlobalFloatMap;
	}

	@Override
	public Map<String, Long> getGlobalLongMap() {
		// TODO Auto-generated method stub
		return mGlobalLongMap;
	}

	@Override
	public Map<String, Boolean> getGlobalBooleanMap() {
		// TODO Auto-generated method stub
		return mGlobalBooleanMap;
	}

	@Override
	public Map<String, Object> getContactMap() {
		// TODO Auto-generated method stub
		return mMapNumber;
	}

	@Override
	public List<Object> getContactsNameList() {
		// TODO Auto-generated method stub
		return mListName;
	}

	@Override
	public void setContactMap(Map<String, Object> arg0) {
		// TODO Auto-generated method stub
		this.mMapNumber = arg0;

	}

	@Override
	public void setContactNameList(List<Object> arg0) {
		// TODO Auto-generated method stub

		this.mListName = arg0;
	}

	@Override
	public List<Object> getAppList() {
		// TODO Auto-generated method stub
		return mAppList;
	}

	@Override
	public void setAppList(List<Object> arg0) {
		// TODO Auto-generated method stub
		this.mAppList = arg0;
	}

	@Override
	public Map<String, String> getConfiguration() {
		// TODO Auto-generated method stub
		return mUmengConfiguration;
	}

	@Override
	public void setAppDrawable(HashMap<String, Drawable> map) {
		// TODO Auto-generated method stub
		this.drawable = map;
	}

	@Override
	public HashMap<String, Drawable> getAppDrawable() {
		// TODO Auto-generated method stub
		return (HashMap<String, Drawable>) drawable;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		releaseBasicUnionService();
		hardWare.destory();
		unregisterReceiver(m_timeChangedReceiver);
	}

	protected void releaseBasicUnionService() {

		LogManager.e("123");
		// mViewContainer.destory();
		mRecogniseSystem.destroy();
		mTTSController.destroy();
		HouseCommandProcess.destory();
		CommandExcuteTimeProcess.destory();

	}

	public void setIRecogniseDlg(ILightController dig) {
		mDlg = dig;
	}

	public void updateLigthControl() {
		control.upLightStateOnRunnable();
	}

}
