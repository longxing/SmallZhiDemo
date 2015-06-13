package com.voice.assistant.main.newmusic;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;

import com.base.upgrade.UpgradeSupport;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.sup.common.utl.date.FestvalUtil;
import com.iii360.sup.common.utl.file.DownloadProgressListener;
import com.iii360.sup.common.utl.file.FileDownloader;
import com.iii360.sup.common.utl.file.FileUtil;
import com.voice.assistant.main.music.MediaInfo;
import com.voice.assistant.main.music.MyMusicHandler;
import com.voice.assistant.main.music.db.MusicDBHelper;

@SuppressLint("SdCardPath")
public class MusicInfoManager {
	
	private final static String TAG = "Music MusicInfoManager";
	
	public static final String MUSIC_SAVE_POSE = "/sdcard/VoiceAssistant/localMusic/";
	public static final String NET_MUSIC_CACAHE_PATH = "/sdcard/VoiceAssistant/netMusic/";
	public static final String NET_MUSIC_PATH = "/sdcard/VoiceAssistant/music/";
	public static final String MY_OWN_NET_MUSIC_PATH = "http://cdnmusic.hezi.360iii.net/";
	private static Object obj = new Object();

	private static final HashMap<String, MusicInfo> MUSIC_INFOS = new HashMap<String, MusicInfo>();
	public static WifiUserData USER_DATA = new WifiUserData();
	static {
		try {
			String content = FileUtil.getFileLineContent("/sdcard/VoiceAssistant/models/userInfo", 0);
			USER_DATA.setFromString(content);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static boolean isMediaExist(MusicInfo info) {
		File f = new File(MUSIC_SAVE_POSE + info.mID);
		return f.exists() && !f.isDirectory();
	}

	public static void updateLocalMedia(List<MusicInfo> infos, final BasicServiceUnion mUnion) {
		LogManager.d(TAG,"updateLocalMedia size = " + infos.size() + "");
		final MusicDBHelper musicDb = new MusicDBHelper(mUnion.getBaseContext().getContext());
		File[] musics = new File(MUSIC_SAVE_POSE).listFiles();
		final HashMap<String, String> hash = new HashMap<String, String>();
		if (musics != null) {
			for (File music : musics) {
				hash.put(music.getName(), "");
			}
		}
		// 排除本地已有歌曲，作为最终需要下载的歌曲项
		final ArrayList<MusicInfo> needDownLoad = new ArrayList<MusicInfo>();
		for (MusicInfo info : infos) {
			if (hash.containsKey(info.mID)) {
				hash.remove(info.mID);
				MUSIC_INFOS.put(info.mID, info);
			} else {
				needDownLoad.add(info);
			}
		}
		// 先删掉本次更新不包含的歌曲
		for (String name : hash.keySet()) {
			LogManager.d(TAG, "updateLocalMedia full delete");
			File f = new File(MUSIC_SAVE_POSE + name);
			musicDb.deleteMusicInfoById(name);
			f.delete();
		}
		// 记录需要下载的歌曲的个数
		setMusiscNumbers(mUnion, needDownLoad.size());
		LogManager.d(TAG, "updateLocalMedia, 排除本地已有，needDownLoad totalSize=" + needDownLoad.size());
		// 下载歌曲
		for (final MusicInfo info : needDownLoad) {
			LogManager.d(TAG, "updateLocalMedia needDownLoad   " + info.mName + info.mDownLoadUri);
			LogManager.d(TAG,"updateLocalMedia " + info.mDownLoadUri);
			try {
				FileDownloader fileDownloader = new FileDownloader(info.mDownLoadUri, new File(MUSIC_SAVE_POSE), 5, info.mID);
				fileDownloader.download(new DownloadProgressListener() {
					@Override
					public void onDownloadSize(String fileUrl, int size) {
						// LogManager.d(TAG, "updateLocalMedia " + info.mID + "  " + size);
						if (size < 0) {
							descMusicNumbers(mUnion);
							LogManager.e(TAG, "updateLocalMedia onDownloadError:" + fileUrl);
						}
					}

					@Override
					public void onDownloadResultSuccess(String fileUrl, String filename) {
						// TODO Auto-generated method stub
						LogManager.d(TAG, " updateLocalMedia downloadNumber:" + getMusiscNumbers(mUnion) + "musicId:" + info.mID + "musicNmae:" + filename);
						descMusicNumbers(mUnion);
						try {
							// 信息插入数据库
							MediaInfo mediaInfo = new MediaInfo(null, null);
							mediaInfo._Id = info.mID;
							mediaInfo._name = info.mName;
							mediaInfo._singerName = info.mSingerName;
							mediaInfo._isFromNet = false;
							mediaInfo._path = "sdcard/VoiceAssistant/localMusic/" + info.mID;
							mediaInfo._updateTime = System.currentTimeMillis();
							musicDb.add(mediaInfo);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							LogManager.e(TAG, "updateLocalMedia load 200 music add to db error");
						}
						MUSIC_INFOS.put(info.mID, info);
						// 下载完成
						if (getMusiscNumbers(mUnion) == 0) {
							LogManager.d(TAG, "updateLocalMedia musics update finish");
							// 满足正在播放+本地曲目， 更新歌曲库
							if (mUnion.getBaseContext().getGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING)
									&& mUnion.getBaseContext().getGlobalInteger(KeyList.GKEY_MEDIA_ROLE) == KeyList.GKEY_MEDIA_ROLE_LOCAL) {
								MyMusicHandler myMusicHandler = (MyMusicHandler) mUnion.getMediaInterface();
								myMusicHandler.setOnComplation(new MyMusicHandler.OnCompletionListener() {
									@Override
									public boolean onCompletion(MediaPlayer mp) {
										mUnion.getCommandEngine().handleText("唱歌");
										return false;
									}

								});
							}
							// 允许自动更新
							UpgradeSupport.removeTask(mUnion.getBaseContext().getContext(), "UPGRADE_TASK_MEDIA_LIB");
						}
					}

					@Override
					public void onDownloadError(String fileUrl, String filename, File tempfilename, boolean reload) {
						// TODO Auto-generated method stub
						if (tempfilename.exists()) {
							tempfilename.delete();
						}
						if (reload) {
							addMusicNumbers(mUnion);
						} else {
							descMusicNumbers(mUnion);
						}
						LogManager.e(TAG,"onDownloadError:" + filename + "---" + reload);
					}

					@Override
					public void onDownloadCancle(String fileUrl, String filename) {
						// TODO Auto-generated method stub
						LogManager.e(TAG, "onDownloadCancle:" + filename);
					}

				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LogManager.e(e.toString());
			}
			LogManager.d(TAG, "updateLocalMedia excuterd");
		}

	}

	// 减少音乐单的数量
	public static void descMusicNumbers(BasicServiceUnion mUnion) {
		synchronized (obj) {
			int downloadNumber = mUnion.getBaseContext().getGlobalInteger(KeyList.LOCAL_MUSIC_LOAD_COUNT);
			mUnion.getBaseContext().setGlobalInteger(KeyList.LOCAL_MUSIC_LOAD_COUNT, downloadNumber - 1);
		}
	}

	// 增加音乐单的数量
	public static void addMusicNumbers(BasicServiceUnion mUnion) {
		synchronized (obj) {
			int downloadNumber = mUnion.getBaseContext().getGlobalInteger(KeyList.LOCAL_MUSIC_LOAD_COUNT);
			mUnion.getBaseContext().setGlobalInteger(KeyList.LOCAL_MUSIC_LOAD_COUNT, downloadNumber + 1);
		}
	}

	// 获取要更新的音乐单的数量
	public static int getMusiscNumbers(BasicServiceUnion mUnion) {
		synchronized (obj) {
			return mUnion.getBaseContext().getGlobalInteger(KeyList.LOCAL_MUSIC_LOAD_COUNT);
		}
	}

	// 设置要更新的音乐单的数量
	public static void setMusiscNumbers(BasicServiceUnion mUnion, int size) {
		synchronized (obj) {
			mUnion.getBaseContext().setGlobalInteger(KeyList.LOCAL_MUSIC_LOAD_COUNT, size);
		}

	}

	/**
	 * 补全本地歌曲信息
	 * 
	 * @param infos
	 * @param mUnion
	 */
	public static void updateLocalMusicInfoSqlLite(Context context, List<String> musicIds, MusicDBHelper musicDb) {
		List<String> needUpdateInfos = new ArrayList<String>();
		for (String id : musicIds) {
			List<MediaInfo> selectResults = musicDb.selectById(id);
			if (!(selectResults != null && selectResults.size() > 0)) {
				needUpdateInfos.add(id);
				LogManager.d(TAG, "need update sqlMusic info id =" + id);
			}
		}
		if (needUpdateInfos.size() > 0) {
			// 向网络请求音乐信息
			List<MusicInfo> updateMusicInfos = UpdateOrDownMusicInfos.updateMusicInfosInList("http://hezi.360iii.net:48080/webapi/queryMusic_queryMusic?", needUpdateInfos);
			if (updateMusicInfos != null && updateMusicInfos.size() > 0) {
				for (MusicInfo musicInfo : updateMusicInfos) {
					// 信息插入数据库
					MediaInfo mediaInfo = new MediaInfo(null, null);
					mediaInfo._Id = musicInfo.mID;
					mediaInfo._name = musicInfo.mName;
					mediaInfo._singerName = musicInfo.mSingerName;
					mediaInfo._isFromNet = false;
					mediaInfo._path = "sdcard/VoiceAssistant/localMusic/" + musicInfo.mID;
					mediaInfo._updateTime = System.currentTimeMillis();
					musicDb.add(mediaInfo);
				}
				LogManager.d(TAG, "update local music infos：size =" + updateMusicInfos.size());
			}
		} else {
			LogManager.d(TAG, "update local music infos no size ");
		}

	};

	//
	public static List<MediaInfo> reSortMusic(ArrayList<MediaInfo> infos, boolean sex, int age, String model) {
		LogManager.d(TAG, "reSortMusic infos.size()" + infos.size());
		if (USER_DATA != null) {
			sex = USER_DATA.getSex().equals("男");
			String birthDay = USER_DATA.getBirth();
			if (birthDay != null && birthDay.length() == 8) {
				try {
					int birth = Integer.valueOf(birthDay);
					birth = birth / 10000;
					age = Calendar.getInstance().get(Calendar.YEAR) - birth;
					LogManager.d(TAG, sex + " sex&age " + age);
				} catch (Exception e) {
					// TODO: handle exception
					LogManager.printStackTrace(e);
				}
			}
		}
		int RANDOM_NUMBER = 16;
		int AGE = 10;
		int MODEL = 10;
		int TIME = 10;
		int SEX_DISTANSE = 14;
		int FASTVAL = 18;

		String festval = FestvalUtil.getCurrentFestval();
		// LogManager.e(festval);

		for (MediaInfo info : infos) {
			info._musicInfo.mScore = 0;
			if (info._musicInfo.isAgeFit(age)) {
				info._musicInfo.mScore += AGE;
			}
			if (info._musicInfo.isSexFit(sex)) {
				info._musicInfo.mScore += SEX_DISTANSE;
			}

			if (info._musicInfo.isMoodFit(model)) {
				info._musicInfo.mScore += MODEL;
			}

			if (info._musicInfo.isTimeFit()) {
				info._musicInfo.mScore += TIME;
			}

			if (info._musicInfo.isHolidayFit(festval)) {
				info._musicInfo.mScore += FASTVAL;
			}

			info._musicInfo.mScore += new Random().nextInt(RANDOM_NUMBER);
		}
		ArrayList<MediaInfo> ii = new ArrayList<MediaInfo>();
		HashMap<Integer, ArrayList<MediaInfo>> tempHash = new HashMap<Integer, ArrayList<MediaInfo>>();

		for (MediaInfo info : infos) {
			int score = info._musicInfo.mBaseNum + info._musicInfo.mScore;
			if (score > 200) {
				score = 200;
			}
			if (score < -2000) {
				score = -2000;
			}
			if (tempHash.containsKey(score)) {
				tempHash.get(score).add(info);

			} else {
				ArrayList<MediaInfo> tempinfos = new ArrayList<MediaInfo>();
				tempinfos.add(info);
				tempHash.put(score, tempinfos);
			}

		}
		for (int i = 200; i >= -2000; i--) {
			if (tempHash.containsKey(i)) {
				ii.addAll(tempHash.get(i));
			}
		}
		tempHash.clear();
		LogManager.d(TAG, "ii.size()" + ii.size());
		return ii;
	}

	public static boolean isHaveMusicInfo(String id) {
		return MUSIC_INFOS.containsKey(id);
	}

	public static MusicInfo getMusicInfo(String id) {
		return MUSIC_INFOS.get(id);
	}

	public static void putMusicInfo(String id, MusicInfo info) {
		MUSIC_INFOS.put(id, info);
	}

}
