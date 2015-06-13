package com.iii360.box.fragment;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.iii360.box.R;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;

/**
 * 引导页前两张
 * 
 * @author terry
 * 
 */
public class ImageViewFragment extends Fragment {
	private Bitmap bitmap;
	private ImageView imageView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_imageview, null);
		initViews(v);
		return v;
	}

	@Override
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
			bitmap = null;
		}
	}
	private void initDatas(){
		Bundle bundle = getArguments();
		int index = bundle.getInt(KeyList.PKEY_GUIDE_VIEW_RESID);
		String resourcePath = null;
		if (index == 1) {
			resourcePath = "guide_view1.png";
		} else {
			resourcePath = "guide_view2.png";
		}
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
		LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,BoxManagerUtils.getScreenWidthPx(getActivity()) * bitmapHeight
				/ bitmapWidth);
		// Log.i("info", ""+(int)
		// getActivity().getResources().getDimension(R.dimen.guide_view_margin_top));
		params.topMargin = (int) getActivity().getResources().getDimension(R.dimen.guide_view_margin_top);
		imageView.setLayoutParams(params);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bitmap);
	}
	private void initViews(View v) {
	
		imageView = (ImageView) v.findViewById(R.id.guideview_imageview);

		// ((ImageView)
		// v.findViewById(R.id.guideview_imageview)).setImageResource(resouceId);
	}
}
