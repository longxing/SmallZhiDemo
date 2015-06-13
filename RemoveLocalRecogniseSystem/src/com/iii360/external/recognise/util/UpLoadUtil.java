package com.iii360.external.recognise.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import com.iii360.base.common.utl.LogManager;
import com.iii360.sup.common.utl.ShellUtils;

public class UpLoadUtil {
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10 * 1000000;
	private static final String CHARSET = "utf-8";
	private static String UPLOAD_FILE_NAME = "file";
	public static String REQUEST_URL = "http://record.hezi.360iii.net:58080/fileupload/upload";
	public static String ftpUrlForSingle = "121.41.62.6";
	private static String deviceType = "Box";
	private static String ftpUrl = "record.hezi.360iii.net";
	private static String ftpUserName = "smallzhiftp";
	private static String ftpUserPassWord = "TG6aFvccHP";
	private static FTP ftp = null;

	public static String uploadFile(File file, String urlStr) {
		// File file = new File(fileName);
		String result = null;

		String BOUNDARY = UUID.randomUUID().toString();
		String PREFIX = "--";
		String LINE_END = "\r\n";

		String CONTENT_TYPE = "multipart/form-data";
		try {
			Date data = new Date();
			LogManager.e(data.getYear() + "/" + (data.getMonth() + 1) + "/" + data.getDate() + "/" + data.getHours() + ":" + data.getMinutes() + " " + urlStr);
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", "utf-8");
			// conn.setChunkedStreamingMode(1024);
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			if (file != null) {
				/**
				 * get box sn number
				 */
				String boxSN = ShellUtils.readSerialNumber();
				String fileName = deviceType + "_" + boxSN + "_" + file.getName();
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				sb.append("Content-Disposition: form-data; name=\"" + UPLOAD_FILE_NAME + "\"; filename=\"" + fileName + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset=utf-8" + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();

				int res = conn.getResponseCode();
				LogManager.d("requestCode=" + res);
				if (res == 200) {
					LogManager.d("request success");
					InputStream input = conn.getInputStream();
					StringBuffer sb1 = new StringBuffer();
					int ss;
					while ((ss = input.read()) != -1) {
						sb1.append((char) ss);
					}
					result = sb1.toString();
				} else {
					LogManager.e("upload file request error");
				}
				result = res + "";
			}
		} catch (MalformedURLException e) {
			LogManager.printStackTrace(e);
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
			LogManager.printStackTrace(e);
		}
		return result;
	}

	/***
	 * ftp 上传音频压缩文件
	 * 
	 * @param file
	 * @param urlStr
	 * @return
	 */
	public static Result uploadFileByFtp(File file, String urlStr, boolean isDeleteFile) {
		Result result = null;
		if (ftp == null) {
			ftp = new FTP(ftpUrl, ftpUserName, ftpUserPassWord);
			ftp.setDeleteFileAfterUpload(isDeleteFile);
		}
		try {
			ftp.openConnect();
			result = ftp.uploadingForZipFile(file, urlStr);
		} catch (IOException e) {
			LogManager.e(e.toString());
		} finally {
			if (ftp != null) {
				try {
					ftp.closeConnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}