package com.iii360.box.music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.info.WifiMusicInfos;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic.ResultForMusicListener;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.database.MusicSearchDao;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.box.view.XListView;
import com.iii360.box.view.XListView.IXListViewListener;
import com.iii360.sup.common.utl.LogManager;
import com.voice.assistant.main.newmusic.NetResourceMusicInfo;

public class MusicSearchActivity extends BaseActivity implements OnClickListener, IXListViewListener {
	EditText musicSearchEt;
	private Button searchBtn;
	private ImageView deleteIv;
	private XListView listView;
	MusicSearchDao musicSearchDao;
	private MusicSearchItemAdapter adapter;
	private LinearLayout historyTag;
	private ArrayList<MusicSearchBean> beans;
	private int per_page = 20;
	private String lastKey;
	public static final int ADAPTER_SHOW_TYPE_HISTORY = 1;
	public static final int ADAPTER_SHOW_TYPE_LOAD = 2;
	public static final int ADAPTER_SHOW_TYPE_RESULT = 3;
	private MyProgressDialog myProgressDialog;

	private boolean textEmpty;// 输入框是不是空的，数据响应较慢时，清空输入框时不需要显示查询的数据

	private ArrayList<MusicSearchBean> historyBeans;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_search);
		setupView();
		addListeners();
		getHistoryData();
	}

	void getHistoryData() {
		historyBeans = musicSearchDao.getAllHistory();
		listView.setPullLoadEnable(false);
		if (historyBeans == null || historyBeans.isEmpty()) {
			historyTag.setVisibility(View.GONE);
			adapter.setBeans(null, ADAPTER_SHOW_TYPE_HISTORY);
			adapter.notifyDataSetChanged();
		} else {
			historyTag.setVisibility(View.VISIBLE);
			historyBeans.add(new MusicSearchBean(getString(R.string.clear_history), null, 0));
			adapter.setBeans(historyBeans, ADAPTER_SHOW_TYPE_HISTORY);
			adapter.notifyDataSetChanged();
		}
	}

	private void addListeners() {
		searchBtn.setOnClickListener(this);
		deleteIv.setOnClickListener(this);
		listView.setXListViewListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (adapter.getType() == ADAPTER_SHOW_TYPE_RESULT) {
					play(position - 1);
				} else {
					// 历史记录
					if (adapter.getType() == ADAPTER_SHOW_TYPE_HISTORY && (position - 1) == historyBeans.size() - 1) {
						musicSearchDao.deleteAll();
						getHistoryData();
						return;
					}
					MusicSearchBean bean = null;
					try {
						if (adapter.getType() == ADAPTER_SHOW_TYPE_HISTORY) {
							bean = historyBeans.get(position - 1);
						} else {
							bean = beans.get(position - 1);
						}
						bean.setMessage(bean.getMessage().replaceAll("\\^", " "));
						musicSearchDao.add(bean);
						search("" + bean.getMessage().replaceAll("\\^", " "), false, true);
						musicSearchEt.setText("" + bean.getMessage().replaceAll("\\^", " "));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		});
		musicSearchEt.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = s.toString().trim();
				if (TextUtils.isEmpty(text)) {
					textEmpty = true;
					searchBtn.setVisibility(View.GONE);
					deleteIv.setVisibility(View.GONE);
					getHistoryData();
					listView.setPullLoadEnable(false);
				} else {
					textEmpty = false;
					searchBtn.setVisibility(View.VISIBLE);
					deleteIv.setVisibility(View.VISIBLE);
					historyTag.setVisibility(View.GONE);
					if ((text).equals(lastKey)) {
						return;
					}
					listView.setPullLoadEnable(false);
					adapter.setBeans(null, ADAPTER_SHOW_TYPE_HISTORY);
					adapter.notifyDataSetChanged();
					search(text, false, false);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});

	}


	protected void play(int position) {
		myProgressDialog.setMessage(getString(R.string.ba_update_date));
		myProgressDialog.show();
		WifiMusicInfos infos = new WifiMusicInfos();
		ArrayList<MusicSearchBean> tempAudios = adapter.getBeans();
		int count = 0;
		for (int i = position; count < 20 && i < tempAudios.size(); i++) {
			MusicSearchBean audio = tempAudios.get(i);
			String[] arr = audio.getMessage().split("\\^");
			String singer = arr[0];
			NetResourceMusicInfo Info = new NetResourceMusicInfo(arr[1], singer, "-1", audio.getUrl());
			infos.setNetMusicInfos(Info);
			count++;
		}
		LogUtil.i("发送list大小：" + infos.getNetMusicInfos().size());
		mWifiCRUDForMusic.playNetResource(infos, WifiCRUDForMusic.NOT_SET_ID, new ResultForMusicListener() {

			@Override
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				handler.post(new Runnable() {
					public void run() {
						if (myProgressDialog != null && !isFinishing()) {
							myProgressDialog.dismiss();
						}
					}
				});
				// TODO Auto-generated method stub
				if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
					ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
					LogManager.e("发送网络播放资源失败");
				}
			}
		});
	}

	protected void addPlayList(int position) {
		// ToastUtils.show(getApplicationContext(), "addPlayList----------" +
		// bean.getMessage() + "------" + bean.getUrl());
	}

	protected void search(String key, final boolean isLoadMore, final boolean isResult) {
		lastKey = key;
		if (isResult && !isLoadMore) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(musicSearchEt.getWindowToken(), 0);
			}
			myProgressDialog.setMessage("正在搜索...");
			myProgressDialog.show();

		}
		try {
			key = URLEncoder.encode(key, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		int currPage = 1;
		if (isLoadMore) {
			try {
				currPage = beans.get(beans.size() - 1).getPage() + 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		key = key.replaceAll(" ", "%20").trim();
		final String url = "http://hezi.360iii.net:48080/webapi/queryMusic_queryMusicApp.action?key=" + key + "&page=" + currPage + "&per_page=" + per_page;
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();

				try {
					HttpResponse res = client.execute(new HttpGet(url));
					if (res.getStatusLine().getStatusCode() != 200) {
						throw new Exception("errorcode:" + res.getStatusLine().getStatusCode());
					}
					HttpEntity entity = res.getEntity();
					InputStream in = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
					parseStream(reader, isLoadMore);
					handler.post(new UpdateViewTask(isResult));
				} catch (Exception e) {
					ToastUtils.show(getApplicationContext(), "获取数据失败");
					e.printStackTrace();
					handler.post(new Runnable() {
						public void run() {
							if (myProgressDialog != null && !isFinishing()) {
								myProgressDialog.dismiss();
							}
							historyTag.setVisibility(View.GONE);
							listView.setPullLoadEnable(false);
							listView.stopLoadMore();
							listView.stopRefresh();
							adapter.setBeans(null, ADAPTER_SHOW_TYPE_HISTORY);
							adapter.notifyDataSetChanged();
						}
					});
				}
			};
		}.start();
	}

	private Handler handler = new Handler();
	private WifiCRUDForMusic mWifiCRUDForMusic;

	private class UpdateViewTask implements Runnable {
		boolean isResult;

		public UpdateViewTask(boolean isResult) {
			this.isResult = isResult;
		}

		@Override
		public void run() {
			synchronized (MusicSearchActivity.this) {
				if (isResult) {
					if (myProgressDialog != null && !isFinishing()) {
						myProgressDialog.dismiss();
					}
				}
				if (textEmpty) {
					return;
				}
				historyTag.setVisibility(View.GONE);
				if (beans != null && beans.size() >= 10) {
					listView.setPullLoadEnable(true);
				} else {
					listView.setPullLoadEnable(false);
				}
				listView.stopLoadMore();
				listView.stopRefresh();
				if (isResult) {
					adapter.setBeans(beans, ADAPTER_SHOW_TYPE_RESULT);
				} else {
					adapter.setBeans(beans, ADAPTER_SHOW_TYPE_LOAD);
				}
				adapter.notifyDataSetChanged();
			}

		}

	}

	protected void onDestroy() {
		super.onDestroy();
		if (myProgressDialog != null && !isFinishing()) {
			myProgressDialog.dismiss();
		}
		myProgressDialog = null;
	};

	private void setupView() {
		this.setViewHead("搜索");
		mWifiCRUDForMusic = new WifiCRUDForMusic(getBoxIp(), getBoxTcpPort());
		searchBtn = (Button) findViewById(R.id.music_search_btn);
		myProgressDialog = new MyProgressDialog(context);
		myProgressDialog.setMessage("正在搜索...");
		myProgressDialog.setCanceledOnTouchOutside(false);
		deleteIv = (ImageView) findViewById(R.id.music_search_delete_iv);
		listView = (XListView) findViewById(R.id.music_search_listview);
		listView.setPullRefreshEnable(false);
		listView.setPullLoadEnable(true);
		historyTag = (LinearLayout) findViewById(R.id.music_search_history_tag);
		historyTag.setVisibility(View.GONE);
		adapter = new MusicSearchItemAdapter(null, this, ADAPTER_SHOW_TYPE_HISTORY);
		listView.setAdapter(adapter);
		searchBtn.setVisibility(View.GONE);
		deleteIv.setVisibility(View.GONE);
		musicSearchDao = new MusicSearchDao(this);
		musicSearchEt = (EditText) findViewById(R.id.music_search_et);
		musicSearchEt.requestFocus();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.music_search_delete_iv:
			musicSearchEt.setText("");
			break;
		case R.id.music_search_btn:
			// search("" + musicSearchEt.getText().toString());//TODO 跳转页面
			String key = musicSearchEt.getText().toString().trim();
			if (TextUtils.isEmpty(key)) {
				ToastUtils.show(getApplicationContext(), "请输入关键词");
				return;
			}
			musicSearchDao.add(new MusicSearchBean(key, "", System.currentTimeMillis()));
			search(key, false, true);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			String key = musicSearchEt.getText().toString().trim();
			if (TextUtils.isEmpty(key)) {
				ToastUtils.show(getApplicationContext(), "请输入关键词");
				return true;
			}
			musicSearchDao.add(new MusicSearchBean(key, "", System.currentTimeMillis()));
			search(key, false, true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void parseStream(BufferedReader reader, boolean isLoadMore) throws IOException {
		synchronized (this) {

			ArrayList<MusicSearchBean> tempBeans = new ArrayList<MusicSearchBean>();
			if (beans != null) {
				tempBeans = new ArrayList<MusicSearchBean>(beans);
			}

			int page = 1;
			if (isLoadMore && !tempBeans.isEmpty()) {
				page = tempBeans.get(tempBeans.size() - 1).getPage() + 1;
			}
			beans = new ArrayList<MusicSearchBean>();
			try {
				String line = null;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					// 心肝宝贝(首届全国[爱肝日]主题曲)[刘德华^心肝宝贝(首届全国[爱肝日]主题曲)][http://nie.dfe.yymommy.com/mp3_128_1/03/96/0378de5e046e6a4b40a9a4def21f4496.mp3?k=d80872c3f056c337&t=1420880987]
					String deleteLastChar = line.substring(0, line.lastIndexOf("]"));
					MusicSearchBean bean = new MusicSearchBean(deleteLastChar.substring(deleteLastChar.indexOf("[") + 1, deleteLastChar.lastIndexOf("]")).trim(), line.substring(
							line.indexOf("http://"), line.lastIndexOf("]")), System.currentTimeMillis());
					bean.setPage(page);
					beans.add(bean);
				}
				if (isLoadMore) {
					if (!tempBeans.isEmpty() && !beans.isEmpty()) {
						try {
							if (!tempBeans
									.get(tempBeans.size() - 1)
									.getUrl()
									.substring(tempBeans.get(tempBeans.size() - 1).getUrl().lastIndexOf("/") + 1, tempBeans.get(tempBeans.size() - 1).getUrl().indexOf("?"))
									.equals(beans.get(beans.size() - 1).getUrl()
											.substring(beans.get(beans.size() - 1).getUrl().lastIndexOf("/") + 1, beans.get(beans.size() - 1).getUrl().indexOf("?")))) {
								tempBeans.addAll(beans);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					beans = new ArrayList<MusicSearchBean>(tempBeans);
				}
			} finally {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {
		int type = adapter.getType();
		if (type == ADAPTER_SHOW_TYPE_RESULT) {
			search(lastKey, true, true);
		} else {
			search(lastKey, true, false);
		}

	}
}
