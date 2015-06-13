package com.voice.assistant.main.music.httpproxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class MusicDownloadThread extends Thread {
	static private final String TAG = "Music DownloadThread";
	private String mUrl;
	private File mtrueSaveFile;
	private File mtempFile;
	private long mDownloadSize;
	private int mTargetSize;
	private boolean mStop = false;
	private boolean mDownloading = false;
	private boolean mStarted = false;
	private boolean mError = false;

	public MusicDownloadThread(String url, String trueSavePath, int targetSize) {
		mUrl = url;
		mtrueSaveFile = new File(trueSavePath);
		String mtempFileString = trueSavePath.substring(0, trueSavePath.lastIndexOf(".") + 1) + "temp";
		mTargetSize = targetSize;
		// 如果文件存在，删除重新下载
		mtempFile = new File(mtempFileString);
		if (mtempFile.exists() && mtempFile.length() < targetSize) {
			mtempFile.delete();
		} else {
			mDownloadSize = 0;
		}
	}

	@Override
	public void run() {
		mDownloading = true;
		download();
	}

	/** 启动下载线程 */
	public void startThread() {
		if (!mStarted) {
			this.start();
			// 只能启动一次
			mStarted = true;
		}
	}

	/** 停止下载线程 */
	public void stopThread() {
		mStop = true;
	}

	/** 是否正在下载 */
	public boolean isDownloading() {
		return mDownloading;
	}

	/**
	 * 是否下载异常
	 * 
	 * @return
	 */
	public boolean isError() {
		return mError;
	}

	/**
	 * 获取下载文件的大小
	 * 
	 * @return
	 */
	public long getDownloadedSize() {
		return mDownloadSize;
	}

	/** 是否下载成功 */
	public boolean isDownloadSuccessed() {
		return (mDownloadSize != 0 && mDownloadSize >= mTargetSize);
	}

	private void download() {
		InputStream is = null;
		FileOutputStream os = null;
		Log.d(TAG, "download start:");
		try {
			URL url = new URL(mUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setInstanceFollowRedirects(true);// 允许重定向
			is = urlConnection.getInputStream();
			os = new FileOutputStream(mtempFile);
			int len = 0;
			byte[] bs = new byte[1024*5];
			while (!mStop && mDownloadSize < mTargetSize && ((len = is.read(bs)) != -1)) {
				Log.e(TAG, "download write len =  " + len);
				os.write(bs, 0, len);
				mDownloadSize += len;
			}
		} catch (Exception e) {
			mError = true;
			if (mtempFile.exists()) {
				mtempFile.delete();
			}
			Log.e(TAG, "download error:" + e.toString());
		} finally {
			try {
				if (os != null)
					os.close();
				if (is != null)
					is.close();
			} catch (Exception e) {
				Log.e(TAG, "close stream exception:" + e.toString());
			}
			mDownloading = false;
			// 清除空文件
			if (isDownloadSuccessed()) {
				mtempFile.renameTo(mtrueSaveFile);
			}
		}
	}
}
