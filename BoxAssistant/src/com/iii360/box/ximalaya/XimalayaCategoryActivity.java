package com.iii360.box.ximalaya;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.iii360.box.MyApplication;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.HttpUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.view.MyProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

/****
 * 获得热门Tag列表 http://3rd.ximalaya.com/categories/0/tags?i_am=smallzhi
 * 
 * 根据不同的Tag获得热门专辑列表（红色标记为上面接口得到的参数）
 * 
 * http://3rd.ximalaya.com/categories/0/hot_albums?i_am=smallzhi&tag=%E9%83%AD%
 * E5%BE%B7%E7%BA%B2%E7%9B%B8%E5%A3%B0&page=1&per_page=5
 * 
 * 根据专辑id获得专辑声音列表（红色标记为上面接口得到的参数）
 * 
 * http://3rd.ximalaya.com/albums/290488/tracks?i_am=smallzhi&page=1&per_page=5&
 * is_asc=true
 * 
 * 
 * @author terry
 * 
 */
public class XimalayaCategoryActivity extends BaseActivity {
	private GridView gridView;
	private CategoryAdapter adapter;
	static final int COLUMN_NUM = 3;
	private ArrayList<String> tagNames;
	private ArrayList<String> imageUrls;
	private MyProgressDialog mProgressDialog;
	private String account = "smallzhi";
	private String uni;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ximalaya_category_layout);
		setupView();
		addListeners();
		initData();
	}
	@Override
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
	private void initData() {
		mProgressDialog.show();
		new Thread() {
			public void run() {
				// http://3rd.ximalaya.com/categories/0/tags?i_am=smallzhi
				HttpEntity entity = HttpUtils.getEntitywithGetMethod(KeyList.KEY_XIMALAYA_BASEURL + "categories/0/tags?i_am=" + account + "&uni="
						+ uni);
				if (entity == null) {
					ToastUtils.show(context, "获取数据失败");
					handler.post(dismissDialogTask);
					return;
				}
				try {
					parserJson(EntityUtils.toString(entity, "utf-8"));
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtils.show(context, "获取数据失败");
				}
				handler.post(dismissDialogTask);
			};
		}.start();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter.close();
		handler.post(dismissDialogTask);
		mProgressDialog = null;

	}

	/***
	 * "ret": 0, "category_id": 0, "tags": [ { "name": "郭德纲相声",
	 * "cover_url_small":
	 * "http://fdfs.xmcdn.com/group3/M03/65/6E/wKgDsVJ7CNnAvmUDAAAeUWi2sBo837.jpg"
	 * , "cover_url_large":
	 * "http://fdfs.xmcdn.com/group3/M03/65/0E/wKgDslJ7CNnyanIMAAAeUWi2sBo213.jpg"
	 * }]
	 * 
	 * @param json
	 * @throws Exception
	 */
	private void parserJson(String json) throws Exception {
		JSONArray array = new JSONObject(json).getJSONArray("tags");
		tagNames = new ArrayList<String>();
		imageUrls = new ArrayList<String>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			imageUrls.add(obj.getString("cover_url_small"));
			tagNames.add(obj.getString("name"));
		}
		handler.post(updateViewTask);
	}

	private Handler handler = new Handler();
	private Runnable updateViewTask = new Runnable() {

		@Override
		public void run() {
			adapter.setData(tagNames, imageUrls);
			adapter.notifyDataSetChanged();
		}
	};
	private Runnable dismissDialogTask = new Runnable() {

		@Override
		public void run() {
			if (mProgressDialog != null && !isFinishing()) {
				mProgressDialog.dismiss();
			}
		}
	};

	private void addListeners() {
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String tagName = tagNames.get(position);
				Intent intent = new Intent(getApplicationContext(), XimalayaAlbumActivity.class);
				intent.putExtra(KeyList.KEY_XIMALAYA_TAG_NAME, tagName);
				// Toast.makeText(getApplicationContext(), ""+tagName,
				// Toast.LENGTH_SHORT).show();
				startActivity(intent);
			}
		});

	}

	private void setupView() {
		this.setViewHead("热门");
		mProgressDialog = new MyProgressDialog(context);
		mProgressDialog.setMessage(getString(R.string.ba_update_date));
		gridView = (GridView) findViewById(R.id.ximalaya_gridview);
		uni = MyApplication.getSerialNums().get(getPrefString(KeyList.GKEY_BOX_IP_ADDRESS));
		if (uni == null)
			uni = "SZA0A2507C8Y";
		uni=uni.trim();
		gridView.setNumColumns(COLUMN_NUM);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new CategoryAdapter(this, null, null, gridView);
		gridView.setAdapter(adapter);
	}
}
