package com.iii.wifi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import android.os.Environment;

import com.iii.client.WifiConfig;

public class FileProperties {
	public static final String TCP_PORT_NAME = "tcpPort";
	public static final String UDP_PORT_NAME = "udpPort";

	private Properties mProperties;

	public FileProperties() {
		// TODO Auto-generated constructor stub
		loadConfig();
	}

	public void loadConfig() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory().getPath() + "/wifiserver.config");
			mProperties = new Properties();

			try {
				FileInputStream inputStream;
				FileOutputStream outputStream;

				// if(file.exists()&&file.isFile()){
				// file.delete();
				// }

				if (!file.exists()) {
					file.createNewFile();
					inputStream = new FileInputStream(file);
					outputStream = new FileOutputStream(file);
					mProperties.load(inputStream);
					mProperties.setProperty(TCP_PORT_NAME, WifiConfig.TCP_DEFAULT_PORT + "");
					mProperties.setProperty(UDP_PORT_NAME, WifiConfig.UDP_DEFAULT_PORT + "");
					mProperties.store(outputStream, "config for wifiserver");
					outputStream.close();
				} else {
					inputStream = new FileInputStream(file);
					mProperties.load(inputStream);
				}

				inputStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getTcpPort() {
		if (mProperties != null) {
			return mProperties.getProperty(TCP_PORT_NAME);
		}
		return WifiConfig.TCP_DEFAULT_PORT + "";
	}

	public String getUdpPort() {
		if (mProperties != null) {
			return mProperties.getProperty(UDP_PORT_NAME);
		}
		return WifiConfig.UDP_DEFAULT_PORT + "";
	}
}
