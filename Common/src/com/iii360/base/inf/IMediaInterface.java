package com.iii360.base.inf;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;

public interface IMediaInterface {

	public final static int PLAY_MODE_SIGNAL = 0;
	public final static int PLAY_MODE_ALL = 1;
	public final static int PLAY_MODE_LOOPSIGNAL = 2;
	public final static int PLAY_MODE_LOOPALL = 3;
	
	public interface OnEndOfLoopListener {
		public boolean onEndOfLoop();
	}

	public void setMediaInfoList(Object info);
	
	public void addMediaInfoList(Object info);

	public void release();

	public void playNext();

	public void playPre();

	public void pause();

	public void resume();

	public void start();

	public boolean isPlaying();

	public boolean isInPlayState();

	public int getDuration();

	public int getCurrentPosition();

	public void setPlayMode(int mode);

	public void setOnParePare(OnPreparedListener listener);

	public void setOnComplation(OnCompletionListener listener);

	public void setOnEndOfLoopListener(OnEndOfLoopListener listener);
	
	public void setOnError(OnErrorListener listener);

	public int getPlayMode();
	
	public void setPlayType(int type);
	
	public MediaPlayer getMediaPlayer();
}
