package com.iii360.base.common.utl;

import com.iii360.sup.common.utl.SupKeyList;

public class KeyList extends SupKeyList {
	/*
	 * APP run status
	 */
	public final static int RUN_STATUS_IDLE = 0;
	public final static int RUN_STATUS_BUSY = 1;

	/*
	 * 设备测试
	 */
	public final static String GKEY_HARDWARE = "GKEY_HARDWARE";
	public final static String GKEY_DEVICE_CASE = "GKEY_DEVICE_CASE";
	public static long RESET_BEGIN = 0;// 组合键reset+logo，开启设备测试
	public static boolean NEED_DEVICE_CASE = false;// 设备测试
	public static boolean RESET_BUTTON_CASE = false;// reset测试
	public static boolean HORN_CASE = false;// 喇叭测试
	public static boolean VOICEUP_BUTTON_CASE = false;// 红心键
	public static boolean LOGO_BUTTON_CASE = false;// logo键
	public static boolean VOICEDOWN_BUTTON_CASE = false;// 垃圾桶键
	public static boolean BATTERY_CASE = false;// 电池测试
	public static boolean WAKELIGHT_CASE = false;// 唤醒灯测试
	public static boolean LOGOLIGHT_CASE = false;// logo灯测试
	public static boolean NETLIGHT_CASE = false;// 网络灯测试
	public static boolean WIFI_CASE = false;// 网卡测试
	public static boolean MIC_CASE = false;// 听筒测试
	public static boolean HEADSET_CASE = false;// 耳机孔测试

	/*
	 * APP use status
	 */
	public final static boolean IS_OPEN_ANIMATION = true;
	public final static boolean IS_SHOW_LOG = true;
	public final static int FINAL_INT_NOTIFY_MEDIA_ID = 5;
	public final static int FINAL_INT_NOTIFY_SET_TTS_ID = 14;
	public final static int FINAL_INT_SPEECH_STATUS_END = 1;
	public final static int FINAL_INT_SPEECH_STATUS_ERROR = 2;
	public final static int FINAL_INT_SPEECH_STATUS_INVAILD = -1;
	public final static int FINAL_INT_SPEECH_STATUS_START = 0;
	public final static int SERVICE_ID_CLOSE_FLOAT_BUTTON = 2;
	public final static int SERVICE_ID_SET_RECOGNIZER = 0;
	public final static int SERVICE_ID_SHOW_FLOAT_BUTTON = 1;
	public final static int SERVICE_ID_START_PARER = 3;
	public final static int TTS_PLAYER_TYPE_JT = 0;
	public final static int TTS_PLAYER_TYPE_SHENGDA = 1;
	public final static int TTS_PLAYER_TYPE_USC = 4;
	public final static int TTS_PLAYER_TYPE_USC_NET = 5;
	public final static int TTS_PLAYER_TYPE_XUNFEI = 2;
	public final static int TTS_PLAYER_TYPE_XUNFEI_NET = 3;

