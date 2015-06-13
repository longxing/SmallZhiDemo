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
import android.widget.ListView;

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

public class GoodMusicListFragment extends Fragment {
	private Activity activity;
	private ListView listview;
	private MusicListItemAdapter adapter;
	private WifiCRUDForMusic crudForMusic;
	private List<WifiMusicInfo> musics;

	public GoodMusicListFragment(Activity activity) {
		this.activity = activity;
	}

	private class UpdateViewTask implements Runnable {
		private List<WifiMusicInfo> musics;

		public UpdateViewTask(List<WifiMusicInfo> musics) {
			if (musics == null)
				this.musics = new ArrayList<WifiMusicInfo>();
			else
				this.musics = musics;
		}

		@Override
		public void run() {
			adapter.setMusics(musics);
			adapter.notifyDataSetChanged();
		}

	}

	private Handler handler = new Handler();
	private BasePreferences basePreferences;

	private void getGoodMusicData() {
		LogUtil.i("获取红心列表");
		crudForMusic = new WifiCRUDForMusic(BoxManagerUtils.getBoxIP(activity), BoxManagerUtils.getBoxTcpPort(activity));
		crudForMusic.getMusicList(new ResultForMusicListener() {
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				dismissDialog(false);
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					LogUtil.i("红心--获取的数据==" + new Gson().toJson(infos));
					getstate();
					updateListView(infos);
				} else {
					ToastUtils.show(activity, R.string.ba_get_info_error_toast);
				}
			}
		});
	}

	protected void updateListView(List<WifiMusicInfo> infos) {
		musics = infos != null ? infos : new ArrayList<WifiMusicInfo>();
		if (infos != null && !infos.isEmpty()) {
			if (infos.size() == 1 && (infos.get(0).getName() == null || infos.get(0).getAuthor() == null)) {
				ToastUtils.show(activity, "您没有收藏歌曲");
			} else {
				handler.post(new UpdateViewTask(infos));
			}
		} else {
			ToastUtils.show(activity, "您没有收藏歌曲");
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_goodmusic, null);
		initView(v);
		addListener();
		return v;
	}

	private void addListener() {
	}

	private void initView(View v) {
		basePreferences = new BasePreferences(activity);
		listview = (ListView) v.findViewById(R.id.music_goodmusic_listview);
		listview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new MusicListItemAdapter(activity, null, MusicListItemAdapter.FRAGMENT_GOODMUSIC_LIST);
		listview.setAdapter(adapter);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (musics == null) {
				showDialog();
				getGoodMusicData();
			}else{
				getstate();
			}
		}
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
}
