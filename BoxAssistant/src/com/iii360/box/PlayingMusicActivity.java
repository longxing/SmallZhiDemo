package com.iii360.box;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.info.WifiVolume;
import com.iii.wifi.dao.manager.WifiCRUDForVolume;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic.ResultForMusicListener;
import com.iii360.box.adpter.MusicPagerAdapter;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.music.MusicListActivity;
import com.iii360.box.util.AdaptUtil;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.box.view.NewViewHead;
import com.iii360.box.view.RotateImageView;
import com.iii360.box.view.RotateReleaseImageView;
import com.iii360.box.voice.VoiceRecognizeActivity;

public class PlayingMusicActivity extends BaseActivity implements OnClickListener, IView, OnSeekBarChangeListener {

	private SeekBar seekBar;
	private WifiCRUDForVolume mWifiCRUDForVolume;
	protected int mCurrVolume;
	protected int mMaxVolume;
	private WifiCRUDForMusic mWifiCRUDForMusic;
	private static final int HANDLE_GET_STATE_SUCCESS = 1;
	private static final int HANDLE_GET_STATE_FAIL = 2;
	private static final int HANDLE_OPERATION_FAIL = 3;
	private static final int HANDLER_GET_STATE_VOLUME = 4;
	private Button playOrPause;
	ImageButton playPre;
	ImageButton playNext;
	private static final String MUSIC_STATE_STOP = "0";
	private static final String MUSIC_STATE_PLAYING = "1";
	private static final String MUSIC_STATE_PAUSE = "2";
	private static final String MUSIC_STATE_DLAN = "3";
	private String musicName;
	private String singer;
	private TextView songTv, singerTv;
	private long requestDelayed = 0;
	private long volumeRequestPeriod = 1500;
	private long changeButtonStateDelayed = 2000;
	private long collectTime = 0;
	private long volumeChangeTime = 0;
	private LinearLayout music_player_dlan;
	private RelativeLayout music_player_normal;
	private long hiddenSeekBarDelay = 4000;
	private boolean isChangeAction = false;
	private ViewPager playing_music_flipper;
	private ImageView badMusicBtn, goodMusicBtn, sharedBtn;
	private RotateReleaseImageView playerOperate;
	private RelativeLayout playerMainLayout;
	private boolean isOperateButton;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_GET_STATE_SUCCESS:
				updateView((WifiMusicInfo) msg.obj);
				break;
			case HANDLE_GET_STATE_FAIL:
				// ToastUtils.show(context, R.string.ba_get_info_error_toast);
				break;
			case HANDLE_OPERATION_FAIL:
				ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
				break;
			case HANDLER_GET_STATE_VOLUME:
				getstate();
				mHandler.sendEmptyMessageDelayed(HANDLER_GET_STATE_VOLUME, volumeRequestPeriod);
				break;
			}
		};
	};
	private MyProgressDialog progress;

	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		mHandler.sendEmptyMessage(HANDLER_GET_STATE_VOLUME);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mHandler.removeMessages(HANDLER_GET_STATE_VOLUME);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playing_music);
		try {
			initViews();
			initDatas();
			mHandler.sendEmptyMessage(HANDLER_GET_STATE_VOLUME);
			getstate();
			progress = new MyProgressDialog(this);
			progress.setMessage(getString(R.string.ba_update_date));
			progress.setCanceledOnTouchOutside(false);
		} catch (OutOfMemoryError e) {
			finish();
		}
	}

	public void dismissDialog() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				if (progress != null && !isFinishing())
					progress.dismiss();
			}
		});
	}

	public void showDialog() {
		if (progress == null)
			return;
		progress.show();
	}

	/*****
	 * 判断字符串中有没有英文
	 * 
	 * @param text
	 * @return
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (progress != null && !isFinishing()) {
			progress.dismiss();
		}
		progress = null;
		playerMainLayout.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		if (bgTempbm != null && !bgTempbm.isRecycled())
			bgTempbm.recycle();
		bgTempbm = null;
	}

	private boolean isDlanPlay;
	private WifiMusicInfo currMusicInfo;

	protected void updateView(WifiMusicInfo info) {
		currMusicInfo = info;
		if (System.currentTimeMillis() - collectTime > changeButtonStateDelayed) {
			if (currMusicInfo.is_isCollected()) {
				goodMusicBtn.setImageResource(R.drawable.player_good_music_pressed);
			} else {
				goodMusicBtn.setImageResource(R.drawable.player_good_music_selector);
			}
		}
		String currState = info.getPlayStatus();
		if (MUSIC_STATE_DLAN.equals(currState)) {
			isDlanPlay = true;
			music_player_dlan.setVisibility(View.VISIBLE);
			music_player_normal.setVisibility(View.GONE);
		} else {
			isDlanPlay = false;
			music_player_dlan.setVisibility(View.GONE);
			music_player_normal.setVisibility(View.VISIBLE);
			String currMusicName = info.getName() != null ? info.getName() : "";
			String currSinger = info.getAuthor() != null ? info.getAuthor() : "";
			int currVolume = info.getMusicCurrVolume() - 1;
			int maxVolume = info.getMusicMaxVolume() - 1;
			LogManager.e("volume :" + currVolume + "," + maxVolume);
			if (maxVolume != seekBar.getMax()) {
				seekBar.setMax(maxVolume);
			}
			if (System.currentTimeMillis() - volumeChangeTime > changeButtonStateDelayed) {
				if (currVolume != seekBar.getProgress()) {
					seekBar.setTag("unchange");
					seekBar.setProgress(currVolume);
				}
			}
			if (!isOperateButton) {
				if (MUSIC_STATE_STOP.equals(currState) || MUSIC_STATE_PAUSE.equals(currState)) {
					if (!"".equals(getPrefString(KeyList.PKEY_SELECT_MUSIC_ID))) {
						setPrefString(KeyList.PKEY_SELECT_MUSIC_ID, "");
					}
					playerRoundIv.pauseRound();
					playerOperate.release();
					playOrPause.setBackgroundResource(R.drawable.player_play_btn_selector);
				} else if (MUSIC_STATE_PLAYING.equals(currState)) {
					if (info.getMusicId() != null && !info.getMusicId().equals(getPrefString(KeyList.PKEY_SELECT_MUSIC_ID))) {
						setPrefString(KeyList.PKEY_SELECT_MUSIC_ID, info.getMusicId());
					}
					playerOperate.startRound();
					playerRoundIv.startRound();
					playOrPause.setBackgroundResource(R.drawable.player_pause_btn_selector);
				}
			}
			// }
			if (!currMusicName.equals(musicName)) {
				playerMainLayout.setBackgroundDrawable(getBgRandom(playerMainLayout));
				songTv.setText(currMusicName);
			}
			if (!currSinger.equals(singer)) {
				singerTv.setText(currSinger);
			}
			musicName = currMusicName;
			singer = currSinger;
		}

	}

	int times;
	private int currentIndex;
	private ImageView[] guideViewPoints;
	private RotateImageView playerRoundIv;
	private View toXmlyBtn;
	private int[] bg_resources = { R.drawable.player_bg1, R.drawable.player_bg2, R.drawable.player_bg3, R.drawable.player_bg4, R.drawable.player_bg5, R.drawable.player_bg6 };
	private Bitmap bgTempbm;

	private Drawable getBgRandom(View view) {
		view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		if (bgTempbm != null && !bgTempbm.isRecycled()) {
			bgTempbm.recycle();
			bgTempbm = null;
		}
		int res = bg_resources[new Random().nextInt(bg_resources.length)];
		InputStream is = getResources().openRawResource(res);
		Bitmap bm = BitmapFactory.decodeStream(is);
		bgTempbm = bm;
		BitmapDrawable bd = new BitmapDrawable(getResources(), bm);
		return bd;
	}

	@Override
	public void initViews() {
		music_player_dlan = (LinearLayout) findViewById(R.id.music_player_dlan);
		badMusicBtn = (ImageView) findViewById(R.id.player_bad_music_btn);
		goodMusicBtn = (ImageView) findViewById(R.id.player_good_music_btn);
		sharedBtn = (ImageView) findViewById(R.id.player_shared_music_btn);
		playerMainLayout = (RelativeLayout) findViewById(R.id.player_main_relativelayout);
		playerMainLayout.setBackgroundDrawable(getBgRandom(playerMainLayout));
		sharedBtn.setVisibility(View.GONE);
		// music_player_normal.setOnClickListener(new MyOnClickListener());
		if (AdaptUtil.isNewProtocol252()) {
			goodMusicBtn.setVisibility(View.VISIBLE);
			badMusicBtn.setVisibility(View.VISIBLE);
		} else {
			goodMusicBtn.setVisibility(View.GONE);
			badMusicBtn.setVisibility(View.GONE);
		}
		playOrPause = (Button) findViewById(R.id.music_player_play_or_pause);
		playNext = (ImageButton) findViewById(R.id.music_player_next);
		playPre = (ImageButton) findViewById(R.id.music_player_last);
		playing_music_flipper = (ViewPager) findViewById(R.id.playing_music_flipper);
		playOrPause.setOnClickListener(this);
		playNext.setOnClickListener(this);
		playPre.setOnClickListener(this);
		goodMusicBtn.setOnClickListener(this);
		badMusicBtn.setOnClickListener(this);
		sharedBtn.setOnClickListener(this);
		seekBar = (SeekBar) findViewById(R.id.music_player_seekBar);
		NewViewHead.showAll(context, getString(R.string.ba_main_music), R.drawable.player_to_list_btn_selector).setOnClickListener(this);
		mWifiCRUDForVolume = new WifiCRUDForVolume(getBoxIp(), getBoxTcpPort());
		LinearLayout guideViewPointLinearlayout = (LinearLayout) findViewById(R.id.guide_view_point_linearlayout);
		// TODO 去掉歌词
		guideViewPointLinearlayout.setVisibility(View.GONE);
		guideViewPoints = new ImageView[2];
		for (int i = 0; i < guideViewPoints.length; i++) {
			guideViewPoints[i] = (ImageView) guideViewPointLinearlayout.getChildAt(i);
			guideViewPoints[i].setEnabled(false);
		}
		LayoutInflater inflater = LayoutInflater.from(this);
		ArrayList<View> list = new ArrayList<View>();
		View view = inflater.inflate(R.layout.playing_music_name, null);
		music_player_normal = (RelativeLayout) view.findViewById(R.id.music_player_normal);
		songTv = (TextView) view.findViewById(R.id.player_music_name);
		songTv.setSelected(true);
		singerTv = (TextView) view.findViewById(R.id.player_music_singer);
		toXmlyBtn = view.findViewById(R.id.player_to_xmly_btn);
		toXmlyBtn.setOnClickListener(this);
		// 点击显示或隐藏seekbar
		view.findViewById(R.id.music_player_normal).setOnClickListener(new MyOnClickListener());
		view.findViewById(R.id.music_player_normal).setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				// setParam();
				// iatDialog.setListener(recognizerDialogListener);
				// iatDialog.show();
				startVoiceActivity();
				return false;
			}
		});
		playerRoundIv = (RotateImageView) view.findViewById(R.id.player_round_iv);
		setRotateImageView();
		playerOperate = (RotateReleaseImageView) view.findViewById(R.id.player_operate_iv);
		setRotateImageView_operate();
		list.add(view);
		view = inflater.inflate(R.layout.playing_music_lrc, null);
		// TODO 去掉歌词
		// list.add(view);
		playing_music_flipper.setAdapter(new MusicPagerAdapter(list));
		guideViewPoints[0].setEnabled(true);
		playing_music_flipper.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				setCurDot(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	public void startVoiceActivity() {
		if (!AdaptUtil.isNewProtocol252()) {
			ToastUtils.show(context, R.string.old_box_tip);
			return;
		}
		Intent intent = new Intent(context, VoiceRecognizeActivity.class);
		startActivity(intent);
	}

	private void setRotateImageView() {
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) playerRoundIv.getLayoutParams();
		Bitmap bm = ((BitmapDrawable) (getResources().getDrawable(R.drawable.player_round))).getBitmap();
		params.width = bm.getWidth();
		params.height = bm.getHeight();
		if (BoxManagerUtils.getScreenDensity(context)> 1.5) {
			params.topMargin = 80;
		}
		playerRoundIv.setLayoutParams(params);
		playerRoundIv.roundCenter();
		playerRoundIv.setBitmap(bm);
		playerRoundIv.invalidate();
	}

	private void setRotateImageView_operate() {
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) playerOperate.getLayoutParams();
		Bitmap bm = ((BitmapDrawable) (getResources().getDrawable(R.drawable.player_start01))).getBitmap();
		params.height = bm.getHeight();
		params.width = bm.getWidth();
		float density = BoxManagerUtils.getScreenDensity(context);
		if (density > 1.5) {
			params.topMargin = 60;
		}
		playerOperate.setLayoutParams(params);
		playerOperate.setBitmap(bm);
		if (density <= 1.5) {
			playerOperate.setMaxDegrees(30);
		} else {
			playerOperate.setMaxDegrees(42);
		}

		playerOperate.setRoundRadiuX(0.7963);
		playerOperate.setRoundRadiuY(0.2045);
		playerOperate.invalidate();
	}

	private void setCurDot(int positon) {
		if (currentIndex == positon) {
			return;
		}

		guideViewPoints[positon].setEnabled(true);
		guideViewPoints[currentIndex].setEnabled(false);
		currentIndex = positon;
	}

	/**
	 * 获取音乐状态
	 */
	private void getstate() {
		LogManager.e("request state and volume");
		mWifiCRUDForMusic.playState(new ResultForMusicListener() {
			@Override
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				if (infos != null) {
					LogManager.e("state and volume result errorCode ：" + errorCode + "data：" + new Gson().toJson(infos));
				} else {
					LogManager.e("state and volume result errorCode ：" + errorCode + "data：" + infos);
				}
				if (WifiCRUDUtil.isSuccess(errorCode) && infos != null && !infos.isEmpty()) {
					Message msg = new Message();
					msg.obj = infos.get(0);
					msg.what = HANDLE_GET_STATE_SUCCESS;
					mHandler.sendMessage(msg);
				} else {
					mHandler.sendEmptyMessage(HANDLE_GET_STATE_FAIL);
				}
			}
		});
	}

	@Override
	public void initDatas() {
		mWifiCRUDForMusic = new WifiCRUDForMusic(getBoxIp(), getBoxTcpPort());
		seekBar.setOnSeekBarChangeListener(this);
	}

	private void badMusic() {
		progress.show();
		mWifiCRUDForMusic.badMusic(new ResultForMusicListener() {
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				mHandler.post(dismissDialog);
				if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
					ToastUtils.show(getApplicationContext(), "操作失败");
				}
			}
		});
	}

	private void goodMusicOrCancel() {
		progress.show();
		if (currMusicInfo != null && currMusicInfo.is_isCollected()) {
			mWifiCRUDForMusic.delete(currMusicInfo.getMusicId() + "", new ResultForMusicListener() {
				public void onResult(String errorCode, List<WifiMusicInfo> infos) {
					mHandler.post(dismissDialog);
					if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
						ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								collectTime = System.currentTimeMillis();
								goodMusicBtn.setImageResource(R.drawable.player_good_music_selector);
							}
						});
					}
				}
			});
		} else {
			mWifiCRUDForMusic.goodMusic(new ResultForMusicListener() {
				public void onResult(String errorCode, List<WifiMusicInfo> infos) {
					mHandler.post(dismissDialog);
					if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
						ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
					} else {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								collectTime = System.currentTimeMillis();
								goodMusicBtn.setImageResource(R.drawable.player_good_music_pressed);
							}
						});
					}
				}
			});
		}
	}

	private Runnable dismissDialog = new Runnable() {
		public void run() {
			if (progress != null && !isFinishing()) {
				progress.dismiss();
			}
		}
	};

	@Override
	public void onClick(View v) {
		seekBar.setVisibility(View.GONE);
		switch (v.getId()) {
		case R.id.player_bad_music_btn:
			badMusic();
			break;
		case R.id.player_to_xmly_btn:

			break;
		case R.id.player_good_music_btn:
			goodMusicOrCancel();
			break;
		case R.id.player_shared_music_btn:
			break;
		case R.id.head_left_textview:
			// ToastUtils.show(this, "back");
			finish();
			break;
		case R.id.head_right_btn:
			// ToastUtils.show(this, "list");
			if (AdaptUtil.isNewProtocol252()) {
				startToActvitiyNoFinish(MusicListActivity.class);
				overridePendingTransition(R.anim.in_from_down, R.anim.out_to_up);
			} else {
				startToActvitiyNoFinish(MainMyLoveActivity.class);
				overridePendingTransition(R.anim.in_from_down, R.anim.out_to_up);
			}

			break;
		case R.id.music_player_play_or_pause:

			playOrPause();
			break;
		case R.id.music_player_next:
			next();
			break;
		case R.id.music_player_last:
			pervious();
			break;
		}
	}

	private void next() {
		mWifiCRUDForMusic.playNext(new ResultForMusicListener() {
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				LogManager.e("音乐操作回调next");
				handleResult(errorCode, infos);
			}
		});
	}

	private void pervious() {
		mWifiCRUDForMusic.playPre(new ResultForMusicListener() {
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				LogManager.e("音乐操作回调pervious");
				handleResult(errorCode, infos);
			}
		});
	}

	private void playOrPause() {
		isOperateButton = true;
		mWifiCRUDForMusic.playOrPause(new ResultForMusicListener() {
			public void onResult(String errorCode, final List<WifiMusicInfo> infos) {
				LogManager.e("音乐操作回调playOrPause");
				handleResult(errorCode, infos);
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (MUSIC_STATE_PLAYING.equals(infos.get(0).getPlayStatus())) {
								playerRoundIv.startRound();
								playerOperate.startRound();
								playOrPause.setBackgroundResource(R.drawable.player_pause_btn_selector);
							} else {
								playerOperate.release();
								playerRoundIv.pauseRound();
								playOrPause.setBackgroundResource(R.drawable.player_play_btn_selector);

							}
							mHandler.postDelayed(new Runnable() {

								@Override
								public void run() {
									isOperateButton = false;

								}
							}, 4000);
						}
					});
				}
			}
		});
	}

	private void handleResult(String errorCode, List<WifiMusicInfo> infos) {
		LogManager.e("音乐操作结果errorCode：" + errorCode);
		if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
			mHandler.sendEmptyMessage(HANDLE_OPERATION_FAIL);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (isDlanPlay) {
			return super.onKeyUp(keyCode, event);
		}
		if (KeyEvent.KEYCODE_VOLUME_DOWN == keyCode || KeyEvent.KEYCODE_VOLUME_UP == keyCode) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		if (isDlanPlay) {
			return;
		}
		if (!isChangeAction) {
			return;
		}

		String auto = (String) arg0.getTag();
		if (auto != null && auto.equals("unchange")) {
			arg0.setTag(null);
			return;
		}
		mHandler.removeCallbacks(r, null);
		mHandler.postDelayed(r, hiddenSeekBarDelay);
		seekBar.setVisibility(View.VISIBLE);
		setVolumnAction(arg1);
	}

	private void setVolumnAction(int arg1) {
		mWifiCRUDForVolume.setVolumeInfo(arg1 + 1, new WifiCRUDForVolume.ResultForVolumeListener() {
			@Override
			public void onResult(String type, String errorCode, WifiVolume wifiVolume) {
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					volumeChangeTime = System.currentTimeMillis();
					LogManager.e("set box volume success...");
				} else {
					LogManager.e("set box volume error...");
				}
			}
		});
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		isChangeAction = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		isChangeAction = false;
		setVolumnAction(arg0.getProgress());
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isDlanPlay) {
			return super.onKeyDown(keyCode, event);
		}
		// 获取手机当前音量值

		switch (keyCode) {

		// 音量减小

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			mHandler.removeCallbacks(r, null);
			mHandler.postDelayed(r, hiddenSeekBarDelay);
			seekBar.setVisibility(View.VISIBLE);
			int pro = seekBar.getProgress();
			if (pro > 0) {
				pro--;
				seekBar.setProgress(pro);
				setVolumnAction(pro);
			}
			// 音量减小时应该执行的功能代码

			return true;

			// 音量增大

		case KeyEvent.KEYCODE_VOLUME_UP:
			// seekBar.setVisibility(View.VISIBLE);
			// 音量增大时应该执行的功能代码
			mHandler.removeCallbacks(r, null);
			mHandler.postDelayed(r, hiddenSeekBarDelay);
			seekBar.setVisibility(View.VISIBLE);
			pro = seekBar.getProgress();
			if (pro < seekBar.getMax()) {
				pro++;
				seekBar.setProgress(pro);
				setVolumnAction(pro);
			}
			return true;

		}

		return super.onKeyDown(keyCode, event);

	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			int state = seekBar.getVisibility();
			mHandler.removeCallbacks(r, null);
			if (state == View.GONE) {
				seekBar.setVisibility(View.VISIBLE);
				mHandler.postDelayed(r, hiddenSeekBarDelay);
			} else {
				mHandler.removeCallbacks(r, null);
				seekBar.setVisibility(View.GONE);
			}
		}

	}

	Runnable r = new Runnable() {
		public void run() {
			seekBar.setVisibility(View.GONE);
		}
	};

}
