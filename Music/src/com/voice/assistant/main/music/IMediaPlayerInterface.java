package com.voice.assistant.main.music;


import android.media.MediaPlayer;
import android.widget.MediaController.MediaPlayerControl;

import com.voice.assistant.main.music.MediaPlayerService.OnBufferingOverdueListener;


public interface IMediaPlayerInterface extends MediaPlayerControl {

    public final static int PLAY_MODE_SIGNAL = 0;
    public final static int PLAY_MODE_ALL = 1;
    public final static int PLAY_MODE_LOOPSIGNAL = 2;
    public final static int PLAY_MODE_LOOPALL = 3;
    public final static int PLAY_MODE_ONLINE = 4;

    public void setMediaInfo(MediaInfo info);
    
    public void simplePause();
    
    public void simpleStart();

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l);

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l);

    public void setOnErrorListener(MediaPlayer.OnErrorListener l);
    
    public void setOnBufferingOverdueListener(OnBufferingOverdueListener onBufferingOverdueListener);

    public void release();
//
//    public void prepareAsync();

    public int getDownPer();
    
    public void setPreMediaInfoList(String path);
    
    public MediaPlayerService getMediaPlayerService();

}
