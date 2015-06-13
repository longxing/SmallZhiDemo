package com.iii360.box.fragment;

import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.iii360.box.R;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.connect.BootActivity;
import com.iii360.box.connect.UnConnectWifiActivity;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.WifiUtils;

public class WithButtonFragment extends Fragment {
	private BasePreferences basePreferences;
	private Bitmap bitmap;
	private ImageView imageView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		basePreferences = new BasePreferences(getActivity());
		View v = inflater.inflate(R.layout.fragment_imageview_withbutton, null);
		initViews(v);
		return v;
	}

	public void onStart() {
//		Log.i("info", "onStart");
		super.onStart();
		initDatas();
	}
	@Override
	public void onStop() {
//		Log.i("info", "onStop");
		super.onStop();
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			System.gc();
			bitmap=null;
		}
	}
	private void initDatas(){
		String resourcePath = "guide_view3.png";
		InputStream in = null;
		try {
			in = getActivity().getAssets().open(resourcePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (in == null)
			return;
		bitmap = BitmapFactory.decodeStream(in);
		try {
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int bitmapHeight = bitmap.getHeight();
		int bitmapWidth = bitmap.getWidth();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, BoxManagerUtils.getScreenWidthPx(getActivity())
				* bitmapHeight / bitmapWidth);
		params.topMargin = (int) getActivity().getResources().getDimension(R.dimen.guide_view_margin_top);
		imageView.setLayoutParams(params);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bitmap);
	}

	private void initViews(View v) {

		imageView = (ImageView) v.findViewById(R.id.guideview_imageview);
	
		final String inner = getArguments().getString("inner");
		((Button) v.findViewById(R.id.guideview_enter_btn)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (inner != null && inner.equals("yes")) {
					getActivity().finish();
				} else {
					basePreferences.setPrefBoolean(KeyList.PKEY_HAVE_ENTER_RECODE, true);
					if (!WifiUtils.isConnectWifi(getActivity())) {
						Intent intent = new Intent(getActivity(), UnConnectWifiActivity.class);
						intent.putExtra(KeyList.PKEY_BOOLEAN_APP_START_NO_WIFI, true);
						getActivity().startActivity(intent);
						getActivity().finish();
						return;
					}
					getActivity().startActivity(new Intent(getActivity(), BootActivity.class));
					getActivity().finish();
				}

			}
		});
	}
}
