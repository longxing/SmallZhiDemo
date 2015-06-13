package com.smallzhi.clingservice.util;

import com.iii360.sup.common.utl.LogManager;

import android.content.Context;
import android.media.AudioManager;

public class VolumeCotroller {
    
    public static void setVolume(Context context, int volume) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(Constants.STREAM_TYPE, volume, 0);
    }
    
    public static int getMaxVolume(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return mAudioManager.getStreamMaxVolume(Constants.STREAM_TYPE);
    }
    

    public static int getVolume(Context context) {
    	AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    	return mAudioManager.getStreamVolume(Constants.STREAM_TYPE);
    }
}
