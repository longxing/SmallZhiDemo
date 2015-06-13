package com.voice.assistant.main.newmusic;

import java.util.Map;

public interface IMusic {
    /**
     * 播放和暂停,0没有播放的歌曲1正在播放2暂停,小于0表示操作失败
     */
    int playOrPause();

    /**
     * 上一首,小于0表示操作失败
     */
    int playPre();

    /**
     * 下一首,小于0表示操作失败
     */
    int playNext();

    /**
     * 垃圾桶按键,小于0表示操作失败
     */
    int badMusic();

    /**
     * 红星按键,小于0表示操作失败
     */
    int goodMusic();

    /**
     * "state":0, // 0没有播放的歌曲1正在播放2暂定 
     * "data":"{id, songName, singerName}" 
     *  歌曲信息，与playMusic的数据一致（当前没有关于播放进度的数据）
     * 
     * @return 歌曲播放信息和状态
     */
    Map<Integer, String> playState();

}
