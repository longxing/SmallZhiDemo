package com.iii360.sup.common.utl.file;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iii360.sup.common.utl.LogManager;

public class FileDownloader {
	private static final String TAG = "FileDownloader";

	public static final String URLERRPR = "don't connection this url";
	public static final String RESPONSERROR = "server no response";

	/* 已下载文件长度 */
	private int downloadSize = 0;

	/* 原始文件长度 */
	private int fileSize = 0;

	/* 线程数 */
	private DownloadThread[] threads;

	/* 本地保存文件 */
	private File saveTmpFile;

	/* 本地保存文件 */
	private File saveTrueFile;

	/* 缓存各线程下载的长度 */
	private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();

	/* 每条线程下载的长度 */
	private int block;

	/* 下载路径 */
	private String downloadUrl;

	private boolean mIsCancelDownload = false;// 是否取消下载

	private String mTrueFileName;// 真实的文件名称
	private String mTempFileName;// 临时的文件名称

	private boolean isBackground = false;//

	/**
	 * 获取线程数
	 */
	public int getThreadSize() {
		return threads.length;
	}

	/**
	 * 获取文件大小
	 * 
	 * @return
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * 累计已下载大小
	 * 
	 * @param size
	 */
	protected synchronized void append(int size) {
		downloadSize += size;
	}

	/**
	 * 更新指定线程最后下载的位置
	 * 
	 * @param threadId
	 *            线程id
	 * @param pos
	 *            最后下载的位置
	 */
	protected synchronized void update(int threadId, int pos) {
		this.data.put(threadId, pos);
	}

	public FileDownloader(String downloadUrl, File fileSaveDir, int threadNum) {
		this(downloadUrl, fileSaveDir, threadNum, null);
	}

	/**
	 * 构建文件下载器
	 * 
	 * @param downloadUrl
	 *            下载路径
	 * @param fileSaveDir
	 *            文件保存目录
	 * @param threadNum
	 *            下载线程数
	 */
	public FileDownloader(String downloadUrl, File fileSaveDir, int threadNum, String fileName) {
		try {
			this.downloadUrl = downloadUrl;
			URL url = new URL(this.downloadUrl);
			if (!fileSaveDir.exists())
				fileSaveDir.mkdirs();
			this.threads = new DownloadThread[threadNum];
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.connect();
			int responsecode = conn.getResponseCode();
			if (responsecode == 200) {
				this.fileSize = conn.getContentLength();// 根据响应获取文件大小
				if (this.fileSize <= 0) {
					LogManager.e("filesize  error ");
					throw new RuntimeException("Unkown file size");
				}
				if (fileName != null) {
					mTrueFileName = fileName;
				} else {
					mTrueFileName = getFileName(conn);// 获取文件名称
				}
				mTempFileName = getTempFileName();

				this.saveTmpFile = new File(fileSaveDir, mTempFileName);// 构建保存文件
				LogManager.e(this.saveTmpFile.getPath() + this.saveTmpFile.getName());
				this.saveTrueFile = new File(fileSaveDir + "/" + mTrueFileName);

				if (this.data.size() == this.threads.length) {// 下面计算所有线程已经下载的数据长度
					for (int i = 0; i < this.threads.length; i++) {
						this.downloadSize += this.data.get(i + 1);
					}
					print("已经下载的长度" + this.downloadSize);
				}

				// 计算每条线程下载的数据长度
				this.block = (this.fileSize % this.threads.length) == 0 ? this.fileSize / this.threads.length : this.fileSize / this.threads.length + 1;

			} else {
				LogManager.e("respons error " + responsecode);
				throw new RuntimeException(RESPONSERROR);
			}
		} catch (Exception e) {
			this.fileSize = -1;
			LogManager.e(e.toString());
		}
	}

	/**
	 * 获取文件名
	 * 
	 * @param conn
	 * @return
	 */
	private String getFileName(HttpURLConnection conn) {
		String filename = this.downloadUrl.substring(this.downloadUrl.lastIndexOf('/') + 1);

		if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null)
					break;

