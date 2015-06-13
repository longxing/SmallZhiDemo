package com.iii.wifiserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Xml;
import cn.com.broadlink.blnetwork.BLNetwork;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iii.wifiserver.receiver.BatteryBroadcastReciver;
import com.iii360.sup.common.base.MyApplication;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.ShellUtils;
import com.iii360.sup.common.utl.SuperBaseContext;

/**
 * wifiServer 程序入口
 * 
 * @author Peter
 * @data 2015年5月22日下午2:38:09
 */
public class WifiServerApplication extends MyApplication implements Thread.UncaughtExceptionHandler {

	public static String api_id = "api_id";
	public static String command = "command";
	public static String CODE = "code";
	public static String licenseValue = "IDqOTOuVhMNQz8XWEc2wqmrjuYeTDGtBlMkm6AT1mmKKNLTrl45x4KzHGywehG/TzmSMIDnemvSlaNMSyYceBTJnNVQ10LKQ9sNzVIBX21r87yx+quE=";
	private String copyPath = "/mnt/sdcard/com.iii.wifiserver";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(new BatteryBroadcastReciver(), filter);

		Thread.setDefaultUncaughtExceptionHandler(this);
		if (!new File(copyPath).exists()) {
			InputStream fin = null;
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try {
				ShellUtils.execute(false, "su", "-c", "cp", "-fr", "/data/data/com.iii.wifiserver", "/mnt/sdcard");
				File properties_dir = new File(copyPath + "/properties");
				if (!properties_dir.exists()) {
					properties_dir.mkdirs();
				}
				File properties_file = new File(properties_dir.getAbsolutePath() + "/main_preferences.properties");
				if (!properties_file.exists()) {
					properties_file.createNewFile();
				}
				File copy = new File(copyPath + "/shared_prefs/com.iii.wifiserver_preferences.xml");
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
		init();
	}

	public void init() {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				BLNetwork mBlNetwork = BLNetwork.getInstanceBLNetwork(WifiServerApplication.this);
				JsonObject initJsonObjectIn = new JsonObject();
				String initOut;
				initJsonObjectIn.addProperty(api_id, 1);
				initJsonObjectIn.addProperty(command, "network_init");
				initJsonObjectIn.addProperty("license", licenseValue);
				String string = initJsonObjectIn.toString();
				initOut = mBlNetwork.requestDispatch(string);
				JsonObject initJsonObjectOut = new JsonParser().parse(initOut).getAsJsonObject();
			}
		}.start();

	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		LogManager.e(e.toString());
		new SuperBaseContext(this).setPrefBoolean("PKEY_IS_UNCAUGHT_EXCEPTION", true);
		Intent intent = new Intent(this, DogControllerService.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
