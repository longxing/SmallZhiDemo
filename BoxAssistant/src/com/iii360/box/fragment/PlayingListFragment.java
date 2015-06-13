package com.iii360.box.fragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic.ResultForMusicListener;
import com.iii360.box.R;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.music.MusicListItemAdapter;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.XListView;
import com.iii360.box.view.XListView.IXListViewListener;

public class PlayingListFragment extends Fragment implements IXListViewListener {
	private XListView xListView;
	private MusicListItemAdapter adapter;
	private WifiCRUDForMusic crudForMusic;
	public Activity activity;

	public PlayingListFragment(Activity activity) {
		this.activity = activity;
	}

	public void setNeedUpdate(boolean needUpdate) {
		if (activity != null) {
			Class<? extends Activity> cls = activity.getClass();
			try {
				Method method = cls.getMethod("setNeedUpdate", new Class[] { boolean.class });
				method.invoke(activity, new Object[] { needUpdate });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isNeedUpdate() {
		if (activity != null) {
			Class<? extends Activity> cls = activity.getClass();
			try {
				Method method = cls.getMethod("isNeedUpdate", new Class[] {});
				return (Boolean) method.invoke(activity, new Object[] {});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_playinglist, null);
		initViews(v);
		addListener();
		return v;
	}

	private void addListener() {
		xListView.setXListViewListener(this);
	}

	private void getstate() {
		LogManager.e("request state and volume");
		crudForMusic.playState(new ResultForMusicListener() {
			@Override
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				if (WifiCRUDUtil.isSuccess(errorCode) && infos != null && !infos.isEmpty()) {
					basePreferences.setPrefString(KeyList.PKEY_SELECT_MUSIC_ID, infos.get(0).getMusicId());
					handler.post(new Runnable() {
						public void run() {
							adapter.notifyDataSetChanged();
						}
					});
				} else {
				}
			}
		});
	}

	private List<WifiMusicInfo> currMusics;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (currMusics == null || isNeedUpdate()) {
				adapter = new MusicListItemAdapter(activity, null, MusicListItemAdapter.FRAGMENT_PLAY_LIST);
				if (xListView != null) {
					xListView.setPullLoadEnable(false);
					xListView.setPullRefreshEnable(false);
					xListView.setAdapter(adapter);
				}

				showDialog();
				loadPlayListData(false);
			} else {
				getstate();
			}
		}
	}

	public void showDialog() {
		if (activity != null) {
			Class<? extends Activity> cls = activity.getClass();
			try {
				Method method = cls.getMethod("showDialog", new Class[] {});
				method.invoke(activity, new Object[] {});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void dismissDialog(boolean isSetNull) {
		if (activity != null) {
			Class<? extends Activity> cls = activity.getClass();
			try {
				Method method = cls.getMethod("dismissDialog", new Class[] { boolean.class });
				method.invoke(activity, new Object[] { isSetNull });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class UpdateViewTask implements Runnable {
		private List<WifiMusicInfo> musics;

		public UpdateViewTask() {
		}

		public UpdateViewTask(List<WifiMusicInfo> musics) {
			if (musics == null)
				this.musics = new ArrayList<WifiMusicInfo>();
			else
				this.musics = musics;
		}

		@Override
		public void run() {
			if (musics == null) {
				xListView.stopLoadMore();
				xListView.stopRefresh();
			} else {
				adapter.setMusics(musics);
				adapter.notifyDataSetChanged();
				xListView.stopLoadMore();
				xListView.stopRefresh();
				if (musics.size() > 9) {
					xListView.setPullLoadEnable(true);
				} else {
					xListView.setPullLoadEnable(false);
				}
			}
		}

	}

	/***
	 * 获取当时播放列表
	 */
	private void loadPlayListData(final boolean isFresh) {
		LogUtil.i("获取当时播放列表");
		int page = 1;
		if (adapter.getCount() != 0 && !isFresh) {
			page = adapter.getItem(adapter.getCount() - 1).getCurrPage() + 1;
		}
		crudForMusic = new WifiCRUDForMusic(BoxManagerUtils.getBoxIP(activity), BoxManagerUtils.getBoxTcpPort(activity));
		crudForMusic.getMusicListCurrent(new ResultForMusicListener() {

			@Override
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				dismissDialog(false);
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					LogUtil.i("当时播放--获取的数据==" + new Gson().toJson(infos));
					setNeedUpdate(false);
					getstate();
					updateListView(infos, isFresh);
				} else {
					ToastUtils.show(activity, R.string.ba_get_info_error_toast);
					handler.post(new UpdateViewTask());
				}

			}
		}, page);
	}

	/****
	 * 更新listview要判断listview当前的items,这个方法在工作线程
	 */
	protected void updateListView(List<WifiMusicInfo> infos, boolean isFresh) {
		currMusics = infos != null ? infos : new ArrayList<WifiMusicInfo>();
		List<WifiMusicInfo> musics = new ArrayList<WifiMusicInfo>();
		if (infos == null) {
			handler.post(new UpdateViewTask());
			return;
		}
		if (infos.size() == 0) {
			handler.post(new UpdateViewTask());
			return;
		}

		if (infos.size() == 1 && (infos.get(0).getName() == null || infos.get(0).getAuthor() == null)) {
			handler.post(new UpdateViewTask());
			return;
		}

		if (adapter.getCount() == 0) {
			for (int i = 0; i < infos.size(); i++) {
				WifiMusicInfo musicInfo = infos.get(i);
				musicInfo.setCurrPage(1);
				musics.add(musicInfo);
			}
			handler.post(new UpdateViewTask(musics));
		} else {
			List<WifiMusicInfo> oldData = adapter.getMusics();
			if (!isFresh) {
				int page = oldData.get(oldData.size() - 1).getCurrPage() + 1;
				for (int i = 0; i < infos.size(); i++) {
					WifiMusicInfo musicInfo = infos.get(i);
					musicInfo.setCurrPage(page);
					if (!oldData.contains(musicInfo)) {
						musics.add(musicInfo);
					}
				}
				List<WifiMusicInfo> data = new ArrayList<WifiMusicInfo>(oldData);
				data.addAll(musics);
				handler.post(new UpdateViewTask(data));
			}
		}

	}

	private Handler handler = new Handler();
	private BasePreferences basePreferences;

	private void initViews(View v) {
		basePreferences = new BasePreferences(activity);
		xListView = (XListView) v.findViewById(R.id.music_play_list_listview);
		xListView.setPullLoadEnable(false);
		xListView.setPullRefreshEnable(false);
		xListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		if (adapter == null) {
			adapter = new MusicListItemAdapter(activity, null, MusicListItemAdapter.FRAGMENT_PLAY_LIST);
		}
		xListView.setAdapter(adapter);
	}

	@Override
	public void onRefresh() {
		loadPlayListData(true);
	}

	@Override
	public void onLoadMore() {
		loadPlayListData(false);
	}

}
