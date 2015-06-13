package com.iii360.box.ximalaya;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.info.WifiMusicInfos;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic.ResultForMusicListener;
import com.iii360.box.MyApplication;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.HttpUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.BottomMenu;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.box.view.XListView;
import com.iii360.box.view.XListView.IXListViewListener;
import com.iii360.sup.common.utl.LogManager;
import com.voice.assistant.main.newmusic.NetResourceMusicInfo;

public class XimalayaAudioActivity extends BaseActivity implements IXListViewListener, OnClickListener {
	private XListView mListView;
	private ArrayList<XimalayaAudio> audios;
	private Album album;
	private AudioAdapter adapter;
	private String account = "smallzhi";
	private int perPage = 10;
	private WifiCRUDForMusic mWifiCRUDForMusic;
	private String uni;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ximalaya_audio_layout);
		getIntentData();
		setupView();
		addListeners();
		myProgressDialog.show();
		onLoadMore();
		mWifiCRUDForMusic = new WifiCRUDForMusic(getBoxIp(), getBoxTcpPort());
	}

	/*****
	 * http://3rd.ximalaya.com/albums/290488/tracks?i_am=smallzhi&page=1&
	 * per_page=5&is_asc=true
	 * 
	 * @param isRefresh是不是下拉刷新
	 */
	private void loadDataMore(final boolean isRefresh) {
		new Thread() {
			public void run() {
				int page = 1;
				if (!isRefresh && audios != null && !audios.isEmpty()) {
					page = audios.get(audios.size() - 1).getPage() + 1;
				}
				HttpEntity entity = HttpUtils.getEntitywithGetMethod(KeyList.KEY_XIMALAYA_BASEURL + "albums/" + album.getId() + "/tracks?i_am="
						+ account + "&page=" + page + "&per_page=" + perPage + "&is_asc=true" + "&uni=" + uni);
				try {
					if (entity == null)
						throw new Exception("获取数据失败");
					String json = EntityUtils.toString(entity);
					LogUtil.i("" + json);
					parseJson(isRefresh, json);
				} catch (Exception e) {
					e.printStackTrace();
					if (e.getMessage().contains("not accessable")) {
						runOnUiThread(new Runnable() {
							public void run() {
								ToastUtils.show(getApplicationContext(), "该专辑暂无资源,请试试其它专辑");
								finish();
							}
						});
					} else {
						ToastUtils.show(getApplicationContext(), e.getMessage());
					}

				}
				handler.post(updateViewTask);
				handler.post(new DismissDialog(false));
			};
		}.start();
	}

	private Handler handler = new Handler();
	private Runnable updateViewTask = new Runnable() {

		@Override
		public void run() {
			adapter.setAudios(audios);
			adapter.notifyDataSetChanged();
			mListView.stopLoadMore();
			mListView.stopRefresh();
			if (audios != null && audios.size() >= 9) {
				mListView.setPullLoadEnable(true);
			} else {
				mListView.setPullLoadEnable(false);
			}
		}
	};
	private MyProgressDialog myProgressDialog;

	protected void parseJson(boolean isRefresh, String json) throws Exception {
		synchronized (this) {
			JSONObject obj = new JSONObject(json);
			int state = obj.getInt("ret");
			if (state != 0) {
				throw new Exception("album not accessable");
			}
			int page = obj.getInt("page");
			obj = obj.getJSONObject("album");
			JSONArray array = obj.getJSONArray("tracks");
			if (isRefresh) {
				if (audios != null && !audios.isEmpty()) {
					audios = new ArrayList<XimalayaAudio>(audios);
					insertIntoFirst(audios, array, page);
				}
			} else {
				if (audios == null) {
					audios = new ArrayList<XimalayaAudio>();
				} else {
					audios = new ArrayList<XimalayaAudio>(audios);
				}
				appendToAudio(audios, array, page);
			}
		}

	}

	private void appendToAudio(ArrayList<XimalayaAudio> audios, JSONArray array, int page) throws Exception {

		for (int i = 0; i < array.length(); i++) {
			JSONObject audioObj = array.getJSONObject(i);
			XimalayaAudio audio = new XimalayaAudio();
			audio.setId(audioObj.getInt("id"));
			audio.setPage(page);
			audio.setAudioUrl32(audioObj.getString("play_url_32"));
			audio.setAudioUrl64(audioObj.getString("play_url_64"));
			try {
				audio.setNickName(audioObj.getString("nickname").trim());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				audio.setTitle(audioObj.getString("title").trim());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!audios.contains(audio))
				audios.add(audio);
		}
	}

	private void insertIntoFirst(ArrayList<XimalayaAudio> audios, JSONArray array, int page) throws Exception {
		for (int i = array.length() - 1; i >= 0; i--) {
			JSONObject audioObj = array.getJSONObject(i);
			XimalayaAudio audio = new XimalayaAudio();
			audio.setId(audioObj.getInt("id"));
			audio.setPage(page);
			audio.setAudioUrl32(audioObj.getString("play_url_32"));
			audio.setAudioUrl64(audioObj.getString("play_url_64"));
			try {
				audio.setNickName(audioObj.getString("nickname").trim());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				audio.setTitle(audioObj.getString("title").trim());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!audios.contains(audio))
				audios.add(0, audio);
		}
	}

	private void getIntentData() {
		album = (Album) getIntent().getSerializableExtra(KeyList.KEY_XIMALAYA_ALBUM_NAME);
	}

	private void addListeners() {
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// ToastUtils.show(getApplicationContext(), "" +
				// audio.getTitle());
				play(position-1);
			}
		});
	}

	protected void showBottomMenu(final int position) {
		BottomMenu menu = new BottomMenu(context);
		menu.dismissDeleteBtn();
		menu.dismissAddToPlayListBtn();
		menu.setAddToPlayListListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPlayList(position);
			}
		});
		menu.setPlayListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				play(position);
			}
		});
		menu.show();
	}

	protected void addPlayList(int position) {
		// ToastUtils.show(getApplicationContext(), "addPlayList:::" +
		// audio.getTitle() + "-------" + audio.getAudioUrl32());
	}

	protected void play(int position) {
		// ToastUtils.show(getApplicationContext(), "play:::" + audio.getTitle()
		// + "-------" + audio.getAudioUrl32());
		WifiMusicInfos infos = new WifiMusicInfos();
		ArrayList<XimalayaAudio> tempAudios = adapter.getAudios();
		int count = 0;
		for (int i = position; count < 20 && i < tempAudios.size(); i++) {
			XimalayaAudio audio = tempAudios.get(i);
			NetResourceMusicInfo Info = new NetResourceMusicInfo(audio.getTitle(), audio.getNickName(), String.valueOf(audio.getId()),
					(TextUtils.isEmpty(audio.getAudioUrl32()) ||"null".equals(audio.getAudioUrl32()))? audio.getAudioUrl64() : audio.getAudioUrl32());
			infos.setNetMusicInfos(Info);
			count++;
		}
		myProgressDialog.show();
		LogUtil.i("发送list大小:" + infos.getNetMusicInfos().size());
		mWifiCRUDForMusic.playNetResource(infos, WifiCRUDForMusic.NOT_SET_ID, new ResultForMusicListener() {

			@Override
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				handler.post(new DismissDialog(false));
				// TODO Auto-generated method stub
				if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
					ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
					LogManager.e("发送网络播放资源失败");
				}
			}
		});
	}

	private void setupView() {
		this.setViewHead("" + album.getTitle());
		myProgressDialog = new MyProgressDialog(context);
		uni = MyApplication.getSerialNums().get(getPrefString(KeyList.GKEY_BOX_IP_ADDRESS));
		if (uni == null)
			uni = "SZA0A2507C8Y";
		uni=uni.trim();
		myProgressDialog.setMessage(getString(R.string.ba_update_date));
		myProgressDialog.setCanceledOnTouchOutside(false);
		mListView = (XListView) findViewById(R.id.ximalaya_audio_listview);
		adapter = new AudioAdapter(this, null);
		mListView.setPullLoadEnable(false);
		mListView.setAdapter(adapter);
	}

	private class DismissDialog implements Runnable {
		private boolean isSetNull;

		public DismissDialog(boolean isSetNull) {
			this.isSetNull = isSetNull;
		}

		public void run() {
			if (myProgressDialog != null && !isFinishing())
				myProgressDialog.dismiss();
			if (isSetNull)
				myProgressDialog = null;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.post(new DismissDialog(true));
	}

	@Override
	public void onRefresh() {
		loadDataMore(true);
	}

	@Override
	public void onLoadMore() {
		loadDataMore(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.music_paly_layout:

			break;
		case R.id.music_add_playlist_layout:
			break;
		}
	}
}
