package com.iii360.box.view;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iii360.box.MainMyLoveActivity;
import com.iii360.box.R;

public class ViewHead {

	public static void showLeft(final Activity context, final String title,
			final int resid) {
		// TODO Auto-generated method stub
		ImageButton mHeadBackBtn = (ImageButton) context
				.findViewById(R.id.head_back_btn);
		mHeadBackBtn.setImageResource(resid);
		final TextView mHeadTitle = (TextView) context
				.findViewById(R.id.head_title_tv);

		new Handler(context.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHeadTitle.setText(title);
			}
		});

		mHeadBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				context.finish();
			}
		});
	}

	public static ImageButton showRight(final Activity context,
			final String title, final int resid) {
		ImageButton mHeadBackBtn = (ImageButton) context
				.findViewById(R.id.head_back_btn);
		ImageButton mRightBtn = (ImageButton) context
				.findViewById(R.id.head_right_btn);
		mHeadBackBtn.setVisibility(View.GONE);
		mRightBtn.setVisibility(View.VISIBLE);
		mRightBtn.setImageResource(resid);
		// mRightBtn.setBackgroundResource(resid);

		final TextView mHeadTitle = (TextView) context
				.findViewById(R.id.head_title_tv);
		new Handler(context.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHeadTitle.setText(title);
			}
		});
		return mRightBtn;
	}

	public static ImageButton showAll(final Activity context,
			final String title, final int resid) {
		// TODO Auto-generated method stub
		ImageButton mHeadBackBtn = (ImageButton) context
				.findViewById(R.id.head_back_btn);
		// mHeadBackBtn.setBackgroundResource(R.drawable.ba_back_btn_selector);
		mHeadBackBtn.setImageResource(R.drawable.ba_back_btn_selector);

		ImageButton mRightBtn = (ImageButton) context
				.findViewById(R.id.head_right_btn);
		mRightBtn.setVisibility(View.VISIBLE);
		// mRightBtn.setBackgroundResource(rightResid);
		mRightBtn.setImageResource(resid);

		final TextView mHeadTitle = (TextView) context
				.findViewById(R.id.head_title_tv);

		new Handler(context.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHeadTitle.setText(title);
			}
		});

		mHeadBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				context.finish();
				if (context instanceof MainMyLoveActivity) {
					context.overridePendingTransition(0, R.anim.out_from_up);
				}
			}
		});

		return mRightBtn;
	}

}
