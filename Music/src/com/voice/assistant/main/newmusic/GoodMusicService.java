package com.voice.assistant.main.newmusic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.common.MyApplication;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.voice.assistant.main.music.KeyList;
import com.voice.assistant.main.music.MediaInfo;
import com.voice.assistant.main.music.MediaUtil;
import com.voice.assistant.main.music.MyMusicHandler;
import com.voice.assistant.main.music.PlayMusicFromAssistantBox;

public class GoodMusicService extends Service {
	
	private final static String TAG = "Music GoodMusicService";
	
	private BasicServiceUnion mUnion;
	private IChangeListener onEndListen;
	private IChangeListener onBeginListen;

	public void onCreate() {
		MyApplication app = (MyApplication) getApplication();
		mUnion = app.getUnion();
	};

	IGoodMusicService.Stub mBinder = new IGoodMusicService.Stub() {
		@Override
		public void setPlayEndListen(IChangeListener onEnd) throws RemoteException {
			// TODO Auto-generated method stub
			onEndListen = onEnd;
		}

		@Override
		public void setPlayBeginListen(IChangeListener onBegin) throws RemoteException {
			// TODO Auto-generated method stub
			onBeginListen = onBegin;
		}

		@Override
		public String playMusic(String id) throws RemoteException {
			return new PlayMusicFromAssistantBox(GoodMusicService.this, mUnion).playMusicById(id, onBeginListen, onEndListen);
		}

		@Override
		public String getGoodMusics() throws RemoteException {
			// TODO Auto-generated method stub
			LogManager.d(TAG,"getGoodMusic");
			ArrayList<MusicInfo> infos = MusicUtil.getGoodMediaList(GoodMusicService.this);
			List<String> ids = new ArrayList<String>();
			JSONObject result = new JSONObject();
			JSONArray musics = new JSONArray();
			try {
				for (MusicInfo info : infos) {
					JSONObject music = MusicUtil.mediaDesc(info, GoodMusicService.this);
					musics.put(music);
					ids.add(info.mID);
				}
				result.put("musics", musics);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				LogManager.printStackTrace(e);
			}
			// 更新喜爱的音乐的数据库信息
			new MediaUtil(GoodMusicService.this).updateSqlMusicInfos(ids);
			LogManager.d(TAG, result.toString());
			return result.toString();
		}

		@Override
		public String deleteMusic(String id) throws RemoteException {
			// TODO Auto-generated method stub
			LogManager.d(TAG, "delete id" + id);
			mUnion.getBaseContext().setGlobalString(KeyList.GKEY_STR_CURRENT_MEDIAINFO, id);
			MusicUtil.logMusic(MusicUtil.COMMAND_BAD, mUnion.getBaseContext());
			return null;
		}

		@Override
		public Map playState() throws RemoteException {
			int state = 0;
			String dataString = "null";
			if (mUnion.getBaseContext().getPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN")
					|| mUnion.getBaseContext().getPrefBoolean("PKEY_CUREENT_MUSIC_IS_AIRPLAY")) {
				state = 3;// dlna
			} else {
				MyMusicHandler handler = (MyMusicHandler) mUnion.getMediaInterface();
				if (handler.isInPlayState()) {
					if (handler.isPlaying()) {
						state = 1;
					} else {
						state = 2;
					}
				} else {
					state = 0;
				}
				MediaInfo info = handler.getCurrentMediaInfo();
				if (info != null) {
					dataString = MediaUtil.mediaDesc(info, mUnion.getBaseContext().getContext()).toString();
				}

			}
			Map map = new HashMap();
			map.put("state", state);
			map.put("data", dataString);
			LogManager.d(TAG,"playState" + map.toString());
			return map;
		}

		@Override
		public int playOrPause() throws RemoteException {
			LogManager.d(TAG, "playOrPause");
			if (mUnion.getBaseContext().getPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN")) {
				return 0;
			}
			try {
				if (mUnion.getBaseContext().getGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING)
						&& mUnion.getBaseContext().getGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING)) {
					mUnion.getCommandEngine().handleText("暂停");
					return 2;
				} else {
					if (mUnion.getBaseContext().getGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING)) {
						// mBasicServiceUnion.getCommandEngine().handleText("继续");
						mUnion.getCommandEngine().handleText("继续");
					} else {
						mUnion.getCommandEngine().handleText("唱歌");
					}
					return 1;
				}
			} catch (Exception e) {
				return -1;
			}
		}

		@Override
		public int playPre() throws RemoteException {
			LogManager.d(TAG, "  playPre");
			try {
				mUnion.getCommandEngine().handleText("上一首");
			} catch (Exception e) {
				return -1;
			}
			return 1;
		}

		@Override
		public int playNext() throws RemoteException {
			LogManager.d(TAG, " playNext");
			try {
				mUnion.getCommandEngine().handleText("下一首");
			} catch (Exception e) {
				return -1;
			}
			return 1;
		}

		@Override
		public int badMusic() throws RemoteException {
			LogManager.d(TAG,"badMusic");
			try {
				mUnion.getCommandEngine().handleText("难听");
			} catch (Exception e) {
				return -1;
			}
			return 1;
		}

		@Override
		public int goodMusic() throws RemoteException {
			LogManager.d(TAG, "goodMusic");
			try {
				mUnion.getCommandEngine().handleText("好听");
			} catch (Exception e) {
				return -1;
			}
			return 1;
		}

		@Override
		public String getLocalMusics(int page) throws RemoteException {
			LogManager.d(TAG, "getLocalMusics");
			ArrayList<MusicInfo> infos = MusicUtil.getLocalMediaListScaneLocalMusic(GoodMusicService.this, page);
			List<String> ids = new ArrayList<String>();

			JSONObject result = new JSONObject();
			JSONArray musics = new JSONArray();
			try {
				for (MusicInfo info : infos) {
					JSONObject music = MusicUtil.mediaDesc(info, GoodMusicService.this);
					musics.put(music);
					ids.add(info.mID);
				}
				result.put("musics", musics);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				LogManager.printStackTrace(e);
			}
			LogManager.e(result.toString());
			return result.toString();
		}

		@Override
		public String getCurrentMusics(int page) throws RemoteException {
			ArrayList<MusicInfo> infos = MusicUtil.getCurrentMediaList(GoodMusicService.this, page);
			List<String> ids = new ArrayList<String>();
			JSONObject result = new JSONObject();
			JSONArray musics = new JSONArray();
			try {
				for (MusicInfo info : infos) {
					JSONObject music = MusicUtil.mediaDesc(info, GoodMusicService.this);
					musics.put(music);
					ids.add(info.mID);
				}
				result.put("musics", musics);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				LogManager.printStackTrace(e);
			}
			// 更新喜爱的音乐的数据库信息
			new MediaUtil(GoodMusicService.this).updateSqlMusicInfos(ids);
			LogManager.d(TAG, result.toString());
			return result.toString();
		}

		@Override
		public void setNetMusicResources(List<NetResourceMusicInfo> infos) throws RemoteException {
			LogManager.d(TAG, "助手端传递的喜马拉雅的音乐列表：" + infos.size() + "------" + infos.get(0).getName());
			new PlayMusicFromAssistantBox(GoodMusicService.this, mUnion).setNetResourceAndPalyFirst(infos, 0);
		}

		@Override
		public String playLocalMusic(String id, String currentPosition) throws RemoteException {
			// TODO Auto-generated method stub
			return new PlayMusicFromAssistantBox(GoodMusicService.this, mUnion).playLocalMusic(id, currentPosition,onBeginListen,onEndListen);
		}

		@Override
		public int setMusicForRemind(String id) throws RemoteException {
			return MusicUtil.setMusicForRemind(GoodMusicService.this,id);
		}

		@Override
		public void playGoodList(int position) throws RemoteException {
			MusicUtil.playGoodList(GoodMusicService.this, position,mUnion);
		}
		
		
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
}
