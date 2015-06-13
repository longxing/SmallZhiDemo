package com.smallzhi.airplayService;

import com.iii360.sup.common.utl.LogManager;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AirPlayPlayer {
	private static final String TAG			= "SmallZhi AirPlayPlayer";
	AudioTrack player = null;
	public static final int SAMPLE_RATE			= 44100;
	public static final int PLAYBACK_STREAM		= AudioManager.STREAM_MUSIC;
	public static final int AUDIO_FORMANT		= AudioFormat.ENCODING_PCM_16BIT;
	public int mBufferSize						= 32*1024;

	//缓冲大小 ＝ 最小帧数*(通道 ＝＝  CHANNNEL_OUT_STERO？2:1)*(音频格式 ＝＝ PCM16？2:1)
	
	public AirPlayPlayer() {
		super();
		player = new AudioTrack(PLAYBACK_STREAM
								,SAMPLE_RATE
								,AudioFormat.CHANNEL_OUT_STEREO
								,AUDIO_FORMANT
								,mBufferSize
								,AudioTrack.MODE_STREAM);
		
		
	}
	
	public void setVolume(int volume){
		if(null != player){
		}	
	}
	
	public int play(){
		int result = AirPlayCommon.AIRPLAY_ERROR;
		return result;
	}
	
	public int pause(){
		int result = AirPlayCommon.AIRPLAY_ERROR;
		return result;
	}
	
	public int write(int shortsize, short[]shortdata ){
		int result = AirPlayCommon.AIRPLAY_ERROR;

		if(null != player)
		{
			result = player.write(shortdata, 0, shortsize);
			if(result == shortsize){
				LogManager.d(TAG,"write suitable");
			}else if(result < shortsize){
				//some PCM data is not written,should backup for next playing
				LogManager.d(TAG,"write not suitable");
			}
			else if(AudioTrack.ERROR_INVALID_OPERATION == result){
				LogManager.e(TAG,"write ERROR_INVALID_OPERATION");
			}
			else if(AudioTrack.ERROR_BAD_VALUE == result){
				LogManager.e(TAG,"write ERROR_BAD_VALUE");
			}
			else {
				LogManager.e(TAG,"write unkown error code");
			}
		}	
		return result;
	}
}
