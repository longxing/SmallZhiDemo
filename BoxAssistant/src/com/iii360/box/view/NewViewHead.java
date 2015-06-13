package com.iii360.box.view;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iii360.box.MainMyLoveActivity;
import com.iii360.box.R;

public class NewViewHead {
	public static void showLeft(final Activity context, final String title) {
		// TODO Auto-generated method stub
		final TextView mback = (TextView) context
				.findViewById(R.id.head_left_textview);
		final TextView mHeadTitle = (TextView) context
				.findViewById(R.id.head_title_tv);
		context.findViewById(R.id.head_right_btn).setVisibility(View.GONE);
		new Handler(context.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				mHeadTitle.setText(title);
			}
		});
		mback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				context.finish();
			}
		});
	}

	public static ImageButton showAll(final Activity context,
			final String title, int resId) {
		ImageButton mHeadBackBtn = (ImageButton) context
				.findViewById(R.id.head_right_btn);
		mHeadBackBtn.setImageResource(resId);
		mHeadBackBtn.setVisibility(View.VISIBLE);
		final TextView mback = (TextView) context
				.findViewById(R.id.head_left_textview);
		final TextView mHeadTitle = (TextView) context
				.findViewById(R.id.head_title_tv);
		new Handler(context.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				mHeadTitle.setText(title);
			}
		});
		mback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				context.finish();
				if (context instanceof MainMyLoveActivity) {
					context.overridePendingTransition(0, R.anim.out_from_up);
				}
			}
		});
		return mHeadBackBtn;

	}

	public static void showOnlyTitle(final Activity context, final String title) {
		context.findViewById(R.id.head_left_textview).setVisibility(View.GONE);
		context.findViewById(R.id.head_right_btn).setVisibility(View.GONE);
		final TextView mHeadTitle = (TextView) context
				.findViewById(R.id.head_title_tv);
		new Handler(context.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				mHeadTitle.setText(title);
			}
		});
	}
}
