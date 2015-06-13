//ID20120528006 liaoyixuan begin
package com.voice.recognise;

import com.iii360.sup.common.utl.LogManager;

import android.content.Context;
import android.os.Handler;


public class VoiceRecogniseFactory {
	private VoiceRecogniseFactory() {
	};

	// public static final int VOICE_RECOGNISE_TYPE_IFLYTEK = 0;
	public static final int VOICE_RECOGNISE_TYPE_GOOGLE = 0;
	public static final int VOICE_RECOGNISE_TYPE_CUSTOM = 1;
	// ID20120504003 liaoyixuan begin
	public static final int VOICE_RECOGNISE_TYPE_OUTSIDE = 2;
	// ID20120504003 liaoyixuan end

	// public static final int SPEECH_RECOGNISE_TYPE_IFLYTEK = 0;
	public static final int SPEECH_RECOGNISE_TYPE_GOOGLE = 0;
	public static final int SPEECH_RECOGNISE_TYPE_NUANCE = 1;

	private static VoiceRecogniseAbstract mVoiceRecognise;

	// ID20120504003 liaoyixuan begin
	public static VoiceRecognise createNewVoiceRecognise(Context context, Handler handler, int type) {
		VoiceRecognise ret = null;
		switch (type) {
		case VOICE_RECOGNISE_TYPE_GOOGLE:
			// ret = new VoiceRecogniseGoogle(context, handler);
			break;
		case VOICE_RECOGNISE_TYPE_CUSTOM:
			// ret = new VoiceRecogniseCustom(context, handler);
			break;
		}

		return ret;
	}

	// ID20120504003 liaoyixuan end

	public static boolean isCanEnterCommandMode() {
		if (mVoiceRecognise != null) {
			return mVoiceRecognise.isCanEnterCommandMode();
		}
		return false;
	}

	public static final void release() {
		mVoiceRecognise = null;
	}

	private static final int getType(String type) {
		int typeValue = VOICE_RECOGNISE_TYPE_GOOGLE;
		if (type != null && !type.trim().equals("")) {
			typeValue = Integer.parseInt(type);
		}
		return typeValue;
	}

	public static final VoiceRecognise createVoiceRecognise(String type) {
		return createVoiceRecognise(getType(type));
	}

	public static final VoiceRecognise createVoiceRecognise(int type) {
		if (mVoiceRecognise != null) {
			switch (type) {
			case VOICE_RECOGNISE_TYPE_GOOGLE:
				// mVoiceRecognise = new VoiceRecogniseGoogle(mVoiceRecognise);
				break;
			case VOICE_RECOGNISE_TYPE_CUSTOM:
				// mVoiceRecognise = new VoiceRecogniseCustom(mVoiceRecognise);
				break;
			}
		}

		return mVoiceRecognise;
	}

	public static final VoiceRecognise createVoiceRecognise(Context context, Handler handler, String type) {

		return createVoiceRecognise(context, handler, getType(type));
	}

	public static final VoiceRecognise createVoiceRecognise(Context context, Handler handler, int type) {
		LogManager.d("VoiceRecogniseFactory", "createVoiceRecognise", "type:" + type);
		switch (type) {
		case VOICE_RECOGNISE_TYPE_GOOGLE:
			// mVoiceRecognise = new VoiceRecogniseGoogle(context, handler);
			break;
		case VOICE_RECOGNISE_TYPE_CUSTOM:
			// mVoiceRecognise = new VoiceRecogniseCustom(context, handler);
			break;
		}
		return mVoiceRecognise;

	}

	public static final ISpeechRecognizer createSpeechRecognise(Context context, int type) {
		switch (type) {
		case SPEECH_RECOGNISE_TYPE_GOOGLE:
			LogManager.d("Create google recognizer");
			return new GoogleRecognizer(context);
		case SPEECH_RECOGNISE_TYPE_NUANCE:
			LogManager.d("Create nuance recognizer");
			// return new NuanceRecognizer(context);
		default:
			return new GoogleRecognizer(context);
		}
	}
}
// ID20120528006 liaoyixuan end