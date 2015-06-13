package com.iii360.base.umeng;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;

/**
 * 自定义事件统计 统计事件的发送策略可以通过后台配置
 * 
 * （1）您需要在程序的入口 Activity 中添加 MobclickAgent.updateOnlineConfig( mContext );
 * 1>定义重启上传 ************************* 2>间隔一段时间上传 （间隔时间90~一天） *************
 * （2）使用普通测试流程，请先在程序入口添加以下代码打开调试模式 MobclickAgent.setDebugMode( true )；
 * 打开调试模式后，您可以在logcat中查看您的数据是否成功发送到友盟服务器
 * ，以及集成过程中的出错原因等，友盟相关log的tag是MobclickAgent。
 * 
 * @author Peter
 * @data 2015年5月5日下午2:18:03
 */

public class UmengUtil {
	/**
	 * logo事件统计
	 */
	public static final String LOGO_SHORTCLICK = "logoClick";
	public static final String LOGO_SHORTCLICK_CONTENT = "logo点击";
	public static final String LOGO_LONGCLICK = "logoLongClick";
	public static final String LOGO_LONGCLICK_CONTENT = "logo长按";
	/**
	 * 红心事件统计
	 */
	public static final String RED_HEART_SHORTCLICK = "redHeartClick";
	public static final String RED_HEART_SHORTCLICK_CONTENT = "红心点击";
	public static final String RED_HEART_LONGCLICK = "redHeartLongClick";
	public static final String RED_HEART_LONGCLICK_CONTENT = "红心长按";
	/**
	 * 垃圾桶事件统计
	 */
	public static final String TRASH_SHORTCLICK = "theTrashClick";
	public static final String TRASH_SHORTCLICK_CONTENT = "垃圾桶点击";
	public static final String TRASH_LONGCLICK = "theTrashLongClick";
	public static final String TRASH_LONGCLICK_CONTENT = "垃圾桶长按";

	/**
	 * 误唤醒
	 */
	public static final String ERROR_WAKEUP = "errorWakeUp";
	public static final String ERROR_WAKEUP_CONTENT = "误唤醒";
	/**
	 * 一句话唤醒
	 */
	public static final String A_WORD_WAKEUP = "aWordWakeUp";
	public static final String A_WORD_WAKEUP_CONTENT = "一句话唤醒";
	/***
	 * 唤醒词唤醒
	 */
	public static final String WAKEUP_WORD_TO_WAKEUP = "wakupWordToWakeUp";
	public static final String WAKEUP_WORD_TO_WAKEUP_CONTENT = "唤醒词唤醒";

	/***
	 * 重置事件
	 */
	public static final String RESET_APP_SYSTEM = "resetAppSystem";
	public static final String RESET_APP_SYSTEM_CONTENT = "重置应用";

	/***
	 * 第三方协议播放
	 */
	public static final String THE_THIRD_PROTOCOL_PLAY = "theThirdProtocolPlay";
	public static final String THE_THIRD_PROTOCOL_PLAY_CONTENT = "第三方协议播放";

	/**
	 * 
	 * @param context
	 *            Context
	 * @param eventId
	 *            事件ID
	 * @param content
	 *            事件内容。 Umeng SDK说明，必须在一个session启动完了之后。才能统计得到事件。
	 */
	public static void onEvent(Context context, String eventId, String content) {
		if (content == null || "".equals(content)) {

			MobclickAgent.onEvent(context, eventId);

		} else {

			MobclickAgent.onEvent(context, eventId, content);

		}
	}

}
