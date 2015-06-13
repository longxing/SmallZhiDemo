package com.voice.assistant.main.newmusic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.sup.common.utl.NetWorkUtil;
import com.iii360.sup.common.utl.ShellUtils;
import com.iii360.sup.common.utl.SystemUtil;
import com.iii360.sup.common.utl.file.FileUtil;
import com.iii360.sup.common.utl.file.ObjUtil;
import com.voice.assistant.main.music.MediaInfo;
import com.voice.assistant.main.music.MediaInfoList;
import com.voice.assistant.main.music.MediaUtil;
import com.voice.assistant.main.music.PlayMusicFromAssistantBox;
import com.voice.assistant.main.music.db.MusicDBHelper;

public class MusicUtil {

	private final static String TAG = "Music MusicUtil";

	public static final int COMMAND_EXIT = 0;
	public static final int COMMAND_STOP = 1;
	public static final int COMMAND_START = 2;
	public static final int COMMAND_NEXT = 3;
	public static final int COMMAND_PRE = 4;
	public static final int COMMAND_RANDOM = 5;
	public static final int COMMAND_CIRCLE = 6;
	public static final int COMMAND_GOOD = 7;
	public static final int COMMAND_BAD = 8;
	public static final int COMMAND_UNPLAY = -1;
	private static int pageSizes = 10;

