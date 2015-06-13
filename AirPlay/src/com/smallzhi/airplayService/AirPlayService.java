package com.smallzhi.airplayService;

import android.R.integer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.iii360.sup.common.utl.LogManager;
import com.smallzhi.airplayService.AirPlayCommon;
import com.unc.airplay.NativeAirplay;
import com.unc.airplay.INativeAirplayCb;
import com.unc.airplay.NativeAirplay.PlayCmd;

public class AirPlayService extends Service implements INativeAirplayCb{

	private static final String TAG			= "SmallZhi AirPlayService";
	private 			 int mState				= AirPlayCommon.STATE_STOPED;//当前播放状态
	
	NativeAirplay 	mAirPlayObj = null;
	
	
	AirPlayPlayer	mPlayer		= null;
	private int 		mDeviceID;			//当前控制设备的信息
	private int 		mVolume;				//当前设备播放音量
	private int 		mTrackDuration;		//当前播歌曲总长度
	private int 		mPostion;			//当前播放歌曲位置
		
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		LogManager.i(TAG,"onBind ...");
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		LogManager.i(TAG,"onUnbind ...");
		return super.onUnbind(intent);
	}
	
	private int getState() {
		synchronized (this) {
			return mState;	
		}

	}
	
	private void setState(int state){
		synchronized (this) {
		mState = state;
		}
	}
		
	public int getDeviceID(){
		return mDeviceID;
	}
	
	public int getVolume(){
		return mVolume;
	}
	
	public int getTrackDuration(){
		return mTrackDuration;
	}
	
	
	public int getPostion(){
		return mPostion;
	}
	
	public void setDeviceID(int deviceID){
		mDeviceID = deviceID;
	}
	
	public void setVolume(int volume){
		//set player volume
		mVolume = volume;
		if(null != mPlayer){
			mPlayer.setVolume(volume);
		}
		else {
			LogManager.e(TAG,"setVolume error...");
		}
	}
	
	int AirplayInit(String deviceName){
		int result = AirPlayCommon.AIRPLAY_ERROR;
		
		mPlayer = new AirPlayPlayer();
		if(null == mPlayer)
		{
			LogManager.e(TAG,"creat player error...");
			return result;	
		}	
		
		//注册receiver接受主程序的控制指令，如：按LOG键暂停歌曲播放
		IntentFilter filter = new IntentFilter();
		filter.addAction(AirPlayCommon.ASS_INCREASE_VOLUME);
		filter.addAction(AirPlayCommon.ASS_DECREASE_VOLUME);
		filter.addAction(AirPlayCommon.ASS_MEDIA_PLAY);
		filter.addAction(AirPlayCommon.ASS_MEDIA_PAUSE);
		filter.addAction(AirPlayCommon.ASS_MEDIA_STOP);
		registerReceiver(receiver, filter);
			
		//初始化SDK引擎
		mAirPlayObj = new NativeAirplay(this);
		if(null == mAirPlayObj)
		{
			LogManager.e(TAG,"creat AirPlay SDK Obj error...");
			return result;
		}
		
		//启动SDK引擎
		//This API should add return type
		mAirPlayObj.AirplayStart();
		
		LogManager.e(TAG,"AirplayInit sucess...");
		return result;
	}
	
	int AirplayFinal(){
		int result = AirPlayCommon.AIRPLAY_ERROR;
		if(null != mAirPlayObj)
		{
			mAirPlayObj.AirplayStop();
		}
		unregisterReceiver(receiver);
		LogManager.e(TAG,"AirplayFinal sucess...");
		return result;
	}
		
	//异步处理AirPlay SDK引擎消息通知
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AirPlayCommon.MESSAGE_STATE:
			{
				mState = msg.arg1;//该字段存储播放状态值
				
				if(AirPlayCommon.STATE_PLAYING == mState)
				{
					LogManager.d(TAG,"message MESSAGE_STATE =  STATE_PLAYING");
				}
				else if(AirPlayCommon.STATE_STOPED == mState)
				{
					LogManager.d(TAG,"message MESSAGE_STATE =  STATE_PLAYING");
				}	
				break;
			}
			case AirPlayCommon.MESSAGE_TIME:
			{
				mTrackDuration	= msg.arg1;//该字段存储歌曲总长度
				mPostion			= msg.arg2;//该字段存储播放进度
				LogManager.d(TAG,"message MESSAGE_TIME... mTrackDuration= " +mTrackDuration +" mPostion= " + mPostion );
				break;
			}
			case AirPlayCommon.MESSAGE_VOLUME:
			{
				int volume = msg.arg1;//该字段存储播放音量
				setVolume(volume);
				LogManager.d(TAG,"message MESSAGE_VOLUME...");
				break;
			}	
			default:
			{
				LogManager.e(TAG,"message error...");
				break;
			}

			}
		}
		
	};
	
	
	/*****************************************************/
	/*******initiative*** API 代表由音响端发起的反向控制******/
	/****************************************************/
	
	//
		public int initiativePlay()
		{
			LogManager.e(TAG,"initiativePlay ...");
			int result = AirPlayCommon.AIRPLAY_ERROR;
			result = mAirPlayObj.controlAirplay(PlayCmd.PLAY);
			result = mPlayer.play();
			return result;
		}
		
		public int initiativePause()
		{
			LogManager.e(TAG,"initiativePause ...");
			int result = AirPlayCommon.AIRPLAY_ERROR;
			result = mAirPlayObj.controlAirplay(PlayCmd.PAUSE);
			result = mPlayer.pause();
			return result;
		}
		
		public int initiativeNext()
		{
			LogManager.e(TAG,"initiativeNext ...");
			int result = AirPlayCommon.AIRPLAY_ERROR;
			result = mAirPlayObj.controlAirplay(PlayCmd.NEXT);
			return result;
		}
		
		public int initiativePrev()
		{
			LogManager.e(TAG,"initiativePrev ...");
			int result = AirPlayCommon.AIRPLAY_ERROR;
			result = mAirPlayObj.controlAirplay(PlayCmd.PREV);
			return result;
		}
		
		public int initiativeVolDown()
		{
			LogManager.e(TAG,"initiativeVolDown ...");
			int result = AirPlayCommon.AIRPLAY_ERROR;
			result = mAirPlayObj.controlAirplay(PlayCmd.VOLDOWN);
			return result;
		}
		
		public int initiativeVolUp()
		{
			LogManager.e(TAG,"initiativeVolUp ...");
			int result = AirPlayCommon.AIRPLAY_ERROR;
			result = mAirPlayObj.controlAirplay(PlayCmd.VOLUP);
			return result;
		}

		//处理主程序发送的控制指令：如：按键暂停音乐播放
		BroadcastReceiver receiver = new BroadcastReceiver() {
		
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				int reusult = AirPlayCommon.AIRPLAY_ERROR;
				
				if(AirPlayCommon.ASS_INCREASE_VOLUME.equals(action)){
					reusult = initiativeVolUp();
				}
				else if(AirPlayCommon.ASS_DECREASE_VOLUME.equals(action)){
					reusult = initiativeVolDown();
				}
				else if(AirPlayCommon.ASS_MEDIA_PLAY.equals(action)){
					reusult = initiativePlay();
				}
				else if(AirPlayCommon.ASS_MEDIA_PAUSE.equals(action)){
					reusult = initiativePause();
				}
				else if(AirPlayCommon.ASS_MEDIA_STOP.equals(action)){
					//TODO add stop API
					LogManager.e(TAG,"onReceive stop action");
				}
				else {
					LogManager.e(TAG,"onReceive error action");
				}
			}	
		};
		
		
		/********************************************/
		/****************Airplay SDK 回调接口实现******/
		/********************************************/

		@Override
		public int AirplayCoverUpdate(int size, byte[] data) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int AirplayMetadataUpdate(String song, String artist, String ablum) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int AirplayStatusUpdate(String state) {
			// TODO Auto-generated method stub
			int messg_state = AirPlayCommon.STATE_STOPED;
			if(state.contains("playing"))
			{
				messg_state = AirPlayCommon.STATE_PLAYING;
			}
			else if(state.contains("stoped"))
			{	
				messg_state = AirPlayCommon.STATE_STOPED;
			}
			
			Message msg = new Message();
			msg.arg1 = messg_state;//该字段存储播放状态值
			msg.what = AirPlayCommon.MESSAGE_STATE;//消息类型：播放控制状态
			if(null == mHandler){
				return AirPlayCommon.AIRPLAY_ERROR;
			}
					
			mHandler.sendMessage(msg);
			return AirPlayCommon.AIRPLAY_SUCESS;
		}

		@Override
		public int AirplayTimeUpdate(int postion, int total) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.arg1 = total;//该字段存储歌曲总长度
			msg.arg2 = mPostion;//该字段存储播放进度
			msg.what = AirPlayCommon.MESSAGE_TIME;//消息类型：播放进度
			if(null == mHandler){ 
				return AirPlayCommon.AIRPLAY_ERROR;
			}
					
			mHandler.sendMessage(msg);
			return AirPlayCommon.AIRPLAY_SUCESS;
		
		}

		@Override
		public int AirplayTrackCreate() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int AirplayTrackDestory() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int AirplayTrackWrite(int size, short[] data) {
			// TODO Auto-generated method stub
			//This Api is block ,not need copy data again
			if(null != mPlayer){
				mPlayer.write(size, data);
			}
			return 0;
		}

		@Override
		public int AirplayVolUpdate(int volume) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.arg1 = volume;//该字段存储播放音量
			msg.what = AirPlayCommon.MESSAGE_VOLUME;//消息类型：音量控制消息
			if(null == mHandler){
				return AirPlayCommon.AIRPLAY_ERROR;
			}
					
			mHandler.sendMessage(msg);
			return AirPlayCommon.AIRPLAY_SUCESS;
		}
		
}
