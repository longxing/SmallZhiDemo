package com.iii360.sup.common.utl;

public class SupKeyList {
	/*
	 * App debug support
	 */
	public static boolean IS_TTS_DEBUG = false;
	public static boolean IS_RECORD_RECOGNIZER_TIME = true;
	public static long VOICE_RECOGNIZER_BEGIN = 0;
	public static long VOICE_RECOGNIZER_FINISH = 0;
	
	public static long SEMANTEME_RECOGNIZER_BEGIN = 0;
	public static long SEMANTEME_RECOGNIZER_FINISH = 0;
	public static long COMMAND_START_EXECUTE = 0; //暂时是放歌命令
	public static boolean IS_PLAYER_DEBUG = false;
	public static boolean IS_RECO_DEBUG = false;

	public static boolean IS_WAKEUP_IN_PLAYING_DEBUG = false;
	public static boolean OUTPUT_BUFFER_LOG = false;
	
	public static String UPLOAD_VOICE_FILE_NAME ="未知";
	
	
	public static final String PKEY_STRING_SPEECH_ONLINE_RESULT = "PKEY_STRING_SPEECH_ONLINE_RESULT";

	public static final String PKEY_STRING_SPEECH_ONLINE_FILE_PATH = "PKEY_STRING_SPEECH_ONLINE_FILE_PATH";

	public static final String PKEY_STRING_SPEECH_ONLINE_TIME = "PKEY_STRING_SPEECH_ONLINE_TIME";

	public static final String PKEY_STRING_SPEECH_LOCAL_RESULT = "PKEY_STRING_SPEECH_LOCAL_RESULT";

	public static final String PKEY_STRING_SPEECH_LOCAL_FILE_PATH = "PKEY_STRING_SPEECH_LOCAL_FILE_PATH";

	public static final String PKEY_STRING_SPEECH_LOCAL_TIME = "PKEY_STRING_SPEECH_LOCAL_TIME";
	
	public static final String PKEY_STRING_SPEECH_ONLINE_TIME_FOR_LONGCLICK = "PKEY_STRING_SPEECH_ONLINE_TIME_FOR_LONGCLICK";
	
	
	
	/**
	 * 1  思必驰离线，2 思必驰在线，3 长按思必驰在线 
	 */
	public static String RECONGINSE_ENGINE_TYPE = "RECONGINSE_ENGINE_TYPE"; 
	
	
	/**
	 * 记录当前助手端的是否为新的ip
	 */
	public static final String CURRENT_TCP_PORT_IS_NEW ="CURRENT_TCP_PORT_IS_NEW";
	
	/**
	 * 记录当前盒子IP
	 */
	public static final String CURRENT_TCP_IP ="CURRENT_TCP_IP";
	
	public static String CURRENT_DEVICE_SN ="";

}
