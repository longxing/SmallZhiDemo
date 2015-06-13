package com.voice.assistant.main.music;
import com.voice.assistant.main.music.MediaInfo;
interface IMusicControlService {
        void    init();
        void    setMediaInfoList(in MediaInfo info);
        void    start();
        void    pause();
        void    release();
        void    prepareAsync();
        int     getDuration();
        int     getCurrentPosition();
        void    seekTo(int pos);
        boolean isPlaying();
        int     getBufferPercentage();
        boolean canPause();
        boolean canSeekBackward();
        boolean canSeekForward();

}