	/*
	 * broad cast key
	 */
	public final static String AKEY_AUTO_START_RECOGNISE = "AKEY_AUTO_START_RECOGNISE";
	public final static String AKEY_CHANGE_RECOGNISE_ENGINE = "AKEY_CHANGE_RECOGNISE_ENGINE";
	public final static String AKEY_CHANGE_TTS_ENGINE = "AKEY_CHANGE_TTS_ENGINE";
	public final static String AKEY_COMMAND_CALL = "com.voice.assistant.action.CALL";
	public final static String AKEY_COMMAND_CHAT = "com.voice.assistant.action.CHAT";
	public final static String AKEY_COMMAND_CONFIRM = "com.voice.assistant.action.CONFIRM";
	public final static String AKEY_COMMAND_DELETE_APP = "com.voice.assistant.action.DELETE_APP";
	public final static String AKEY_COMMAND_DOWNLOAD_APP = "com.voice.assistant.action.DOWNLOAD_APP";
	public final static String AKEY_COMMAND_ERROR = "com.voice.assistant.action.ERROR";
	public final static String AKEY_COMMAND_EXTEND_EXIT = "com.voice.assistant.action.EXTEND_EXIT";
	public final static String AKEY_COMMAND_HELP = "com.voice.assistant.action.HELP";
	public final static String AKEY_COMMAND_LOCAL = "com.voice.assistant.action.LOCAL";
	public final static String AKEY_COMMAND_LOCATION_HOT = "com.voice.assistant.action.LOCATION_HOT";
	public final static String AKEY_COMMAND_MEDIA_CONTROL = "com.voice.assistant.action.MEDIA_CONTROL";
	public final static String AKEY_COMMAND_NAVIGATION = "com.voice.assistant.action.NAVIGATION";
	public final static String AKEY_COMMAND_OPEN_APP_AND_WEB = "com.voice.assistant.action.OPEN_APP_AND_WEB";
	public final static String AKEY_COMMAND_PALY_MEDIA = "com.voice.assistant.action.PALY_MEDIA";
	public final static String AKEY_COMMAND_QUERY_WEATHER = "com.voice.assistant.action.QUERY_WEATHER";
	public final static String AKEY_COMMAND_READ_SMS = "com.voice.assistant.action.READ_SMS";
	public final static String AKEY_COMMAND_REMIND = "com.voice.assistant.action.REMIND";
	public final static String AKEY_COMMAND_SEARCH = "com.voice.assistant.action.SEARCH";
	public final static String AKEY_COMMAND_SEND_SMS = "com.voice.assistant.action.SEND_SMS";
	public final static String AKEY_COMMAND_STUDY = "com.voice.assistant.action.STUDY";
	public final static String AKEY_COMMAND_TRANSLATION = "com.voice.assistant.action.TRANSLATION";
	public final static String AKEY_COMMAND_WEIBO = "com.voice.assistant.action.WEIBO";
	public final static String AKEY_DELEY_TIME = "AKEY_DELEY_TIME";
	public final static String AKEY_HANDLE_CLOUD_RECOGNISE = "AKEY_HANDLE_CLOUD_RECOGNISE";
	public final static String AKEY_HANDLE_NEW_AUDIO_COME = "AKEY_HANDLE_NEW_AUDIO_COME";
	public final static String AKEY_HANDLE_RECORD_FAILURE = "AKEY_HANDLE_RECORD_FAILURE";
	public final static String AKEY_HANDLE_WAKE_UP = "AKEY_HANDLE_WAKE_UP";
	public final static String AKEY_HIDDEN_WAKEUP_FLAG = "AKEY_HIDDEN_WAKEUP_FLAG";
	public final static String AKEY_LINECONTROL_START_RECOGNISE = "AKEY_LINECONTROL_START_RECOGNISE";
	public final static String AKEY_MEDIA_ERROR = "com.voice.assistant.MEDIA_ERROR";
	public final static String AKEY_PARSE_INPUT = "com.voice.assistant.action.PARSE_INPUT";
	public final static String AKEY_PARSE_RESULT = "com.voice.assistant.action.PARSE_RESULT";
	public final static String AKEY_PLAYCOMPLATION = "com.voice.assistant.PLAYCOMPLATION";
	public final static String AKEY_PRERARED = "com.voice.assistant.PRERARED";
	public final static String AKEY_PUSH = "com.voice.assistant.PUSH";
	public final static String AKEY_RESET_SCREEN_LIGHTNESS = "AKEY_RESET_SCREEN_LIGHTNESS";
	public final static String AKEY_RESPONE = "com.voice.assistant.action.RESPONE";
	public final static String AKEY_RESTRAT_WAKEUP_SERVICE = "com.iii360.external.wakeup.AnotherWakeupOpeningBroadcastReciever";
	public final static String AKEY_SCREEN_SHOT = "AKEY_SCREEN_SHOT";
	public final static String AKEY_SHOW_ANSWER = "AKEY_SHOW_ANSWER";
	public final static String AKEY_SHOW_USAGE = "AKEY_SHOW_USAGE";
	public final static String AKEY_SHOW_WAKEUP_FLAG = "AKEY_SHOW_WAKEUP_FLAG";
	public final static String AKEY_SMS_STOP_TTS = "AKEY_SMS_STOP_TTS";
	public final static String AKEY_SPEECH = "com.voice.assistant.action.SPEECH";
	public final static String AKEY_SYS_CLOSE_ACTION = "AKEY_SYS_CLOSE_ACTION";

