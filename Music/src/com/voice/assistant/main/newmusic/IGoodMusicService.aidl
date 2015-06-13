package com.voice.assistant.main.newmusic;
import com.voice.assistant.main.newmusic.IChangeListener;
import com.voice.assistant.main.newmusic.NetResourceMusicInfo;
import java.util.Map;
interface IGoodMusicService{

	String getGoodMusics();
	String getLocalMusics(int page);
	String getCurrentMusics(int page);
	String playMusic(String id);
	String playLocalMusic(String id,String currentPosition);
	String deleteMusic(String id);
	void setNetMusicResources(in  List<NetResourceMusicInfo> infos);
	void setPlayBeginListen(in IChangeListener onBegin);
	void setPlayEndListen(in IChangeListener onEnd);
	int setMusicForRemind(String id);
	void playGoodList(int position);
	Map playState();
	
    
    int playOrPause();
    int playPre();
    int playNext();
    
    int badMusic();
    int goodMusic();
}