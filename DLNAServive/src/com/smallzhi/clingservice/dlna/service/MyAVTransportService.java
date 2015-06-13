package com.smallzhi.clingservice.dlna.service;

import java.util.Locale;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.avtransport.AbstractAVTransportService;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportSettings;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.model.TransportStatus;

import android.R.bool;
import android.content.Context;

import com.iii360.sup.common.utl.LogManager;
import com.smallzhi.clingservice.media.PlayServiceGetter;

public class MyAVTransportService extends AbstractAVTransportService {
    private final static int TIME_SECOND = 1000;
    private final static int TIME_MINUTE = TIME_SECOND * 60;
    private final static int TIME_HOUR = TIME_MINUTE * 60;
    
//    //发送到主程序控制音乐相关属性
    public static final String DLAN_MUSIC_TO_MAIN_PLAY_ACTION = "com.voice.assistant.DlnaMusicReceiver.play";
    public static final String DLAN_MUSIC_TO_MAIN_PAUSE_ACTION = "com.voice.assistant.DlnaMusicReceiver.pause";
    public static final String DLAN_MUSIC_TO_MAIN_STOP_ACTION = "com.voice.assistant.DlnaMusicReceiver.stop";
    public static final String DLAN_CONTROLLER = "com.voice.assistant.DlnaMusicReceiver.dlna";


    private String mCurrentURI			= null;
    private String mCurrentMetaData		= null;
    private PlayServiceGetter mGetter	= null;
    
    //Dlan module wroking state
    // mState == false means dlna has been stopped
    // mState == true means dlna has been working
    public static boolean mState = false;
    
    private static final String TAG = "SmallZhiDLNA，MyAVTransportService";
    
    private Context context;

 //   public MyAVTransportService(LastChange lastChange, Context context,PlayServiceGetter getter) {
 //  	super(lastChange);
    public MyAVTransportService(Context context,PlayServiceGetter getter) { 
        mGetter = getter;
        this.context = context;
    }

	@Override
	protected TransportAction[] getCurrentTransportActions(
			UnsignedIntegerFourBytes arg0) throws Exception {
		// TODO Auto-generated method stub
		
        TransportAction[] actions = null;
        if (mGetter.getPlayService() != null) {

            TransportState state = TransportState.valueOrCustomOf(mGetter.getPlayService().IGetPlayerState());
            switch (state) {
            case STOPPED:
                actions = new TransportAction[] { TransportAction.Play};
                LogManager.d(TAG,"getCurrentTransportActions STOPPED");
                break;
            case PLAYING:
                actions = new TransportAction[] { TransportAction.Stop, TransportAction.Seek, TransportAction.Pause };
                LogManager.d(TAG,"getCurrentTransportActions PLAYING");
            case PAUSED_PLAYBACK:
                actions = new TransportAction[] { TransportAction.Play, TransportAction.Stop, TransportAction.Pause, TransportAction.Seek };
                LogManager.d(TAG,"getCurrentTransportActions PAUSED_PLAYBACK");
            default:
                actions = null;
                break;
            }

        } 
        return actions;
	}

	@Override
	@UpnpAction(out = {
			@UpnpOutputArgument(name = "PlayMedia", stateVariable = "PossiblePlaybackStorageMedia", getterName = "getPlayMediaString"),
			@UpnpOutputArgument(name = "RecMedia", stateVariable = "PossibleRecordStorageMedia", getterName = "getRecMediaString"),
			@UpnpOutputArgument(name = "RecQualityModes", stateVariable = "PossibleRecordQualityModes", getterName = "getRecQualityModesString") })
	public DeviceCapabilities getDeviceCapabilities(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0)
			throws AVTransportException {
		// TODO Auto-generated method stub
		
		 LogManager.d(TAG,"getDeviceCapabilities");
		 return new DeviceCapabilities(new StorageMedium[] { StorageMedium.NETWORK });
	}

	@Override
	@UpnpAction(out = {
			@UpnpOutputArgument(name = "NrTracks", stateVariable = "NumberOfTracks", getterName = "getNumberOfTracks"),
			@UpnpOutputArgument(name = "MediaDuration", stateVariable = "CurrentMediaDuration", getterName = "getMediaDuration"),
			@UpnpOutputArgument(name = "CurrentURI", stateVariable = "AVTransportURI", getterName = "getCurrentURI"),
			@UpnpOutputArgument(name = "CurrentURIMetaData", stateVariable = "AVTransportURIMetaData", getterName = "getCurrentURIMetaData"),
			@UpnpOutputArgument(name = "NextURI", stateVariable = "NextAVTransportURI", getterName = "getNextURI"),
			@UpnpOutputArgument(name = "NextURIMetaData", stateVariable = "NextAVTransportURIMetaData", getterName = "getNextURIMetaData"),
			@UpnpOutputArgument(name = "PlayMedium", stateVariable = "PlaybackStorageMedium", getterName = "getPlayMedium"),
			@UpnpOutputArgument(name = "RecordMedium", stateVariable = "RecordStorageMedium", getterName = "getRecordMedium"),
			@UpnpOutputArgument(name = "WriteStatus", stateVariable = "RecordMediumWriteStatus", getterName = "getWriteStatus") })
	public MediaInfo getMediaInfo(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0)
			throws AVTransportException {
		// TODO Auto-generated method stub
		TransportState state = TransportState.STOPPED;
        state = TransportState.valueOrCustomOf(mGetter.getPlayService().IGetPlayerState());
        

			LogManager.d(TAG,"getMediaInfo " + "mCurrentURI" + mCurrentURI);
			LogManager.d(TAG,"getMediaInfo " + "mCurrentMetaData" + mCurrentMetaData);
			return new MediaInfo(mCurrentURI, mCurrentMetaData);
		

	}

