package com.iii360.box.music;

import android.os.Bundle;

import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.KeyList;

public class MusicSearchResultActivity extends BaseActivity {
	private String keyWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_search_result);
		getIntentData();
		setupView();
	}

	private void setupView() {
		this.setViewHead("搜索结果("+keyWord+")");
	}

	private void getIntentData() {
		keyWord = getIntent().getExtras().getString(KeyList.KEY_MUSIC_SEARCH_KEY_EXTRA, "");
	}
}
