package com.iii.wifi.dao.newmanager;

import java.net.Socket;
import java.util.List;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.info.WifiMusicInfos;
import com.iii.wifi.dao.manager.WifiCreateAndParseSockObjectManager;

/**
 * 音乐接口
 * 
 * @author Administrator
 * 
 */
public class WifiCRUDForMusic extends AbsWifiCRUDForObject implements IWifiCRUDForMusic {
	/**
	 * 不需要设置ID
	 */
	public static final String NOT_SET_ID = null;

	public WifiCRUDForMusic(String ip, int port) {
		super(ip, port);
		// TODO Auto-generated constructor stub
	}

	public interface ResultForMusicListener {
		public void onResult(String errorCode, List<WifiMusicInfo> infos);
	}

	public void getMusicList(final ResultForMusicListener resultListener) {
		setMusicStatus(AbsWifiCRUDForObject.MUSIC_SELECT, NOT_SET_ID, resultListener);
	}

	public void getMusicListLocal(final ResultForMusicListener resultListener, int page) {
		setMusicStatusLocal(AbsWifiCRUDForObject.MUSIC_SELECT_LOCAL, NOT_SET_ID, resultListener, page);
	}

	public void getMusicListCurrent(final ResultForMusicListener resultListener, int page) {
		setMusicStatusLocal(AbsWifiCRUDForObject.MUSIC_SELECT_CURRENT, NOT_SET_ID, resultListener, page);
	}

	public void delete(final String id, final ResultForMusicListener resultListener) {
		setMusicStatus(AbsWifiCRUDForObject.MUSIC_DELETE_BY_ID, id, resultListener);
	}

	public void play(final String id, final ResultForMusicListener resultListener) {
		setMusicStatus(AbsWifiCRUDForObject.MUSIC_PLAY, id, resultListener);
	}
	
	public void play(int position, final ResultForMusicListener resultListener) {
		playGoodMusic(AbsWifiCRUDForObject.MUSIC_GOOD_PLAY, resultListener,position);
	}
	
	public void playLocalMusicByIdOrPosition(final String id, final String position, final ResultForMusicListener resultListener) {
		playLocalMusicByID(id, position, resultListener);
	}
	
	public void setLocalMusicForRemind(ResultForMusicListener resultListener, String id) {
		setMusicStatus(AbsWifiCRUDForObject.STE_LOCAL_MUSIC_FOR_REMIND, id, resultListener);
	}
	
	// 播放喜马拉雅
	public void playNetResource(final WifiMusicInfos musicInfosList, final String id, final ResultForMusicListener resultListener) {
		sendNetResourceToWifiServer(musicInfosList, AbsWifiCRUDForObject.MUSIC_PLAY, id, resultListener);
	}

	// ==========================new interface=============================
	@Override
	public void playOrPause(ResultForMusicListener resultListener) {
		// TODO Auto-generated method stub
		setMusicStatus(AbsWifiCRUDForObject.MUSIC_PLAY_OR_PAUSE, NOT_SET_ID, resultListener);
	}

	@Override
	public void playPre(ResultForMusicListener resultListener) {
		// TODO Auto-generated method stub
		setMusicStatus(AbsWifiCRUDForObject.MUSIC_PREVIOUS, NOT_SET_ID, resultListener);
	}

	@Override
	public void playNext(ResultForMusicListener resultListener) {
		// TODO Auto-generated method stub
		setMusicStatus(AbsWifiCRUDForObject.MUSIC_NEXT, NOT_SET_ID, resultListener);
	}

	@Override
	public void badMusic(ResultForMusicListener resultListener) {
		// TODO Auto-generated method stub
		setMusicStatus(AbsWifiCRUDForObject.MUSIC_BAD, NOT_SET_ID, resultListener);
	}

	@Override
	public void goodMusic(ResultForMusicListener resultListener) {
		// TODO Auto-generated method stub
		setMusicStatus(AbsWifiCRUDForObject.MUSIC_GOOD, NOT_SET_ID, resultListener);
	}

