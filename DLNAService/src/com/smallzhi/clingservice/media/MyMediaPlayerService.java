package com.smallzhi.clingservice.media;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.UUID;

import org.fourthline.cling.model.ServiceManager;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportState;

import android.R.string;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.preference.PreferenceManager;
import android.util.Log;

import com.iii360.sup.common.utl.LogManager;
import com.smallzhi.clingservice.dlna.service.AndroidUpnpInitService;
import com.smallzhi.clingservice.dlna.service.MyAVTransportService;
import com.smallzhi.clingservice.dlna.service.MyRendererControlService;
import com.smallzhi.clingservice.util.BasePreferences;
import com.smallzhi.clingservice.util.Constants;
import com.smallzhi.clingservice.util.VolumeCotroller;
import com.voice.assistant.main.music.MediaPlayerService;
import com.voice.assistant.main.music.MediaPlayerService.OnMusicPreparedListener;

import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

/**
 * 
 * @author Hank
 * 
 */
public class MyMediaPlayerService implements IPlayService {
	private TransportState mState;
	private String mPath;
	private BasePreferences mBasePreferences;
	private int mPausePosition;
	private int mDuration;

	private static MyMediaPlayerService self;
	private Context context;
	
	
	// TO fix KUGOU play next auto issue
	private int mLastpos = 0;
	private int mLastdur = 0;
	private int KUGOU_POSTION_ADD	= 5000;
	
	private ServiceManager<MyAVTransportService> mAVTransportManager = null;
	private ServiceManager<MyRendererControlService> mRenderingControl = null;
	

	// 发送到主程序控制音乐相关属性
	public static final String DLAN_MUSIC_TO_MAIN_PLAY_ACTION = "com.voice.assistant.DlnaMusicReceiver.play";
	public static final String DLAN_MUSIC_TO_MAIN_PAUSE_ACTION = "com.voice.assistant.DlnaMusicReceiver.pause";
	public static final String DLAN_MUSIC_TO_MAIN_STOP_ACTION = "com.voice.assistant.DlnaMusicReceiver.stop";
	public static final String DLAN_CONTROLLER = "com.voice.assistant.DlnaMusicReceiver.dlna";

	// 调用music项目中的音乐控制类
	private MediaPlayerService mMusicControlService;

	private static final String TAG = "SmallZhiDLNA，MyMediaPlayerService";
	
	
	public MyMediaPlayerService(Context context) {
		super();
		this.context = context;
		init();
	}
	
	// 该类采用单子模式
	public static MyMediaPlayerService getInstance(Context context) {
		if (self == null) {
			self = new MyMediaPlayerService(context);
		}
		return self;
	}
	
	public void setManager(ServiceManager transportManager,ServiceManager RenderingControl){
		if(null != transportManager && null != RenderingControl){
			mAVTransportManager = transportManager;
			mRenderingControl	= RenderingControl;
		}
	}
	
	// 获取音乐服务类
	public void init() {
		mBasePreferences = new BasePreferences(context);
		mMusicControlService = MediaPlayerService.getInstance(context);
		mState = TransportState.STOPPED;
	}

