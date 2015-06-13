package com.voice.assistant.main.newmusic;

import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.iii.wifi.util.HardwareUtils;
import com.iii360.sup.common.utl.LogManager;

/**
 * 绑定音乐服务
 * 
 * @author Administrator
 * 
 */
public class MusicData implements IMusic {
	public final static String action = "com.voice.assistant.main.newmusic.GoodMusicService";
	private Context context;
	private IGoodMusicService mIGoodMusicService;
	private MusicServiceListener musicListener;
	public final static int OPERATION_MUSIC_ERROR = -1;

	public void setMusicListener(MusicServiceListener musicListener) {
		this.musicListener = musicListener;
	}

	public interface MusicServiceListener {
		public void onPlayEnd();
	}

	public IGoodMusicService getIGoodMusicService() {
		if (mIGoodMusicService == null) {
			bindServer();
		}
		return mIGoodMusicService;
	}

	public MusicData(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		bindServer();
	}

	public void bindServer() {
		context.bindService(new Intent(action), mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	public void unBindServer() {
		context.unbindService(mServiceConnection);
	}

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music onServiceDisconnected");
			mIGoodMusicService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music onServiceConnected");
			mIGoodMusicService = IGoodMusicService.Stub.asInterface(service);
		}
	};

	public String getMusicList() {
		try {
			LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music get list : " + mIGoodMusicService.getGoodMusics());
			if(getIGoodMusicService()==null){
				return "";
			}
			return mIGoodMusicService.getGoodMusics();
		} catch (Exception e) {
			// TODO Aut"o-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public String getMusicListLocal(int page) {
		try {
			LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music get list : " + mIGoodMusicService.getGoodMusics());
			if(getIGoodMusicService()==null){
				return "";
			}
			return mIGoodMusicService.getLocalMusics(page);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public String getMusicListCurrent(int page) {
		try {
			LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music get list : getCurrentMusics" );
			if(getIGoodMusicService()==null){
				return "";
			}
			String  aString = mIGoodMusicService.getCurrentMusics(page);
			return aString;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public int setMusicForRemind(String id) {
		try {
			if(getIGoodMusicService()==null){
				return -1;
			}
			return mIGoodMusicService.setMusicForRemind(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void playGoodList(int position){
		try {
			if(getIGoodMusicService()==null){
				return ;
			}
			mIGoodMusicService.playGoodList(position);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String setNetMusicResource(List<NetResourceMusicInfo> infos) {
		try {
			LogManager.e(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music set netlist : " + infos.get(0).getName());
			if(getIGoodMusicService()==null){
				return "-1";
			}
			mIGoodMusicService.setNetMusicResources(infos);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "-1";
	}

	public void deleteMusic(String id) {
		LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music deleteMusic id = " + id);
		try {
			if(getIGoodMusicService()==null){
				return ;
			}
			getIGoodMusicService().deleteMusic(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void playMusic(String id) {
		LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music playMusic id = " + id);
		try {
			if(getIGoodMusicService()==null){
				return;
			}
			getIGoodMusicService().playMusic(id);
			getIGoodMusicService().setPlayEndListen(new IChangeListener() {
				@Override
				public IBinder asBinder() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void onCommandChange() throws RemoteException {
					// TODO Auto-generated method stub
					LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music onPlayEnd");

					if (musicListener != null) {
						musicListener.onPlayEnd();
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void playLocalMusic(String id,String poisiton) {
		LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music playMusic id = " + id);
		try {
			if(getIGoodMusicService()==null){
				return ;
			}
			getIGoodMusicService().playLocalMusic(id,poisiton);
			getIGoodMusicService().setPlayEndListen(new IChangeListener() {
				@Override
				public IBinder asBinder() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void onCommandChange() throws RemoteException {
					// TODO Auto-generated method stub
					LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music onPlayEnd");

					if (musicListener != null) {
						musicListener.onPlayEnd();
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public int playOrPause() {
		// TODO Auto-generated method stub
		LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music playPause");
		try {
			if(getIGoodMusicService()==null){
				return OPERATION_MUSIC_ERROR;
			}
			return getIGoodMusicService().playOrPause();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return OPERATION_MUSIC_ERROR;
	}

	@Override
	public int playPre() {
		// TODO Auto-generated method stub
		LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music playPre");
		try {
			if(getIGoodMusicService()==null){
				return OPERATION_MUSIC_ERROR;
			}
			return getIGoodMusicService().playPre();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return OPERATION_MUSIC_ERROR;
	}

	@Override
	public int playNext() {
		// TODO Auto-generated method stub
		LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music playNext");
		try {
			if(getIGoodMusicService()==null){
				return OPERATION_MUSIC_ERROR;
			}
			return getIGoodMusicService().playNext();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return OPERATION_MUSIC_ERROR;
	}

	@Override
	public int badMusic() {
		// TODO Auto-generated method stub
		LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music badMusic");
		try {
			if(getIGoodMusicService()==null){
				return OPERATION_MUSIC_ERROR;
			}
			return getIGoodMusicService().badMusic();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return OPERATION_MUSIC_ERROR;
	}

	@Override
	public int goodMusic() {
		// TODO Auto-generated method stub
		LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music goodMusic");
		try {
			if(getIGoodMusicService()==null){
				return OPERATION_MUSIC_ERROR;
			}
			return getIGoodMusicService().goodMusic();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return OPERATION_MUSIC_ERROR;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, String> playState() {
		// TODO Auto-generated method stub
		LogManager.i(HardwareUtils.INVOKE_MAIN_PORGRAM_HEAD + "Music playState");
		try {
			if(getIGoodMusicService()==null){
				return null;
			}
			return getIGoodMusicService().playState();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
