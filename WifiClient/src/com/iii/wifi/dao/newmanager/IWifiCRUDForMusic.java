package com.iii.wifi.dao.newmanager;

import com.iii.wifi.dao.newmanager.WifiCRUDForMusic.ResultForMusicListener;

public interface IWifiCRUDForMusic {

    /**
     * 播放和暂停
     * @param resultListener
     */
    void playOrPause(ResultForMusicListener resultListener);

//    /**
//     * 恢复播放
//     * @param resultListener
//     */
//    void playResume(ResultForMusicListener resultListener);

    /**
     * 上一首
     * @param resultListener
     */
    void playPre(ResultForMusicListener resultListener);

    /**
     * 下一首
     * 
     * @param resultListener
     */
    void playNext(ResultForMusicListener resultListener);

    /**
     * 垃圾桶
     * @param resultListener
     */
    void badMusic(ResultForMusicListener resultListener);

    /**
     * 红星
     * @param resultListener
     */
    void goodMusic(ResultForMusicListener resultListener);

    /**
     * "state":0, // 0没有播放的歌曲1正在播放2暂停 "data":"{id, songName, singerName}"
     * 歌曲信息，与playMusic的数据一致（当前没有关于播放进度的数据）
     * @param resultListener
     */
    void playState(ResultForMusicListener resultListener);
    

}
