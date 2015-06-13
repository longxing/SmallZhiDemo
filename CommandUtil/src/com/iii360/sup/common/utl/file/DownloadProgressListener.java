package com.iii360.sup.common.utl.file;

import java.io.File;
/**
 * 下载监听接口
 * @author Peter
 *
 */
public interface DownloadProgressListener {
	/**
	 * 下载文件的进度
	 * @param size
	 */
	public void onDownloadSize(String fileUrl,int size);
	/**
	 * 下载成功
	 * @param fileUrl
	 * @param filename
	 */
	public void onDownloadResultSuccess(String fileUrl,String filename);
	/**
	 * 下载取消
	 * @param fileUrl
	 * @param filename
	 */
	public void onDownloadCancle(String fileUrl,String filename);
	
	/**
	 * 下载错误
	 * @param fileUrl 
	 * @param filename
	 * @param tempfilename
	 * @param reload 是否重新下载
	 */
	public void onDownloadError(String fileUrl,String filename,File tempfilename,boolean reload);
}
