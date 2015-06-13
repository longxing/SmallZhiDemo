package com.voice.assistant.main.music.httpproxy;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import com.iii360.sup.common.utl.EncryptMethodUtil;
import com.voice.assistant.main.newmusic.MusicInfoManager;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * 代理服务器工具类
 * 
 * @author Peter
 */
public class HttpGetProxyUtils {
	public final static String TAG = "Music HttpGetProxyUtils";
	public final static long musicCacheMaxSize = 700 * 1024 * 1024;
	public final static int musicCacheMaxNum = 20;
	/** 下载线程 */
	private static MusicDownloadThread downloadThread = null;

	/**
	 * 获取重定向后的URL，即真正有效的链接
	 * 
	 * @param urlString
	 * @return
	 */
	protected static String getRedirectUrl(String urlString) {
		String result = urlString;
		// 取得取得默认的HttpClient实例
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlString);
		try {
			// 重定向设置连接服务器
			httpClient.setRedirectHandler(new RedirectHandler() {
				public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
					int statusCode = response.getStatusLine().getStatusCode();
					if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY) || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) || (statusCode == HttpStatus.SC_SEE_OTHER)
							|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
						// 此处重定向处理
						return null;
					}
					return null;
				}

				public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
					return false;
				}
			});
			HttpResponse response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY) || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) || (statusCode == HttpStatus.SC_SEE_OTHER)
					|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
				Header locationHeader = response.getFirstHeader("Location");
				if (locationHeader != null) {
					String locationUrl = locationHeader.getValue();
					httpClient.getConnectionManager().shutdown();// 释放连接
					return getRedirectUrl(locationUrl);// 防止多次重定向
				}
			}
		} catch (ClientProtocolException ex) {
			Log.e(TAG, ex.toString());
		} catch (IOException ex) {
			Log.e(TAG, ex.toString());
		}
		httpClient.getConnectionManager().shutdown();// 释放连接
		return result;
	}

	public static String getSubString(String source, String startStr, String endStr) {
		int startIndex = source.indexOf(startStr) + startStr.length();
		int endIndex = source.indexOf(endStr, startIndex);
		return source.substring(startIndex, endIndex);
	}

	/**
	 * 获取有效的文件名
	 * 
	 * @param str
	 * @return
	 */
	public static String getValidFileName(String str) {
		int startIndex = str.lastIndexOf("/");
		int endIndex = str.lastIndexOf(".");
		return str.substring(startIndex + 1, endIndex);
	}

	public static String getBufferDir() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(MusicInfoManager.NET_MUSIC_CACAHE_PATH);
			if (!dir.exists())
				dir.mkdirs();
			return MusicInfoManager.NET_MUSIC_CACAHE_PATH;
		} else {
			Log.i(TAG, "can not find sdcard!");
			return null;
		}
	}

	/**
	 * 获取外部存储器可用的空间
	 * 
	 * @return
	 */
	static protected long getAvailaleSize(String dir) {
		StatFs stat = new StatFs(dir);// path.getPath());
		long totalBlocks = stat.getBlockCount();// 获取block数量
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize; // 获取可用大小
	}

	/**
	 * 获取文件夹内的文件，按日期排序，从旧到新
	 * 
	 * @param dirPath
	 * @return
	 */
	public static List<File> getFilesSortByDate(String dirPath) {
		List<File> result = new ArrayList<File>();
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0)
			return result;
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			}
		});
		for (int i = 0; i < files.length; i++) {
			result.add(files[i]);
			Log.i(TAG, i + ":" + files[i].lastModified() + "---" + files[i].getPath());
		}
		return result;
	}

	/**
	 * 获取本地已有歌曲或者已经下载过的歌曲路径
	 * 
	 * @param fileUrl
	 * @return
	 */
	public static String getLocalOrCacheMusicsPath(String fileUrl) {
		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
		if (!fileUrl.startsWith(MusicInfoManager.MY_OWN_NET_MUSIC_PATH)) {
			fileName = EncryptMethodUtil.generatePassword(fileUrl) + ".mp3";
		}
		String downLoadCacheMusicPath = MusicInfoManager.NET_MUSIC_PATH + fileName;
		File cachefile = new File(downLoadCacheMusicPath);
		if (cachefile.exists()) {
			return downLoadCacheMusicPath;
		}
		return null;
	}

	/**
	 * 删除多余的缓存文件
	 * 
	 * @param dirPath
	 * @param maximun
	 */
	public static void asynRemoveBufferFile(final String dirPath, final int maximun) {
		new Thread() {
			public void run() {
				List<File> lstBufferFile = getFilesSortByDate(dirPath);
				while (lstBufferFile.size() > maximun) {
					Log.i(TAG, "---delete " + lstBufferFile.get(0).getPath());
					lstBufferFile.get(0).delete();
					lstBufferFile.remove(0);
				}
			}
		}.start();
	}

	public static void startDownload(String url) throws Exception {
		String fileCacheDir = getBufferDir();
		HttpGetProxyUtils.asynRemoveBufferFile(fileCacheDir, musicCacheMaxNum);
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		String mMediaFilePath = fileCacheDir + fileName;
		File tmpFile = new File(mMediaFilePath);
		URLConnection conn = new URL(url).openConnection();
		conn.connect();
		int fileSize = conn.getContentLength();
		if (tmpFile.exists() && tmpFile.length() >= fileSize) {
			Log.i(TAG, "file exists:" + mMediaFilePath + " size:" + tmpFile.length());
			return;
		}
		stopDownload();
		downloadThread = new MusicDownloadThread(url, mMediaFilePath, fileSize);
		downloadThread.startThread();
		Log.i(TAG, "----startDownload:" + mMediaFilePath);
	}

	/**
	 * 停止下载
	 */
	public static void stopDownload() {
		if (downloadThread != null && downloadThread.isDownloading())
			downloadThread.stopThread();
	}

}
