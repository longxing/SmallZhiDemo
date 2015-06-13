package com.voice.voicesoundwave;

import com.iii360.sup.common.utl.LogManager;

import android.content.Context;
import android.media.AudioManager;

public class AudioManagerStreamVolume {
	public static AudioManagerStreamVolume mAudioManagerStreamVolume;
    private AudioManager mAudioManager;
    private int currentlyVolume;
	public static AudioManagerStreamVolume getIntence(Context context) {
		if (mAudioManagerStreamVolume == null) {
			mAudioManagerStreamVolume = new AudioManagerStreamVolume(context);
		}
		return mAudioManagerStreamVolume;
	}

	private AudioManagerStreamVolume(Context context) {
		mAudioManager = (AudioManager)(context.getSystemService(Context.AUDIO_SERVICE)); 
	}
	public void setVolumeMax() {
		currentlyVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}
	public void seterVolume() {
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentlyVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}
}
