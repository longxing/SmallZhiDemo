package com.voice.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import com.iii360.base.common.utl.KeyList;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.NetWorkUtil;
import com.iii360.sup.common.utl.TimerTicker;
import com.iii360.sup.common.utl.file.DownloadProgressListener;
import com.iii360.sup.common.utl.file.FileDownloader;

public class UpdateRunable implements Runnable {

	private BasicServiceUnion mBasicServiceUnion;
	private TimerTicker mTicker;
	private String getDownLoadURI = "";

	public UpdateRunable(BasicServiceUnion basicServiceUnion) {
		mBasicServiceUnion = basicServiceUnion;
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 2);
		c.set(Calendar.MINUTE, 0);
		mTicker = new TimerTicker(c.getTimeInMillis(), true, 1, Calendar.DAY_OF_MONTH);
		pushStack();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		InputStreamReader reader = NetWorkUtil.getNetworkInputStreamReader(getDownLoadURI);
		BufferedReader br = new BufferedReader(reader);
		try {
			String realUri = br.readLine();
			if (realUri != null && realUri.length() > 0) {
				LogManager.e(realUri);
				FileDownloader downloader = new FileDownloader(realUri, new File("/sdcard/"), 1,
						"newVoiceAssistant.apk");
				try {
					downloader.download(new DownloadProgressListener() {

						public void onDownloadSize(String fileUrl,int size) {
							// TODO Auto-generated method stub

						}
						@Override
						public void onDownloadResultSuccess(String fileUrl, String filename) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onDownloadCancle(String fileUrl, String filename) {
							// TODO Auto-generated method stub
							
						}
						@Override
						public void onDownloadError(String fileUrl, String filename, File tempfilename, boolean reload) {
							// TODO Auto-generated method stub
							
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LogManager.printStackTrace();
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace();
		}

	}

	private void pushStack() {
		mBasicServiceUnion.getTaskSchedu().pushStackatTime(this, mTicker.getRunTime(), KeyList.TASKKEY_UPGRADE);
	}

}
