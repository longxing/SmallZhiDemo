package com.iii360.external.recognise.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import android.os.Environment;

import com.iii360.base.common.utl.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.ShellUtils;
import com.iii360.sup.common.utl.file.FileUtil;
import com.iii360.sup.common.utl.file.ZipUtil;

public class RecordUpLoadHandler {
	private static final String SAVE_PATH = Environment.getExternalStorageDirectory().getPath() + "/wakeup_save/";
	public static final String SAVE_PATH_ZIP = Environment.getExternalStorageDirectory().getPath() + "/wakeupSaveZip/";
	private int mTotalBuffLength;
	private Queue<byte[]> mQueue;
	private static final String TAG = "RecordUpLoadHandler";
	private static final String HardWareType = "Box";
	private static final long RECORD_FILE_LIMIT = 200 * 1024 * 1024;

	public void beforeStartRecord() {
		LogManager.e(TAG + "  " + "beforeStartRecord");
		try {
			this.mQueue = new LinkedList<byte[]>();
			this.mTotalBuffLength = 0;
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public void record(byte[] buffer) {
		if (this.mQueue == null || buffer == null || buffer.length <= 0) {
			return;
		}
		this.mQueue.offer(buffer);
		this.mTotalBuffLength += buffer.length;
	}

	/***
	 * 
	 * 2014-12-03 以前的上传文件的方法
	 * 
	 * @param haveResult
	 * @param result
	 * @return
	 */
	@Deprecated
	public String stopRecord(boolean haveResult, String result) {
		if (this.mQueue == null || this.mTotalBuffLength <= 0) {
			return null;
		}
		File file = new File(SAVE_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		String saveFileName = result.replace(",", "").replace("，", "").replace(".", "").replace("。", "").replace("？", "").replace("?", "").replace("!", "").replace("！", "").replace(" ", "");
		int startIndex = saveFileName.indexOf("_");
		int endIndex = saveFileName.lastIndexOf("_");
		if (endIndex - startIndex > 15) {
			saveFileName = saveFileName.substring(0, startIndex) + saveFileName.substring(startIndex, startIndex + 10) + saveFileName.substring(endIndex, saveFileName.length());
		}
		String tempFileName = SAVE_PATH + saveFileName + System.currentTimeMillis() + ".wav";
		LogManager.e(TAG + "  " + tempFileName);
		try {
			AudioFileUtil.generateWAVFile(tempFileName, mQueue, mTotalBuffLength);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace(e);
			// e.printStackTrace();
		}
		this.mQueue = null;
		this.mTotalBuffLength = 0;
		return tempFileName;
	}

	/***
	 * 停止录音，保存录音文件到本地
	 * 
	 * @param haveResult
	 *            是否有识别结果
	 * @param result
	 * @return
	 */
	public String stopRecordToSaveFile(boolean haveResult, String result) {
		if (this.mQueue == null || this.mTotalBuffLength <= 0) {
			return null;
		}
		File file = new File(SAVE_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		String saveFileName = result.replace(",", "").replace("，", "").replace(".", "").replace("。", "").replace("？", "").replace("?", "").replace("!", "").replace("！", "").replace(" ", "");
		int startIndex = saveFileName.lastIndexOf("_");
		int endIndex = saveFileName.lastIndexOf(".");
		if (endIndex - startIndex > 15) {
			saveFileName = saveFileName.substring(0, startIndex) + saveFileName.substring(startIndex, startIndex + 15) + "..." + saveFileName.substring(endIndex, saveFileName.length() - 1);
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(SAVE_PATH);
		buffer.append(HardWareType);
		buffer.append("_");
		buffer.append(ShellUtils.readSerialNumber().replace("\n", ""));
		buffer.append("_");
		buffer.append(new SimpleDateFormat(KeyList.DATA_FORMAT_FIRST).format(new Date(System.currentTimeMillis())));
		buffer.append("_");
		buffer.append(saveFileName);
		buffer.append(".wav");
		LogManager.e(TAG + "  " + buffer.toString());
		// 语音文件名保存
		KeyList.UPLOAD_VOICE_FILE_NAME = buffer.substring(buffer.lastIndexOf("/") + 1);
		try {
			AudioFileUtil.generateWAVFile(buffer.toString(), mQueue, mTotalBuffLength);
			File tempFile = new File(buffer.toString());
			if (tempFile.exists() && tempFile.length() <= 0)
				tempFile.delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace(e);
			// e.printStackTrace();
		}
		this.mQueue = null;
		this.mTotalBuffLength = 0;
		return buffer.toString();
	}

	/**
	 * 上传压缩的文件包
	 */
	public static void upLoadFileToServer() {
		File dirs = new File(SAVE_PATH);
		if (!dirs.exists()) {
			return;
		}
		File zipFile = new File(SAVE_PATH_ZIP);
		if (!zipFile.exists()) {
			zipFile.mkdirs();
		}
		String sn = ShellUtils.readSerialNumber();
		String sendFileName = SAVE_PATH_ZIP + sn.replace("\n", "") + "_" + new SimpleDateFormat(KeyList.DATA_FORMAT_FIRST).format(new Date(System.currentTimeMillis())) + ".zip";
		File zipfiles = new File(sendFileName);
		ZipUtil.zip(dirs, zipfiles);
		Result result = UpLoadUtil.uploadFileByFtp(zipFile, "", true);
		if (result != null && result.isSucceed()) {
			LogManager.e("speech voice zipFile upload success!");
		} else {
			FileUtil.DeleteFileToLimit(zipFile, RECORD_FILE_LIMIT);
			LogManager.e("speech voice zipFile upload fail!");
		}
	}

	/**
	 * 上传单个语音文件
	 */
	public static void upLoadFileToServerBySingle() {
		File dirs = new File(SAVE_PATH);
		if (!dirs.exists()) {
			return;
		}
		// File[] files = dirs.listFiles();
		// for (File file : files) {
		Result result = UpLoadUtil.uploadFileByFtp(dirs, "", true);
		if (result != null && result.isSucceed()) {
			dirs.delete();
			LogManager.e("speech voice single file upload success!");
		} else {
			LogManager.e("speech voice single file upload fail!");
		}
		// }
	}

}
