package com.smallzhi.airplayService;

import java.security.PublicKey;

import android.provider.SyncStateContract.Constants;

public class AirPlayCommon {
	public final static	int AIRPLAY_SUCESS				= 0;
	public final static	int AIRPLAY_ERROR				= -1;
	
	public final static	int MESSAGE_VOLUME				= 10;	//音量控制消息
	public final static	int MESSAGE_TIME					= 20;	//播放时间进度更新消息
	public final static	int MESSAGE_STATE				= 30;	//播放状态消息
	
	public final static	int STATE_PLAYING				= 31;	//播放状态:播放中
	public final static	int STATE_STOPED					= 32;	//播放状态：停止
	
	//ASS means Assistant
	//以下消息为Airplay模块接收主程序的控制指令，如：按LOG键暂停播放歌曲
	public final static	String ASS_INCREASE_VOLUME			= "IKEY_INCREASE_VOLUME";
	public final static	String ASS_DECREASE_VOLUME			= "IKEY_DECREASE_VOLUME";
	public final static	String ASS_MEDIA_PLAY				= "IKEY_MEDIA_PLAY";
	public final static	String ASS_MEDIA_STOP				= "IKEY_MEDIA_STOP";
	public final static	String ASS_MEDIA_PAUSE				= "IKEY_MEDIA_PAUSE";
	
	//ASS means Assistant. *_2_* ,"2" means "TO"
	//以下消息为Airplay模块发送给主程序的状态通知，
	//如：(1)iPhone设备控制音响播放歌曲，主程序接收到 MEDIA_PLAY_2_ASS ,停止其他播放模块（如：DLNA），停止唤醒
	//   (2)iPhone设备控制音响暂停歌曲，主程序接收到 MEDIA_STOP_2_ASS,开启唤醒
	
	public final static	String MEDIA_PLAY_2_ASS				= "IKEY_MEDIA_PLAY_2_ASS";
	public final static	String MEDIA_STOP_2_ASS				= "IKEY_MEDIA_STOP_2_ASS";
	public final static	String MEDIA_PAUSE_2_ASS				= "IKEY_MEDIA_PAUSE_2_ASS";
	
}