	public final static String DOMAIN_NAME = "http://teach.360iii.net:8080/";
	public final static String DOWNLOAD_FROM_STATE = "DOWNLOAD_FROM_STATE";//

	/*
	 * unkown
	 */
	public final static String EKEY_APP_ID = "EKEY_APP_ID";
	public final static String EKEY_COMMAND_PARAM = "EKEY_COMMAND_PARAM";
	public final static String EKEY_OPEN_URL = "EKEY_OPEN_URL";
	public final static String EKEY_RECOGNISE_RESULT = "EKEY_RECOGNISE_RESULT";
	public final static String EKEY_REPONSE_RESULT = "EKEY_REPONSE_RESULT";
	public final static String EKEY_SERVICE_ID = "EKEY_SERVICE_ID";
	public final static String EKEY_SPEECH_STATUS = "EKEY_SPEECH_STATUS";
	public final static String EKEY_TTS_SHOW_ERROR = "EKEY_TTS_SHOW_ERROR";
	public final static String EKEY_WAKE_UP_SERVECE_ID = "EKEY_WAKE_UP_SERVECE_ID";
	/*
	 * file key
	 */
	public final static String FKEY_MEDIA_INFO_FILE = "/sdcard/VoiceAssistant/mediaInfo";
	public final static String FKEY_MEDIA_DOWN_FILE = "/sdcard/VoiceAssistant/musics.txt";

