package com.voice.assistant.main.music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.RemoteException;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.IMediaInterface;
import com.iii360.base.inf.IMediaInterface.OnEndOfLoopListener;
import com.iii360.sup.common.utl.EncryptMethodUtil;
import com.voice.assistant.main.newmusic.IChangeListener;
import com.voice.assistant.main.newmusic.MusicInfo;
import com.voice.assistant.main.newmusic.MusicInfoManager;
import com.voice.assistant.main.newmusic.MusicUtil;
import com.voice.assistant.main.newmusic.NetResourceMusicInfo;

public class PlayMusicFromAssistantBox {

	private static final String TAG = "Music PlayMusicFromAssistantBox";

	private static final String GOOD_MUSIC_BEGIN = "com.voice.assistant.main.GOOD_MUSIC_BEGIN";
	private static final String GOOD_MUSIC_END = "com.voice.assistant.main.GOOD_MUSIC_END";

	private MediaUtil mediaUtil;
	private Context mContext;
	private BasicServiceUnion union;
	// 音乐播放类
	private MediaPlayerService mMusicControlService;

	public PlayMusicFromAssistantBox(Context mContext, BasicServiceUnion union) {
		this.mContext = mContext;
		this.union = union;
		mMusicControlService = MediaPlayerService.getInstance(mContext);
	}

	public void setNetResourceAndPalyFirst(List<NetResourceMusicInfo> infos, int position) {
		MediaInfoList medialist = new MediaInfoList();
		ArrayList<MediaInfo> meidainfos = new ArrayList<MediaInfo>();
		MusicInfo info = null;
		MediaInfo mediaInfo = null;
		for (int i = 0; i < infos.size(); i++) {
			// 创建musicInfo 喜马拉雅将url作为唯一的id
			String id = infos.get(i).getMusicUrl();
			if (!id.startsWith(MusicInfoManager.MY_OWN_NET_MUSIC_PATH)) {
				id = EncryptMethodUtil.generatePassword(id);
			}
			if (MusicInfoManager.isHaveMusicInfo(id)) {
				info = MusicInfoManager.getMusicInfo(id);
			} else {
				info = new MusicInfo(id);
				MusicInfoManager.putMusicInfo(id, info);
			}
			info.mBaseNum -= 50;
			info.mSingerName = infos.get(i).getAuthor();
			info.mName = infos.get(i).getName();

			// 第一首歌曲，与当前播放的歌曲同名不加入当前的播放列表
			String currentMuaicName = new BaseContext(mContext).getGlobalString(KeyList.CURRENT_PLAY_MUSIC_NAME);
			if (currentMuaicName != null && currentMuaicName.equals(info.mName) && i == 0 && infos.size() != 1) {
				LogManager.i(TAG, "current music the same before,skip index:" + i + "----name:" + info.mName);
				continue;
			}
			LogManager.i(TAG, "current music the different before index:" + i + "----name:" + info.mName);
			if (!MusicInfoManager.isHaveMusicInfo(info.mID)) {
				MusicInfoManager.putMusicInfo(info.mID, info);
			}
			File f1 = new File(MusicInfoManager.MUSIC_SAVE_POSE + id);
			File f2 = new File(MusicInfoManager.NET_MUSIC_PATH + id + ".mp3");
			// 创建mediaInfo
			mediaInfo = new MediaInfo(id);
			if (f1.exists()) {
				mediaInfo._isFromNet = false;
				info.mDownLoadUri = f1.getPath();
			} else if (f2.exists()) {// 本地音乐库
				mediaInfo._isFromNet = false;
				info.mDownLoadUri = f2.getPath();
			} else {// 网络
				mediaInfo._isFromNet = true;
				info.mDownLoadUri = infos.get(i).getMusicUrl();
			}
			mediaInfo._name = info.mName;
			mediaInfo._singerName = info.mSingerName;
			mediaInfo._duration = "--:--";
			mediaInfo._path = info.mDownLoadUri;
			mediaInfo._musicInfo = info;
			meidainfos.add(mediaInfo);
		}
		medialist.addAll(meidainfos);
		LogManager.d(TAG, "send to box net music success");
		playMedia(medialist, 0);
	}

