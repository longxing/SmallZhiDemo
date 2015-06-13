package com.iii.wifi.manager.impl;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.iii.wifi.dao.imf.WifiVolumeCotroller;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.info.WifiMusicInfos;
import com.iii.wifi.dao.manager.WifiCreateAndParseSockObjectManager;
import com.iii.wifi.dao.newmanager.AbsWifiCRUDForObject;
import com.iii.wifi.http.parsrer.ParserData;
import com.iii.wifi.http.parsrer.ParserMusic;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.voice.assistant.main.newmusic.MusicData;
import com.voice.assistant.main.newmusic.NetResourceMusicInfo;

/**
 * 盒子音乐控制
 * 
 * @author Administrator
 * 
 */
public class WifiResponseForMusic extends AbsWifiResponse {
	private String type;
	private MusicData mMusicData;
	private WifiVolumeCotroller mWifiVolumeCotroller;
	/**
	 * 0没有播放的歌曲1正在播放2暂停，小于0表示操作失败
	 */
	private int mOperCode = -1;

	@Override
	public String getResponse(WifiJSONObjectInfo obj, Context context) {
		// TODO Auto-generated method stub
		WifiMusicInfos infos = (WifiMusicInfos) obj.getObject();
		type = infos.getType();
		WifiMusicInfo info = null;
		if (infos.getWifiInfos() != null) {
			info = infos.getWifiInfos().get(0);
		}
		mMusicData = KeyList.sMusicData;
		LogManager.e("type=" + type);
		if (type.equals(AbsWifiCRUDForObject.MUSIC_SELECT)) {
			String musicGson = mMusicData.getMusicList();

			mResult = WifiCreateAndParseSockObjectManager.createWifiMusicInfos(type, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, "0", musicGson);

		} else if (type.equals(AbsWifiCRUDForObject.MUSIC_SELECT_LOCAL)) {

			String musicGson = mMusicData.getMusicListLocal(infos.getPage());

			mResult = WifiCreateAndParseSockObjectManager.createWifiMusicInfos(type, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, "0", musicGson);
		} else if (type.equals(AbsWifiCRUDForObject.MUSIC_SELECT_CURRENT)) {

			String musicGson = mMusicData.getMusicListCurrent(infos.getPage());
			
			mResult = WifiCreateAndParseSockObjectManager.createWifiMusicInfos(type, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, "0", musicGson);

		} else {
			if (type.equals(AbsWifiCRUDForObject.MUSIC_CURRENT_PLAY_STATUS)) {
				Map map = mMusicData.playState();
				int state = 0;
				String data = null;
				if (map != null) {
					LogManager.i("MUSIC_CURRENT_PLAY_STATUS===============" + map.toString());
				} else {
					LogManager.i("MUSIC_CURRENT_PLAY_STATUS===============" + null);
				}

				if (map != null && !map.isEmpty()) {
					// 0没有播放的歌曲1正在播放2暂停3dlan
					try {
						state = (Integer) map.get("state");

						if (state != 3) {
							data = (String) map.get("data");
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LogManager.e("main map data error=" + Log.getStackTraceString(e));
					}
				}

				mOperCode = state;
				// mOperCode = Integer.parseInt(state);

				if (!TextUtils.isEmpty(data) && !"null".equals(data)) {

					ParserData parserMusic = new ParserMusic(state + "");
					try {
						info = (WifiMusicInfo) parserMusic.getParserData(data);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					info.setPlayStatus(state + "");
				}

				// 设置音量
				mWifiVolumeCotroller = new WifiVolumeCotroller(context, AudioManager.STREAM_MUSIC);
				info.setMusicCurrVolume(mWifiVolumeCotroller.getCurrentVolume());
				info.setMusicMaxVolume(mWifiVolumeCotroller.getMaxVolume());

			} else if (type.equals(AbsWifiCRUDForObject.MUSIC_PLAY)) {
				LogManager.i("MUSIC_PLAY " + info.getMusicId());
				mMusicData.playMusic(info.getMusicId());
				mOperCode = 10;

			} else if (type.equals(AbsWifiCRUDForObject.MUSIC_PLAY_OR_PAUSE)) {
				// 播放和暂停歌曲播放
				mOperCode = mMusicData.playOrPause();

			} else if (type.equals(AbsWifiCRUDForObject.MUSIC_NEXT)) {
				mOperCode = mMusicData.playNext();

			} else if (type.equals(AbsWifiCRUDForObject.MUSIC_PREVIOUS)) {
				mOperCode = mMusicData.playPre();

			} else if (type.equals(AbsWifiCRUDForObject.MUSIC_DELETE_BY_ID)) {
				LogManager.i("MUSIC_DELETE_BY_ID " + info.getMusicId());
				mMusicData.deleteMusic(info.getMusicId());
				mOperCode = 10;

			} else if (type.equals(AbsWifiCRUDForObject.MUSIC_BAD)) {
				mOperCode = mMusicData.badMusic();

			} else if (type.equals(AbsWifiCRUDForObject.MUSIC_GOOD)) {
				mOperCode = mMusicData.goodMusic();
			} else if (type.equals(AbsWifiCRUDForObject.MUSIC_PLAY_NET_RESOURCES)) {
				List<NetResourceMusicInfo> infoList = infos.getNetMusicInfos();
				mMusicData.setNetMusicResource(infoList);
				mOperCode = 10;
				info = new WifiMusicInfo();
			} else if (type.equals(AbsWifiCRUDForObject.PLAY_LOCAL_MUSIC_BY_ID)) {
				LogManager.i("PLAY_LOCAL_MUSIC_BY_ID " + info.getMusicId());
				mMusicData.playLocalMusic(info.getMusicId(), info.getCuurPosition());
				mOperCode = 10;
			}else if(type.equals(AbsWifiCRUDForObject.STE_LOCAL_MUSIC_FOR_REMIND)){
				mOperCode = mMusicData.setMusicForRemind(info.getMusicId());
			}else if(type.equals(AbsWifiCRUDForObject.MUSIC_GOOD_PLAY)){
				mMusicData.playGoodList(infos.getPosition());
				mOperCode = 10;
			}
			LogManager.e("mOperCode=" + mOperCode);
			info.setPlayStatus(mOperCode + "");
			if (mOperCode >= 0) {
				mResult = WifiCreateAndParseSockObjectManager.createWifiMusicInfos(type, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
			} else {
				mResult = WifiCreateAndParseSockObjectManager.createWifiMusicInfos(type, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, info);
			}
		}

		return mResult;
	}
}
