package com.iii.wifi.util;

/**
 * 智能硬件类型
 * 
 * @author river
 * 
 */
public class HardwareUtils {
	/**
	 * 窝窝智能开关
	 */
	public final static boolean SW_WOWO = false;

	public final static boolean SW_ORVIBO = true;

	public final static boolean SW_HUANTENG = true;

	public final static boolean SW_HEZI_PUSH = true;

	public final static boolean SW_YKB_SWITCH = true; // 遥控宝开关

	public final static boolean SW_BROADLINK = true; // 博联开关
	
	
	

	// control command：1->打开*** ;0->关闭***

	// ===============传输数据格式:"JS_"+数据类型+操作类型 start======================
	/**
	 * 没有配置的设备列表数据头标示
	 */
	public final static String ACTION_DEVIDE_LIST_HEAD = "JS_DL";

	/**
	 * 音乐停止播放标记
	 */
	public final static String ACTION_MUSIC_STOP = "JS_MUSIC_STOP";

	/**
	 * 盒子音乐播放状态改变
	 */
	public final static String AKEY_MUSIC_STATUS_CHANGE = "JS_MUSIC_STATUS_CHANGE";

	// ===============传输数据格式:"JS_"+数据类型+操作类型 end=========================

	/**
	 * 调用主程序数据头标示
	 */
	public final static String INVOKE_MAIN_PORGRAM_HEAD = "INVOKE_MAIN_PORGRAM_HEAD---->";

	public final static String MAC_ADRESS_SEPERATER = "_";

	/**
	 * 机器狗
	 */
	public static String DEVICE_MODEL_WIFI_DOG = "DEVICE_MODEL_WIFI_DOG";

	/**
	 * 博联开关
	 */
	public static String DEVICE_MODEL_BL_SP1 = "DEVICE_MODEL_BL_SP1";
	public static String DEVICE_MODEL_BL_SP2 = "DEVICE_MODEL_BL_SP2";
	public static String DEVICE_MODEL_BL_SPMini = "DEVICE_MODEL_BL_SPMini";
	/**
	 * 博联红外控制器
	 */
	public static String DEVICE_MODEL_BL_RM1 = "DEVICE_MODEL_BL_RM1";
	public static String DEVICE_MODEL_BL_RM2 = "DEVICE_MODEL_BL_RM2";
	public static String DEVICE_MODEL_BL_RMHOME = "DEVICE_MODEL_BL_RMHOME";

	/**
	 * 博联空气检测器
	 */
	public static String DEVICE_MODEL_BL_A1 = "DEVICE_MODEL_BL_A1";

	/**
	 * 幻腾灯
	 */
	public static String DEVICE_MODEL_HT_BULBS = "DEVICE_MODEL_HT_BULBS";

	/**
	 * 窝窝照明开关
	 */
	public static String DEVICE_MODEL_WW_TS = "DEVICE_MODEL_WW_TS";
	/**
	 * orvbio 智能遥控
	 */
	public static String DEVICE_MODEL_OB_ALLONE = "DEVICE_MODEL_OB_ALLONE";
	/**
	 * orvbio s20插座
	 */
	public static String DEVICE_MODEL_OB_S20 = "DEVICE_MODEL_OB_S20";

	// orvbio打开/关闭指令电视Command ID
	public static String DEVICE_MODEL_OB_ALLONE_OPEN_TV = "310110";
	public static String DEVICE_MODEL_OB_ALLONE_CLOSE_TV = "310111";
	// orvbio打开/关闭指令空调Command ID
	public static String DEVICE_MODEL_OB_ALLONE_OPEN_AIR = "311011";
	public static String DEVICE_MODEL_OB_ALLONE_CLOSE_AIR = "311004";
	
	

	/**
	 * 遥控宝
	 */
	public static String DEVICE_MODEL_YKB_3S = "DEVICE_MODEL_YKB_3S";

	/**
	 * wifi单品
	 */
	public static final int WIFI_SINGLE_DEVICE = 0;
	/**
	 * 非wifi单品，有采集功能
	 */
	public static final int WIFI_UNSINGLE_DEVICE = 1;

}
