package com.iii360.sup.common.utl.file;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * 下载线程类
 * @author Administrator
 *
 */
public class DownloadThread extends Thread {
	private static final String TAG = "DownloadThread";
	private File saveFile;
	private URL downUrl;
	private int block;
	
	/* 下载开始位置  */
	private int threadId = -1;	
	private int downLength;
	private boolean finish = false;
	private FileDownloader downloader;
	
	private boolean mIsError = false;//是否出错
	
	private boolean mIsCancelDownload = false;//是否取消下载
	/**
	 * @param downloader:下载器
	 * @param downUrl:下载地址
	 * @param saveFile:下载路径
	 * 
	 */
	public DownloadThread(FileDownloader downloader, URL downUrl, File saveFile, int block, int downLength, int threadId) {
		this.downUrl = downUrl;
		this.saveFile = saveFile;
		this.block = block;
		this.downloader = downloader;
		this.threadId = threadId;
		this.downLength = downLength;
	}
	
	@Override
	public void run() {
		if(downLength < block){//未下载完成
			
			HttpURLConnection connection = null;
			RandomAccessFile randomAccessFile = null;
			InputStream is = null;
			
			try {
				//使用Get方式下载
//				HttpURLConnection http = (HttpURLConnection) downUrl.openConnection();
//				http.setConnectTimeout(5 * 1000);
//				http.setRequestMethod("GET");
//				http.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
//				http.setRequestProperty("Accept-Language", "zh-CN");
//				http.setRequestProperty("Referer", downUrl.toString()); 
//				http.setRequestProperty("Charset", "UTF-8");
				
				int startPos = block * (threadId - 1) + downLength;//开始位置
				int endPos = block * threadId -1;//结束位置
//				http.setRequestProperty("Range", "bytes=" + startPos + "-"+ endPos);//设置获取实体数据的范围
//				http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
//				http.setRequestProperty("Connection", "Keep-Alive");
//				
//				InputStream inStream = http.getInputStream();
//				RandomAccessFile threadfile = new RandomAccessFile(this.saveFile, "rwd");
//				threadfile.seek(startPos);
				
				
				connection = (HttpURLConnection) downUrl.openConnection();
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");
				// 设置范围，格式为Range：bytes x-y;
				connection.setRequestProperty("Range", "bytes="
						+ startPos + "-" + endPos);

				randomAccessFile = new RandomAccessFile(this.saveFile, "rwd");
				randomAccessFile.seek(startPos);
				// 将要下载的文件写到保存在保存路径下的文件中
				is = connection.getInputStream();
				
				
				int offset = -1;
				
				byte[] buffer = new byte[4096];
				
				while ((offset = is.read(buffer)) != -1) {
					randomAccessFile.write(buffer, 0, offset);
					downLength += offset;
					downloader.append(offset);
					
					if (mIsCancelDownload) {
                        break;
                    }
					
				}
				
				
				
				
				randomAccessFile.close();
				is.close();
				print("Thread " + this.threadId + " download finish");
				
				if (!mIsCancelDownload) {
				    this.finish = true;
                }
				
				
			} catch (Exception e) {
				this.downLength = 0;
				e.printStackTrace();
//				System.out.println("下载:============:错误"+e.getMessage());
				print("Thread "+ this.threadId+ ":"+ e);
				
				mIsError = true;
			}finally{
				 downloader.update(this.threadId, downLength);
			}
			
		}
	}
	
	/**
	 * 打印日志信息
	 * @param msg
	 */
	private static void print(String msg){
//		Log.i(TAG, msg);
	}
	
	/**
	 * 下载是否完成
	 * @return
	 */
	public boolean isFinish() {
		return finish;
	}
	
	
	public void setCancelDownload(boolean cancelDownload){
	    this.mIsCancelDownload = cancelDownload;
	    
	}
	
	/**
	 * 已经下载的内容大小
	 * @return 如果返回值为-1,代表下载失败
	 */
	public long getDownLength() {
		return downLength;
	}
	
	
	
	/**
	 * 下载是否出错
	 * @return
	 */
	public boolean isError() {
		return mIsError;
	}
	
	public void setEroor(boolean error){
		mIsError = error;
	}
	
}
