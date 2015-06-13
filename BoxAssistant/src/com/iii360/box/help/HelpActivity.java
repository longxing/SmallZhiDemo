package com.iii360.box.help;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;

public class HelpActivity extends BaseActivity implements OnClickListener {

	private HTML5WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWebView = new HTML5WebView(this);

		if (savedInstanceState != null) {
			mWebView.restoreState(savedInstanceState);
		} else {
			mWebView.loadUrl("http://smallzhi.com/sp/appFAQ.jsp");
		}
		setContentView(mWebView.getLayout());
		setupView();
	}

	private void setupView() {
		((TextView) findViewById(R.id.head_title_tv)).setText("帮助");
		findViewById(R.id.head_left_textview).setOnClickListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			mWebView.stopLoading();
			if (mWebView.inCustomView()) {
				mWebView.hideCustomView();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			mWebView.stopLoading();
			if (mWebView.inCustomView()) {
				mWebView.hideCustomView();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			mWebView.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView.inCustomView()) {
				mWebView.hideCustomView();
				return true;
			}
			if (mWebView.canGoBack()) {
				mWebView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_textview:
			if (mWebView.inCustomView()) {
				mWebView.hideCustomView();
			} else if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				finish();
			}
			break;
		}

	}
}