	@Override
	@UpnpAction(out = {
			@UpnpOutputArgument(name = "Track", stateVariable = "CurrentTrack", getterName = "getTrack"),
			@UpnpOutputArgument(name = "TrackDuration", stateVariable = "CurrentTrackDuration", getterName = "getTrackDuration"),
			@UpnpOutputArgument(name = "TrackMetaData", stateVariable = "CurrentTrackMetaData", getterName = "getTrackMetaData"),
			@UpnpOutputArgument(name = "TrackURI", stateVariable = "CurrentTrackURI", getterName = "getTrackURI"),
			@UpnpOutputArgument(name = "RelTime", stateVariable = "RelativeTimePosition", getterName = "getRelTime"),
			@UpnpOutputArgument(name = "AbsTime", stateVariable = "AbsoluteTimePosition", getterName = "getAbsTime"),
			@UpnpOutputArgument(name = "RelCount", stateVariable = "RelativeCounterPosition", getterName = "getRelCount"),
			@UpnpOutputArgument(name = "AbsCount", stateVariable = "AbsoluteCounterPosition", getterName = "getAbsCount") })
	public PositionInfo getPositionInfo(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0)
			throws AVTransportException {
		// TODO Auto-generated method stub
		
	
		
		 if (mGetter.getPlayService() != null) {
	            int trackDuration = 0;

	            trackDuration = mGetter.getPlayService().IGetDuration();
	            if (trackDuration < 0) {
	                trackDuration = 0;
	            }
	            
	            int currentPos = 0;
	            currentPos = mGetter.getPlayService().IGetCurrentPosition();	
	        	
	            if (currentPos < 0) {
	                currentPos = 0;
	            }
	            
	            LogManager.d(TAG,"getPositionInfo,currentPos = " + currentPos + "trackDuration " + trackDuration);
	            
	            PositionInfo curPosInfo = new PositionInfo(0, formatTimeInfo(trackDuration), mCurrentURI, formatTimeInfo(currentPos),
	                    formatTimeInfo(currentPos));
	            return curPosInfo;
	        }
	        return new PositionInfo();
	}

	@Override
	@UpnpAction(out = {
			@UpnpOutputArgument(name = "CurrentTransportState", stateVariable = "TransportState", getterName = "getCurrentTransportState"),
			@UpnpOutputArgument(name = "CurrentTransportStatus", stateVariable = "TransportStatus", getterName = "getCurrentTransportStatus"),
			@UpnpOutputArgument(name = "CurrentSpeed", stateVariable = "TransportPlaySpeed", getterName = "getCurrentSpeed") })
	public TransportInfo getTransportInfo(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0)
			throws AVTransportException {
		// TODO Auto-generated method stub
		 if (mGetter.getPlayService() != null) {
			 
			 	LogManager.d(TAG,"getTransportInfo");
			 	
	            TransportState state = TransportState.STOPPED;
	            state = TransportState.valueOrCustomOf(mGetter.getPlayService().IGetPlayerState());

	  
	            LogManager.d(TAG,"TransportStatus->" + state);

	            return new TransportInfo(state, TransportStatus.OK);

	        }
	        return null;
	}

	@Override
	@UpnpAction(out = {
			@UpnpOutputArgument(name = "PlayMode", stateVariable = "CurrentPlayMode", getterName = "getPlayMode"),
			@UpnpOutputArgument(name = "RecQualityMode", stateVariable = "CurrentRecordQualityMode", getterName = "getRecQualityMode") })
	public TransportSettings getTransportSettings(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0)
			throws AVTransportException {
		// TODO Auto-generated method stub
			LogManager.d(TAG,"getTransportSettings");
	        return new TransportSettings(PlayMode.NORMAL);
	}

	@Override
	@UpnpAction
	public void next(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0)
			throws AVTransportException {
		// TODO Auto-generated method stub
		LogManager.d(TAG,"next");
		LogManager.i("next");

	}