	/*
	 * globle value key
	 */
	public final static String GKEY_ASSISTANT_CLICK_LINECONTROL_BUTTON = "GKEY_ASSISTANT_CLICK_LINECONTROL_BUTTON";
	public final static String GKEY_BOOL_CAN_RECEIVER_INETENT = "GKEY_BOOL_CAN_RECEIVER_INETENT";
	public final static String GKEY_BOOL_ISFROMFULLSCR_BACK = "GKEY_BOOL_ISFROMFULLSCR_BACK";
	public final static String GKEY_BOOL_RESUME_STATUS = "GKEY_BOOL_RESUME_STATUS";
	public final static String GKEY_BOOL_SHOW_NEWS_KEYWORLD = "GKEY_BOOL_SHOW_NEWS_KEYWORLD";
	public final static String GKEY_INT_PLAY_INDEX = "GKEY_INT_PLAY_INDEX";
	public final static String GKEY_INT_RESUME_POS = "GKEY_INT_RESUME_POS";
	public final static String GKEY_IS_MUSIC_IN_PLAYING = "GKEY_IS_MUSIC_IN_PLAYING";
	public final static String GKEY_IS_MUSIC_PLAYING = "GKEY_IS_MUSIC_PLAYING";
	public final static String GKEY_IS_MUSIC_UPGRADING = "GKEY_IS_MUSIC_UPGRADING";
	public final static String GKEY_IS_NOW_RECOGNING = "GKEY_IS_NOW_RECOGNING";// 识别中
	public final static String GKEY_IS_NOW_BUFF_RECOGNING = "GKEY_IS_NOW_BUFF_RECOGNING";// buff识别中(唤醒成功后）
	public final static String GKEY_IS_NOW_WAKEUP = "GKEY_IS_NOW_WAKEUP";// 唤醒中
	public final static String GKEY_MAP_LOACTION_INFO_CITY = "GKEY_MAP_LOACTION_INFO_CITY";
	public final static String GKEY_OBJ_APPS = "GKEY_OBJ_APPS";
	public final static String GKEY_OBJ_HOME_APP_LIST = "GKEY_OBJ_HOME_APP_LIST";
	public final static String GKEY_OBJ_LAST_MSG = "GKEY_OBJ_LAST_MSG";
	public final static String GKEY_OBJ_LAST_PASER = "GKEY_OBJ_LAST_PASER";
	public final static String GKEY_OBJ_SEARCH_QUESTION_CONTENT = "GKEY_OBJ_SEARCH_QUESTION_CONTENT";
	public final static String GKEY_OBJ_USER_APPS = "GKEY_OBJ_USER_APPS";
	public final static String GKEY_STR_CURRENT_MEDIAINFO = "GKEY_STR_CURRENT_MEDIAINFO";
	public final static String GKEY_STR_CURRENT_SPEAKSEX = "GKEY_STR_CURRENT_SPEAKSEX";
	public final static String GKEY_TTS_CONTORLLER = "GKEY_TTS_CONTORLLER";
	public static final String GKEY_BOOL_CHATMODE = "GKEY_BOOL_CHATMODE";
	public static final String GKEY_LONG_CHATMODE_BEGINTIME = "GKEY_LONG_CHATMODE_BEGINTIME";
	public static final String GKEY_BOOL_CONNECT_CONTROL4 = "GKEY_BOOL_CONNECT_CONTROL4";
	public static final String GKEY_BOOL_IS_CONNECT_WIFIGATE = "GKEY_BOOL_IS_CONNECT_WIFIGATE";
	public static final String GKEY_FORCE_RECOGNISE = "GKEY_FORCE_RECOGNISE";
	public static final String GKEY_IS_WAKEUP_TO_RECOGNISE = "GKEY_IS_WAKEUP_TO_RECOGNISE";

	// chat mode
	public static final String GKEY_BOOL_AUTO_CHATED_MODE = "GKEY_BOOL_AUTO_CHATED_MODE";
	/**
	 * 语音交流场景模式
	 */
	public static final String GKEY_VOICE_COMMUNICATION_MODE = "GKEY_VOICE_COMMUNICATION_MODE";
	/**
	 * 语音交流cause
	 */
	public static final String GKEY_VOICE_COMMUNICATION_CAUSE = "GKEY_VOICE_COMMUNICATION_CAUSE";

	public static final String GKEY_INT_AUTO_CHAT_MODE_NUMBER = "GKEY_INT_AUTO_CHAT_MODE_NUMBER";

	// 语音交互的场景模式
	public static final int DEFAULT_MODE = 0; // 默认模式
	public static final int AUTO_CHATED_MODE = 1; // 聊天模式
	// 场景事件
	public static final int NULL_SCENE_CAUSE = -1; // 空场景事件
	public static final int AWAKEN_WORD_CAUSE = 0; // 唤醒词模式
	public static final int VOICE_COMMAND_CAUSE = 1; // 语音命令(一句话唤醒)模式
	public static final int LONG_CLICK_CAUSE = 2; // 长按事件

	// 强制唤醒是否可以开启
	public final static String GKEY_FORCE_WAKEUP_UNSTOPPABLE = "GKEY_FORCE_WAKEUP_UNSTOPPABLE";
	public final static String GKEY_FORCE_WAKEUP_UNSTARTABLE = "GKEY_FORCE_WAKEUP_UNSTARTABLE";

	// /天气相关配置文件
	public final static String GKEY_CURRENT_CITYCODE_IS_NULL = "GKEY_CURRENT_CITYCODE_IS_NULL";

	public final static String GKEY_CURRENT_CITYCODE_ERROR_COUNT = "GKEY_CURRENT_CITYCODE_ERROR_COUNT";

