package com.iii360.box.util;

import com.iii.wifi.dao.info.BoxModeEnum;

public class KeyList {

	public static final boolean DEVELOP_MODE = false;

	public final static String PHONE_BRAND_XIAOMI = "Xiaomi";

	/**
	 * 没有配置的设备列表数据头标示
	 */
	public final static String ACTION_DEVIDE_LIST_HEAD = "JS_DL";

	/**
	 * 音乐停止播放标记
	 */
	public final static String ACTION_MUSIC_STOP = "JS_MUSIC_STOP";

	/**
	 * 顺序执行才行：
	 * 
	 * 1.手机连接到盒子hezi_ap网络 2.Tcp连接，设置盒子网络 3.手机和盒子在同一网络下，Udp监听接收盒子发来的IP地址
	 * 
	 */

	// public final static int TCP_DEFAULT_PORT = 5678;
	// public final static int UDP_DEFAULT_PORT = 6789;

	public final static String PEKY_TCP_PORT = "PEKY_TCP_PORT";
	public final static String PEKY_UDP_PORT = "PEKY_UDP_PORT";

	public final static String TCP_REQUEST_IP = "192.168.43.1";

	public final static String BOX_WIFI_SSID = DEVELOP_MODE ? "hezi_develop_ap" : "smallzhi_ap";
	public final static String BOX_WIFI_PASSWORD = "12345678";

	public final static String DOG_WIFI_SSID = "HF-A11x_AP";

	public final static String GKEY_DOG = "机器狗";
	public final static String GKEY_WIFI_SINGLE = "wifi单品";

	/**
	 * wifi单品
	 */
	public static final int WIFI_SINGLE_DEVICE = 0;
	/**
	 * 非wifi单品，有采集功能
	 */
	public static final int WIFI_UNSINGLE_DEVICE = 1;

	/**
	 * 发现新设备
	 */
	public static final String PKEY_FIND_DEVICE_TAG = "finddevice::";

	/**
	 * 设备下线了
	 */
	public static final String PKEY_REMOVE_DEVICE_TAG = "removedevice::";

	/**
	 * 设备变更广播
	 */
	public final static String AKEY_CHECK_DEVICE_BRODCAST = "AKEY_CHECK_DEVICE_BRODCAST";

	/**
	 * 状态栏发现新设备开关
	 */
	public final static String IKEY_PUSH_NEW_DEVICE_SWTICH = "IKEY_PUSH_NEW_DEVICE_SWTICH";

	/**
	 * 新设备列表Gson数据
	 */
	public final static String IKEY_NEW_DEVICE_GSON_LIST = "IKEY_NEW_DEVICE_GSON_LIST";
	/**
	 * 新设备列表数据
	 */
	public final static String IKEY_NEW_DEVICE_LIST = "IKEY_NEW_DEVICE_LIST";

	public final static String IKEY_SHOW_NEW_DEVICE_MAC = "IKEY_SHOW_NEW_DEVICE_MAC";

	/**
	 * 显示主界面底部组件
	 */
	public final static String IKEY_SHOW_MAIN_BOTTOM = "IKEY_SHOW_MAIN_BOTTOM";

	/**
	 * 关闭状态栏新配件提醒广播
	 */
	public final static String AKEY_CANCEL_NEW_DEVICE_NOTIFICATION = "AKEY_CANCEL_NEW_DEVICE_NOTIFICATION";
	/**
	 * 发现新设备列表
	 */
	public final static String AKEY_NEW_DEVICE_LIST_ACTION = "AKEY_NEW_DEVICE_LIST_ACTION";

	/**
	 * device字段分隔符
	 */
	public final static String SEPARATOR = "、";

	/**
	 * action字段分隔符
	 */
	public final static String SEPARATOR_ACTION = "||";

	public final static String SEPARATOR_ACTION_SUBLIT = "\\|\\|";

	public final static String[] GKEY_VOICE_MAN_ARRAY = { "普通话:晓燕(女)", "普通话:晓峰(男)", "普通话:楠楠(女童)", "普通话:晓婧(女)", "粤语:晓美(女)", "东北话:小倩(女)" };
	public final static String[] GKEY_VOICE_MAN_ARRAY_INDEX = { "0", "1", "2", "3", "4", "5" };

	public final static String[] GKEY_HOLIDAY_ARRAY = { "一次", "每天", "工作日", "周末" };