	public void playMedia(final MediaInfoList listInfos, final int position) {
		// TODO Auto-generated method stub
		union.getBaseContext().setGlobalInteger(KeyList.GKEY_PLAY_TYPE, KeyList.GKEY_PLAY_TYPE_MUSIC);
		mediaUtil = new MediaUtil(mContext);
		mediaUtil.setList(listInfos);
		listInfos.setCurIndex(position);
		listInfos.setPlayMode(IMediaPlayerInterface.PLAY_MODE_LOOPALL);
		union.getMediaInterface().setOnEndOfLoopListener(new OnEndOfLoopListener() {
			@Override
			public boolean onEndOfLoop() {
				LogManager.i(TAG,"current music play complete!");
				return true;
			}
		});
		union.getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_PLAY_WELCOME, false);
		union.getRecogniseSystem().stopWakeup();
		union.getMediaInterface().setMediaInfoList(listInfos);
		union.getMediaInterface().setPlayType(1);
		union.getMediaInterface().start();
		LogManager.d(TAG, "开始播放喜马拉雅网络歌曲！");
	}

	/**
	 * 播放本地歌曲，根据当前的列表的索引，或者id
	 * 
	 * @param id
	 * @param currentPosition
	 * @param onBeginListen
	 * @param onEndListen
	 * @return
	 */
	public String playLocalMusic(String id, String currentPosition, final IChangeListener onBeginListen, final IChangeListener onEndListen) {
		LogManager.d(TAG, "playLocalMusic ,play id" + id);
		union.getBaseContext().getContext().sendBroadcast(new Intent("AKEY_TO_DLAN_MUSIC_STOP"));
		union.getBaseContext().getContext().sendBroadcast(new Intent("IKEY_MEDIA_STOP"));
		IMediaInterface handler = union.getMediaInterface();
		MediaInfoList mediaInfoList = MusicUtil.tempMediaInfoList.get("tempMediaInfoList");
		MediaUtil.mMediaLists.put("curr", mediaInfoList);
		List<MediaInfo> listInfos = mediaInfoList.getAll();
		if (listInfos == null || mediaInfoList == null) {
			return "";
		}
		LogManager.d(TAG, "playLocalMusic, mediainfo---- size---" + listInfos.size());
		for (int i = 0; i < listInfos.size(); i++) {
			if (id.equals(listInfos.get(i)._Id)) {
				currentPosition = String.valueOf(i);
			}
		}
		mediaInfoList.setCurIndex(Integer.parseInt(currentPosition));
		mediaInfoList.setPlayMode(IMediaPlayerInterface.PLAY_MODE_LOOPALL);
		handler.setOnComplation(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				try {
					if (onEndListen != null) {
						onEndListen.onCommandChange();
					}
				} catch (RemoteException e) {
					LogManager.printStackTrace();
				}
				senCustomBroadCast(false);
			}
		});

		handler.setOnError(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				try {
					if (onEndListen != null) {
						onEndListen.onCommandChange();
					}
				} catch (RemoteException e) {
					LogManager.printStackTrace();
				}
				senCustomBroadCast(false);
				return false;
			}
		});

		handler.setOnParePare(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				try {
					if (onBeginListen != null) {
						onBeginListen.onCommandChange();
					}

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					LogManager.printStackTrace();
				}
				senCustomBroadCast(true);
			}
		});
		handler.setMediaInfoList(mediaInfoList);
		// 不需要欢迎词
		union.getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_PLAY_WELCOME, false);
		handler.start();
		return MediaUtil.mediaDesc(mediaInfoList.get(Integer.parseInt(currentPosition)), mContext).toString();
	}

	/**
	 * 原来的播放喜爱歌曲的接口，根据id播放歌曲
	 * 
	 * @param id
	 * @param onBeginListen
	 * @param onEndListen
	 * @return
	 */

	public String playMusicById(String id, final IChangeListener onBeginListen, final IChangeListener onEndListen) {
		LogManager.d(TAG, "playMusicById play id" + id);
		union.getBaseContext().getContext().sendBroadcast(new Intent("AKEY_TO_DLAN_MUSIC_STOP"));
		union.getBaseContext().getContext().sendBroadcast(new Intent("IKEY_MEDIA_STOP"));
		IMediaInterface handler = union.getMediaInterface();
		MediaInfoList mediaInfoList = MediaUtil.mMediaLists.get("curr");
		int position = -1;
		if (mediaInfoList != null && (position = mediaInfoList.isContainId(id)) != -1) {
			mediaInfoList.setCurIndex(position);
			mediaInfoList.setPlayMode(IMediaPlayerInterface.PLAY_MODE_LOOPALL);
		} else {
			mediaInfoList = new MediaUtil(mContext).getSelectMusic(id);
			mediaInfoList.setPlayMode(IMediaPlayerInterface.PLAY_MODE_SIGNAL);
		}
		// MediaInfo mediaInfo = new MediaInfo("", "", "", false);

		handler.setOnComplation(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				try {
					if (onEndListen != null) {
						onEndListen.onCommandChange();
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					LogManager.printStackTrace();
				}
				senCustomBroadCast(false);
			}
		});

		handler.setOnError(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				try {
					if (onEndListen != null) {
						onEndListen.onCommandChange();
					}

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					LogManager.printStackTrace();
				}
				senCustomBroadCast(false);
				return false;
			}
		});

		handler.setOnParePare(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				try {
					if (onBeginListen != null) {
						onBeginListen.onCommandChange();
					}

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					LogManager.printStackTrace();
				}
				senCustomBroadCast(true);
			}
		});
		handler.setMediaInfoList(mediaInfoList);
		// 不需要欢迎词
		union.getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_PLAY_WELCOME, false);
		handler.start();
		return MediaUtil.mediaDesc(mediaInfoList.get(0), mContext).toString();
	}

	public void senCustomBroadCast(boolean isBegin) {
		Intent i = new Intent();
		if (isBegin) {
			i.setAction(GOOD_MUSIC_BEGIN);
		} else {
			i.setAction(GOOD_MUSIC_END);
		}
		mContext.getApplicationContext().sendBroadcast(i);
	}

}