	@Override
	public void playState(ResultForMusicListener resultListener) {
		// TODO Auto-generated method stub
		setMusicStatus(AbsWifiCRUDForObject.MUSIC_CURRENT_PLAY_STATUS, NOT_SET_ID, resultListener);
	}

	/**
	 * @param status
	 *            操作的数据类型，类型在AbsWifiCRUDForObject中
	 * @param id
	 * @param resultListener
	 */
	private void setMusicStatus(final String status, final String id, final ResultForMusicListener resultListener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				WifiJSONObjectInfo resultObj = null;

				try {
					Socket socket = connect();

					WifiMusicInfo info = new WifiMusicInfo();
					info.setMusicId(id);
					String obj = WifiCreateAndParseSockObjectManager.createWifiMusicInfos(status, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info)+ "\n";

					resultObj = getResult(socket, obj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				resultListener.onResult(resultObj.getError(), ((WifiMusicInfos) resultObj.getObject()).getWifiInfos());
			}
		}).start();
	}

	/***
	 * 发送网络资源到盒子并播放
	 * 
	 * @param infoList
	 * @param status
	 * @param id
	 * @param resultListener
	 */
	private void sendNetResourceToWifiServer(final WifiMusicInfos infoList, final String status, final String id, final ResultForMusicListener resultListener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				WifiJSONObjectInfo resultObj = null;
				try {
					Socket socket = connect();
					String obj = WifiCreateAndParseSockObjectManager.createWifiMusicInfosForNetResource(AbsWifiCRUDForObject.MUSIC_PLAY_NET_RESOURCES,
							WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, infoList)+ "\n";
					resultObj = getResult(socket, obj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				resultListener.onResult(resultObj.getError(), ((WifiMusicInfos) resultObj.getObject()).getWifiInfos());
			}
		}).start();
	}

	private void playLocalMusicByID(final String id, final String position, final ResultForMusicListener resultListener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				WifiJSONObjectInfo resultObj = null;

				try {
					Socket socket = connect();

					WifiMusicInfo info = new WifiMusicInfo();
					info.setMusicId(id);
					info.setCuurPosition(position);
					String obj = WifiCreateAndParseSockObjectManager.createWifiMusicInfos(AbsWifiCRUDForObject.PLAY_LOCAL_MUSIC_BY_ID, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info)+ "\n";
					resultObj = getResult(socket, obj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				resultListener.onResult(resultObj.getError(), ((WifiMusicInfos) resultObj.getObject()).getWifiInfos());
			}
		}).start();
	}

	private void setMusicStatusLocal(final String status, final String id, final ResultForMusicListener resultListener, final int page) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				WifiJSONObjectInfo resultObj = null;

				try {
					Socket socket = connect();

					WifiMusicInfo info = new WifiMusicInfo();
					info.setMusicId(id);
					String obj = WifiCreateAndParseSockObjectManager.createWifiMusicInfosLocal(status, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info, page)+ "\n";

					resultObj = getResult(socket, obj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				resultListener.onResult(resultObj.getError(), ((WifiMusicInfos) resultObj.getObject()).getWifiInfos());
			}
		}).start();
	}
	
	
	private void playGoodMusic(final String status, final ResultForMusicListener resultListener, final int position) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				WifiJSONObjectInfo resultObj = null;

				try {
					Socket socket = connect();

					WifiMusicInfo info = new WifiMusicInfo();
					String obj = WifiCreateAndParseSockObjectManager.createWifiMusicGoodList(status, WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info, position)+ "\n";

					resultObj = getResult(socket, obj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resultListener.onResult(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
					return;
				}
				resultListener.onResult(resultObj.getError(), ((WifiMusicInfos) resultObj.getObject()).getWifiInfos());
			}
		}).start();
	}
	
}
