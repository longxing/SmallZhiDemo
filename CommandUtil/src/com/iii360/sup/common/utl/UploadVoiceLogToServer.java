package com.iii360.sup.common.utl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * 实时上传语音日志到服务器
 * 
 * @author Peter
 * @data 2015年4月10日下午3:30:52
 */
public class UploadVoiceLogToServer extends Thread {
	private static final String Tag = "UploadVoiceLogToServer";
	private static final int TIME_OUT = 10 * 1000000;
	private String voiceString = ""; // 语音文本
	private String otherString = ""; // 其他信息：命令信息
	private long voiceTime = 0L; // 语音识别时间
	private long semanticTime = 0L; // 语义识别时间
	private long commandExecuteTime = 0L; // 命令执行时间
	private String upLoadFileName = ""; // 上传音频文件的文件名
	private SuperBaseContext baseContext = null;
	public static final String DATA_FORMAT_FIRST = "yyyy-MM-dd HH:mm:ss:sss";
	private static String SendVoiceRecongniseResultUrl = " http://unknownlog.360iii.net:28080/unknownlog/log/log_insertLog.action?";

	public UploadVoiceLogToServer(String voiceString, String otherString) {
		this.voiceString = voiceString;
		this.otherString = otherString;
	}

	public UploadVoiceLogToServer(SuperBaseContext baseContext, String voiceString, String otherString) {
		this.baseContext = baseContext;
		this.voiceString = voiceString;
		this.otherString = otherString;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}
		try {
			String sn = ShellUtils.readSerialNumber();
			// 获取语音识别用时，在线语音识别 或者 离线语音识别用时
			int reconginseType = baseContext.getGlobalInteger(SupKeyList.RECONGINSE_ENGINE_TYPE);
			if (reconginseType == 1) {
				voiceTime = baseContext.getGlobalLong(SupKeyList.PKEY_STRING_SPEECH_LOCAL_TIME);
			} else if (reconginseType == 2) {
				voiceTime = baseContext.getGlobalLong(SupKeyList.PKEY_STRING_SPEECH_ONLINE_TIME);
			} else {
				voiceTime = baseContext.getGlobalLong(SupKeyList.PKEY_STRING_SPEECH_ONLINE_TIME_FOR_LONGCLICK);
			}
			if (SupKeyList.SEMANTEME_RECOGNIZER_FINISH > SupKeyList.SEMANTEME_RECOGNIZER_BEGIN && SupKeyList.SEMANTEME_RECOGNIZER_FINISH != 0 && SupKeyList.SEMANTEME_RECOGNIZER_BEGIN != 0) {
				semanticTime = SupKeyList.SEMANTEME_RECOGNIZER_FINISH - SupKeyList.SEMANTEME_RECOGNIZER_BEGIN;
			} else {
				semanticTime = 0;
			}
			if (SupKeyList.COMMAND_START_EXECUTE > SupKeyList.SEMANTEME_RECOGNIZER_FINISH && SupKeyList.COMMAND_START_EXECUTE != 0 && SupKeyList.SEMANTEME_RECOGNIZER_FINISH != 0) {
				commandExecuteTime = SupKeyList.COMMAND_START_EXECUTE - SupKeyList.SEMANTEME_RECOGNIZER_FINISH;
			} else {
				commandExecuteTime = 0;
			}
			String cityName = baseContext.getPrefString("PKEY_ASS_CITY_NAME");
			if (cityName != null && !cityName.equals("")) {
				voiceString = voiceString + "----" + cityName;
			}
			voiceString = URLEncoder.encode(voiceString, "utf-8");
			otherString = URLEncoder.encode(otherString + "命令", "utf-8");
			upLoadFileName = URLEncoder.encode(SupKeyList.UPLOAD_VOICE_FILE_NAME, "utf-8");
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("sn=" + sn);
			sBuilder.append("&words=" + voiceString);
			sBuilder.append("&other=" + otherString);
			sBuilder.append("&t1=" + voiceTime);
			sBuilder.append("&t2=" + semanticTime);
			sBuilder.append("&t3=" + commandExecuteTime);
			sBuilder.append("&file=" + upLoadFileName);
			UploadLog(SendVoiceRecongniseResultUrl, sBuilder.toString());
			resetTimeRecord();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			LogManager.e(e.toString());
		}
	}

	private void UploadLog(String urlStr, String stringContent) {
		urlStr = urlStr + stringContent;
		LogManager.d("UploadVoiceLogToServer", "send voice log to server url=" + urlStr);
		String BOUNDARY = UUID.randomUUID().toString();
		String CONTENT_TYPE = "multipart/form-data";
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(true);
			conn.setConnectTimeout(TIME_OUT);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", "utf-8");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			int res = conn.getResponseCode();
			if (res == 200) {
				LogManager.d("request success res =" + res);
			} else {
				LogManager.e("upload file request error and res =" + res);
			}
		} catch (IOException e) {
			LogManager.e("upload file request error :" + e.toString());
		}
	}

	/**
	 * 重置全局的时间记录
	 */
	private void resetTimeRecord() {
		baseContext.setGlobalLong(SupKeyList.PKEY_STRING_SPEECH_LOCAL_TIME, 0L);
		baseContext.setGlobalLong(SupKeyList.PKEY_STRING_SPEECH_ONLINE_TIME, 0L);
		baseContext.getGlobalLong(SupKeyList.PKEY_STRING_SPEECH_ONLINE_TIME_FOR_LONGCLICK, 0L);
		SupKeyList.SEMANTEME_RECOGNIZER_BEGIN = 0;
		SupKeyList.SEMANTEME_RECOGNIZER_FINISH = 0;
		SupKeyList.COMMAND_START_EXECUTE = 0;
		SupKeyList.UPLOAD_VOICE_FILE_NAME = "";
	}

}