	/*
	 * Media role
	 */
	public static final String GKEY_MEDIA_ROLE = "GKEY_MEDIA_ROLE";
	public static final int GKEY_MEDIA_ROLE_LOCAL = 0;// 本地曲库
	public static final int GKEY_MEDIA_ROLE_SPEC = 1;// 指定曲目
	public static final int GKEY_MEDIA_ROLE_SCOPE = 2;// 范围曲库

	/*
	 * Media type
	 */
	public static final int GKEY_PLAY_TYPE_MUSIC = 0;
	public static final int GKEY_PLAY_TYPE_STORY = 1;
	public static final int GKEY_PLAY_TYPE_JOKE = 2;
	public static final String GKEY_PLAY_TYPE = "GKEY_PLAY_TYPE";
	public static final String GKEY_IS_PLAY_WELCOME = "GKEY_IS_PLAY_WELCOME";

	/*
	 * light contol
	 */
	public static final String GKEY_WAKEUP_LIGHT_CONTROL = "GKEY_WAKEUP_LIGHT_CONTROL";
	public static final String GKEY_NET_LIGHT_CONTROL = "GKEY_NET_LIGHT_CONTROL";

	/*
	 * float key
	 */
	public final static String IKEY_IS_COME_FLOAT_BUTTON = "IKEY_IS_COME_FLOAT_BUTTON";
	public final static String IKEY_MEDIA_ERROR_EXTRA = "IKEY_MEDIA_ERROR_EXTRA";
	public final static String IKEY_MEDIA_ERROR_WHAT = "IKEY_MEDIA_ERROR_WHAT";
	public final static String IKEY_MEDIA_INFO = "IKEY_MEDIA_INFO";
	public final static String IKEY_MEDIA_IS_CONTINUE = "IKEY_MEDIA_IS_CONTINUE";
	public final static String IKEY_MEDIA_LIST = "IKEY_MEDIA_LIST";

	public final static String MEKEY_STR_ROBOTID = "com.voice.platform.meta.ROBOTID";
	public final static String MEKEY_STR_SERVER_ADDRESS = "com.voice.platform.meta.SERVER_ADDRESS";

	/*
	 * 设置的天气相关配置文件
	 */
	public final static String PKEY_ASS_CITY_CODE = "PKEY_ASS_CITY_CODE"; // 天预报的城市Code
	public final static String PKEY_ASS_CITY_NAME = "PKEY_ASS_CITY_NAME"; // 天气预报的城市Name
	public final static String PKEY_ASS_HAS_VOICE = "PKEY_ASS_HAS_VOICE";

	public final static String PKEY_ASSISTANT_WAKE_UP_ALWAYS_RUN = "PKEY_ASSISTANT_WAKE_UP_ALWAYS_RUN";
	public final static String PKEY_ASSISTANT_WAKE_UP = "PKEY_ASSISTANT_WAKE_UP";
	public final static String PKEY_CUR_PACKAGE = "PKEY_CUR_PACKAGE";
	public final static String PKEY_CURVERION_SET = "PKEY_CURVERION_SET";

	public final static String PKEY_IMEI = "PKEY_IMEI";
	public final static String PKEY_IN_LOGIN = "PKEY_IN_LOGIN";
	public final static String PKEY_IS_AUTO_CHAT = "PKEY_IS_AUTO_CHAT";

	public final static String PKEY_IS_NEED_SCREEN_OFF_OPEN_WAKE_UP = "PKEY_IS_NEED_SCREEN_OFF_OPEN_WAKE_UP";
	public final static String PKEY_IS_NEED_WIFI_THEN_OPEN_WAKE_UP = "PKEY_IS_NEED_WIFI_THEN_OPEN_WAKE_UP";
	public final static String PKEY_IS_WAKE_UP_ACTIVE = "PKEY_IS_WAKE_UP_ACTIVE";
	public final static String PKEY_KEEP_AWAKE = "PKEY_KEEP_AWAKE";
	public final static String PKEY_KEEP_WEATHER_BROADCAST = "PKEY_KEEP_WEATHER_BROADCAST";
	public final static String PKEY_KEEP_WEATHER_TASKID = "PKEY_KEEP_WEATHER_TASKID";