	@Override
	@UpnpAction
	public void pause(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0)
			throws AVTransportException {
		// TODO Auto-generated method stub
		LogManager.d(TAG,"pause");
	        
	        if (mGetter.getPlayService() != null) {
	            
	            mGetter.getPlayService().IPause();
	        }
	        
//			UnsignedIntegerFourBytes stopFlag = new UnsignedIntegerFourBytes(0);
			
//			try {
//				transportService.getManager().getImplementation().pause(stopFlag);
//			} catch (AVTransportException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
	}

	@Override
	@UpnpAction
	public void play(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0,
			@UpnpInputArgument(name = "Speed", stateVariable = "TransportPlaySpeed") String arg1)
			throws AVTransportException {
		// TODO Auto-generated method stub
		   LogManager.d(TAG,"play");
	        
	        if (mGetter.getPlayService() != null) {
	            
	            mGetter.getPlayService().IPlay();
	        }
	}

	@Override
	@UpnpAction
	public void previous(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0)
			throws AVTransportException {
		// TODO Auto-generated method stub
		   LogManager.d(TAG,"previous");
	}

	@Override
	@UpnpAction
	public void record(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0)
			throws AVTransportException {
		// TODO Auto-generated method stub
		  LogManager.d(TAG,"record");
	}

	@Override
	@UpnpAction
	public void seek(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0,
			@UpnpInputArgument(name = "Unit", stateVariable = "A_ARG_TYPE_SeekMode") String arg1,
			@UpnpInputArgument(name = "Target", stateVariable = "A_ARG_TYPE_SeekTarget") String arg2)
			throws AVTransportException {
		// TODO Auto-generated method stub
	
	        if (mGetter.getPlayService() != null) {
	            SeekMode seekMode;
	            seekMode = SeekMode.valueOrExceptionOf(arg1);
	            if (!seekMode.equals(SeekMode.REL_TIME)) {
	                throw new IllegalArgumentException();
	            }
	            int seconds = (int) ModelUtil.fromTimeString(arg2);
	            
	            
	            
	        	LogManager.d(TAG,"seek， time = " + seconds);
	            mGetter.getPlayService().ISeek(seconds * TIME_SECOND);
	        }
	}

	@Override
	@UpnpAction
	public void setAVTransportURI(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0,
			@UpnpInputArgument(name = "CurrentURI", stateVariable = "AVTransportURI") String arg1,
			@UpnpInputArgument(name = "CurrentURIMetaData", stateVariable = "AVTransportURIMetaData") String arg2)
			throws AVTransportException {
		// TODO Auto-generated method stub
			LogManager.d(TAG,"setAVTransportURI");
	        mCurrentURI = arg1;
	        mCurrentMetaData = arg2;
	        if (mGetter.getPlayService() != null) {
	        		LogManager.d(TAG,"setAVTransportURI URL = " + arg1);
	        		LogManager.d(TAG,"setAVTransportURI MetaData = " + arg2);
	            mGetter.getPlayService().ISetUrl(arg1, arg2);
	            setState(true);
	        }
	        else{
	        	LogManager.d(TAG,"setAVTransportURI URL is null");
	        }
	}

	@Override
	@UpnpAction
	public void setNextAVTransportURI(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0,
			@UpnpInputArgument(name = "NextURI", stateVariable = "AVTransportURI") String arg1,
			@UpnpInputArgument(name = "NextURIMetaData", stateVariable = "AVTransportURIMetaData") String arg2)
			throws AVTransportException {
		// TODO Auto-generated method stub
			LogManager.d(TAG,"setNextAVTransportURI");
	}

	@Override
	@UpnpAction
	public void setPlayMode(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0,
			@UpnpInputArgument(name = "NewPlayMode", stateVariable = "CurrentPlayMode") String arg1)
			throws AVTransportException {
		// TODO Auto-generated method stub
		LogManager.d(TAG,"setPlayMode");
	}

	@Override
	@UpnpAction
	public void setRecordQualityMode(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0,
			@UpnpInputArgument(name = "NewRecordQualityMode", stateVariable = "CurrentRecordQualityMode") String arg1)
			throws AVTransportException {
		// TODO Auto-generated method stub
		 LogManager.d(TAG,"setRecordQualityMode");
	}

	@Override
	@UpnpAction
	public void stop(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0)
			throws AVTransportException {
		// TODO Auto-generated method stub
		 LogManager.d(TAG,"dlan stop");
	        
	        if (mGetter.getPlayService() != null) {
	            mGetter.getPlayService().IStop();
	            setState(false);
	        }
	}
	 private String formatTimeInfo(int timeVal) {
	        int hour = timeVal / TIME_HOUR;
	        int minute = (timeVal - hour * TIME_HOUR) / TIME_MINUTE;
	        int second = (timeVal - hour * TIME_HOUR - minute * TIME_MINUTE) / TIME_SECOND;
	        return String.format(Locale.US, "%02d:%02d:%02d", hour, minute, second);
	    }

	@Override
	public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean getState(){
		return mState;
	}
	
	public void setState(boolean state){
		mState = state;
	}
	
	
	@Override
	public LastChange getLastChange() {
		// TODO Auto-generated method stub
		return super.getLastChange();
	}
	
}