	public final static String[] GKEY_DEVICE_ARRAY = { "电视机", "空调", "冰箱", "洗衣机", "电风扇", "窗帘", "投影仪", "门", "灯", "顶灯", "射灯", "夜灯", "水晶灯", "调光灯", "壁灯", "灯带", "筒灯", "吊灯", "背景灯", "吸顶灯", "地暖", "电热毯",
			"热水器", "扫地机器人", "电饭煲", "马桶", "台灯", "机顶盒", "饮水机", "微波炉", "加湿香薰器", "暖气", "抽湿机", "空气净化器", "路由器", "电脑", "鱼缸", "电脑音箱", "机顶盒", "电视盒子", "音响", "烤箱", "摄像头", "游戏机" };

	public final static String[] GKEY_ROOM_NAME_ARRAY = { "客厅", "卧室", "主卧", "客卧", "次卧", "书房", "主卫", "餐厅", "厨房", "次卫", "厕所", "南阳台", "北阳台", "过道", "阳台", "客厅阳台", "大厅", "前厅", "走廊", "会议室", "洗手台", "客卧一",
			"客卧二", "客卧三" };

	public final static String[] GKEY_OPERATION_DEVICE_ARRAY = { "打开", "关闭", "开/关" };

	public final static String[] GKEY_USERINFO_SEX_ARRAY = { "男", "女" };
	public final static String GKEY_USERINFO_BIRTH = "1985-01-01";
	public final static String[] GKEY_USERINFO_EDU_ARRAY = { "大专", "本科", "硕士", "博士", "其他" };
	public final static String[] GKEY_USERINFO_MARRIAGE_ARRAY = { "保密", "已婚", "未婚" };
	public final static String[] GKEY_USERINFO_CHILDREN_ARRAY = { "无", "有" };
	public final static String[] GKEY_USERINFO_BOOLD_ARRAY = { "未知", "A", "B", "AB", "O" };
	public final static String[] GKEY_USERINFO_STAR_SIGN_ARRAY = { "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座" };

	/**
	 * 各种数据模式类型
	 */
	public final static BoxModeEnum[] GKEY_MODE_ARRAY = { BoxModeEnum.MODE_GO_HOME, BoxModeEnum.MODE_LEAVE_HOME, BoxModeEnum.MODE_GET_UP, BoxModeEnum.MODE_SLEEP, BoxModeEnum.MODE_BREAKFAST,
			BoxModeEnum.MODE_LUNCH, BoxModeEnum.MODE_DINNER };
	public final static String IKEY_BOXMODE_ENUM = "IKEY_BOXMODE_ENUM";
	public final static String IKEY_BOXMODE_DETAIL_DATA = "IKEY_BOXMODE_DETAIL_DATA";

	// 存储模式的格式为：MODE_GO_HOME 1||2||3 模式名称+控制设备ID
	public final static String PKEY_MODE_GO_HOME = "MODE_GO_HOME";
	public final static String PKEY_MODE_LEAVE_HOME = "MODE_LEAVE_HOME";
	public final static String PKEY_MODE_GET_UP = "MODE_GET_UP";
	public final static String PKEY_MODE_SLEEP = "MODE_SLEEP";
	public final static String PKEY_MODE_BREAKFAST = "MODE_BREAKFAST";
	public final static String PKEY_MODE_LUNCH = "MODE_LUNCH";
	public final static String PKEY_MODE_DINNER = "MODE_DINNER";

	public final static String GKEY_SET = "已设置";
	public final static String GKEY_UNSET = "未设置";

	// public final static String COLLECT_COMMAND_DEVICE_ID = "-1";

	/**
	 * 盒子IP地址
	 */
	public final static String GKEY_BOX_IP_ADDRESS = "GKEY_BOX_IP_ADDRESS";
	/**
	 * 网络wifi的SSID
	 */
	public final static String GKEY_WIFI_SSID = "GKEY_WIFI_SSID";
	/**
	 * 网络wifi的密码
	 */
	public final static String GKEY_WIFI_PASSWORD = "GKEY_WIFI_PASSWORD";

	/**
	 * LED灯控制时间段开关
	 */
	public final static String GKEY_LED_SWITCH = "GKEY_LED_SWITCH";
	public final static String GKEY_LED_START_TIME = "GKEY_LED_START_TIME";
	public final static String GKEY_LED_END_TIME = "GKEY_LED_END_TIME";

