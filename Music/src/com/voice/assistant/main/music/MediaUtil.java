package com.voice.assistant.main.music;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.sup.common.utl.EncryptMethodUtil;
import com.iii360.sup.common.utl.SuperBaseContext;
import com.voice.assistant.main.music.db.MusicDBHelper;
import com.voice.assistant.main.newmusic.MusicInfo;
import com.voice.assistant.main.newmusic.MusicInfoManager;
import com.voice.assistant.main.newmusic.MusicUtil;

public class MediaUtil {

	private static final String TAG = "Music MediaUtil";

	private String mAction = "";
	private String mName = "";
	private String mSingerName = "";

	private MediaInfoList mMediaList = new MediaInfoList();
	private Context mContext;
	private static final long FIVE_HOUR = 1 * 3600 * 1000 / 2;// 5小时内
	private static final long DAY_HOUR = 24 * 3600 * 1000;// 24小时内
	private SuperBaseContext baseContext = null;

	private int currentLocalMusicId = 0;

	private MusicDBHelper mMusicDBHelper;
	public static Map<String, MediaInfoList> mMediaLists = new HashMap<String, MediaInfoList>();

	public MediaUtil(Context context) {
		mContext = context;
		baseContext = new SuperBaseContext(context);
		mMediaList.setPlayMode(IMediaPlayerInterface.PLAY_MODE_LOOPALL);
		mMusicDBHelper = new MusicDBHelper(context);
	}

	public void setSinger(String singer) {
		mSingerName = singer;
	}

	public void setSongName(String song) {
		mName = song;
	}

	public void setAction(String action) {
		mAction = action;
	}

	public MediaInfoList getLocalList() {
		setPlayValue();
		return mMediaList;
	}

	public MediaInfoList getList() {
		return mMediaList;
	}

	public void setList(MediaInfoList infoList) {
		this.mMediaList = infoList;
		saveCurrentPlayList(infoList);
	}

	// 保存当前播放列表
	public void saveCurrentPlayList(MediaInfoList infoList) {
		mMediaLists.clear();
		String currentLocalMusicalId = baseContext.getPrefString(KeyList.CURRENT_LOCAL_MUSIC_ID);
		setCurrentLocalMusicId(infoList.isContainId(currentLocalMusicalId));
		mMediaLists.put("curr", infoList);
	}

	private void setPlayValue() {
		getSelectMusic();
	}

	// 设置当前播放歌曲为(LOCALMUSIC)本地播放时的歌曲ID
	public int getCurrentLocalMusicId() {
		return currentLocalMusicId;
	}

	public void setCurrentLocalMusicId(int currentLocalMusicId) {
		this.currentLocalMusicId = currentLocalMusicId;
	}

	public void parseCommand(CommandInfo arg) {
		mName = arg.getArg(1);
		mName = mName.trim();
		if (!mName.contains("[")) {
			// 本地音乐
			mSingerName = arg.getArg(2);
			mSingerName = mSingerName.trim();

			if (mName.contains("'")) {
				mName = mName.replace("'", "''");
			}
			mAction = arg.getArg(0);
			mAction = mAction.trim();

			IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
			gloableHeap.getGlobalIntegerMap().put(KeyList.GKEY_MEDIA_ROLE, KeyList.GKEY_MEDIA_ROLE_LOCAL);
			setPlayValue();
		} else {
			// 网络音乐
			getNetValueNew(arg, false);
		}

	}

