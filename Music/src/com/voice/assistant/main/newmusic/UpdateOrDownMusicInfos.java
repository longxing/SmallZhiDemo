package com.voice.assistant.main.newmusic;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.iii360.base.common.utl.LogManager;
import com.iii360.sup.common.utl.ShellUtils;

public class UpdateOrDownMusicInfos {
	
	private final static String TAG = "Music UpdateOrDownMusicInfos";
	
	private static final int TIME_OUT = 10 * 10000000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码
	public static final String SUCCESS = "1";
	public static final String FAILURE = "0";

	public static List<MusicInfo> updateMusicInfosInList(String RequestURL, List<String> musicIds) {
		String idsString = "";
		for (int i = 0; i < musicIds.size(); i++) {
			if(i != musicIds.size()-1){
				idsString += "id="+musicIds.get(i)+"&";
			}else {
				idsString += "id="+musicIds.get(i);
			}
		}
		return updateMusicInfosByIds(RequestURL, idsString);
	}

	public static List<MusicInfo> updateMusicInfosByIds(String RequestURL, String musicIds) {
		RequestURL = RequestURL + musicIds; 
		List<MusicInfo> needUpdateInfos = new ArrayList<MusicInfo>();
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
		InputStream inputStream = null;
		DataOutputStream dos = null;
		try {
			String boxSN = ShellUtils.readSerialNumber();
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
//			dos = new DataOutputStream(conn.getOutputStream());
//			StringBuffer sb = new StringBuffer();
//			sb.append(PREFIX);
//			sb.append(BOUNDARY);
//			sb.append(LINE_END);
//			sb.append("Content-Disposition: form-data; sn=\"" + boxSN + "\"; ids=\"" + musicIds.toString() + "\"" + LINE_END);
//			sb.append(LINE_END);
//			dos.write(sb.toString().getBytes());
//			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
//			dos.write(end_data);
//			dos.flush();
			if(conn.getResponseCode() ==200){
				inputStream = conn.getInputStream();
				if (inputStream != null) {
					needUpdateInfos = SyncMusicRunable.getMusicInfos(inputStream);
				}
			}else {
				LogManager.e(TAG, "update local music sql error!");
			}
		} catch (IOException e) {
			LogManager.e(TAG, "update local music sql error :" +e.toString());
			e.printStackTrace();
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return needUpdateInfos;
	}
}
