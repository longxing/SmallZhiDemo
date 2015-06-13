package com.voice.recognise;

import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;

public interface VoiceRecognise {

	public final static int RECOGNIZE_MODE_COMMON = 0;
	public final static int RECOGNIZE_MODE_WIDGET = 1;
	public final static int RECOGNIZE_MODE_EXCAT = 2;
	public static final String CONTENT_ID_CONTACT = "CONTENT_ID_CONTACT";
	public static final String CONTENT_ID_APP = "CONTENT_ID_APP";

	public static interface OnRecognizerEndListener {
		/**
		 * @param text
		 *            : recognizer result
		 * @return true : processe is ended, not call common handler false :
		 *         call common handler when processed this listener
		 */
		public boolean onRecognizerEnd(String text);
	}

	public static final String ENGINE_TYPE_ACCESS_POINT_2G = "wap_proxy=cmwap";
	public static final String ENGINE_TYPE_RECOGNISE_TEXT = "sms";
	public static final String ENGINE_TYPE_SEARCH_ADDRESS = "poi";
	public static final String ENGINE_TYPE_SEARCH_HOTWORD = "vsearch";
	public static final String ENGINE_TYPE_SEARCH_MEDIA = "video";
	public static final String ENGINE_TYPE_RECOGNISE_COMMAND = "asr";

	public static final int ASS_MODE_COMMAND = 0;
	public static final int ASS_MODE_CHAT = 1;
	public static final int ASS_MODE_VISTA = 2;

	public void init();

	public void handText(Intent data);
	public void handText(String text);

	public void setOnCancelListener(OnCancelListener l);

	// public void setRecognizerMode(int mode);
	public void setRecogniseEngineType(String type);

	public boolean uploadContent(String contentId, String keys);

	public void setOnRecognizerEndListener(OnRecognizerEndListener l);

	public void startRecognise();

	public void setRecognizer(int type);
	public void cancel();
	public void destory();
	public boolean isShowing();
    boolean isCanEnterCommandMode();
    public void setRecogniseDlg(IRecogniseDlg dlg);

}
