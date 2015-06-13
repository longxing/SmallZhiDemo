package com.voice.upgrade.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import android.os.ILedsService;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.iii360.sup.common.utl.Animate;

public class LightService {
	private static LightService self = new LightService();

	public static LightService getInstance() {
		return self;
	}
	
	ILedsService mledsev = null;

	private LightService() {
		mledsev = ILedsService.Stub.asInterface(ServiceManager
				.getService("leds"));
	}

	/**
	 * 开关
	 * 
	 * @return 助手端led灯
	 */
	public boolean ledSwitch() {
		readPrefXML();
		
//		String lightOn = map.get(PKEY_BUTTON_LINGHT_ON);
		String lightOn = properties.getProperty(PKEY_BUTTON_LINGHT_ON);
		if ("true".equals(lightOn)) {
			return true;
		}
		return false;
	}

//	private XmlPullParser mXmlPullParser = null;
//	private String currentKey = null;
	private Properties properties;
//	private Map<String, String> map = new HashMap<String, String>();

	private void readPrefXML() {
//		String prefs = "/data/data/com.voice.assistant.main/shared_prefs/com.voice.assistant.main_preferences.xml";
		String prefs = "/mnt/sdcard/com.voice.assistant.main/properties/main_preferences.properties";
		FileInputStream fin = null;
		InputStream in=null;
		try {
			properties=new Properties();
			fin = new FileInputStream(prefs);
			in = new BufferedInputStream(fin);
			properties.load(in); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if (fin != null) {
					fin.close();
				}
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// 设置权限
//		try {
//			ShellUtils.execute(false, "su", "-c", "chmod", "777", prefs);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
//		FileInputStream fin = null;
//		try {
//			fin = new FileInputStream(prefs);
//			map = new HashMap<String, String>();
//			mXmlPullParser = Xml.newPullParser();
//			mXmlPullParser.setInput(fin, "utf-8");
//			int evtType = mXmlPullParser.getEventType();
//			while (evtType != XmlPullParser.END_DOCUMENT) {
//				switch (evtType) {
//
//				case XmlPullParser.START_TAG:
//
//					handleStartTag(mXmlPullParser.getName());
//					break;
//				case XmlPullParser.TEXT:
//
//					handText(mXmlPullParser.getText());
//					break;
//				case XmlPullParser.END_TAG:
//					handleEndTag(mXmlPullParser.getName());
//					break;
//				}
//				evtType = mXmlPullParser.next();
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (XmlPullParserException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (fin != null) {
//				try {
//					fin.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		LogManager.d("voice.assistant :" + map.toString());
	}

//	private void handleEndTag(String name) {
//
//	}

//	private void handText(String text) {
//		if (currentKey == null) {
//
//		} else if (currentKey.equals(PKEY_BUTTON_LINGHT_CLOSE_TIME)) {
//			properties.put(currentKey, text.trim());
//			currentKey = null;
//		} else if (currentKey.equals(PKEY_BUTTON_LINGHT_OPEN_TIME)) {
//			properties.put(currentKey, text.trim());
//			currentKey = null;
//		}
//	}

	private static final String PKEY_BUTTON_LINGHT_CLOSE_TIME = "PKEY_BUTTON_LINGHT_CLOSE_TIME";
	private static final String PKEY_BUTTON_LINGHT_OPEN_TIME = "PKEY_BUTTON_LINGHT_OPEN_TIME";
	private static final String PKEY_BUTTON_LINGHT_ON = "PKEY_BUTTON_LINGHT_ON";

//	private void handleStartTag(String name) {
//		String key = mXmlPullParser.getAttributeValue(null, "name");
//		if ("string".equals(name)) {
//			if (PKEY_BUTTON_LINGHT_CLOSE_TIME.equals(key)) {
//				currentKey = PKEY_BUTTON_LINGHT_CLOSE_TIME;
//				return;
//			} else if (PKEY_BUTTON_LINGHT_OPEN_TIME.equals(key)) {
//				currentKey = PKEY_BUTTON_LINGHT_OPEN_TIME;
//				return;
//			}
//		} else if ("boolean".equals(name)) {
//			if (PKEY_BUTTON_LINGHT_ON.equals(key)) {
//				currentKey = PKEY_BUTTON_LINGHT_ON;
//				properties.put(currentKey,
//						mXmlPullParser.getAttributeValue(null, "value").trim());
//				return;
//			}
//		}
//		currentKey = null;
//	}

	private Animate animation = new Animate();

	public boolean isWorking() {
		if (this.ledSwitch()) {
			String hhmm = new SimpleDateFormat("HHmm").format(new Date());
			// 是否在休眠时间内
			if(properties.getProperty(PKEY_BUTTON_LINGHT_CLOSE_TIME)==null || properties.getProperty(PKEY_BUTTON_LINGHT_OPEN_TIME)==null){
				return true;
			}
			if (properties.getProperty(PKEY_BUTTON_LINGHT_CLOSE_TIME).compareTo(properties.getProperty(PKEY_BUTTON_LINGHT_OPEN_TIME)) >= 0) {
				if (hhmm.compareTo(properties.getProperty(PKEY_BUTTON_LINGHT_CLOSE_TIME)) >= 0)
					return false;
				if (hhmm.compareTo(properties.getProperty(PKEY_BUTTON_LINGHT_OPEN_TIME)) <= 0)
					return false;
			} else {
				if (hhmm.compareTo(properties.getProperty(PKEY_BUTTON_LINGHT_CLOSE_TIME)) >= 0
						&& hhmm.compareTo(properties.getProperty(PKEY_BUTTON_LINGHT_OPEN_TIME)) <= 0) {
					return false;
				}
			}
			
		}
		return true;
	}

	/**
	 * 播放动画
	 * 
	 * @return 操作成功
	 */
	public void playAnimation() {
		Animate.post(new Runnable() {
			
			@Override
			public void run() {
				// 控制灯
				animation.start(new Runnable() {
					private boolean aniLightOn = false;

					@Override
					public void run() {
						try {
							if (!aniLightOn) {
								// 亮灯
								mledsev.setLedsBrightness(0, 0, 10);
							} else {
								// 灭灯
								mledsev.setLedsBrightness(0, 0, 0);
							}
							aniLightOn = !aniLightOn;
							Thread.sleep(200);
						} catch (RemoteException e) {
							Thread.currentThread().interrupt();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				});
			}
		});
	}

	/**
	 * 停止动画
	 * 
	 * @return 操作成功
	 */
	public void stopAnimation() {
		Animate.post(new Runnable() {
			@Override
			public void run() {
				animation.stop();
			}
		});
	}
}
