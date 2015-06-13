package com.iii.wifi.util;

import com.iii360.sup.common.utl.MessageQueue;
import com.iii360.sup.common.utl.net.TcpClient;
import com.iii360.sup.common.utl.net.UdpClient;
import com.voice.assistant.main.newmusic.MusicData;

public class KeyList {

	public static final boolean DEVELOP_MODE = false;

	public static final String PKEY_WIFINAME = "PKEY_WIFINAME";
	public static final String PKEY_WIFIPASSWD = "PKEY_WIFIPASSWD";
	public static final String PKEY_WIFICIPHERTYPE = "PKEY_WIFICIPHERTYPE";

	public static final String PKEY_WEATHER_STATUS = "PKEY_WEATHER_STATUS";
	public static final String PKEY_ASS_CITY_NAME = "PKEY_ASS_CITY_NAME"; // 天气预报的城市Name

	public static final String PKEY_WEATHER_TIME = "PKEY_WEATHER_TIME";
	public static final String PKEY_WEATHER_REPORT_CITYNAME = "PKEY_WEATHER_REPORT_CITYNAME";
	public static final String PKEY_TTS_TYPE = "PKEY_TTS_TYPE";
	public static final String PKEY_LOCATION = "PKEY_LOCATION";

	public static final String PKEY_LED_TIME = "PKEY_LED_TIME";
	public static final String PKEY_LED_STATUS = "PKEY_LED_STATUS";
	public static final String PKEY_BATTERY_LEVEL = "PKEY_BATTERY_LEVEL";
	public static final String PKEY_HUANTENG_USERNAME = "PKEY_HUANTENG_USERNAME";
	public static final String PKEY_HUANTENG_PASSWORD = "PKEY_HUANTENG_PASSWORD";
	public static final String PKEY_HUANTENG_ADD_TIME = "PKEY_HUANTENG_ADD_TIME";
	public static final String PKEY_HUANTENG_ACCOUNT_CHANGE_ACTION = "PKEY_HUANTENG_ACCOUNT_CHANGE_ACTION";
	public static final String PKEY_SEND_BROADCAST_NETCHECK = "PKEY_SEND_BROADCAST_NETCHECK";

	public static final String AKEY_STOP_SOUND_WAVE = "wifiserver.action.stop.soundwave";
	public static final String AKEY_RESTORE_SOUND_WAVE = "wifiserver.action.restore.soundwave";

	public static final String PKEY_APNAME = DEVELOP_MODE ? "hezi_develop_ap" : "smallzhi_ap";
	public static final String PKEY_APPASSWD = "12345678";

	public static final String PKEY_WIFI_IP_TAG = "ip::";
	public static final String PKEY_WIFI_END_TAG = "end::";
	public static final String PKEY_FIND_DEVICE_TAG = "finddevice::";
	public static final String PKEY_REMOVE_DEVICE_TAG = "removedevice::";

	public static final String GKEY_WIFI_ENABLE_DOG_FOUND = "GKEY_WIFI_ENABLE_DOG_FOUND";
	public static final String GKEY_WIFI_DOG_FOUNDED = "GKEY_WIFI_DOG_FOUNDED";

	public static final String[] WIFI_STATUS_INFO = { "已连接", "未连接", "未登陆", "不在范围内" };
	public static final String[] WIFI_PASSWD_TYPE = { "WPA", "WEP", "NONE" };

	// public static final int TCP_DEFAULT_PORT = 5000;
	// public static final int UDP_DEFAULT_PORT = 6000;

	public static final String PKEY_TCP_PORT = "PKEY_TCP_PORT";
	public static final String PKEY_UDP_PORT = "PKEY_UDP_PORT";
	public static final String CURRENT_TCP_PORT_IS_NEW = "CURRENT_TCP_PORT_IS_NEW";

	public static final int DOG_UDP_PORT = 48899;

	public static final int NORMAL_DELAY_TIME = 10000;
	public static final int LONG_DELAY_TIME = 20000;
	public static final int SHORT_DELAY_TIME = 10000;

