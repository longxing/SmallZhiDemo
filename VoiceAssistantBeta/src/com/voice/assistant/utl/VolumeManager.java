package com.voice.assistant.utl;

import android.content.Context;
import android.media.AudioManager;

public class VolumeManager {
    private AudioManager mAudioManager;

    public VolumeManager(Context context) {
        // TODO Auto-generated constructor stub
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public int getCurrAlarmVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
    }

    public void setAlarmVolume(int v) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, v, 0);
    }
  
}
