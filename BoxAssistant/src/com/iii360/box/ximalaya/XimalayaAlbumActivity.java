package com.iii360.box.ximalaya;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.iii360.box.MyApplication;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.HttpUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.box.view.XListView;
import com.iii360.box.view.XListView.IXListViewListener;
import com.nostra13.universalimageloader.core.ImageLoader;

/***
 * * http://3rd.ximalaya.com/categories/0/hot_albums?i_am=smallzhi&tag=%E9%83%AD
 * %E5%BE%B7%E7%BA%B2%E7%9B%B8%E5%A3%B0&page=1&per_page=5
 * 
 * @author terry
 * 
 */
public class XimalayaAlbumActivity extends BaseActivity implements IXListViewListener {

	private XListView mListView;
	private String tagName;
	private int perPage = 10;
	private String account = "smallzhi";
	private AlbumAdapter adapter;
	private ArrayList<Album> albums;
	private MyProgressDialog myProgressDialog;
	private String uni;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ximalaya_album_layout);
		getIntentData();
		setupView();
		addListener();
		myProgressDialog.show();
		onLoadMore();
	}

	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			ImageLoader.getInstance().clearMemoryCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 
	 * @param isRefresh
	 *            是不是下拉刷新
	 * 
	 */
	private void loadDataMore(final boolean isRefresh) {
		new Thread() {
			public void run() {
				int currentp = 0;
				if (isRefresh)
					currentp = 1;
				else if (albums != null && !albums.isEmpty()) {
					currentp = albums.get(albums.size() - 1).getCurrentPage() + 1;
				} else {
					currentp = 1;
				}

				try {
					HttpEntity entity = HttpUtils.getEntitywithGetMethod(KeyList.KEY_XIMALAYA_BASEURL + "categories/0/hot_albums?i_am=" + account
							+ "&tag=" + URLEncoder.encode(tagName, "utf-8") + "&page=" + currentp + "&per_page=" + perPage + "&uni=" + uni);
					// Log.i("info", "" + EntityUtils.toString(entity));
					parseJson(EntityUtils.toString(entity, "utf-8"), isRefresh);

				} catch (Exception e) {
					e.printStackTrace();
					ToastUtils.show(getApplicationContext(), "获取数据失败");
				}
				handler.post(new DismissDialog(false));
				handler.post(new UpdateViewTask());

			};
		}.start();
	}

	private class UpdateViewTask implements Runnable {

		@Override
		public void run() {
			adapter.setData(albums);
			adapter.notifyDataSetChanged();
			mListView.stopLoadMore();
			mListView.stopRefresh();
			if (albums != null && albums.size() >= 9) {
				mListView.setPullLoadEnable(true);
			} else {
				mListView.setPullLoadEnable(false);
			}
		}

	}

	private Handler handler = new Handler();

	/***
	 * 
	 * @param json
	 * @param isFresh是不是下拉刷新
	 * @return
	 * @throws Exception
	 */
	protected boolean parseJson(String json, boolean isFresh) throws Exception {
		synchronized (this) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00");
			JSONArray array = new JSONObject(json).getJSONArray("albums");
			int currentPage = new JSONObject(json).getInt("page");
			if (isFresh) {
				if (albums != null && !albums.isEmpty()) {
					albums = new ArrayList<Album>(albums);
					try {
						for (int i = array.length() - 1; i >= 0; i--) {
							JSONObject obj = array.getJSONObject(i);
							String path = obj.getString("cover_url_middle");
							String title = obj.getString("title");
							int id = obj.getInt("id");
							String time = obj.getString("last_uptrack_at");
							Album album = new Album();
							album.setId(id);
							album.setCurrentPage(currentPage);
							album.setImageUrl(path);
							album.setTitle(title);
							try {
								album.setLastUpTrack(sdf.parse(time));
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (!albums.contains(album))
								albums.add(0, album);

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				if (albums == null) {
					albums = new ArrayList<Album>();
				} else {
					albums = new ArrayList<Album>(albums);
				}

				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					String path = obj.getString("cover_url_middle");
					String title = obj.getString("title");
					int id = obj.getInt("id");
					String time = obj.getString("last_uptrack_at");
					Album album = new Album();
					album.setId(id);
					album.setCurrentPage(currentPage);
					album.setImageUrl(path);
					album.setTitle(title);
					try {
						album.setLastUpTrack(sdf.parse(time));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!albums.contains(album)) {
						albums.add(album);
					}

				}
			}
			if (array.length() == 0)
				return true;
			return false;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter.close();
		handler.post(new DismissDialog(true));
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

	private void getIntentData() {
		tagName = getIntent().getStringExtra(KeyList.KEY_XIMALAYA_TAG_NAME);
	}

	private void addListener() {
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Album album = albums.get(position - 1);
				Intent intent = new Intent(getApplicationContext(), XimalayaAudioActivity.class);
				intent.putExtra(KeyList.KEY_XIMALAYA_ALBUM_NAME, album);
				startActivity(intent);
			}
		});
	}

	private void setupView() {
		this.setViewHead(tagName != null ? tagName : "");
		uni = MyApplication.getSerialNums().get(getPrefString(KeyList.GKEY_BOX_IP_ADDRESS));
		if (uni == null)
			uni = "SZA0A2507C8Y";
		uni=uni.trim();
		myProgressDialog = new MyProgressDialog(context);
		myProgressDialog.setMessage(getString(R.string.ba_update_date));
		myProgressDialog.setCanceledOnTouchOutside(false);
		mListView = (XListView) findViewById(R.id.ximalaya_album_listview);
		mListView.setPullLoadEnable(false);
		adapter = new AlbumAdapter(context, null, mListView);
		mListView.setAdapter(adapter);
	}

	@Override
	public void onRefresh() {
		loadDataMore(true);
	}

	@Override
	public void onLoadMore() {
		loadDataMore(false);
	}
}
