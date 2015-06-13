package com.iii360.box.music;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic.ResultForMusicListener;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.box.view.XListView;
import com.iii360.box.view.XListView.IXListViewListener;

public class MusicPlayListActivity extends BaseActivity implements IXListViewListener, OnClickListener {

	private XListView xListView;
	private MusicListItemAdapter adapter;
	private WifiCRUDForMusic crudForMusic;
	/***
	 * 通过接受上个页面传的boolean值判断是什么类型的列表
	 */
	private boolean isLocal;
	public MyProgressDialog dialog;
	private RelativeLayout searchBtn;
	private View searchBelowLine;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_playlist);
		getIntentData();
		setupView();
		addListeners();
		dialog.show();
		if (isLocal) {
			loadLocalMusicData(false);
		} else
			loadPlayListData(false);
	}

	/**
	 * 获取音箱本地歌曲
	 */
	private void loadLocalMusicData(final boolean isFresh) {
		LogUtil.e( "获取获取音箱本地歌曲");
		int page = 1;
		if (adapter.getCount() != 0 && !isFresh) {
			page = adapter.getItem(adapter.getCount() - 1).getCurrPage() + 1;
		}
		crudForMusic = new WifiCRUDForMusic(getBoxIp(), getBoxTcpPort());
		crudForMusic.getMusicListLocal(new ResultForMusicListener() {
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				handler.post(new DismissDialog(false));
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					LogUtil.i("获取的数据==" + new Gson().toJson(infos));
					updateListView(infos, isFresh);
				} else {
					ToastUtils.show(context, R.string.ba_get_info_error_toast);
					handler.post(new UpdateViewTask());
				}
			}
		}, page);
	}

	private Runnable changeSearchBtnStateTask = new Runnable() {

		@Override
		public void run() {
			if (adapter.getMusics().isEmpty()) {
				searchBtn.setVisibility(View.VISIBLE);
				searchBelowLine.setVisibility(View.VISIBLE);
			} else {
				searchBtn.setVisibility(View.GONE);
				searchBelowLine.setVisibility(View.GONE);
			}
		}
	};

	/****
	 * 更新listview要判断listview当前的items,这个方法在工作线程
	 */
	protected void updateListView(List<WifiMusicInfo> infos, boolean isFresh) {
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

	private void getIntentData() {
		try {
			isLocal = getIntent().getExtras().getBoolean(KeyList.KEY_ISLOCALMUSIC_EXTRA_BOOLEAN, false);
		} catch (Exception e) {
		}

	}

	int i = 1;
	int j = 1;

	/***
	 * 获取当时播放列表
	 */
	private void loadPlayListData(final boolean isFresh) {
		LogUtil.i( "获取当时播放列表");
		int page = 1;
		if (adapter.getCount() != 0 && !isFresh) {
			page = adapter.getItem(adapter.getCount() - 1).getCurrPage() + 1;
		}
		crudForMusic = new WifiCRUDForMusic(getBoxIp(), getBoxTcpPort());
		crudForMusic.getMusicListCurrent(new ResultForMusicListener() {

			@Override
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				handler.post(new DismissDialog(false));
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					LogUtil.i( "获取的数据==" + new Gson().toJson(infos));
					updateListView(infos, isFresh);
				} else {
					ToastUtils.show(context, R.string.ba_get_info_error_toast);
					handler.post(new UpdateViewTask());
				}

			}
		}, page);
	}

	/**
	 * 获取音乐状态
	 */
	private void getstate() {
		LogManager.e("request state and volume");
		crudForMusic.playState(new ResultForMusicListener() {
			@Override
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				if (WifiCRUDUtil.isSuccess(errorCode) && infos != null && !infos.isEmpty()) {
					setPrefString(KeyList.PKEY_SELECT_MUSIC_ID, infos.get(0).getMusicId());
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

	private void addListeners() {
		xListView.setXListViewListener(this);
		searchBtn.setOnClickListener(this);
	}

	private void setupView() {
		if (isLocal) {
			this.setViewHead("预置歌曲");
		} else{
			((TextView)findViewById(R.id.head_title_tv)).setText("播放列表");
			findViewById(R.id.head_left_textview).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
					overridePendingTransition(0, R.anim.out_from_up);
				}
			});
		}
//			this.setViewHead("播放列表");
		dialog = new MyProgressDialog(context);
		dialog.setCanceledOnTouchOutside(false);
		searchBtn = (RelativeLayout) findViewById(R.id.music_play_list_search);
		searchBelowLine = findViewById(R.id.music_play_list_search_below_line);
		dialog.setMessage(getString(R.string.ba_update_date));
		xListView = (XListView) findViewById(R.id.music_play_list_listview);
		xListView.setPullLoadEnable(false);
		xListView.setPullRefreshEnable(false);
		adapter = new MusicListItemAdapter(this, null, isLocal ? MusicListItemAdapter.DATA_LOCAL_MUSIC : MusicListItemAdapter.DATA_PLAY_LIST);
		xListView.setAdapter(adapter);
		xListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		if (!isLocal)
			handler.post(changeSearchBtnStateTask);

	}
	public void dismissDialog(boolean isSetNull){
		handler.post(new DismissDialog(isSetNull));
	}
	public void showDialog(){
		if(dialog==null){
			dialog = new MyProgressDialog(this);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage(getString(R.string.ba_update_date));
		}
		dialog.show();
	}
	private Handler handler = new Handler();

	private class DismissDialog implements Runnable {
		private boolean isSetNull;

		/****
		 * 
		 * 
		 * @param isSetNull
		 *            是不是需要设置成null
		 */
		public DismissDialog(boolean isSetNull) {
			this.isSetNull = isSetNull;
		}

		public void run() {
			if (dialog != null && !isFinishing())
				dialog.dismiss();
			if (isSetNull)
				dialog = null;
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
				if (!isLocal)
					handler.post(changeSearchBtnStateTask);
			} else {
				adapter.setMusics(musics);
				if (!isLocal)
					handler.post(changeSearchBtnStateTask);
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

	@Override
	public void onRefresh() {
		if (isLocal) {
			loadLocalMusicData(true);
		} else
			loadPlayListData(true);
	}

	@Override
	public void onLoadMore() {
		if (isLocal) {
			loadLocalMusicData(false);
		} else
			loadPlayListData(false);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		adapter.notifyDataSetChanged();
		getstate();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.post(new DismissDialog(true));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.music_play_list_search:
			startToActvitiyNoFinish(MusicSearchActivity.class);
			break;
		}
	}
}
