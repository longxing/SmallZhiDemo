package com.iii.wifi.dao.imf;

import android.content.Context;
import android.media.AudioManager;

import com.iii360.sup.common.utl.LogManager;

public class WifiVolumeCotroller {
    private Context context;
    private AudioManager mAudioManager;
    public static final String GET_BOX_VOLUME = "GET_BOX_VOLUME";
    public static final String SET_BOX_VOLUME = "SET_BOX_VOLUME";
    public static final String GET_TTS_VOLUME = "GET_TTS_VOLUME";
    public static final String SET_TTS_VOLUME = "SET_TTS_VOLUME";
    private int streamType;

    public WifiVolumeCotroller(Context context,int type) {
        // TODO Auto-generated constructor stub
        this.context = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        streamType=type;
    }

    public int getCurrentVolume() {
        return mAudioManager.getStreamVolume(streamType);
    }

    public int getMaxVolume() {
        return mAudioManager.getStreamMaxVolume(streamType);
    }

    public void setVolume(int volume) {
        LogManager.i("music volume=" + volume);
        if (volume < 1) {
            volume = 1;
        }
        mAudioManager.setStreamVolume(streamType, volume, 0);
    }

    public int getAlramMaxVloume() {
        return mAudioManager.getStreamMaxVolume(streamType);
    }
}