	public final static String GKEY_WEATHER_SWITCH = "GKEY_WEATHER_SWITCH";
	public final static String GKEY_WEATHER_HOLIDAY = "GKEY_WEATHER_HOLIDAY";
	public final static String GKEY_WEATHER_TIME = "GKEY_WEATHER_TIME";

	public final static String GKEY_VOICE_MAN = "GKEY_VOICE_MAN";
	public final static String GKEY_VOICE_MAN_INDEX = "GKEY_VOICE_MAN_INDEX";

	public final static String IKEY_ROOM_ID = "IKEY_ROOM_ID";
	public final static String IKEY_ROOM_NAME = "IKEY_ROOM_NAME";
	public final static String IKEY_DEVICE_ID = "IKEY_DEVICE_ID";
	public final static String IKEY_DEVICE_NAME = "IKEY_DEVICE_NAME";
	public final static String IKEY_DEVICE_IS_STUDY = "IKEY_DEVICE_IS_STUDY";
	public final static String IKEY_FATTING_NAME = "IKEY_FATTING_NAME";
	public final static String IKEY_EXISTS_ROOM_NAME = "IKEY_EXISTS_ROOM_NAME";
	public final static String IKEY_EXISTS_ROOM_IDS = "IKEY_EXISTS_ROOM_IDS";
	/**
	 * 设备类型
	 */
	public final static String IKEY_DEVICE_MODEL = "IKEY_DEVICE_MODEL";

	public final static String IKEY_DOG_COLLECT_AIR_COMMAND = "IKEY_DOG_COLLECT_AIR_COMMAND";
	public final static String IKEY_ACTION = "IKEY_ACTION";

	// edit
	public final static String IKEY_EDIT_DEVICE = "IKEY_EDIT_DEVICE";

	// DetailDeviceInfo
	public final static String IKEY_DETAIL_DEVICE_INFO_BUNDLE = "IKEY_DETAIL_DEVICE_INFO_BUNDLE";
	public final static String IKEY_DEVICEINFO_BEAN = "IKEY_DEVICEINFO_BEAN";
	public final static String IKEY_DEVICE_CONTROL_LIST = "IKEY_DEVICE_CONTROL_LIST";
	// WifiControlInfo
	public final static String IKEY_CONTROLINFO_BEAN = "IKEY_CONTROLINFO_BEAN";
	public final static String IKEY_WIFI_SSID = "IKEY_WIFI_SSID";

	public final static String IKEY_MAP_NEW_DEVICE = "IKEY_MAP_NEW_DEVICE";
	public final static String IKEY_NEW_DEVICE_FITTING = "IKEY_NEW_DEVICE_FITTING";
	public final static String IKEY_NEW_DEVICE_MAC = "IKEY_NEW_DEVICE_MAC";
	public final static String IKEY_DEVICE_UPDATE = "IKEY_DEVICE_UPDATE";
	public final static String IKEY_WIFIDEVICEINFO_ENTITY = "IKEY_WIFIDEVICEINFO_ENTITY";

	public final static String PKEY_ROOM_NAME = "PKEY_ROOM_NAME";

	public final static String PKEY_INPUT_WIFI_SSID = "PKEY_INPUT_WIFI_SSID";
	public final static String PKEY_INPUT_WIFI_PASSWORD = "PKEY_INPUT_WIFI_PASSWORD";

	public final static String AKEY_WIFI_CHNAGE = "AKEY_WIFI_CHNAGE";
	/**
	 * 检测到UDP信号
	 */
	public final static String AKEY_UDP_BRODCAST = "AKEY_UDP_BRODCAST";

	public final static String AKEY_UPDATE_EXPANDABLE_LIST = "AKEY_UPDATE_EXPANDABLE_LIST";
	public final static String AKEY_GET_REMIND_DATA = "AKEY_GET_REMIND_DATA";
	public final static String IKEY_ELIST_GROUP_POSITION = "IKEY_ELIST_GROUP_POSITION";
	public final static String IKEY_ELIST_CHILD_POSITION = "IKEY_ELIST_CHILD_POSITION";

	public final static String IKEY_BY_WIFI_SWITCH_WIFI = "IKEY_BY_WIFI_SWITCH_WIFI";

