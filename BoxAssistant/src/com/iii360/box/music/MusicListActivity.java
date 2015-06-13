package com.iii360.box.music;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.iii360.box.MyApplication;
import com.iii360.box.R;
import com.iii360.box.adpter.GuideViewFragmentAdapter;
import com.iii360.box.fragment.GoodMusicListFragment;
import com.iii360.box.fragment.PlayingListFragment;
import com.iii360.box.view.MyProgressDialog;

public class MusicListActivity extends FragmentActivity implements OnClickListener {
	private ViewPager viewContainer;
	private Button playingBtn, goodmusicBtn;
	private View playingBelowView, goodmusicBelowView;
	private boolean needUpdate;

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.ActivityNoAnimationTheme);
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_music_list);
		setupView();
		fillView();
		addListeners();
	}

	private void addListeners() {
		findViewById(R.id.head_left_textview).setOnClickListener(this);
		playingBtn.setOnClickListener(this);
		goodmusicBtn.setOnClickListener(this);
		viewContainer.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				showFragment(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	protected void showFragment(int index) {
		switch (index) {
		case 0:
			playingBelowView.setBackgroundColor(Color.parseColor("#3898d8"));
			goodmusicBelowView.setBackgroundColor(Color.parseColor("#cccccc"));
			playingBtn.setTextColor(Color.parseColor("#3898d8"));
			goodmusicBtn.setTextColor(Color.parseColor("#333333"));
			break;
		case 1:
			goodmusicBelowView.setBackgroundColor(Color.parseColor("#3898d8"));
			playingBelowView.setBackgroundColor(Color.parseColor("#cccccc"));
			goodmusicBtn.setTextColor(Color.parseColor("#3898d8"));
			playingBtn.setTextColor(Color.parseColor("#333333"));
			break;
		}
	}

	private MyProgressDialog dialog;

	private void setupView() {
		((TextView) findViewById(R.id.head_title_tv)).setText("歌曲列表");
		dialog = new MyProgressDialog(this);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage(getString(R.string.ba_update_date));
		viewContainer = (ViewPager) findViewById(R.id.music_list_viewContainer);
		playingBtn = (Button) findViewById(R.id.music_list_playing_tag_btn);
		goodmusicBtn = (Button) findViewById(R.id.music_list_goodmusic_tag_btn);
		playingBelowView = findViewById(R.id.music_list_playing_tag_below_view);
		goodmusicBelowView = findViewById(R.id.music_list_goodmusic_tag_below_view);
		playingBelowView.setBackgroundColor(Color.parseColor("#3898d8"));
		goodmusicBelowView.setBackgroundColor(Color.parseColor("#cccccc"));
		playingBtn.setTextColor(Color.parseColor("#3898d8"));
		goodmusicBtn.setTextColor(Color.parseColor("#333333"));
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

	private void fillView() {
		ArrayList<Fragment> list = new ArrayList<Fragment>();
		list.add(new PlayingListFragment(this));
		list.add(new GoodMusicListFragment(this));
		GuideViewFragmentAdapter adapter = new GuideViewFragmentAdapter(getSupportFragmentManager(), list);
		viewContainer.setAdapter(adapter);
	}

	public void dismissDialog(boolean isSetNull) {
		handler.post(new DismissDialog(isSetNull));
	}

	public void showDialog() {
		if (dialog == null) {
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

	@Override
	protected void onDestroy() {
		dismissDialog(true);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.music_list_playing_tag_btn:
			viewContainer.setCurrentItem(0);
			break;
		case R.id.music_list_goodmusic_tag_btn:
			viewContainer.setCurrentItem(1);
			break;
		case R.id.head_left_textview:
			// viewContainer.setCurrentItem(1);
			finish();
			overridePendingTransition(0, R.anim.out_from_up);
			break;

		}
	}
}