	public String GetDLNAUuid()
	{
		String uuid = null;
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		uuid = mPerferences.getString("DLNAUUID", "");
		if("" == uuid){
			// new a UUID for DLNA
			UUID uuidTemp = UUID.randomUUID();
			uuid = uuidTemp.toString();
			SharedPreferences.Editor editor = mPerferences.edit();
			editor.putString("DLNAUUID", uuid);
			editor.apply();
		}
		return uuid;
	}
	
		
	// *****************音乐的监听方法**********************//s
	private OnCompletionListener onCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer arg0) {
			// TODO Auto-generated method stub
			LogManager.d(TAG,"MyMediaPlayerService onCompletion");
			mState = TransportState.STOPPED;
			
			mLastpos = mMusicControlService.getCurrentPosition() + KUGOU_POSTION_ADD;
			mLastdur = mMusicControlService.getDuration();
			
			mAVTransportManager.getImplementation().getLastChange().setEventedValue(0,  new AVTransportVariable.TransportState(TransportState.STOPPED));
			try {
				mAVTransportManager.getImplementation().getLastChange().setEventedValue(0,  new AVTransportVariable.AVTransportURI(new URI(mPath)));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TransportAction[] actions = null;
			actions = new TransportAction[] { TransportAction.Play};
			mAVTransportManager.getImplementation().getLastChange().setEventedValue(0,  new AVTransportVariable.CurrentTransportActions(actions));
	    		mAVTransportManager.getImplementation().getLastChange().fire(mAVTransportManager.getImplementation().getPropertyChangeSupport());
				
			IStop();
		}
	};
	// private OnPreparedListener onPreparedListener = new OnPreparedListener()
	// {
	//
	// @Override
	// public void onPrepared(MediaPlayer arg0) {
	// // TODO Auto-generated method stub
	// LogManager.i("MyMediaPlayerService:IPlay onPrepared ok ...");
	// MediaPlayerService.messageQueue().post(new Runnable() {
	//
	// @Override
	// public void run() {
	//
	//
	// }
	// });
	// }
	// };
	private OnErrorListener onErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			LogManager.e(TAG,"dlna Error.......");
			mState = TransportState.STOPPED;
			IStop();
			return false;
		}
	};

	/********************* 音乐控制 ****************/
	@Override
	public void IPlay() {
		// TODO Auto-generated method stub
		LogManager.d(TAG, "----IPlay");
		MediaPlayerService.messageQueue().post(new Runnable() {

			@Override
			public void run() {
				try {
					
					mAVTransportManager.getImplementation().getLastChange().setEventedValue(0,  new AVTransportVariable.TransportState(TransportState.PLAYING));	
					mAVTransportManager.getImplementation().getLastChange().setEventedValue(0,  new AVTransportVariable.AVTransportURI(new URI(mPath)));
					mAVTransportManager.getImplementation().getLastChange().setEventedValue(0,  new AVTransportVariable.AbsoluteTimePosition(String.valueOf( mMusicControlService.getCurrentPosition())));
		    			mAVTransportManager.getImplementation().getLastChange().fire(mAVTransportManager.getImplementation().getPropertyChangeSupport());
		    		
					 Intent intent = new Intent(DLAN_MUSIC_TO_MAIN_PLAY_ACTION);
			         intent.putExtra(DLAN_CONTROLLER, true);
			         context.sendBroadcast(intent);
					if (!mMusicControlService.isPlaying() && mState == TransportState.PAUSED_PLAYBACK) {
						mBasePreferences.setPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN", true);
						mMusicControlService.start();
						mState = TransportState.PLAYING;
					} else {
						mState = TransportState.CUSTOM;
						if (mMusicControlService != null) {
							mBasePreferences.setPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN", true);
							
							LogManager.d(TAG, "IPlay loading ...");
							mMusicControlService.reset();
							mMusicControlService.setOnCompletionListener(onCompletionListener);
							//mMusicControlService.setOnPreparedListener(onPreparedListener);
							mMusicControlService.setOnErrorListener(onErrorListener);
							mMusicControlService.setMediaInfoList(mPath);
//							mState = TransportState.PLAYING;
//							mMusicControlService.start();
							
							mMusicControlService.setOnMusicPreparedListener(new OnMusicPreparedListener() {
								
								@Override
								public void onPrepared(MediaPlayer mp) {
									// TODO Auto-generated method stub
									LogManager.d(TAG,"IPlay loading complete...");
								
									MediaPlayerService.messageQueue().post(new Runnable() {
										@Override
										public void run() {
											// TODO Auto-generated method stub
											mState = TransportState.PLAYING;
											mMusicControlService.start();
										}
									});
									
									
									MediaPlayerService.messageQueue().post(new Runnable() {
										@Override
										public void run() {
											// TODO Auto-generated method stub
											try {
												Thread.sleep(1000);//This TASK only to fix IOS QQ bug :  ios QQ bug not get state while start
											} catch (InterruptedException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											mAVTransportManager.getImplementation().getLastChange().setEventedValue(0,  new AVTransportVariable.TransportState(mState));
											mAVTransportManager.getImplementation().getLastChange().setEventedValue(0,  new AVTransportVariable.AbsoluteTimePosition(String.valueOf( mMusicControlService.getCurrentPosition())));
								    			mAVTransportManager.getImplementation().getLastChange().fire(mAVTransportManager.getImplementation().getPropertyChangeSupport());
										}
									});
								
								
								}
							});
				
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mBasePreferences.setPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN", false);
					LogManager.d(TAG,"play error :" + Log.getStackTraceString(e));
				}
			}
		});
	}

	@Override
	public void IPause() {
		// TODO Auto-generated method stub
		MediaPlayerService.messageQueue().post(new Runnable() {

			@Override
			public void run() {
				if (mState == TransportState.PLAYING) {
					mBasePreferences.setPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN", true);
				}
				if (mMusicControlService != null && mBasePreferences.getPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN")) {
					mState = TransportState.PAUSED_PLAYBACK;
					LogManager.d(TAG,"----IPause");
					Intent intent = new Intent(DLAN_MUSIC_TO_MAIN_PAUSE_ACTION);
					intent.putExtra(DLAN_CONTROLLER, true);
					context.sendBroadcast(intent);
					mPausePosition = mMusicControlService.getCurrentPosition();
					mDuration = mMusicControlService.getDuration();

					mMusicControlService.pause();
					mAVTransportManager.getImplementation().getLastChange().setEventedValue(0,  new AVTransportVariable.TransportState(TransportState.PAUSED_PLAYBACK));	
		    		mAVTransportManager.getImplementation().getLastChange().fire(mAVTransportManager.getImplementation().getPropertyChangeSupport());
				
					
				}
			}
		});
	}

	@Override
	public void IStop() {
		// TODO Auto-generated method stub
		MediaPlayerService.messageQueue().post(new Runnable() {

			@Override
			public void run() {
				mState = TransportState.STOPPED;
				LogManager.d(TAG,"----IStop");
				if (mMusicControlService != null && mBasePreferences.getPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN")) {
					mBasePreferences.setPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN", false);
					try {
						Intent intent = new Intent(DLAN_MUSIC_TO_MAIN_STOP_ACTION);
						intent.putExtra(DLAN_CONTROLLER, true);
						context.sendBroadcast(intent);
						mMusicControlService.dlnaStop();
						
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		});
	}

	@Override
	public void ISimpleStop() {
		// TODO Auto-generated method stub
		MediaPlayerService.messageQueue().post(new Runnable() {

			@Override
			public void run() {
				mState = TransportState.STOPPED;
				LogManager.d(TAG,"----ISimpleStop");
				if (mMusicControlService != null && mBasePreferences.getPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN")) {
					mBasePreferences.setPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN", false);
					try {
						mMusicControlService.dlnaStop();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		});
	}

	@Override
	public void ISeek(final int whereto) {

		// TODO Auto-generated method stub
		MediaPlayerService.messageQueue().post(new Runnable() {

			@Override
			public void run() {
				if (mMusicControlService != null) {
					mMusicControlService.seekTo(whereto);
					
					mAVTransportManager.getImplementation().getLastChange().setEventedValue(0,  new AVTransportVariable.TransportState(mState));
					mAVTransportManager.getImplementation().getLastChange().setEventedValue(0,  new AVTransportVariable.AbsoluteTimePosition(String.valueOf(whereto)));
		    			mAVTransportManager.getImplementation().getLastChange().fire(mAVTransportManager.getImplementation().getPropertyChangeSupport());
		    		
					if(TransportState.PAUSED_PLAYBACK == mState){
						mPausePosition = whereto;
					}
				}
			}
		});
	}

	@Override
	public void ISetVolume(int volume) {
		// TODO Auto-generated method stub
		LogManager.d(TAG,"----ISetVolume=" + volume);
		VolumeCotroller.setVolume(context, volume);
	//	mRenderingControl.getImplementation().getLastChange().setEventedValue(0, new RenderingControlVariable.Volume(new ChannelVolume(Channel.Master,volume)));
	}
	
	
	public void IPushVolomeChange(){
		
		int volume = VolumeCotroller.getVolume(context)*100/VolumeCotroller.getMaxVolume(context);;
		LogManager.d(TAG,"----IPushVolomeChange=" + volume);
		
		mRenderingControl.getImplementation().getLastChange().setEventedValue(0, new RenderingControlVariable.Volume(new ChannelVolume(Channel.Master,volume)));
		mRenderingControl.getImplementation().getLastChange().fire(mRenderingControl.getImplementation().getPropertyChangeSupport());
		
	}

	@Override
	public String IGetPlayerState() {
		// TODO Auto-generated method stub
		LogManager.d(TAG,"----IGetPlayerState=" + mState.getValue());

		return mState.getValue();
	}

	@Override
	public int IGetDuration() {
		// TODO Auto-generated method stub
		int dur = 0;
		if (mMusicControlService != null) {
			if (mState == TransportState.PLAYING) {
				dur = mMusicControlService.getDuration();
			} else if (mState == TransportState.PAUSED_PLAYBACK) {
				dur = mDuration;
			}
		}
		
		if (mState == TransportState.STOPPED){
			return mLastdur;
		}
		
		return dur;
	}

	@Override
	public int IGetCurrentPosition() {
		// TODO Auto-generated method stub
		int pos = 0;
		if (mMusicControlService != null) {
			if (mState == TransportState.PLAYING) {
				pos = mMusicControlService.getCurrentPosition();
			} else if (mState == TransportState.PAUSED_PLAYBACK) {
				pos = mPausePosition;
			}
		}
		
		if (mState == TransportState.STOPPED){
			return mLastpos;
		}
		
		return pos;
	}

	@Override
	public void ISetUrl(String uri, String uriMetaData) {
		// TODO Auto-generated method stub
		LogManager.d(TAG,"play uri :" + uri + "||uriMetaData=" + uriMetaData);
		mLastpos = 0;
		mLastdur = 0;
		mPath = uri;
		
	}
	
	
	

}
