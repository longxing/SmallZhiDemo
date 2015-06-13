package com.iii360.sup.common.utl.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class SimpleFileDownload {

	private static final int TIME_OUT = 10 * 10000000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码
	public static final String SUCCESS = "1";
	public static final String FAILURE = "0";

	public static boolean downLoad(String RequestURL, String savepos) {
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		FileOutputStream fos = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
//			conn.setDoInput(true); // 允许输入流
//			conn.setDoOutput(true); // 允许输出流
//			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("GET"); // 请求方式
//			conn.setRequestProperty("Charset", CHARSET); // 设置编码
//			conn.setRequestProperty("connection", "keep-alive");
//			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			if (savepos != null) {
				File f = new File(savepos);
				inputStream = conn.getInputStream();
				byte[] b = new byte[1024];
				fos = new FileOutputStream(f,false);
				int i;
				while ((i = inputStream.read(b)) > 0) {
					fos.write(b, 0, i);
				}
				fos.flush();
				
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

}
