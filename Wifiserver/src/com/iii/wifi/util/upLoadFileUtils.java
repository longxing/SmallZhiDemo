package com.iii.wifi.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import android.util.Log;

import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.ShellUtils;
import com.iii360.sup.common.utl.file.FileUpload;
import com.iii360.sup.commonutil.AsnyHttp.AsyncHttpClient;
import com.iii360.sup.commonutil.AsnyHttp.AsyncHttpResponseHandler;
import com.iii360.sup.commonutil.AsnyHttp.RequestParams;

public class upLoadFileUtils {
	private static final String sendLogUrl = "http://api.smallzhi.com/v/b/pushLog";
//	private static final String sendLogUrl = "http://192.168.20.30/v/b/pushLog";

	public static void sendLogToServer() {
		// TODO Auto-generated method stub
		try {
			File file = new File(LogManager.saveImportantLogDir);
			if (!file.exists()) {
				LogManager.i("mnt/sdcard/LogmangerBox/  current dir not exist!");
				return;
			}
			for (File f : file.listFiles()) {
				RequestParams params = new RequestParams();
				params.put("log", new FileInputStream(f), f.getName());
				params.put("deviceKey", ShellUtils.readSerialNumber());
				map.put(f.getName(), 0);
				sentLogByAsyHttp(params,f.getName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogManager.i("upLoadFileUtils !" + e.toString());
		}
	}
	private static Map<String, Integer>map = new HashMap<String, Integer>();
	private static void sentLogByAsyHttp(final RequestParams params,final String name) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.post(sendLogUrl, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					if (responseBody != null) {
						String str = new String(responseBody);
						LogManager.i("upLoadFileUtils send log to server success!" + str);
					} else {
						LogManager.i("upLoadFileUtils send log to server fail!OnSuccess");
					}
				} catch (Exception e) {
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// TODO Auto-generated method stub
				try {
					if (responseBody != null) {
						String str = new String(responseBody);
						LogManager.i("upLoadFileUtils send log to server fail! " + str);
					}
					LogManager.i("upLoadFileUtils send log to server fail!");

				} catch (Exception e) {
				}
			}
		});
	}
	
	public static void sendLogToServerByHttp(){
		File file = new File(LogManager.saveImportantLogDir);
		if (!file.exists()) {
			LogManager.i("mnt/sdcard/LogmangerBox/  current dir not exist!");
			return;
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("deviceKey", ShellUtils.readSerialNumber());
		HashMap<String,File> files = new HashMap<String, File>();
		for (File f : file.listFiles()) {
			files.put(f.getName(),f.getAbsoluteFile());
			try {
				FileUpload.post(sendLogUrl, params, files);
				LogManager.i("upLoadFileUtils send log to server success!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogManager.i("upLoadFileUtils send log to server fail!"+e);
			}
			files.clear();
		}
	
	}
	
	
	

}