	/**
	 * 更新数据库音乐信息
	 * 
	 * @param ids
	 * @param mMusicDBHelper
	 */
	private void updateSqlMusicInfos(final List<String> ids, final MusicDBHelper mMusicDBHelper) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				MusicInfoManager.updateLocalMusicInfoSqlLite(mContext, ids, mMusicDBHelper);
			}
		}.start();
	}

	/**
	 * 更新数据库音乐信息 更新喜爱的音乐的数据库信息
	 * 
	 * @param ids
	 */
	public void updateSqlMusicInfos(final List<String> ids) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				MusicInfoManager.updateLocalMusicInfoSqlLite(mContext, ids, mMusicDBHelper);
			}
		}.start();
	}

	private void getNetValueNew(CommandInfo arg, boolean isAppend) {
		ArrayList<MediaInfo> mediaValues = new ArrayList<MediaInfo>();
		if (arg._commandName != null) {

			mName = arg.getArg(1);
			if (mName == null) {
				return;
			}
			String status = arg.getArg(2);
			LogManager.e("status" + status);

			mName = mName.trim();
			mAction = arg.getArg(0);
			if (mAction != null) {
				mAction = mAction.trim();
			}
			String values[] = mName.split(",");
			boolean firstMusic = true;
			for (String value : values) {
				LogManager.e("value" + value);
				String names[] = value.split("\\[");
				MediaInfo mediaInfo = null;
				MusicInfo info;
				if (names.length == 3) {
					LogManager.e("names" + names[0]);
					String id = names[0];
					// 网络歌曲解析歌曲信息
					String m = names[1];
					m = m.replace("]", "");
					String ms[] = m.split("\\^");
					String singerName = m;
					String name = id;
					if (ms.length >= 2) {
						singerName = ms[0];
						name = ms[1];
					}
					String downLoadUrl = names[2].replace("]", "");

					if (!downLoadUrl.startsWith(MusicInfoManager.MY_OWN_NET_MUSIC_PATH)) {
						id = EncryptMethodUtil.generatePassword(downLoadUrl);
					}

					// 创建musicInfo
					if (MusicInfoManager.isHaveMusicInfo(id)) {
						info = MusicInfoManager.getMusicInfo(id);
					} else {
						info = new MusicInfo(id);
						MusicInfoManager.putMusicInfo(id, info);
					}
					info.mBaseNum -= 50;
					info.mSingerName = singerName;
					info.mName = name;
					// 第一首歌曲，与当前播放的歌曲同名不加入当前的播放列表
					String currentPlayMusicName = baseContext.getGlobalString(KeyList.CURRENT_PLAY_MUSIC_NAME);
					if (currentPlayMusicName != null && currentPlayMusicName.equals(name) && firstMusic && values.length > 1) {
						firstMusic = false;
						continue;
					}
					if (!MusicInfoManager.isHaveMusicInfo(info.mID)) {
						MusicInfoManager.putMusicInfo(info.mID, info);
					}
					File f1 = new File(MusicInfoManager.MUSIC_SAVE_POSE + id);
					File f2 = new File(MusicInfoManager.NET_MUSIC_PATH + id + ".mp3");
					// 创建mediaInfo
					mediaInfo = new MediaInfo(id);
					if (f1.exists()) {// 本地tts库
						mediaInfo._isFromNet = false;
						info.mDownLoadUri = f1.getPath();
					} else if (f2.exists()) {// 本地音乐库
						mediaInfo._isFromNet = false;
						info.mDownLoadUri = f2.getPath();
					} else {// 网络
						mediaInfo._isFromNet = true;
						info.mDownLoadUri = downLoadUrl;
					}
					mediaInfo._duration = "--:--";
					mediaInfo._path = info.mDownLoadUri;
					mediaInfo._name = info.mName;
					mediaInfo._singerName = info.mSingerName;
					mediaInfo._musicInfo = info;
					mMusicDBHelper.addCheck(mediaInfo);

					LogManager.e("add mediaInfo=" + mediaInfo.toString());

					// mediaInfo._isFromNet = false;
					if (status.equals("0") && names[0].startsWith("99999")) {// 笑话：以99999开头
						// 5小时内不会出现同一个笑话
						// if (System.currentTimeMillis() - info.mPlayTime <
						// FIVE_HOUR) {
						// info.mPlayTime = System.currentTimeMillis() -
						// FIVE_HOUR;
						// continue;
						// } else {
						info.mPlayTime = System.currentTimeMillis() - FIVE_HOUR;
						// mediaInfo._isFromNet = false;
						// }
						// 24小时内不会播两次笑话
						// if (System.currentTimeMillis() - info.mSecondPlayTime
						// < DAY_HOUR) {
						// info.mSecondPlayTime = System.currentTimeMillis() -
						// DAY_HOUR;
						// continue;
						// } else {
						info.mSecondPlayTime = System.currentTimeMillis() - DAY_HOUR;
						// }
					} else if (names[0].startsWith("98989")) {// 故事：以98989开头
						info.mPlayTime = System.currentTimeMillis() - FIVE_HOUR;
						info.mSecondPlayTime = System.currentTimeMillis() - DAY_HOUR;
					}
					mediaValues.add(mediaInfo);
				}
			}
			// 是否为指定歌曲
			String spec = arg.getArg(2);
			IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
			if (spec == null || spec.length() == 0) {
				// 本地曲目，大循环播放
				mMediaList.setPlayMode(IMediaPlayerInterface.PLAY_MODE_LOOPALL);

				gloableHeap.getGlobalIntegerMap().put(KeyList.GKEY_MEDIA_ROLE, KeyList.GKEY_MEDIA_ROLE_LOCAL);
			} else if (spec.equals("1")) {
				// 指定曲目，循环包
				mMediaList.setPlayMode(IMediaPlayerInterface.PLAY_MODE_LOOPSIGNAL);

				gloableHeap.getGlobalIntegerMap().put(KeyList.GKEY_MEDIA_ROLE, KeyList.GKEY_MEDIA_ROLE_SPEC);
			} else {
				// 范围曲库，网络错误时，大循环播放
				mMediaList.setPlayMode(IMediaPlayerInterface.PLAY_MODE_ALL);

				gloableHeap.getGlobalIntegerMap().put(KeyList.GKEY_MEDIA_ROLE, KeyList.GKEY_MEDIA_ROLE_SCOPE);
			}
			LogManager.e(mMediaList.size() + "mMediaList.size()");
			mMediaList.addAll(mediaValues);
			saveCurrentPlayList(mMediaList);
		}
	}

	public MediaInfoList getSelectMusic(String... ids) {
		mMediaList = mMediaLists.get("curr");
		int currentPosition = mMediaList.isContainId(ids[0]);
		if (mMediaList != null && currentPosition != -1) {
			mMediaList.setCurIndex(currentPosition);
			return mMediaList;
		} else {
			for (String id : ids) {
				// 读取收藏歌曲信息
				MusicInfo musicInfo;
				if (MusicInfoManager.isHaveMusicInfo(id)) {
					musicInfo = MusicInfoManager.getMusicInfo(id);
				} else {
					musicInfo = new MusicInfo(id);
					musicInfo.mName = id;
				}

				// 判断是否为网络歌曲
				File f1 = new File(MusicInfoManager.MUSIC_SAVE_POSE + id);
				File f2 = new File(MusicInfoManager.NET_MUSIC_PATH + id + ".mp3");
				MediaInfo mediaInfo;
				// 创建mediaInfo
				mediaInfo = new MediaInfo(id);
				if (f1.exists()) {// 本地tts库
					mediaInfo._isFromNet = false;
					musicInfo.mDownLoadUri = f1.getPath();
				} else if (f2.exists()) {// 本地音乐库
					mediaInfo._isFromNet = false;
					musicInfo.mDownLoadUri = f2.getPath();
				} else {// 网络
					mediaInfo._isFromNet = true;
				}
				mediaInfo._duration = "--:--";
				mediaInfo._path = musicInfo.mDownLoadUri;
				mediaInfo._name = musicInfo.mName;
				mediaInfo._singerName = musicInfo.mSingerName;
				mediaInfo._musicInfo = musicInfo;
				mMediaList.add(mediaInfo);
				saveCurrentPlayList(mMediaList);
			}
		}
		return mMediaList;
	}

	// 遍历sdcard中的localMusic文件夹中的所有文件
	public MediaInfoList scaneLocalMusicForInfos() {
		ArrayList<MediaInfo> mediaValues = new ArrayList<MediaInfo>();
		List<String> musicIds = new ArrayList<String>();
		File[] myAllFiles = new File(MusicInfoManager.MUSIC_SAVE_POSE).listFiles();
		if (myAllFiles != null) {
			for (File f : myAllFiles) {
				MediaInfo mediaInfo = new MediaInfo(f.getName(), f.getAbsolutePath(), "--:--", false);
				MusicInfo info;
				String id = f.getName();
				// LogManager.e("id" + id);
				if (id.endsWith("tmp")) {
					continue;
				}
				musicIds.add(id);
				if (MusicInfoManager.isHaveMusicInfo(id)) {
					info = MusicInfoManager.getMusicInfo(id);
				} else {
					info = new MusicInfo(id);
					MusicInfoManager.putMusicInfo(id, info);
				}
				mediaInfo._musicInfo = info;
				mediaInfo._Id = info.mID;
				mediaInfo._updateTime = f.lastModified();
				// 5个小时内同一首歌不会播放第二遍，24小时内不会播放第三遍
				// if (System.currentTimeMillis() - info.mPlayTime < FIVE_HOUR)
				// {
				// continue;
				// } else {
				// info.mPlayTime = 0;
				// }
				// if (System.currentTimeMillis() - info.mSecondPlayTime <
				// DAY_HOUR) {
				// continue;
				// } else {
				// info.mSecondPlayTime = 0;
				// }
				mediaValues.add(mediaInfo);
			}
		}
		// mMediaList.addAll(MusicInfoManager.reSortMusic(mediaValues, false,
		// 25, ""));
		mMediaList.addAll(mediaValues);
		/**
		 * 补全数据库音乐信息
		 */
		updateSqlMusicInfos(musicIds, mMusicDBHelper);
		return mMediaList;
	}

	// 遍历本地localMusic发给助手端
	public MediaInfoList getCurrentLocalMusicFiles() {
		ArrayList<MediaInfo> mediaValues = new ArrayList<MediaInfo>();
		List<String> musicIds = new ArrayList<String>();
		File[] myAllFiles = new File(MusicInfoManager.MUSIC_SAVE_POSE).listFiles();
		if (myAllFiles != null) {
			for (File f : myAllFiles) {
				MediaInfo mediaInfo = new MediaInfo(f.getName(), f.getAbsolutePath(), "--:--", false);
				MusicInfo info;
				String id = f.getName();
				if (id.endsWith("tmp")) {
					continue;
				}
				musicIds.add(id);
				if (MusicInfoManager.isHaveMusicInfo(id)) {
					info = MusicInfoManager.getMusicInfo(id);
				} else {
					info = new MusicInfo(id);
					MusicInfoManager.putMusicInfo(id, info);
				}
				mediaInfo._musicInfo = info;
				mediaInfo._Id = info.mID;
				mediaInfo._updateTime = f.lastModified();
				mediaValues.add(mediaInfo);
			}
		}
		MeidaInfoComparator comparator = new MeidaInfoComparator();
		Collections.sort(mediaValues, comparator);
		mMediaList.addAll(mediaValues);
		return mMediaList;
	}

	// 获取本地歌曲信息
	public MediaInfoList getSelectMusic() {
		mMediaList = scaneLocalMusicForInfos();
		saveCurrentPlayList(mMediaList);
		return mMediaList;
	}

	public static JSONObject mediaDesc(MediaInfo info, Context context) {

		MusicDBHelper db = new MusicDBHelper(context);
		String id = info._Id;
		LogManager.d(TAG, "start id = " + info._Id);
		if (id != null && id.startsWith("/sdcard/VoiceAssistant/localMusic")) {
			String[] endId = id.split("\\/");
			id = endId[endId.length - 1];
		}

		LogManager.d(TAG, "search id = " + id);
		List<MediaInfo> list = db.selectById(id);
		String songName = info._name == null ? "" : info._name;
		String singerName = info._singerName == null ? "" : info._singerName;

		// 记录当前播放方歌曲：包括本地歌曲，助手端推送歌曲，语音搜索歌曲
		new BaseContext(context).setGlobalString(KeyList.CURRENT_PLAY_MUSIC_NAME, info._name);
		LogManager.i(TAG, "mediaDesc songName=" + songName + "||singerName=" + singerName);

		if (list != null && !list.isEmpty()) {
			songName = list.get(0)._name;
			singerName = list.get(0)._singerName;
		} else {
			LogManager.e(TAG, "mediaDesc is null ");
		}

		try {
			JSONObject music = new JSONObject();
			music.put("id", info._Id);
			music.put("songName", songName);
			music.put("singerName", singerName);
			HashMap<String, MusicInfo> goodMusic = MusicUtil.goodMusic;
			if (goodMusic != null && goodMusic.containsKey(info._Id)) {
				info._isCollected = true;
			} else {
				info._isCollected = false;
			}
			music.put("isCollected", info._isCollected);
			return music;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
