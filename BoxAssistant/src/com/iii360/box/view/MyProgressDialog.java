package com.iii360.box.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.iii360.box.R;

/**
 * @author terry
 * 
 */
public class MyProgressDialog extends Dialog {
	private String mTitle;
	private ImageView view;
	private TextView tv;
	private OnBackPressListener onBackPressListener;

	public MyProgressDialog(Context context) {
		super(context, R.style.loading_dialog);
		// TODO Auto-generated constructor stub
	}

	public MyProgressDialog(Context context, String title) {
		super(context, R.style.loading_dialog);
		this.mTitle = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progressdialog_layout);
		view = (ImageView) findViewById(R.id.img);
		tv = (TextView) findViewById(R.id.tipTextView);
		this.setCanceledOnTouchOutside(false);
	}

	public void setMessage(String msg) {
		this.mTitle = msg;
	}

	@Override
	public void show() {
		super.show();
		tv.setText(mTitle != null ? mTitle : "");
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.load_animation);
		view.startAnimation(animation);
	}

	public void setEditText(String content) {

	}

	public String getContent() {
		return null;
	}

	public void setOnBackPressListener(OnBackPressListener onBackPressListener) {
		this.onBackPressListener = onBackPressListener;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (onBackPressListener != null)
			onBackPressListener.onBack();
		this.dismiss();

	}

	public interface OnBackPressListener {
		void onBack();
	}
}
