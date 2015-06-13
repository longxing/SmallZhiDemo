package com.iii360.box;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic.ResultForMusicListener;
import com.iii360.box.adpter.MainMyLoveListApdater;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.PingYinUtil;
import com.iii360.box.util.PinyinComparator;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.box.view.SlideBar;
import com.iii360.box.view.SlideBar.OnTouchLetterChangeListenner;

/**
 * 我的最爱
 * 
 * @author Administrator
 * 
 */
public class MainMyLoveActivity extends BaseActivity implements IView {
	private ListView mListView;
	private MainMyLoveListApdater mMainMyLoveListApdater;
	private TextView mFloatLetterTv;
	private SlideBar mSlideBar;
	public final static long MUSIC_PLAY_TIMEOUT = 5 * 60 * 1000;
	private TextView mytagBtn;
	private TextView myloveBack;
	/**
	 * 歌曲列表
	 */
	private List<WifiMusicInfo> mMusicList;
	/**
	 * 歌曲名称首字母列表
	 */
	private List<String> mPinyinList;
	private MyProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_mylove);
		initViews();
		initDatas();
	}

	@Override
	public void initViews() {
		mytagBtn = (TextView) findViewById(R.id.mylove_tag_bn);
		mytagBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startToActvitiyNoFinish(MainTagActivity.class);
			}
		});
		myloveBack = (TextView) findViewById(R.id.head_left_textview);
		myloveBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.out_from_up);
			}
		});
		TextView myloveTitle = (TextView) findViewById(R.id.head_title_tv);
		myloveTitle.setText("我的最爱");
		mListView = (ListView) findViewById(R.id.main_music_list);
		mSlideBar = (SlideBar) findViewById(R.id.music_slidebar);
		mFloatLetterTv = (TextView) findViewById(R.id.music_float_letter_tv);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.out_from_up);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void initDatas() {
		// TODO Auto-generated method stub
		mProgressDialog = new MyProgressDialog(context);
		mProgressDialog.setMessage(getString(R.string.ba_update_date));
		mProgressDialog.show();

		long lastPlayMusicTime = getPrefLong(KeyList.PKEY_SELECT_MUSIC_TIME, 0);
		final long startTime = System.currentTimeMillis();

		if (startTime - lastPlayMusicTime > MUSIC_PLAY_TIMEOUT) {
			LogManager.i("播放歌曲超时，恢复默认状态");
			setPrefString(KeyList.PKEY_SELECT_MUSIC_ID, "-1");
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				WifiCRUDForMusic mWifiCRUDForMusic = new WifiCRUDForMusic(getBoxIp(), getBoxTcpPort());
				mWifiCRUDForMusic.getMusicList(new ResultForMusicListener() {
					@Override
					public void onResult(String errorCode, List<WifiMusicInfo> infos) {
						// TODO Auto-generated method stub
						if (WifiCRUDUtil.isSuccessAll(errorCode)) {
							LogManager.i("getMusicList ok" + infos.size());
							mMusicList = infos;
							Collections.sort(mMusicList, new PinyinComparator());
							setFirstPinYin();
							mHandler.sendEmptyMessage(HANDLER_LOAD_ADAPTER);
						} else {
							LogManager.i("getMusicList error");
							ToastUtils.show(context, R.string.ba_get_info_error_toast);
						}

						while (System.currentTimeMillis() - startTime < 500) {

						}
						if (mProgressDialog != null && !isFinishing())
							mProgressDialog.dismiss();
					}
				});
			}
		}).start();

	}

	/**
	 * @return 拼音首字母列表
	 */
	private List<String> setFirstPinYin() {
		mPinyinList = new ArrayList<String>();
		String pinyinTemp = "";
		for (WifiMusicInfo info : mMusicList) {
			pinyinTemp = PingYinUtil.getPingYin(info.getName());
			mPinyinList.add(pinyinTemp.substring(0, 1));
		}

		return mPinyinList;
	}

	public static final int HANDLER_LOAD_ADAPTER = 1;
	public static final int HANDLER_DISSMIS_DIALOG = 2;
	public static final int HANDLER_TIMEOUT = 3;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int what = msg.what;

			switch (what) {

			case HANDLER_LOAD_ADAPTER:
				LogManager.i("come...");
				mMainMyLoveListApdater = new MainMyLoveListApdater(context, mMusicList, mHandler);
				mListView.addFooterView(LayoutInflater.from(context).inflate(R.layout.view_small_line, null));
				mListView.setAdapter(mMainMyLoveListApdater);

				mSlideBar.setOnTouchLetterChangeListenner(new OnTouchLetterChangeListenner() {
					@Override
					public void onTouchLetterChange(boolean isTouched, String s) {
						mFloatLetterTv.setText(s);
						if (isTouched) {
							mFloatLetterTv.setVisibility(View.VISIBLE);
						} else {
							mFloatLetterTv.postDelayed(new Runnable() {
								@Override
								public void run() {
									mFloatLetterTv.setVisibility(View.GONE);
								}
							}, 100);
						}
						int position = mPinyinList.indexOf(s);
						mListView.setSelection(position);
					}
				});

				break;

			case HANDLER_DISSMIS_DIALOG:
				if (mProgressDialog != null && !MainMyLoveActivity.this.isFinishing()) {
					mProgressDialog.dismiss();
				}

				break;

			case HANDLER_TIMEOUT:
				setPrefString(KeyList.PKEY_SELECT_MUSIC_ID, "-1");
				mHandler.sendEmptyMessage(HANDLER_LOAD_ADAPTER);

				break;

			default:
				break;
			}
		}
	};

	protected void onDestroy() {
		if (mProgressDialog != null && !this.isFinishing())
			mProgressDialog.dismiss();
		mProgressDialog = null;
		super.onDestroy();
	};
}