	public final static String PKEY_MUSIC_CIRCLE = "PKEY_MUSIC_CIRCLE";
	public final static String PKEY_MUSIC_EXIT = "PKEY_MUSIC_EXIT";
	public final static String PKEY_MUSIC_NEXT = "PKEY_MUSIC_NEXT";
	public final static String PKEY_MUSIC_PAUSE = "PKEY_MUSIC_PAUSE";
	public final static String PKEY_MUSIC_PRE = "PKEY_MUSIC_PRE";
	public final static String PKEY_MUSIC_RESUME = "PKEY_MUSIC_RESUME";
	public final static String PKEY_NEED_START_IMEDIATELEY_AFTER_RECOGNISE = "PKEY_NEED_START_IMEDIATELEY";

	public final static String PKEY_OPEN_FLOAT_BUTTON = "PKEY_OPEN_FLOAT_BUTTON";
	public final static String PKEY_OPEN_RECOGNISE_SERVICE = "PKEY_OPEN_RECOGNISE_SERVICE";
	public final static String PKEY_PUSH_NOTIFIA_VOICE = "PKEY_PUSH_NOTIFIA_VOICE";
	public final static String PKEY_RECOGNISE_ENGINE = " PKEY_RECOGNISE_ENGINE ";
	public final static String PKEY_SAVE_SELECT_WEEK = "PKEY_SAVE_SELECT_WEEK";
	public final static String PKEY_SAVE_SET_WEATHER_TIME = "PKEY_SAVE_SET_WEATHER_TIME";
	public final static String PKEY_SAVE_SPEECH_WEATHER_INFO = "PKEY_SAVE_SPEECH_WEATHER_INFO";
	public final static String PKEY_SET_VOICESPEED = "PKEY_SET_VOICESPEED";
	public final static String PKEY_SET_WEEK_NUMBER = "PKEY_SET_WEEK_NUMBER";
	public final static String PKEY_TTS_IS_INSTALL_SHENGDA = "PKEY_TTS_IS_INSTALL_SHENGDA";
	public final static String PKEY_TTS_IS_INSTALL_XUNFEI = "PKEY_TTS_IS_INSTALL_XUNFEI";
	public final static String PKEY_TTS_PLAY_CHOOSE = "PKEY_TTS_PLAY_CHOOSE";
	public final static String PKEY_TTS_SHENGDATINGTING_PACKAGENAME = "com.snda.tts.service";
	public final static String PKEY_TTS_WEATHER_VIEW = "PKEY_TTS_WEATHER_VIEW";
	public final static String PKEY_TTS_XUNFEIJIA_PACKAGENAME = "com.iflytek.speechcloud";
	public final static String PKEY_TTS_XUNFEI_PACKAGENAME = "com.iflytek.speechcloud";
	public final static String PKEY_USER_NAME = "PKEY_USER_NAME";

	public final static String PKEY_INCRICE_VOICE_FIRSTCLICK = "PKEY_INCRICE_VOICE_FIRST_CLICK";
	public final static String PKEY_DINCRICE_VOICE_FIRSTCLICK = "PKEY_DINCRICE_VOICE_FIRST_CLICK";

	public final static String PKEY_BUTTON_LINGHT_OPEN_TIME = "PKEY_BUTTON_LINGHT_OPEN_TIME";
	public final static String PKEY_BUTTON_LINGHT_CLOSE_TIME = "PKEY_BUTTON_LINGHT_CLOSE_TIME";
	public final static String PKEY_BUTTON_LINGHT_ON = "PKEY_BUTTON_LINGHT_ON";

