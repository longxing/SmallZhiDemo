package com.iii360.box.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.util.BoxManagerUtils;

public class TipDialog extends Dialog implements android.view.View.OnClickListener {
	private TextView titleTv;
	private ListView itemsLv;
	private String title;
	private RelativeLayout btn;
	private Context context;
	private LinearLayout itemsLayout;
	private String[] items = new String[] {};

	public TipDialog(Context context) {
		super(context, R.style.loading_dialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tip_dialog);
		titleTv = (TextView) findViewById(R.id.tip_dialog_title_tv);
		btn = (RelativeLayout) findViewById(R.id.tip_dialog_btn);
		itemsLayout = (LinearLayout) findViewById(R.id.tip_dialog_items_layout);
		// itemsLv = (ListView) findViewById(R.id.tip_dialog_title_lv);
		btn.setOnClickListener(this);
	}

	public void setItems(String[] args) {
		items = args;
	}

	@Override
	public void show() {
		super.show();
		titleTv.setText(title);
		fillItems();
	}

	private void fillItems() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.topMargin=(int) (5*BoxManagerUtils.getScreenDensity(context));
		for (int i = 0; i < items.length; i++) {
			TextView tv = new TextView(context);
			tv.setText(items[i]);
			tv.setTextSize( 16);
			tv.setTextColor(Color.parseColor("#000000"));
			tv.setLayoutParams(params);
			itemsLayout.addView(tv);
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void onClick(View v) {
		dismiss();
	}
}