	private static final String goodMusicFile = "/mnt/sdcard/com.voice.assistant.main/models/goodmusic";
	public static HashMap<String, MusicInfo> goodMusic;
	public static HashMap<String, MediaInfoList> tempMediaInfoList = new HashMap<String, MediaInfoList>();
	static {

		try {
			goodMusic = (HashMap<String, MusicInfo>) ObjUtil.loadFrom(goodMusicFile);
			if (goodMusic != null) {
				for (MusicInfo info : goodMusic.values()) {
					if (!MusicInfoManager.isHaveMusicInfo(info.mID)) {
						MusicInfoManager.putMusicInfo(info.mID, info);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace();
		}
	}

	static String id;

	public static void logMusic(int oprite, final BaseContext mBaseContext) {
		id = mBaseContext.getGlobalString(KeyList.GKEY_STR_CURRENT_MEDIAINFO);
		LogManager.d(TAG, "old id = " + id);
		try {
			if (!TextUtils.isEmpty(id) && id.startsWith(MusicInfoManager.MUSIC_SAVE_POSE)) {
				String[] endId = id.split("\\/");
				id = endId[endId.length - 1];
			} else if (!TextUtils.isEmpty(id) && id.startsWith(MusicInfoManager.MY_OWN_NET_MUSIC_PATH)) {
				String[] endId = id.split("\\/");
				id = endId[endId.length - 1];
				if (id.endsWith(".mp3")) {
					id = id.substring(0, id.indexOf("."));
				}
			}

			LogManager.d(TAG, "new id = " + id);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String s = SystemUtil.getDeviceId() + "|" + System.currentTimeMillis() + "|" + id + "|" + oprite + "|" + MusicInfoManager.USER_DATA.getSex() + "|" + 1;
		s = s.replaceAll("null", "");

		LogManager.forceOutput(s, KeyList.FKEY_MEDIA_INFO_FILE);
		final String id = mBaseContext.getGlobalString(KeyList.GKEY_STR_CURRENT_MEDIAINFO);
		if (oprite == COMMAND_BAD) {
			if (goodMusic != null) {
				goodMusic.remove(id);
				try {
					ObjUtil.saveTo(goodMusicFile, goodMusic);
				} catch (IOException e) {
					// e.printStackTrace();
					LogManager.printStackTrace(e);
				}
			}
		} else if (oprite == COMMAND_GOOD) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					MusicInfo info = null;
					if (MusicInfoManager.isHaveMusicInfo(id)) {
						info = MusicInfoManager.getMusicInfo(id);
					} else {
						info = new MusicInfo(id);
					}
					if (TextUtils.isEmpty(info.mName) || TextUtils.isEmpty(info.mSingerName)) {
						InputStreamReader reader = NetWorkUtil.getNetworkInputStreamReader("http://hezi.360iii.net:48080/webapi/boxsysteminfo_getRedSongInfo?songId=" + id);
						if (reader != null) {
							BufferedReader br = new BufferedReader(reader);
							try {
								String content = br.readLine();
								JSONObject json = new JSONObject(content);
								String status = json.getString("state");
								if (status.equals("1")) {
									String singerName = json.getString("singerName");
									String songName = json.getString("songName");
									info.mSingerName = singerName;
									info.mName = songName;

									// 保存sqlite歌曲信息
									new MusicDBHelper(mBaseContext.getContext()).addCheck(info.convertMediaInfo());
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								LogManager.printStackTrace(e);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								LogManager.printStackTrace(e);
							}
						}
					}
					if (goodMusic == null) {
						goodMusic = new HashMap<String, MusicInfo>();
					}
					goodMusic.put(id, info);
					try {
						ObjUtil.saveTo(goodMusicFile, goodMusic);
					} catch (IOException e) {
						// e.printStackTrace();
						LogManager.printStackTrace(e);
					}
				}
			}).start();
		}
		LogManager.e(s);
	}

	public static ArrayList<String> getPlayFileList() {
		ArrayList<String> infos = FileUtil.getFileContent(KeyList.FKEY_MEDIA_INFO_FILE);
		ArrayList<String> result = new ArrayList<String>();
		if(null != infos && !infos.isEmpty()){
			for (String s : infos) {
				String[] contents = s.split("\\|");
				if (contents.length > 3) {
					result.add(contents[2]);
				}
			}
		}	
		return result;
	}

	public static ArrayList<MusicInfo> getGoodMediaList(Context context) {
		MusicDBHelper db = new MusicDBHelper(context);
		ArrayList<MusicInfo> infos = new ArrayList<MusicInfo>();
		boolean needUpdate = false;
		if (goodMusic != null) {
			for (String id : goodMusic.keySet()) {
				if (id != null) {
					MusicInfo info = goodMusic.get(id);
					List<MediaInfo> list = db.selectById(id);
					if (list != null && !list.isEmpty()) {
						if (info.mName != list.get(0)._name) {
							info.mName = list.get(0)._name;
							info.mSingerName = list.get(0)._singerName;
							info.mDownLoadUri = list.get(0)._path;
							needUpdate = true;
						}
					}
					infos.add(info);
				}
			}
		}
		if (needUpdate) {
			try {
				ObjUtil.saveTo(goodMusicFile, goodMusic);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return infos;
	}

	//
	public static ArrayList<MusicInfo> getLocalMediaList(Context context, int page) {
		MusicDBHelper db = new MusicDBHelper(context);
		ArrayList<MusicInfo> infos = new ArrayList<MusicInfo>();
		List<MediaInfo> list = db.selectByLocal(page);
		MediaInfoList mMediaList = new MediaInfoList();
		mMediaList.addAll(list);
		tempMediaInfoList.put("tempMediaInfoList", mMediaList);
		for (MediaInfo info : list) {
			MusicInfo info2 = new MusicInfo(info._Id);
			info2.mName = info._name;
			info2.mSingerName = info._singerName;
			info2._isCollected = info._isCollected;
			infos.add(info2);
		}
		return infos;
	}

	// 遍历本地文件夹获取本地歌曲
	public static ArrayList<MusicInfo> getLocalMediaListScaneLocalMusic(Context context, int page) {
		MediaUtil mediaUtil = new MediaUtil(context);
		MediaInfoList mMediaList = mediaUtil.getCurrentLocalMusicFiles();
		tempMediaInfoList.put("tempMediaInfoList", mMediaList);
		ArrayList<MusicInfo> infos = new ArrayList<MusicInfo>();
		HashMap<String, MusicInfo> goodMusic = MusicUtil.goodMusic;
		List<MediaInfo> mediaInfos = mMediaList.getAll();
		int pg = 0;
		if (mediaInfos.size() % pageSizes == 0) {
			pg = mediaInfos.size() / pageSizes;
		} else {
			pg = mediaInfos.size() / pageSizes + 1;
		}
		if (page > pg) {
			return infos;
		}
		ArrayList<MediaInfo> mis = new ArrayList<MediaInfo>();
		if (page == pg) {
			mis.addAll(mediaInfos.subList((page - 1) * pageSizes, mediaInfos.size()));
		} else {
			mis.addAll(mediaInfos.subList((page - 1) * pageSizes, page * pageSizes));
		}
		for (MediaInfo info : mis) {
			MusicInfo info2 = new MusicInfo(info._Id);
			info2.mName = info._name;
			info2.mSingerName = info._singerName;
			if (goodMusic != null && goodMusic.containsKey(info._Id)) {
				info._isCollected = true;
			} else {
				info._isCollected = false;
			}
			info2._isCollected = info._isCollected;
			infos.add(info2);
		}
		return infos;
	}

	public static ArrayList<MusicInfo> getCurrentMediaList(Context context, int page) {
		ArrayList<MediaInfo> mediaInfos = (ArrayList<MediaInfo>) MediaUtil.mMediaLists.get("curr").getAll();
		LogManager.d(TAG, "当前播放列表的大小-------------：" + mediaInfos.size());
		int pg = 0;
		if (mediaInfos.size() % pageSizes == 0) {
			pg = mediaInfos.size() / pageSizes;
		} else {
			pg = mediaInfos.size() / pageSizes + 1;
		}
		ArrayList<MusicInfo> infos = new ArrayList<MusicInfo>();
		if (page > pg) {
			return infos;
		}
		ArrayList<MediaInfo> mis = new ArrayList<MediaInfo>();
		if (page == pg) {
			mis.addAll(mediaInfos.subList((page - 1) * pageSizes, mediaInfos.size()));
		} else {
			mis.addAll(mediaInfos.subList((page - 1) * pageSizes, page * pageSizes));
		}

		HashMap<String, MusicInfo> goodMusic = MusicUtil.goodMusic;
		for (MediaInfo info : mis) {
			MusicInfo info2 = new MusicInfo(info._Id);
			info2.mName = info._name;
			info2.mSingerName = info._singerName;
			if (goodMusic != null && goodMusic.containsKey(info._Id)) {
				info._isCollected = true;
			} else {
				info._isCollected = false;
			}
			info2._isCollected = info._isCollected;
			infos.add(info2);
		}
		return infos;
	}

	public static JSONObject mediaDesc(MusicInfo info, Context context) {
		MusicDBHelper db = new MusicDBHelper(context);
		String id = info.mID;
		LogManager.d(TAG, "MusicUtil start id = " + info.mID);
		// /sdcard/VoiceAssistant/localMusic/773
		if (id.startsWith("/sdcard/VoiceAssistant/localMusic")) {
			String[] endId = id.split("\\/");
			id = endId[endId.length - 1];
		}
		LogManager.d(TAG, "MusicUtil search id = " + id);

		List<MediaInfo> list = db.selectById(id);
		String songName = info.mName == null ? "" : info.mName;
		String singerName = info.mSingerName == null ? "" : info.mSingerName;

		if (list != null && !list.isEmpty()) {
			songName = list.get(0)._name;
			singerName = list.get(0)._singerName;

			LogManager.d(TAG, "MusicUtil mediaDesc songName=" + songName + "||singerName=" + singerName);

		} else {
			LogManager.d(TAG, "MusicUtil mediaDesc is null ");

		}

		info.mName = songName;
		info.mSingerName = singerName;

		try {
			JSONObject music = new JSONObject();
			music.put("id", info.mID);
			music.put("songName", info.mName);
			music.put("singerName", info.mSingerName);
			music.put("isCollected", info._isCollected);
			return music;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int setMusicForRemind(Context context, String id) {
		MusicDBHelper db = new MusicDBHelper(context);
		List<MediaInfo> list = db.selectById(id);
		if (list != null && list.size() > 0) {
			MediaInfo info = list.get(0);
			try {
				File file = new File(info._path);
				if (file != null && file.exists()) {
					String path = "/mnt/sdcard/VoiceAssistant/RingTone";
					if (!new File(path).exists()) {
						new File(path).mkdirs();
					}
					ShellUtils.execute(false, "su", "-c", "cp", "-fr", file.getAbsolutePath(), path);
					// result=ShellUtils.execute(false, "su", "-c",
					// "mv",path+"/"+file.getName(),path+"/"+id);
					new BaseContext(context).setPrefString("KEY_RING_FOR_REMIND", id);
					for (File f : new File(path).listFiles()) {
						if (!f.getName().equals(id)) {
							f.delete();
						}
					}
				} else {
					return -1;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
			return 1;
		} else {
			return -1;
		}
	}

	public static void playGoodList(Context context, int position, BasicServiceUnion union) {
		MediaInfoList infoList = new MediaInfoList();
		List<MediaInfo> minfoList = new ArrayList<MediaInfo>();
		MusicDBHelper db = new MusicDBHelper(context);
		for (String id : goodMusic.keySet()) {
			if (id != null) {
				MusicInfo info = goodMusic.get(id);
				List<MediaInfo> list = db.selectById(id);
				if (list != null && !list.isEmpty()) {
					info.mName = list.get(0)._name;
					info.mSingerName = list.get(0)._singerName;
					info.mDownLoadUri = list.get(0)._path;
					list.get(0)._musicInfo = info;
					minfoList.add(list.get(0));
				} else {
					MediaInfo mediaInfo = new MediaInfo(id);
					mediaInfo._name = info.mName;
					mediaInfo._singerName = info.mSingerName;
					mediaInfo._path = info.mDownLoadUri;
					mediaInfo._musicInfo = info;
					minfoList.add(mediaInfo);
				}
			}
		}
		infoList.addAll(minfoList);
		new PlayMusicFromAssistantBox(context, union).playMedia(infoList, position);

	}
}