				if ("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())) {
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
					if (m.find())
						return m.group(1);
				}
			}

			filename = UUID.randomUUID() + ".tmp";// 默认取一个文件名
		}

		return filename;
	}

	/**
	 * 获取临时文件名
	 * 
	 * @param conn
	 * @return
	 */
	private String getTempFileName() {
		String filename = this.downloadUrl.substring(this.downloadUrl.lastIndexOf('/') + 1);

		if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
			filename = UUID.randomUUID() + ".tmp";// 默认取一个文件名
		} else {

			if (filename.indexOf(".") != -1) {
				filename = filename.substring(0, filename.lastIndexOf("."));
			}
			filename = filename + ".tmp";
		}

		return filename;
	}

	/**
	 * 开始下载文件
	 * 
	 * @param listener
	 *            监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
	 * @return 已下载文件大小
	 * @throws Exception
	 */
	public int download(DownloadProgressListener listener) throws Exception {
		try {
			LogManager.e("file = null ?" + (this.saveTmpFile == null));
			if (this.fileSize > 0 && this.saveTmpFile != null) {
				RandomAccessFile randOut = new RandomAccessFile(this.saveTmpFile, "rw");
				randOut.setLength(this.fileSize);
				randOut.close();
			} else {
				if (this.fileSize == -1) {
					listener.onDownloadSize(this.downloadUrl, -1);
				}
				return this.fileSize;
			}
			URL url = new URL(this.downloadUrl);
			if (this.data.size() != this.threads.length) {
				this.data.clear();
				for (int i = 0; i < this.threads.length; i++) {
					this.data.put(i + 1, 0);// 初始化每条线程已经下载的数据长度为0
				}
			}

			for (int i = 0; i < this.threads.length; i++) {// 开启线程进行下载
				int downLength = this.data.get(i + 1);
				if (downLength < this.block && this.downloadSize < this.fileSize) {// 判断线程是否已经完成下载,否则继续下载
					this.threads[i] = new DownloadThread(this, url, this.saveTmpFile, this.block, this.data.get(i + 1), i + 1);
					this.threads[i].setPriority(7);
					this.threads[i].setEroor(false);
					this.threads[i].start();
				} else {
					this.threads[i] = null;
				}
			}
			boolean notFinish = true;// 下载未完成
			while (notFinish) {// 循环判断所有线程是否完成下载
				Thread.sleep(1500);
				notFinish = false;// 假定全部线程下载完成
				for (int i = 0; i < this.threads.length; i++) {
					if (this.threads[i] != null && !this.threads[i].isFinish()) {// 如果发现线程未完成下载

						if (this.threads[i].isError()) {// 是否出错
							notFinish = false;
							cancelDownload();
							LogManager.e("error");
							// 发下载错误的通知
							listener.onDownloadError(this.downloadUrl, mTrueFileName, this.saveTmpFile, false);
							return this.downloadSize;
						} else {
							notFinish = true;// 设置标志为下载没有完成
							if (this.threads[i].getDownLength() == -1) {// 如果下载失败,再重新下载
								listener.onDownloadError(this.downloadUrl, mTrueFileName, this.saveTmpFile, true);
								this.threads[i] = new DownloadThread(this, url, this.saveTmpFile, this.block, this.data.get(i + 1), i + 1);
								this.threads[i].setPriority(7);
								this.threads[i].start();
							}
						}

					}
				}
				if (mIsCancelDownload) {
					break;
				}
			}
			if (!mIsCancelDownload) {
				LogManager.e("downLoadOk");
				this.saveTmpFile.renameTo(saveTrueFile);
				listener.onDownloadResultSuccess(this.downloadUrl, saveTrueFile.getName());
			}

		} catch (Exception e) {
			e.printStackTrace();
			LogManager.e("message:" + e.getMessage());
			throw new Exception(e.getMessage());
		}
		return this.downloadSize;
	}

	public void cancelDownload() {
		if (threads == null) {
			return;
		}
		mIsCancelDownload = true;
		DownloadThread downloadThread = null;
		for (int i = 0; i < this.threads.length; i++) {// 开启线程进行下载
			downloadThread = this.threads[i];
			if (downloadThread != null) {
				downloadThread.setCancelDownload(true);
			}
		}
	}

	/**
	 * 获取Http响应头字段
	 * 
	 * @param http
	 * @return
	 */
	public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();

		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null)
				break;
			header.put(http.getHeaderFieldKey(i), mine);
		}

		return header;
	}

	/**
	 * 打印Http头字段
	 * 
	 * @param http
	 */
	public static void printResponseHeader(HttpURLConnection http) {
		Map<String, String> header = getHttpResponseHeader(http);

		for (Map.Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey() != null ? entry.getKey() + ":" : "";
			print(key + entry.getValue());
		}
	}

	/**
	 * 打印日志信息
	 * 
	 * @param msg
	 */
	private static void print(String msg) {
		// Log.i(TAG, msg);
	}

	public File getSaveFile() {
		return saveTrueFile;
	}

	public void setBackgroundDownload(boolean state) {
		this.isBackground = state;
	}

	public boolean getBackgroundDownload() {
		return isBackground;
	}

}