	public static final String PKEY_IS_VOICE_SOUND_WAVE = "PKEY_IS_VOICE_SOUND_WAVE"; // connection status flag & wakeup flag
	public static final String PKEY_IS_UNCAUGHT_EXCEPTION = "PKEY_IS_UNCAUGHT_EXCEPTION";
	/*
	 * umeng key
	 */
	public final static String UMKEY_DOWNLOAD_JIETONG = "umkey_jietong";
	public final static String UMKEY_DOWNLOAD_SAMSUNG = "umkey_jiesamsung";
	public final static String UMKEY_DOWNLOAD_TINGTING = "umkey_tingting";
	public final static String UMKEY_DOWNLOAD_XUNFEI = "umkey_xunfei";
	public final static String UMKEY_PUSH_CONFIG = "umkey_push_cofigpath";
	public final static String UMKEY_SET_UPDATE = "UMKEY_SET_UPDATE";

	public static final int TASKKEY_WEATHER_REPORT = Integer.MAX_VALUE - 1;
	public static final int TASKKEY_MUSIC_UPDATE = Integer.MAX_VALUE - 2;
	public static final int TASKKEY_LIGHT_AUTOON = Integer.MAX_VALUE - 3;
	public static final int TASKKEY_LIGHT_AUTOOFF = Integer.MAX_VALUE - 4;
	public static final int TASKKEY_UPGRADE = Integer.MAX_VALUE - 5;

	// 上传录音文件到服务器
	public static final String STRING_UPLOAD_RECORD_TO_SERVER = "STRING_UPLOAD_RECORD_TO_SERVER";
	public static final int TASKKEY_UPLOAD_RECORD_TO_SERVER = Integer.MAX_VALUE - 6;
	public static final String STRING_WAKEUP_WORD = "STRING_WAKEUP_WORD";

	/**************************** 音乐相关配置文件 ************************************/
	public static final String CURRENT_LOCAL_MUSIC_ID = "CURRENT_LOCAL_MUSIC_ID"; // 播放本地当前音乐ID
	public static final String CURRENT_PLAY_MUSIC_NAME = "CURRENT_PLAY_MUSIC_NAME";// 当前播放音乐名称

	// 控制音量渐变
	/**
	 * 控制音量渐变正在运行
	 */
	public static final String PKEY_CONTROL_VOLUME_FLAG = "PKEY_CONTROL_VOLUME_FLAG";
	/**
	 * 控制音量渐变的开关
	 */
	public static final String PKEY_CONTROL_VOLUME_SWITCH = "PKEY_CONTROL_VOLUME_SWITCH";

	/**
	 * 控制音量渐变的时候的当前音量值
	 */
	public static final String PKEY_CONTROL_VOLUME_VALUE = "PKEY_CONTROL_VOLUME_VALUE";

	/**
	 * LOGO键是否按下
	 */
	public static final String PKEY_STRING_SPEECH_LOGO_IS_START = "PKEY_STRING_SPEECH_LOGO_IS_START";

	/**
	 * 日期格式化类型
	 */
	public static final String DATA_FORMAT_FIRST = "yyyyMMddHHmmsssss";
	/**
	 * 下载的本地音乐的数量
	 */
	public final static String LOCAL_MUSIC_LOAD_COUNT = "-1";

	/**
	 * 唤醒后开始识别
	 */
	public final static String WAKEUP_IS_CALLED_START = "WAKEUP_IS_CALLED_START";

	/**
	 * 日志文件创建日期
	 */
	public final static String NEED_TO_SERVER_LOG_CREATE_DATE = "NEED_TO_SERVER_LOG_CREATE_DATE";

	/**
	 * 记录语音识别结果
	 */
	public static String RECOR__VOICE_RECONGINISE_RESULT = "RECOR__VOICE_RECONGINISE_RESULT";

	/**
	 * 记录家电配置的数量
	 */
	public static String HOUSE_MECHINE_SETTING_COUNT = "HOUSE_MECHINE_SETTING_COUNT";

}
