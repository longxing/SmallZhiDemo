package com.smallzhi.push.util;

import java.util.Date;

/**
 * @description : TODO
 * @author : Tart
 * @date : 2014-11-17 下午6:48:36
 * @version :  1.0
 */
public interface PushConstant {
	String HEART_BEAT = "heart";
	//TYPE
	String TYPE_REQUEST = "req";
	String TYPE_RESPONSE = "resp";
	String TYPE_COMMAND = "cmd";
	String VERSION_HARDWARE = "hardware";
	String VERSION_SOFTWARE = "software";
	//ACTION
	String ACTION_HEART_BEAT = "heart";
	String ACTION_BIND = "bind";
	String ACTION_TTS = "tts";
	String ACTION_MUSIC = "music";
	String ACTION_LOG ="log";
	//STATUS
	String STATUS_ERROR = "error";
	String STATUS_SUCCESS = "success";
	//CONTENT
	String CONTENT_PLAY ="paly";
	String CONTENT_PAUSE ="pause";
	//日期格式化  derby使用
	String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	String STATUS_INVALID = "invalid";
	String STATUS_VALID = "valid";
	//session传输超时 5秒钟
	long SESSION_WRITE_TIMEOUT = 5;
	
	String EXCEPTION_KEY = "exception";
	String MESSAGE_KEY = "msg";
	String CONNECT_STATUS = "conn";
	long CONNECT_FAIL_TIME = 30*1000;//连接失败后过多长时间重连

	long TIME_OUT = 300*1000;//超时5分钟
	int IDLE_TIME = 60;//心跳间隔 60s
	
	//广播设备标识周期 单位毫秒
	long BROADCAST_PERIOD = 10*1000;//5
	String BROADCAST_IP = "224.0.0.1";
	int BROADCAST_PORT = 9090;
	//服务监控时间
	//long MONITOR_PUSH_SERVER_PERIOD = 60*1000;//每60s监控一次
	//long MONITOR_PUSH_SERVER_DELAY = 60*1000;//延迟时间60s
	long MONITOR_PUSH_SERVER_PERIOD = 300*1000;//每5分钟监控一次
	long MONITOR_PUSH_SERVER_DELAY = 60*1000;//延迟时间60s
	Object PUSH_SERVER_NAME = "com.smallzhi.push.manager.PushService";
}