	public final static String PKEY_SAVE_MYTAG_LIST = "PKEY_SAVE_MYTAG_LIST";

	/**
	 * 是否提交了信息
	 */
	public final static String PKEY_IS_COMMIT_USER_INFO = "PKEY_IS_COMMIT_USER_INFO";

	public final static String PKEY_BOX_CURRENT_VOLUME = "PKEY_BOX_CURRENT_VOLUME";
	public final static String PKEY_BOX_MAX_VOLUME = "PKEY_BOX_MAX_VOLUME";
	public final static String PKEY_SINGLE_SELECT_TYPE = "PKEY_SINGLE_SELECT_TYPE";

	/**
	 * 音乐列表选择选项key
	 */
	public final static String PKEY_SELECT_MUSIC_ID = "PKEY_SELECT_MUSIC_ID";

	public final static String PKEY_SELECT_MUSIC_TIME = "PKEY_SELECT_MUSIC_TIME";

	public final static String PKEY_UNCONFIG_DEVICE_TIME = "PKEY_UNCONFIG_DEVICE_TIME";

	// terry start
	/**
	 * 安装以后是不是首次进入程序
	 */
	public final static String PKEY_HAVE_ENTER_RECODE = "PKEY_HAVE_ENTER_RECODE";
	
	public final static String PKEY_UNCONFIG_FITTING_LIST = "PKEY_UNCONFIG_FITTING_LIST";
	public final static String PKEY_UNCONFIG_LIST = "PKEY_UNCONFIG_LIST";

	/**
	 * 引导页key值
	 */
	public final static String PKEY_GUIDE_VIEW_RESID = "PKEY_GUIDE_VIEW_RESID";
	// terry end
	/**
	 * 盒子离线时发广播
	 */
	public static final String ACTION_BOX_OUT_OF_LINE = "com.iii360.box.ACTION_BOX_OUT_OF_LINE";

	/**
	 * 音乐状态发生变化的udp key
	 */
	public final static String AKEY_MUSIC_STATUS_CHANGE = "JS_MUSIC_STATUS_CHANGE";
	public final static String ACTION_MUSIC_STATUS_CHANGE = "com.iii360.box.JS_MUSIC_STATUS_CHANGE";

	public static final String KEY_CONNECTING_AP = "KEY_CONNECTING_AP";
	public static final String KEY_DEVICE_IP = "KEY_DEVICE_IP";

	/**
	 * umeng appkey
	 */
	public static final String KEY_UMENG_APP_KEY = "542263b2fd98c5855b02c0dc";
	/**
	 * 
	 */
	public static final String KEY_TEST_RECODE_STATUS = "KEY_TEST_RECODE_STATUS";
	/***
	 * 有没有配置过tv
	 */
	public final static String KEY_IS_TV_CONFIG = "KEY_IS_TV_CONFIG";
	public final static String KEY_XIMALAYA_BASEURL = "http://3rd.ximalaya.com/";
	public final static String KEY_XIMALAYA_TAG_NAME = "KEY_XIMALAYA_TAG_NAME";
	public final static String KEY_XIMALAYA_ALBUM_NAME = "KEY_XIMALAYA_ALBUM_NAME";
	public final static String KEY_MUSIC_SEARCH_KEY_EXTRA = "KEY_MUSIC_SEARCH_KEY_EXTRA";
	public final static String KEY_HUANTENG_BEAN_KEY_EXTRA = "KEY_HUANTENG_BEAN_KEY_EXTRA";
	public final static String KEY_ISLOCALMUSIC_EXTRA_BOOLEAN = "KEY_ISLOCALMUSIC_EXTRA_BOOLEAN";
	/****
	 * 判断兼容性的标准
	 */
	public final static String KEY_HARDVERSION252 = "2.5.1.0000";
	public final static String KEY_HARDVERSION260 = "2.5.2.0000";
	public final static String KEY_HARDVERSION270 = "2.6.0.0000";
	public final static String PKEY_REQUEST_HARDVERSION = "PKEY_REQUEST_HARDVERSION";
	public final static String KEY_HARDVERSION_EXTRA_STRING = "KEY_HARDVERSION_EXTRA_STRING";
	
	
	public final static String PKEY_BOOLEAN_APP_START_NO_WIFI = "PKEY_BOOLEAN_APP_START_NO_WIFI"; 
}
