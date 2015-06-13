package com.iii360.box.config;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.iii360.box.MainModeActivity;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.BoxManagerUtils;

public class NoSmartActivity extends BaseActivity implements OnClickListener {
	private ImageView noSmartImageView;
	private Bitmap bitmapResource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_smart);
		setupView();
		addListeners();
	}

	private void addListeners() {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmapResource != null && !bitmapResource.isRecycled()) {
			bitmapResource.recycle();
			System.gc();
			bitmapResource = null;
		}
	}

	private void setupView() {
		Class cls = (Class) getIntent().getSerializableExtra("classname");
		if (MainModeActivity.class.equals(cls)) {
			this.setViewHead("情景模式");
		} else if (PartsManagerActivity.class.equals(cls)) {
			this.setViewHead("智能设备");
		}
		noSmartImageView = (ImageView) findViewById(R.id.no_smart_iv);
		InputStream in = null;
		try {
			in = getAssets().open("smart_company_img.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (in == null)
			return;
		bitmapResource = BitmapFactory.decodeStream(in);
		try {
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int bitmapHeight = bitmapResource.getHeight();
		int bitmapWidth = bitmapResource.getWidth();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, BoxManagerUtils.getScreenWidthPx(context) * bitmapHeight
				/ bitmapWidth);
		params.topMargin = (int) getResources().getDimension(R.dimen.no_smart_margintop);
		noSmartImageView.setLayoutParams(params);
		noSmartImageView.setScaleType(ScaleType.CENTER_CROP);
		noSmartImageView.setImageBitmap(bitmapResource);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_textview:
			finish();
			break;
		}
	}
}
