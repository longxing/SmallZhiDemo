package com.iii360.box.help;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.iii360.box.R;
import com.iii360.box.util.BoxManagerUtils;

public class HTML5WebView extends WebView {

	private Context mContext;
	private MyWebChromeClient mWebChromeClient;
	private View mCustomView;
	private FrameLayout mCustomViewContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;

	private FrameLayout mContentView;
	private LinearLayout mBrowserFrameLayout;
	private FrameLayout mLayout;
	private View progressView;
	static final String LOGTAG = "HTML5WebView";

	private void init(Context context) {
		mContext = context;
		Activity a = (Activity) mContext;
		mLayout = new FrameLayout(context);

		mBrowserFrameLayout = (LinearLayout) LayoutInflater.from(a).inflate(R.layout.activity_help, null);
		mContentView = (FrameLayout) mBrowserFrameLayout.findViewById(R.id.main_content);
		mCustomViewContainer = (FrameLayout) mBrowserFrameLayout.findViewById(R.id.fullscreen_custom_content);
		progressView = mBrowserFrameLayout.findViewById(R.id.progress_view);
		hideProgress();
		mLayout.addView(mBrowserFrameLayout, COVER_SCREEN_PARAMS);

		mWebChromeClient = new MyWebChromeClient();
		setWebChromeClient(mWebChromeClient);

		setWebViewClient(new MyWebViewClient());

		// Configure the webview
		WebSettings s = getSettings();
		s.setBuiltInZoomControls(true);
		s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		s.setUseWideViewPort(true);
		s.setLoadWithOverviewMode(true);
		s.setSavePassword(true);
		s.setSaveFormData(true);
		s.setJavaScriptEnabled(true);
		s.setDomStorageEnabled(true);
		mContentView.addView(this);
	}

	private void hideProgress() {
		progressView.setVisibility(View.GONE);
		FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) progressView.getLayoutParams();
		params.width = 0;
		progressView.setLayoutParams(params);
	}

	private void showProgress(int progress) {
		if (progress == 100) {
			hideProgress();
			return;
		}
		progressView.setVisibility(View.VISIBLE);
		int globalWidth =BoxManagerUtils.getScreenWidthPx(mContext);
		int width = globalWidth * progress / 100;
		FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) progressView.getLayoutParams();
		params.width = width;
		progressView.setLayoutParams(params);
	}

	public HTML5WebView(Context context) {
		super(context);
		init(context);
	}

	public HTML5WebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HTML5WebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public FrameLayout getLayout() {
		return mLayout;
	}

	public boolean inCustomView() {
		return (mCustomView != null);
	}

	public void hideCustomView() {
		mWebChromeClient.onHideCustomView();
	}

//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if ((mCustomView == null) && canGoBack()) {
//				goBack();
//				return true;
//			}
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	private class MyWebChromeClient extends WebChromeClient {
		private View mVideoProgressView;
		@Override
		public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
			// Log.i(LOGTAG, "here in on ShowCustomView");
			HTML5WebView.this.setVisibility(View.GONE);

			// if a view already exists then immediately terminate the new one
			if (mCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}

			mCustomViewContainer.addView(view);
			mCustomView = view;
			mCustomViewCallback = callback;
			mCustomViewContainer.setVisibility(View.VISIBLE);
		}

		@Override
		public void onHideCustomView() {

			if (mCustomView == null)
				return;

			// Hide the custom view.
			mCustomView.setVisibility(View.GONE);

			// Remove the custom view from its container.
			mCustomViewContainer.removeView(mCustomView);
			mCustomView = null;
			mCustomViewContainer.setVisibility(View.GONE);
			mCustomViewCallback.onCustomViewHidden();

			HTML5WebView.this.setVisibility(View.VISIBLE);

			// Log.i(LOGTAG, "set it to webVew");
		}
		@Override
		public View getVideoLoadingProgressView() {
			// Log.i(LOGTAG, "here in on getVideoLoadingPregressView");

			if (mVideoProgressView == null) {
				LayoutInflater inflater = LayoutInflater.from(mContext);
				mVideoProgressView = inflater.inflate(R.layout.video_loading_progress, null);
			}
			return mVideoProgressView;
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			((Activity) mContext).setTitle(title);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			showProgress(newProgress);
		}

		@Override
		public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
			callback.invoke(origin, true, false);
		}
	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			// Log.i("info", "onPageFinished");
			super.onPageFinished(view, url);
			hideProgress();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			// TODO Auto-generated method stub
			// Log.i("info", "onReceivedError");
			super.onReceivedError(view, errorCode, description, failingUrl);
			hideProgress();
		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i(LOGTAG, "shouldOverrideUrlLoading: " + url);
			// don't override URL so that stuff within iframe can work properly
			// view.loadUrl(url);
			return false;
		}
	}

	static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
}