	public final static int HOST_CONNECT_SUCESS = 1;
	public final static int HOST_CONNECT_FAIL = 2;
	public final static int AP_CREAT_SUCESS = 3;
	public final static int AP_CREAT_FAIL = 4;
	public final static int GET_UDP_INFO = 5;
	public final static int NEED_FIND_SERVER = 6;
	public final static int TCP_CONNECT_SUCESS = 7;
	public final static int TCP_CONNECT_FAIL = 8;
	public final static int DOG_SET_OVER = 9;

	public final static int START_CHECK_NET_CONNECT_STATUS = 10;

	public static String LOCAL_IP;

	public static TcpClient TCP_CLIENT;
	public static UdpClient UDP_CLIENT;
	public static UdpClient MUTI_UDP_CLIENT;
	public static WifiHelp WIFI_HELP;

	public static TTSUtil TTSUtil;
	// public static String TEMP_DOGMAC;
	public static RemindUtil REMIND_UTIL;

	public static MusicData sMusicData;

	public static MessageQueue messageQueue;
	/**
	 * device字段分隔符、
	 */
	public final static String SEPARATOR = "、";
	/**
	 * 分隔符||
	 */
	public final static String SEPARATOR_ACTION = "||";
	/**
	 * 分隔符，正则匹配
	 */
	public final static String SEPARATOR_ACTION_SUBLIT = "\\|\\|";
	/**
	 * "打开", "关闭", "开/关"
	 */
	public final static String[] OPER_DEVICE_ARRAY = { "打开", "关闭", "开/关" };
	/**
	 * 删除一个设备
	 */
	public final static String IKEY_DELETE_DEVICE = "IKEY_DELETE_DEVICE";
	/**
	 * 删除一个控制
	 */
	public final static String IKEY_DELETE_CONTROL = "IKEY_DELETE_DEVICE";
	/**
	 * 新设备列表Gson数据
	 */
	public final static String IKEY_NEW_DEVICE_GSON_LIST = "IKEY_NEW_DEVICE_GSON_LIST";

	public static final String ACTION_START_CONFIG_THIRDPART = "com.iii.wifiserver.START_CONFIG_THIRDPART";
	public static final String ACTION_END_CONFIG_THIRDPART = "com.iii.wifiserver.END_CONFIG_THIRDPART";

	public static final String AKEY_MUSIC_PLAY = "com.voice.assistant.main.GOOD_MUSIC_BEGIN";
	public static final String AKEY_MUSIC_STOP = "com.voice.assistant.main.GOOD_MUSIC_END";
	public static final String AKEY_COMMAND_CHANGE = "com.iii.wifiserver.receiver.COMMAND_CHANGE";
	// 盒子歌曲变化广播
	public static final String AKEY_HEZI_MUSIC_CHANGE = "com.iii.wifiserver.receiver.MusicChangeReceiver.control";

	// 开机启动的广播
	public static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	/**
	 * [playNext|playPre|pause|resume|start]
	 */
	public static final String AKEY_HEZI_MUSIC_TYPE = "type";
	/**
	 * 为歌曲json的字符串，目前仍缺少时长部分（播放进度/总时长）
	 */
	public static final String AKEY_HEZI_MUSIC_DATA = "data";

	public static final String PKEY_WELCOME_TAG = "PKEY_WELCOME_TAG";

	public static final String PKEY_ORVIBO_START_TIME = "PKEY_ORVIBO_START_TIME";

	public static final String IKEY_LAST_ORVIBO_NUMBER = "IKEY_LAST_ORVIBO_NUMBER";
	public static final String IKEY_LAST_BROADLINK_NUMBER = "IKEY_LAST_BROADLINK_NUMBER";
	public static final String IKEY_LAST_HUANTENG_NUMBER = "IKEY_LAST_HUANTENG_NUMBER";
	/**
	 * new device 窝窝
	 */
	public static final String IKEY_LAST_WOWO_NUMBER = "IKEY_LAST_WOWO_NUMBER";

	/**
	 * 添加操作时间点
	 */
	public static final String SEND_WEATHER_REPORT_TIME = "0";
	public static final String SEND_LED_SLEEPTIME_TIME = "0";

	/**
	 * 记录家电配置的数量
	 */
	public static String HOUSE_MECHINE_SETTING_COUNT = "HOUSE_MECHINE_SETTING_COUNT";

}
